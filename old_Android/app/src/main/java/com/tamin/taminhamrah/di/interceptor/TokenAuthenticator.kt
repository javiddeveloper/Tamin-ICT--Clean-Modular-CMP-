package com.tamin.taminhamrah.di.interceptor

import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.remote.user.UserService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val userServiceProvider: Provider<UserService>
) : Authenticator {

    @Volatile
    private var isLoggingOut = false

    override fun authenticate(route: Route?, response: Response): Request? {

        if (responseCount(response) >= 2) {
            logoutOnce()
            return null
        }

        synchronized(this) {

            val currentAccessToken = TokenHolder.getAccessToken(preferenceManager)
            val requestAccessToken = response.request.header(Constants.AUTHENTICATION)

            if (currentAccessToken != requestAccessToken && !requestAccessToken.isNullOrEmpty()) {
                return response.request.newBuilder()
                    .header(Constants.AUTHENTICATION, currentAccessToken)
                    .build()
            }

            val currentRefreshToken = TokenHolder.getRefreshToken(preferenceManager)
            if (currentRefreshToken.isNullOrEmpty()) {
                logoutOnce()
                return null
            }
            try {
                val call = userServiceProvider.get().refreshToken(refreshToken = currentRefreshToken)
                val tokenResponse = call.execute()

                if (tokenResponse.isSuccessful && tokenResponse.body() != null) {
                    val body = tokenResponse.body()!!
                    val newAccessToken = "Bearer ${body.accessToken}"

                    val newExpireTime = (System.currentTimeMillis() / 1000) + body.expiresIn
                    TokenHolder.updateTokens(preferenceManager, newAccessToken, body.refreshToken, newExpireTime)

                    return response.request.newBuilder()
                        .header(Constants.AUTHENTICATION, newAccessToken)
                        .build()

                } else {
                    logoutOnce()
                    return null
                }
            } catch (e: Exception) {
                return null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private fun logoutOnce() {
        if (!isLoggingOut) {
            isLoggingOut = true
            TokenHolder.logOut(preferenceManager)
            preferenceManager.logOut()
        }
    }
}
