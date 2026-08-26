package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.JobListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository

class GetJobListUseCase(
    private val repository: InspectionRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): JobListDN {
        return repository.getJobs(filters)
    }
}
