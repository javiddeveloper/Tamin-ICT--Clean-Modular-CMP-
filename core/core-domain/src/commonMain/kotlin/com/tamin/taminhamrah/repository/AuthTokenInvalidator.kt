package com.tamin.taminhamrah.repository

/**
 * Ktor's Auth/bearer plugin caches the BearerTokens it gets from loadTokens() in memory and
 * only calls loadTokens() again lazily on the very first authenticated request per HttpClient
 * instance; afterwards it keeps reusing the cached tokens until a 401 triggers refreshTokens().
 * A fresh login never produces a 401, so without this, every HttpClient keeps sending the
 * previous user's access token after logout + login as a different user. Each HttpClient that
 * installs the Auth plugin registers a clear action here; AuthRepository invalidates them all
 * whenever the underlying token changes (logout, new login).
 */
interface AuthTokenInvalidator {
    fun registerClearAction(action: () -> Unit)
    fun invalidateAll()
}
