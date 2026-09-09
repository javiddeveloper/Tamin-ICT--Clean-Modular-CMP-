package com.tamin.taminhamrah.useCases.auth

import com.tamin.taminhamrah.model.auth.DebugLoginResultDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.repository.AuthRepository

class DebugClientCredentialsLoginUseCaseImpl(
    private val authRepository: AuthRepository,
) : DebugClientCredentialsLoginUseCase {
    override suspend fun invoke(
        clientId: String,
        clientSecret: String,
        activate: Boolean,
    ): DebugLoginResultDN {
        val result = authRepository.debugClientCredentialsLogin(
            clientId = clientId,
            clientSecret = clientSecret
        )
        if (result.isSuccess && activate) {
            authRepository.switchTokenSlot(TokenSlot.BACK_TO_BACK)
        }
        return result
    }
}
