package com.tamin.taminhamrah.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>

    suspend fun getAccessToken(): String?

    suspend fun exchangeCodeForTokens(
        code: String,
        codeVerifier: String,
        audience: String,
        redirectUri: String,
        clientId: String,
    ): Boolean

    suspend fun refreshToken(): Boolean

    suspend fun logout()
}
