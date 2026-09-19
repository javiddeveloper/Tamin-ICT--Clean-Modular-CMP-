package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class SaveDisabilityUserInfoUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(body: DisabilitySaveInfoDN): Flow<DisabilityRequestRefDN?> {
        return pensionRepository.saveDisabilityUserInfo(body)
    }
}
