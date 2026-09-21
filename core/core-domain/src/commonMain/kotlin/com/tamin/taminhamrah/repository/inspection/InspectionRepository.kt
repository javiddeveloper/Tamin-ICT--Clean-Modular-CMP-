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

interface InspectionRepository {
    fun getInsurancePage(
        query: ApiQueryParamDN
    ): Flow<PageDN<InspectionPerformedDN>>

    fun getWorkshopInspectionsPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<InspectionPerformedDN>>

    fun getBranchesPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<BranchDN>>

    fun getJobsPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<JobDN>>

    suspend fun submitInspectionRequest(
        request: SubmitInspectionRequestDN
    ): SubmitInspectionRequestResultDN

    suspend fun getInspectionReportPDF(
        inspectionNo: String
    ): PdfDownloadDN
}
