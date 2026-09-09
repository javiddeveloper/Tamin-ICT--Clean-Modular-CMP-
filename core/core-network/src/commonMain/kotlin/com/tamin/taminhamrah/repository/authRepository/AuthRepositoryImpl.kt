package com.tamin.taminhamrah.repository.authRepository

import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.LocalDataClearer
import com.tamin.taminhamrah.repository.AuthTokenInvalidator
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.model.auth.DebugLoginResultDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.repository.AuthRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import co.touchlab.kermit.Logger

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenStoreManager: TokenStoreManager,
    private val localDataClearer: LocalDataClearer,
    private val authTokenInvalidator: AuthTokenInvalidator
) : AuthRepository {

    private val logger = Logger.withTag("AuthRepository")

    // Combined with the active slot, not just the valid flag: switching slots in Developer Options
    // changes which token getToken() returns without the flag moving, and the screen behind the
    // login gate has to re-evaluate on that alone. A release build's slot flow never emits twice,
    // so this stays the single-token check it has always been.
    override val isLoggedIn: Flow<Boolean> = combine(
        tokenStoreManager.tokenValidFlow(),
        tokenStoreManager.activeSlotFlow(),
    ) { isValid, _ ->
        !tokenStoreManager.getToken().isNullOrEmpty() && isValid
    }

    override suspend fun getAccessToken(): String? {
        return tokenStoreManager.getToken()
    }

    override suspend fun exchangeCodeForTokens(
        code: String,
        codeVerifier: String,
        audience: String,
        redirectUri: String,
        clientId: String
    ): Boolean {
        return try {
            val response = authRemoteDataSource.exchangeCodeForTokens(
                redirectUri = redirectUri,
                clientId = clientId,
                code = code,
                codeVerifier = codeVerifier,
                audience = audience
            )

            tokenStoreManager.saveToken(response.accessToken)
            tokenStoreManager.saveRefreshToken(response.refreshToken)
            tokenStoreManager.setTokenValid(true)
            authTokenInvalidator.invalidateAll()
            true
        } catch (e: Exception) {
            print(e)
            false
        }
    }

    override suspend fun debugClientCredentialsLogin(
        clientId: String,
        clientSecret: String,
    ): DebugLoginResultDN {
        val response = authRemoteDataSource.debugClientCredentialsLogin(
            clientId = clientId,
            clientSecret = clientSecret
        )
        val isSuccess = !response.accessToken.isNullOrBlank()
        if (isSuccess) {
            // Stored in its own slot, so the account the tester was signed into survives this
            // login and can be switched back to. Storing is all that happens here — making it the
            // token the app sends is [switchTokenSlot], which the caller decides on: the login
            // screen switches, the token screen's re-issue button deliberately does not.
            tokenStoreManager.saveToken(TokenSlot.BACK_TO_BACK, response.accessToken)
            tokenStoreManager.saveRefreshToken(TokenSlot.BACK_TO_BACK, response.refreshToken)
        }
        return DebugLoginResultDN(
            isSuccess = isSuccess,
            accessToken = response.accessToken,
            tokenType = response.tokenType,
            expiresIn = response.expiresIn,
            error = response.error,
            errorDescription = response.errorDescription,
        )
    }

    override suspend fun refreshToken(): Boolean =
        refreshTokenSlot(tokenStoreManager.getActiveSlot())

    /**
     * Reads and writes the same slot, so refreshing one from Developer Options never moves another
     * one's token. A back-to-back slot has no refresh_token to trade in, so this reports failure
     * for it and the caller re-issues that login instead.
     *
     * Only the active slot's outcome touches the logged-in flag — refreshing a slot the app is not
     * currently authenticating with must not sign the user out of the one it is.
     */
    override suspend fun refreshTokenSlot(slot: TokenSlot): Boolean {
        val isActive = slot == tokenStoreManager.getActiveSlot()
        val currentRefreshToken = tokenStoreManager.getRefreshToken(slot) ?: return false

        return try {
            val response = authRemoteDataSource.refreshTokens(
                refreshToken = currentRefreshToken,
                clientId = NetworkConstants.CLIENT_ID
            )
            tokenStoreManager.saveToken(slot, response.accessToken)
            tokenStoreManager.saveRefreshToken(slot, response.refreshToken)
            if (isActive) {
                tokenStoreManager.setTokenValid(true)
                authTokenInvalidator.invalidateAll()
            }
            true
        } catch (e: Exception) {
            if (isActive) tokenStoreManager.setTokenValid(false)
            false
        }
    }

    override suspend fun switchTokenSlot(slot: TokenSlot) {
        tokenStoreManager.setActiveSlot(slot)
        // Without this the bearer provider keeps handing out the slot we just switched away from.
        authTokenInvalidator.invalidateAll()
    }

    override suspend fun logout() {
        logger.d { "logout() called. Revoking and clearing tokens." }
        // revokeToken() never throws (AuthRemoteDataSourceImpl.revokeToken swallows its own
        // errors), but localDataClearer.clearAll() can (e.g. a DB error) — and it used to run
        // unguarded, so a failure there would abort logout() before the tokens further down
        // this chain (server-side sign-out call, browser SSO cookie clear) ever ran, even
        // though the tokens above had already been wiped. Each step below is now best-effort
        // so one failing step can never block the rest of the logout sequence.
        revokeToken()
        tokenStoreManager.saveToken(null)
        tokenStoreManager.saveRefreshToken(null)
        tokenStoreManager.saveUserId(null)
        tokenStoreManager.saveUserType(null)
        tokenStoreManager.setTokenValid(false)
        authTokenInvalidator.invalidateAll()
        try {
            localDataClearer.clearAll()
        } catch (e: Exception) {
            logger.e(e) { "logout() localDataClearer.clearAll() failed, tokens are still cleared." }
        }
        logger.d { "logout() completed. Tokens cleared." }
    }

    override suspend fun signOut(token: String): Flow<String> = flow {
        logger.d { "signOut() flow started." }
        val result = withContext(NonCancellable) {
            try {
                logout()
            } catch (e: Exception) {
                logger.e(e) { "signOut() logout() step failed unexpectedly, still attempting server sign-out." }
            }
            try {
                authRemoteDataSource.signOut(token)
            } catch (e: Exception) {
                logger.e(e) { "signOut() API call failed after local logout completed." }
                "FAILED_BUT_LOGGED_OUT"
            }
        }
        logger.d { "signOut() emitting result: $result." }
        emit(result)
    }

    override suspend fun revokeToken(): Boolean {
        val accessToken = tokenStoreManager.getToken()
        val refreshToken = tokenStoreManager.getRefreshToken()
        logger.d {
            "revokeToken() - AccessToken: ${accessToken?.take(10)}..., RefreshToken: ${
                refreshToken?.take(
                    10
                )
            }..."
        }
        val result = authRemoteDataSource.revokeToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
        logger.d { "revokeToken() result: $result" }
        return result
    }
}
