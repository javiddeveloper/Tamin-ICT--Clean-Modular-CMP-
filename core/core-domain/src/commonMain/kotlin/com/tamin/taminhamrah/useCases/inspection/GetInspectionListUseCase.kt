package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.InspectionPerformedListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository

class GetInspectionListUseCase(
    private val repository: InspectionRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): InspectionPerformedListDN {
        return repository.getAllInsurance(filters)
    }
}
