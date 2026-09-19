package com.tamin.taminhamrah.useCases.auth

import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.util.NetworkConstants

class GetSignOutUrlUseCase(
    private val developerOptionsRepository: DeveloperOptionsRepository
) {
    operator fun invoke(): String {
        val baseUrl = developerOptionsRepository.getEffectiveBaseUrl(BaseUrlKey.ACCOUNT)
        return "${baseUrl}signout?" +
                "redirect_uri=mytamin://logout" +
                "&response_type=assertion" +
                "&client_id=${NetworkConstants.CLIENT_ID}"
    }
}
