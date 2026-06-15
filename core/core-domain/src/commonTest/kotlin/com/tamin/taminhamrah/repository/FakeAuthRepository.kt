package com.tamin.taminhamrah.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthRepository : AuthRepository {
    override val isLoggedIn: Flow<Boolean> = MutableStateFlow(false)

    var signOutResult: String = ""
    var signOutError: Throwable? = null

    override suspend fun getAccessToken(): String? = null

    override suspend fun exchangeCodeForTokens(
        code: String,
        codeVerifier: String,
        audience: String,
        redirectUri: String,
        clientId: String
    ): Boolean = true

    override suspend fun refreshToken(): Boolean = true

    override suspend fun logout() {}

    override suspend fun signOut(token: String): Flow<String> = flow {
        if (signOutError != null) {
            throw signOutError!!
        }
        emit(signOutResult)
    }

    override suspend fun revokeToken(): Boolean = true
}
