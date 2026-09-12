package com.tamin.taminhamrah.dataSource.authSource

import com.tamin.taminhamrah.model.auth.TokenResponseDto

interface AuthRemoteDataSource {
    suspend fun exchangeCodeForTokens(
        redirectUri: String,
        clientId: String,
        code: String,
        codeVerifier: String,
        audience: String,
    ): TokenResponseDto

    suspend fun refreshTokens(
        refreshToken: String,
        clientId: String,
    ): TokenResponseDto

    suspend fun debugClientCredentialsLogin(
        clientId: String,
        clientSecret: String,
    ): TokenResponseDto

    suspend fun signOut(token: String): String

    suspend fun revokeToken(accessToken: String?, refreshToken: String?): Boolean

}
