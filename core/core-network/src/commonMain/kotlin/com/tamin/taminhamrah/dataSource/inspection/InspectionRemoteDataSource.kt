package com.tamin.taminhamrah.dataSource.inspection

import com.tamin.taminhamrah.model.inspection.BranchDTO
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDTO
import com.tamin.taminhamrah.model.inspection.JobDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestModelDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface InspectionRemoteDataSource {
    suspend fun getAllInsurance(
        query: ApiQueryParamDN
    ): ListData<InspectionPerformedDTO>

    suspend fun getBranches(
        query: ApiQueryParamDN
    ): ListData<BranchDTO>

    suspend fun getJobs(
        query: ApiQueryParamDN
    ): ListData<JobDTO>

    suspend fun submitInspectionRequest(
        request: SubmitInspectionRequestDTO
    ): SubmitInspectionRequestModelDTO
}
