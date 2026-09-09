package com.tamin.taminhamrah.repository.authRepository

import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.LocalDataClearer
import com.tamin.taminhamrah.repository.AuthTokenInvalidator
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.repository.AuthRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import co.touchlab.kermit.Logger

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenStoreManager: TokenStoreManager,
    private val localDataClearer: LocalDataClearer,
    private val authTokenInvalidator: AuthTokenInvalidator
) : AuthRepository {

    private val logger = Logger.withTag("AuthRepository")

    override val isLoggedIn: Flow<Boolean> = tokenStoreManager.tokenValidFlow().map { isValid ->
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

    override suspend fun refreshToken(): Boolean {
        val currentRefreshToken = tokenStoreManager.getRefreshToken() ?: return false

        return try {
            val response = authRemoteDataSource.refreshTokens(
                refreshToken = currentRefreshToken,
                clientId = NetworkConstants.CLIENT_ID
            )
            tokenStoreManager.saveToken(response.accessToken)
            tokenStoreManager.saveRefreshToken(response.refreshToken)
            tokenStoreManager.setTokenValid(true)
            true
        } catch (e: Exception) {
            tokenStoreManager.setTokenValid(false)
            false
        }
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
