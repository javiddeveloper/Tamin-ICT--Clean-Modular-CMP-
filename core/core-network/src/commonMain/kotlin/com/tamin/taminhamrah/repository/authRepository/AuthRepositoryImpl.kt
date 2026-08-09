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

    // NonCancellable because the authorization code is single-use: the moment this request leaves,
    // the server burns the code and issues tokens. If the caller's scope dies before the response
    // is persisted — the login dialog dismissing is enough, and it does dismiss ~16ms after the
    // POST — the tokens are lost *and* the code cannot be replayed, so nothing is ever stored and
    // every later call goes out unauthenticated.
    override suspend fun exchangeCodeForTokens(
        code: String,
        codeVerifier: String,
        audience: String,
        redirectUri: String,
        clientId: String
    ): Boolean = withContext(NonCancellable) {
        logger.i { "exchangeCodeForTokens: entered (codeLen=${code.length}, verifierLen=${codeVerifier.length})" }
        try {
            val response = authRemoteDataSource.exchangeCodeForTokens(
                redirectUri = redirectUri,
                clientId = clientId,
                code = code,
                codeVerifier = codeVerifier,
                audience = audience
            )

            if (response.accessToken.isNullOrBlank()) {
                // A 200 whose body did not carry access_token. Saving null here would *remove* the
                // stored token, so the app looks logged out while the network log shows success.
                logger.e { "exchangeCodeForTokens: 200 but access_token was absent or blank." }
                return@withContext false
            }
            tokenStoreManager.saveToken(response.accessToken)
            response.refreshToken?.takeIf { it.isNotBlank() }
                ?.let { tokenStoreManager.saveRefreshToken(it) }
            tokenStoreManager.setTokenValid(true)
            authTokenInvalidator.invalidateAll()
            logger.d {
                "exchangeCodeForTokens: token stored (len=${response.accessToken?.length}), " +
                    "readback=${tokenStoreManager.getToken()?.length ?: -1}, " +
                    "refreshTokenSent=${!response.refreshToken.isNullOrBlank()}"
            }
            true
        } catch (e: Exception) {
            // Was `print(e)`, which hid the cause entirely: the HTTP call can return 200 and still
            // fail here (deserialization, for one), leaving the network log green and no token saved.
            logger.e(e) { "exchangeCodeForTokens failed; no token was stored." }
            false
        }
    }

    override suspend fun refreshToken(): Boolean {
        val currentRefreshToken = tokenStoreManager.getRefreshToken()
        if (currentRefreshToken.isNullOrBlank()) {
            logger.w { "refreshToken(): no stored refresh token, cannot refresh." }
            return false
        }

        return try {
            val response = authRemoteDataSource.refreshTokens(
                refreshToken = currentRefreshToken,
                clientId = NetworkConstants.CLIENT_ID
            )
            if (response.accessToken.isNullOrBlank()) {
                // Saving a null here would *remove* the stored token, so a 200 carrying no
                // access_token would silently log the user out. Keep what we have instead.
                logger.e { "refreshToken(): 200 but access_token was absent; keeping existing token." }
                return false
            }
            tokenStoreManager.saveToken(response.accessToken)
            // Only overwrite the refresh token when the server actually sent a new one — some
            // token responses omit it, and saveRefreshToken(null) deletes the one that still works.
            response.refreshToken?.takeIf { it.isNotBlank() }
                ?.let { tokenStoreManager.saveRefreshToken(it) }
            tokenStoreManager.setTokenValid(true)
            // The bearer provider caches its tokens per HttpClient and only reloads when cleared,
            // so every client has to be told the stored token just changed.
            authTokenInvalidator.invalidateAll()
            true
        } catch (e: Exception) {
            logger.e(e) { "refreshToken() failed." }
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
