package com.tamin.taminhamrah.di.interceptor

import com.tamin.taminhamrah.data.local.preference.PreferenceManager

object TokenHolder {
    @Volatile
    private var accessToken: String? = null
    @Volatile
    private var refreshToken: String? = null
    @Volatile
    private var expiresAt: Long = 0
    @Volatile
    private var initialized = false

    fun ensureInit(preferenceManager: PreferenceManager) {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    accessToken = preferenceManager.getToken()
                    refreshToken = preferenceManager.getRefreshToken()
                    expiresAt = preferenceManager.getTokenExpireTime()
                    initialized = true
                }
            }
        }
    }

    fun getAccessToken(preferenceManager: PreferenceManager): String {
        ensureInit(preferenceManager)
        return accessToken ?: ""
    }

    fun getRefreshToken(preferenceManager: PreferenceManager): String? {
        ensureInit(preferenceManager)
        return refreshToken
    }

    fun getExpiresAt(preferenceManager: PreferenceManager): Long {
        ensureInit(preferenceManager)
        return expiresAt
    }

    fun updateTokens(
        preferenceManager: PreferenceManager,
        newAccessToken: String,
        newRefreshToken: String,
        newExpiresAt: Long
    ) {
        accessToken = newAccessToken
        refreshToken = newRefreshToken
        expiresAt = newExpiresAt
        preferenceManager.setToken(newAccessToken)
        preferenceManager.setRefreshToken(newRefreshToken)
        preferenceManager.setTokenExpireTime(newExpiresAt)
    }
    fun logOut(
        preferenceManager: PreferenceManager,
    ) {
         updateTokens(preferenceManager,"","",0)
    }
}
