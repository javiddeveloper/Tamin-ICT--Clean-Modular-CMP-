package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow

class GetJobTitleUseCase(private val repository: CommonRepository) {
    operator fun invoke(filters: List<ApiFilterDN>): Flow<JobTitleListDN?> {
        return repository.getJobTitle(ApiQueryParamDN(filters = filters))
    }
}
