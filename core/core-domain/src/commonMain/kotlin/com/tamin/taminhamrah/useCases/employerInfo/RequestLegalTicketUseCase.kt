package com.tamin.taminhamrah.useCases.employerInfo

import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import kotlinx.coroutines.flow.Flow

class RequestLegalTicketUseCase(
    private val repository: EmployerInfoRepository,
) {
    operator fun invoke(mobile: String, email: String, ceoNationalCode: String): Flow<String> =
        repository.requestLegalTicket(mobile, email, ceoNationalCode)
}
