package com.tamin.taminhamrah.useCases.auth

import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.util.NetworkConstants


class HandleAuthDeepLinkUseCaseImpl(
    private val deepLinkManager: DeepLinkManager,
    private val exchangeCodeForTokensUseCase: ExchangeCodeForTokensUseCase,
    private val tokenStoreManager: TokenStoreManager,
) : HandleAuthDeepLinkUseCase {
    override suspend fun invoke(uriString: String): Boolean {
        if (!deepLinkManager.isAuthLogin(uriString)) return false
        val code = deepLinkManager.extractAuthCode(uriString) ?: return false
        val success = exchangeCodeForTokensUseCase(
            code = code,
            audience = NetworkConstants.DEFAULT_AUDIENCE,
            redirectUri = NetworkConstants.REDIRECT_URI,
            clientId = NetworkConstants.CLIENT_ID,
            codeVerifier = tokenStoreManager.getCodeVerifier().orEmpty(),
        )
        return success
    }
}

