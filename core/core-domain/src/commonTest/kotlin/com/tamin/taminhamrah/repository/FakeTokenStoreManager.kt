package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.auth.TokenSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory token store. Reads resolve through the active slot, the same way the real one does in
 * a debug build, so a test can assert both what a slot holds and what the app would send.
 */
class FakeTokenStoreManager : TokenStoreManager {

    val tokens = mutableMapOf<TokenSlot, String?>()
    val refreshTokens = mutableMapOf<TokenSlot, String?>()

    private val activeSlot = MutableStateFlow(TokenSlot.USER)
    private val tokenValid = MutableStateFlow(false)
    private val authProcessing = MutableStateFlow(false)

    // Named apart from the interface's getters, which would otherwise share a JVM signature.
    var storedUserId: String? = null
    var storedUserType: String? = null
    var storedCodeVerifier: String? = null

    override fun saveToken(token: String?) = saveToken(TokenSlot.USER, token)
    override fun getToken(): String? = getToken(activeSlot.value)
    override fun saveRefreshToken(refreshToken: String?) =
        saveRefreshToken(TokenSlot.USER, refreshToken)

    override fun getRefreshToken(): String? = getRefreshToken(activeSlot.value)

    override fun getToken(slot: TokenSlot): String? = tokens[slot]
    override fun saveToken(slot: TokenSlot, token: String?) { tokens[slot] = token }
    override fun getRefreshToken(slot: TokenSlot): String? = refreshTokens[slot]
    override fun saveRefreshToken(slot: TokenSlot, refreshToken: String?) {
        refreshTokens[slot] = refreshToken
    }

    override fun getActiveSlot(): TokenSlot = activeSlot.value
    override fun activeSlotFlow(): Flow<TokenSlot> = activeSlot.asStateFlow()
    override suspend fun setActiveSlot(slot: TokenSlot) {
        if (slot == TokenSlot.AGENT) return
        activeSlot.value = slot
        setTokenValid(!getToken(slot).isNullOrBlank())
    }

    override fun saveUserId(userId: String?) { storedUserId = userId }
    override fun getUserId(): String? = storedUserId
    override fun saveUserType(userType: String?) { storedUserType = userType }
    override fun getUserType(): String? = storedUserType
    override fun saveCodeVerifier(codeVerifier: String?) { storedCodeVerifier = codeVerifier }
    override fun getCodeVerifier(): String? = storedCodeVerifier

    override fun tokenValidFlow(): Flow<Boolean> = tokenValid.asStateFlow()
    override suspend fun setTokenValid(isValid: Boolean) { tokenValid.value = isValid }
    override fun isAuthProcessingFlow(): Flow<Boolean> = authProcessing.asStateFlow()
    override fun setAuthProcessing(isProcessing: Boolean) { authProcessing.value = isProcessing }
}
