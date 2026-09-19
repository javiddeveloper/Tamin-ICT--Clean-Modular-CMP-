package com.tamin.taminhamrah.useCases.employerInfo

import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import kotlinx.coroutines.flow.Flow

class RequestRealTicketUseCase(
    private val repository: EmployerInfoRepository,
) {
    operator fun invoke(mobile: String, email: String): Flow<String> =
        repository.requestRealTicket(mobile, email)
}
