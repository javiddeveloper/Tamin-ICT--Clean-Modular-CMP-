package com.tamin.taminhamrah.repository.inspection

import com.tamin.taminhamrah.model.inspection.BranchListDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedListDN
import com.tamin.taminhamrah.model.inspection.JobListDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.model.request.ApiFilterDN

interface InspectionRepository {
    suspend fun getAllInsurance(
        filters: List<ApiFilterDN> = emptyList()
    ): InspectionPerformedListDN

    suspend fun getBranches(
        filters: List<ApiFilterDN> = emptyList()
    ): BranchListDN

    suspend fun getJobs(
        filters: List<ApiFilterDN> = emptyList()
    ): JobListDN

    suspend fun submitInspectionRequest(
        request: SubmitInspectionRequestDN
    ): SubmitInspectionRequestResultDN
}
