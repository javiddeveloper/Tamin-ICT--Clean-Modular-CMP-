package com.tamin.taminhamrah.useCases.auth


import com.tamin.taminhamrah.repository.AuthRepository

class ExchangeCodeForTokensUseCaseImpl(
    private val authRepository: AuthRepository,
) : ExchangeCodeForTokensUseCase {
    override suspend fun invoke(
        code: String,
        codeVerifier: String,
        audience: String,
        redirectUri: String,
        clientId: String,
    ): Boolean {
        return authRepository.exchangeCodeForTokens(
            code = code,
            codeVerifier = codeVerifier,
            audience = audience,
            redirectUri = redirectUri,
            clientId = clientId,
        )
    }
}
