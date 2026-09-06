package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmissionDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** Step 3 — ثبت نهایی تعهد once the employer accepts the rules. */
class SubmitEmployerAgreementUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(request: EmployerAgreementSubmissionDN): String =
        repository.submitEmployerAgreement(request)
}
