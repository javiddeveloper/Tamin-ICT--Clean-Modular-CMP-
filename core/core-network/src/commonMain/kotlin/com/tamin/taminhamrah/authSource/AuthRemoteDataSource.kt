package com.tamin.core.network.datasource.authSource

import com.tamin.core.network.model.auth.TokenResponseDto

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
}
