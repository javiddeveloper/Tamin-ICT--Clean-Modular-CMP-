package com.tamin.taminhamrah.useCases.auth

interface HandleAuthDeepLinkUseCase {
    suspend operator fun invoke(uriString: String): Boolean
}

