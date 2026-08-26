package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetWageCertificateReportUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN>
    ): Flow<String> {
        return userRepository.getWageCertificateReport(filters)
    }
}
