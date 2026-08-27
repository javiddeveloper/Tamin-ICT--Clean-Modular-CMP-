package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.BranchListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository

class GetBranchListUseCase(
    private val repository: InspectionRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): BranchListDN {
        return repository.getBranches(filters)
    }
}
