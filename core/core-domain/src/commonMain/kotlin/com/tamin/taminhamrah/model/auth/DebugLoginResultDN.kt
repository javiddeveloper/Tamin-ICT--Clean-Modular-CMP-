package com.tamin.taminhamrah.model.auth

/**
 * Result of [com.tamin.taminhamrah.repository.AuthRepository.debugClientCredentialsLogin] — a
 * domain-layer stand-in for the network layer's `TokenResponseDto` (core-domain can't depend on
 * core-network), carrying enough of the raw response for the debug login screen's status text.
 */
data class DebugLoginResultDN(
    val isSuccess: Boolean,
    val accessToken: String?,
    val tokenType: String?,
    val expiresIn: Long?,
    val error: String?,
    val errorDescription: String?,
)
