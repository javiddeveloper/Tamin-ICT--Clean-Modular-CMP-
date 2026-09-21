package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.auth.DebugLoginResultDN
import com.tamin.taminhamrah.model.auth.TokenSlot
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

    /**
     * Debug-only "back-to-back" login (client_credentials grant, no PKCE) — see
     * [com.tamin.taminhamrah.util.AppConfig.isDebug]-gated `DebugLoginViewModel` for the only
     * caller. Resolves its token endpoint from the same
     * [com.tamin.taminhamrah.model.BaseUrlKey.ACCOUNT] override Developer Options exposes for the
     * PKCE flow, so pointing that one entry at a pilot/test environment routes both login
     * methods there together. On a successful login, tokens are persisted through the exact
     * same [TokenStoreManager] calls as [exchangeCodeForTokens], so the rest of the app (auth
     * headers, `isLoggedIn`, refresh) treats it identically to a PKCE login.
     */
    suspend fun debugClientCredentialsLogin(
        clientId: String,
        clientSecret: String,
    ): DebugLoginResultDN

    suspend fun refreshToken(): Boolean

    /**
     * Refreshes one named slot instead of whichever is active — the per-token «رفرش» in Developer
     * Options. Returns `false` when that slot has no refresh token to trade in, which is the
     * normal answer for a client_credentials slot.
     */
    suspend fun refreshTokenSlot(slot: TokenSlot): Boolean

    /**
     * Makes [slot] the one the app authenticates with, and drops the HTTP client's cached bearer
     * so the very next request already carries the new token.
     */
    suspend fun switchTokenSlot(slot: TokenSlot)

    suspend fun logout()

    suspend fun signOut(token: String): Flow<String>

    suspend fun revokeToken(): Boolean
}
