package com.tamin.taminhamrah.repository.inspection

import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeInspectionRepository : InspectionRepository {
    var insurancePageResult: List<InspectionPerformedDN> = emptyList()
    var insurancePageTotal: Int? = null
    var branchPageResult: List<BranchDN> = emptyList()
    var branchPageTotal: Int? = null
    var jobPageResult: List<JobDN> = emptyList()
    var jobPageTotal: Int? = null
    var submitResult: SubmitInspectionRequestResultDN = SubmitInspectionRequestResultDN(id = null)
    var reportPdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)

    var shouldThrowError = false
    var lastInsuranceQuery: ApiQueryParamDN? = null
    var lastBranchQuery: ApiQueryParamDN? = null
    var lastJobQuery: ApiQueryParamDN? = null
    var lastSubmitRequest: SubmitInspectionRequestDN? = null
    var lastReportPdfInspectionNo: String? = null

    override fun getInsurancePage(query: ApiQueryParamDN): Flow<PageDN<InspectionPerformedDN>> = flow {
        if (shouldThrowError) throw RuntimeException("Error")
        lastInsuranceQuery = query
        emit(PageDN(items = insurancePageResult, total = insurancePageTotal))
    }

    override fun getBranchesPage(query: ApiQueryParamDN): Flow<PageDN<BranchDN>> = flow {
        if (shouldThrowError) throw RuntimeException("Error")
        lastBranchQuery = query
        emit(PageDN(items = branchPageResult, total = branchPageTotal))
    }

    override fun getJobsPage(query: ApiQueryParamDN): Flow<PageDN<JobDN>> = flow {
        if (shouldThrowError) throw RuntimeException("Error")
        lastJobQuery = query
        emit(PageDN(items = jobPageResult, total = jobPageTotal))
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
