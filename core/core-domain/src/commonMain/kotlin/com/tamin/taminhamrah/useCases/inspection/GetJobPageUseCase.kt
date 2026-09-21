package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository
import kotlinx.coroutines.flow.Flow

class GetJobPageUseCase(
    private val repository: InspectionRepository
) {
    operator fun invoke(
        query: ApiQueryParamDN
    ): Flow<PageDN<JobDN>> = repository.getJobsPage(query)
}
