package com.tamin.taminhamrah.useCases.auth

interface ExchangeCodeForTokensUseCase {
    suspend operator fun invoke(
        code: String,
        codeVerifier: String = "",
        audience: String,
        redirectUri: String,
        clientId: String,
    ): Boolean
}
