package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class SendPayRollToInboxUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> {
        return pensionRepository.sendPayRollToInbox(filters)
    }
}
