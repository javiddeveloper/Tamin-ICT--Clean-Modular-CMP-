package com.tamin.taminhamrah.feature.taminServices.inspection

import com.tamin.taminhamrah.model.inspection.BranchListDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedListDN
import com.tamin.taminhamrah.model.inspection.JobListDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository

class FakeInspectionRepository : InspectionRepository {
    var allInsuranceResult: InspectionPerformedListDN = InspectionPerformedListDN(total = 0, list = emptyList())
    var branchesResult: BranchListDN = BranchListDN(total = 0, list = emptyList())
    var jobsResult: JobListDN = JobListDN(total = 0, list = emptyList())
    var submitResult: SubmitInspectionRequestResultDN = SubmitInspectionRequestResultDN(id = null)
    var reportPdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)

    var shouldThrowError = false
    var lastAllInsuranceFilters: List<ApiFilterDN>? = null
    var lastBranchesFilters: List<ApiFilterDN>? = null
    var lastJobsFilters: List<ApiFilterDN>? = null
    var lastSubmitRequest: SubmitInspectionRequestDN? = null
    var lastReportPdfInspectionNo: String? = null

    override suspend fun getAllInsurance(filters: List<ApiFilterDN>): InspectionPerformedListDN {
        if (shouldThrowError) throw RuntimeException("Error")
        lastAllInsuranceFilters = filters
        return allInsuranceResult
    }

    override suspend fun getBranches(filters: List<ApiFilterDN>): BranchListDN {
        if (shouldThrowError) throw RuntimeException("Error")
        lastBranchesFilters = filters
        return branchesResult
    }

    override suspend fun getJobs(filters: List<ApiFilterDN>): JobListDN {
        if (shouldThrowError) throw RuntimeException("Error")
        lastJobsFilters = filters
        return jobsResult
    }

    override suspend fun submitInspectionRequest(request: SubmitInspectionRequestDN): SubmitInspectionRequestResultDN {
        if (shouldThrowError) throw RuntimeException("Error")
        lastSubmitRequest = request
        return submitResult
    }

    override suspend fun getInspectionReportPDF(inspectionNo: String): PdfDownloadDN {
        if (shouldThrowError) throw RuntimeException("Error")
        lastReportPdfInspectionNo = inspectionNo
        return reportPdfResult
    }
}
