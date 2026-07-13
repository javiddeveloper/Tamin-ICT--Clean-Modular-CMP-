package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class AuthenticationAndGetPersonalInfoUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(authenticationsCode: Long): Flow<RetirementPersonalDN> {
        return pensionRepository.authenticationAndGetPersonalInfo(authenticationsCode)
    }
}
