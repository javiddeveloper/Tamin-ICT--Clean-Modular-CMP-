package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow

class GetJobTitlePageUseCase(
    private val repository: CommonRepository,
) {
    operator fun invoke(
        query: ApiQueryParamDN,
    ): Flow<PageDN<JobTitleDN>> = repository.getJobTitlePage(query)
}
