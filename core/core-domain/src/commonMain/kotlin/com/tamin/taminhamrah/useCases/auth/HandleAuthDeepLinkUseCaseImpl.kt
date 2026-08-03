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

        tokenStoreManager.setAuthProcessing(true)

        val success = try {
            exchangeCodeForTokensUseCase(
                code = code,
                audience = NetworkConstants.DEFAULT_AUDIENCE,
                redirectUri = NetworkConstants.REDIRECT_URI,
                clientId = NetworkConstants.CLIENT_ID,
                codeVerifier = tokenStoreManager.getCodeVerifier().orEmpty(),
            )
        } finally {
            tokenStoreManager.setAuthProcessing(false)
        }
        return success
    }
}

