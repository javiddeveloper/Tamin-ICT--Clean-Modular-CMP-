package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository

class SubmitInspectionUseCase(
    private val repository: InspectionRepository
) {
    suspend operator fun invoke(
        request: SubmitInspectionRequestDN
    ): SubmitInspectionRequestResultDN {
        return repository.submitInspectionRequest(request)
    }
}
