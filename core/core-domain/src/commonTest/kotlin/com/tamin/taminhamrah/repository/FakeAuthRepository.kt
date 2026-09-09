package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.auth.DebugLoginResultDN
import com.tamin.taminhamrah.model.auth.TokenSlot
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

    override suspend fun debugClientCredentialsLogin(
        clientId: String,
        clientSecret: String
    ): DebugLoginResultDN = DebugLoginResultDN(
        isSuccess = true,
        accessToken = "fake-access-token",
        tokenType = "Bearer",
        expiresIn = 3600,
        error = null,
        errorDescription = null
    )

    override suspend fun refreshToken(): Boolean = true

    override suspend fun refreshTokenSlot(slot: TokenSlot): Boolean = true

    override suspend fun switchTokenSlot(slot: TokenSlot) = Unit

    override suspend fun logout() {}

    override suspend fun signOut(token: String): Flow<String> = flow {
        if (signOutError != null) {
            throw signOutError!!
        }
        emit(signOutResult)
    }

    override suspend fun revokeToken(): Boolean = true
}
