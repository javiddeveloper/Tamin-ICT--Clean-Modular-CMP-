package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.repository.common.CommonRepository

class GetJobTitleUseCase(private val repository: CommonRepository) {
    suspend operator fun invoke(query: ApiQueryParamDN): JobTitleListDN? {
        return repository.getJobTitle(query)
    }
}
