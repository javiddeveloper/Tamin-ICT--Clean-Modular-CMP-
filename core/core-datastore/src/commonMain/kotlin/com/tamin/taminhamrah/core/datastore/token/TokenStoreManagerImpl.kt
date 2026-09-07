/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.core.datastore.token

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class TokenStoreManagerImpl(
    private val settings: Settings
) : TokenStoreManager {

    private val tokenKey = "TOKEN"
    private val refreshTokenKey = "REFRESH_TOKEN"
    private val userKey = "USER_KEY"
    private val userTypeKey = "USER_TYPE"
    private val codeVerifierKey = "CODE_VERIFIER"
    private val tokenValidation = "TOKEN_VALID"
    private val activeSlotKey = "ACTIVE_TOKEN_SLOT"

    private val _tokenValidFlow = MutableStateFlow(
        settings.getBoolean(tokenValidation, false)
    )

    private val _isAuthProcessing = MutableStateFlow(false)

    private val _activeSlotFlow = MutableStateFlow(readActiveSlot())

    /**
     * [TokenSlot.USER] keeps the original key names, so an install that predates slots still finds
     * the session it already had. Only the debug slots get a suffix.
     */
    private fun tokenKeyOf(slot: TokenSlot): String = when (slot) {
        TokenSlot.USER -> tokenKey
        else -> "${tokenKey}_${slot.name}"
    }

    private fun refreshTokenKeyOf(slot: TokenSlot): String = when (slot) {
        TokenSlot.USER -> refreshTokenKey
        else -> "${refreshTokenKey}_${slot.name}"
    }

    private fun readActiveSlot(): TokenSlot {
        if (!AppConfig.isDebug) return TokenSlot.USER
        val stored = settings.getStringOrNull(activeSlotKey) ?: return TokenSlot.USER
        return TokenSlot.entries.firstOrNull { it.name == stored } ?: TokenSlot.USER
    }

    override fun saveToken(token: String?) {
        saveToken(TokenSlot.USER, token)
        // Signing out of the real account must not leave the app running on a debug token: the
        // next session starts from the same slot every time.
        if (token == null) {
            settings.remove(activeSlotKey)
            _activeSlotFlow.value = TokenSlot.USER
        }
    }

    override fun getToken(): String? = getToken(getActiveSlot())

    override fun saveRefreshToken(refreshToken: String?) =
        saveRefreshToken(TokenSlot.USER, refreshToken)

    override fun getRefreshToken(): String? = getRefreshToken(getActiveSlot())

    override fun getToken(slot: TokenSlot): String? =
        settings.getStringOrNull(tokenKeyOf(slot))

    override fun saveToken(slot: TokenSlot, token: String?) {
        val key = tokenKeyOf(slot)
        if (token == null) settings.remove(key) else settings.putString(key, token)
    }

    override fun getRefreshToken(slot: TokenSlot): String? =
        settings.getStringOrNull(refreshTokenKeyOf(slot))

    override fun saveRefreshToken(slot: TokenSlot, refreshToken: String?) {
        val key = refreshTokenKeyOf(slot)
        if (refreshToken == null) settings.remove(key) else settings.putString(key, refreshToken)
    }

    override fun getActiveSlot(): TokenSlot =
        if (AppConfig.isDebug) _activeSlotFlow.value else TokenSlot.USER

    override fun activeSlotFlow(): Flow<TokenSlot> = _activeSlotFlow.asStateFlow()

    override suspend fun setActiveSlot(slot: TokenSlot) {
        // AGENT is not a bearer slot, and a release build has nothing to switch between.
        if (!AppConfig.isDebug || slot == TokenSlot.AGENT) return
        settings.putString(activeSlotKey, slot.name)
        _activeSlotFlow.value = slot
        // The switched-to slot decides whether the app now counts as logged in.
        setTokenValid(!getToken(slot).isNullOrBlank())
    }

    override fun saveUserId(userId: String?) {
        if (userId == null) {
            settings.remove(userKey)
        } else {
            settings.putString(userKey, userId)
        }
    }

    override fun getUserId(): String? {
        return settings.getStringOrNull(userKey)
    }

    override fun saveUserType(userType: String?) {
        if (userType == null) {
            settings.remove(userTypeKey)
        } else {
            settings.putString(userTypeKey, userType)
        }
    }

    override fun getUserType(): String? {
        return settings.getStringOrNull(userTypeKey)
    }

    override fun saveCodeVerifier(codeVerifier: String?) {
        if (codeVerifier == null) {
            settings.remove(codeVerifierKey)
        } else {
            settings.putString(codeVerifierKey, codeVerifier)
        }
    }

    override fun getCodeVerifier(): String? {
        return settings.getStringOrNull(codeVerifierKey)
    }

    override fun tokenValidFlow(): Flow<Boolean> {
        return _tokenValidFlow.asStateFlow()
    }

    override suspend fun setTokenValid(isValid: Boolean) {
        settings.putBoolean(tokenValidation, isValid)
        _tokenValidFlow.value = isValid
    }

    override fun isAuthProcessingFlow(): Flow<Boolean> {
        return _isAuthProcessing.asStateFlow()
    }

    override fun setAuthProcessing(isProcessing: Boolean) {
        _isAuthProcessing.value = isProcessing
    }
}
