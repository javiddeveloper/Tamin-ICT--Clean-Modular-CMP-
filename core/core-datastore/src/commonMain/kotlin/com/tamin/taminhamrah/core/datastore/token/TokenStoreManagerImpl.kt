/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.core.datastore.token

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.repository.TokenStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class TokenStoreManagerImpl(
    private val settings: Settings
) : TokenStoreManager {

    private val tokenKey = "TOKEN"
    private val refreshTokenKey = "REFRESH_TOKEN"
    private val userKey = "USER_KEY"
    private val codeVerifierKey = "CODE_VERIFIER"
    private val tokenValidation = "TOKEN_VALID"

    private val _tokenValidFlow = MutableStateFlow(
        settings.getBoolean(tokenValidation, false)
    )

    override fun saveToken(token: String?) {
        if (token == null) {
            settings.remove(tokenKey)
        } else {
            settings.putString(tokenKey, token)
        }
    }

    override fun getToken(): String? {
        return settings.getStringOrNull(tokenKey)
    }

    override fun saveRefreshToken(refreshToken: String?) {
        if (refreshToken == null) {
            settings.remove(refreshTokenKey)
        } else {
            settings.putString(refreshTokenKey, refreshToken)
        }
    }

    override fun getRefreshToken(): String? {
        return settings.getStringOrNull(refreshTokenKey)
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
}
