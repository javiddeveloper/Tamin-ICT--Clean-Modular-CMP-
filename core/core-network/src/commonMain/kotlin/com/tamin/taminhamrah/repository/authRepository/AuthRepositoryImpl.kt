package com.tamin.taminhamrah.repository.authRepository

import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenStoreManager: TokenStoreManager
) : AuthRepository {

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
        tokenStoreManager.saveToken(null)
        tokenStoreManager.saveRefreshToken(null)
        tokenStoreManager.setTokenValid(false)
    }
}
