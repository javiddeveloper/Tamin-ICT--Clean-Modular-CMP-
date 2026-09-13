/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.auth.TokenSlot
import kotlinx.coroutines.flow.Flow

interface TokenStoreManager {
    /** Writes the real login's token — always [TokenSlot.USER], never the debug slot. */
    fun saveToken(token: String?)

    /**
     * The token the app should send. Resolves to [getActiveSlot]'s token in a debug build and to
     * [TokenSlot.USER]'s in a release build.
     */
    fun getToken(): String?

    /** Writes the real login's refresh token — always [TokenSlot.USER]. */
    fun saveRefreshToken(refreshToken: String?)

    /** The refresh token that pairs with [getToken], resolved the same way. */
    fun getRefreshToken(): String?

    // --- Slots (Developer Options) -------------------------------------------------------------

    /** Reads one slot regardless of which is active — for the token screen and manual refresh. */
    fun getToken(slot: TokenSlot): String?

    fun saveToken(slot: TokenSlot, token: String?)

    fun getRefreshToken(slot: TokenSlot): String?

    fun saveRefreshToken(slot: TokenSlot, refreshToken: String?)

    /** Always [TokenSlot.USER] in a release build. */
    fun getActiveSlot(): TokenSlot

    /** Emits on every switch, so `isLoggedIn` re-reads the token the new slot holds. */
    fun activeSlotFlow(): Flow<TokenSlot>

    /**
     * Switches which slot the app authenticates with. [TokenSlot.AGENT] is not a bearer slot and
     * is ignored here. Callers must invalidate the HTTP client's cached bearer afterwards
     * (see [AuthTokenInvalidator]), or the previous slot's token keeps being sent.
     */
    suspend fun setActiveSlot(slot: TokenSlot)
    fun saveUserId(userId: String?)
    fun getUserId(): String?
    fun saveUserType(userType: String?)
    fun getUserType(): String?
    fun saveCodeVerifier(codeVerifier: String?)
    fun getCodeVerifier(): String?
    fun tokenValidFlow(): Flow<Boolean>
    suspend fun setTokenValid(isValid: Boolean)
    fun isAuthProcessingFlow(): Flow<Boolean>
    fun setAuthProcessing(isProcessing: Boolean)
}
