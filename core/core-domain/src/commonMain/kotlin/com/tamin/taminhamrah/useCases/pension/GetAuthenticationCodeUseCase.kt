package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetAuthenticationCodeUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(): Flow<AuthenticationTicketDN> {
        return pensionRepository.getAuthenticationCode()
    }
}
