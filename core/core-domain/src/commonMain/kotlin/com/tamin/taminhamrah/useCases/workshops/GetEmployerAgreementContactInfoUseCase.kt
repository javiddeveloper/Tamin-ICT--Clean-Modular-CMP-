package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.EmployerContactInfoDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** Step 2 — verify the OTP and read back the employer's identity block. */
class GetEmployerAgreementContactInfoUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(verificationCode: String): EmployerContactInfoDN =
        repository.getEmployerAgreementContactInfo(verificationCode)
}
