package com.tamin.taminhamrah.useCases.auth

import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.util.deriveCodeChallenge
import com.tamin.taminhamrah.util.deriveCodeVerifierChallenge
import com.tamin.taminhamrah.util.generateCodeVerifier
import com.tamin.taminhamrah.util.getCodeVerifierChallengeMethod

class AuthAuthorizeUrlUseCaseImpl(
    private val tokenStoreManager: TokenStoreManager,
    private val developerOptionsRepository: DeveloperOptionsRepository,
) : AuthAuthorizeUrlUseCase {
    override fun invoke(): String {
        val codeVerifier = generateCodeVerifier()
        tokenStoreManager.saveCodeVerifier(codeVerifier)
        val base = developerOptionsRepository.getEffectiveBaseUrl(BaseUrlKey.ACCOUNT)
        val clientId = NetworkConstants.CLIENT_ID
        val challenge = deriveCodeVerifierChallenge(codeVerifier) ?: deriveCodeChallenge(codeVerifier)
        val method = getCodeVerifierChallengeMethod()
        return "${base}server/authorize?" +
            "redirect_uri=mytamin://login" +
            "&response_type=code" +
            "&client_id=$clientId" +
            "&code_challenge=$challenge" +
            "&code_challenge_method=$method"
    }
}
