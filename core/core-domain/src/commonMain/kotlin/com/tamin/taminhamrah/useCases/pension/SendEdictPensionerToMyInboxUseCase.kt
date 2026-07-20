package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class SendEdictPensionerToMyInboxUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> {
        return pensionRepository.sendEdictPensionerToMyInbox(filters)
    }
}
