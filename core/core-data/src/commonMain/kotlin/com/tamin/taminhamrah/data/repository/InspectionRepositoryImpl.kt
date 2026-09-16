package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.mapper.inspection.toDN
import com.tamin.taminhamrah.data.mapper.inspection.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSource
import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InspectionRepositoryImpl(
    private val remoteDataSource: InspectionRemoteDataSource
) : InspectionRepository {

    // Network-only pagination — inspection is not a Room-cached domain (no entity/DAO),
    // so unlike PersonalInboxRepositoryImpl there is no page-0 cache write here.

    override fun getInsurancePage(
        query: ApiQueryParamDN
    ): Flow<PageDN<InspectionPerformedDN>> = flow {
        val data = remoteDataSource.getAllInsurance(query)
        emit(PageDN(items = data.list.orEmpty().map { it.toDN() }, total = data.total))
    }

    override fun getWorkshopInspectionsPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<InspectionPerformedDN>> = flow {
        val data = remoteDataSource.getAllManager(query)
        emit(PageDN(items = data.list.orEmpty().map { it.toDN() }, total = data.total))
    }

    override fun getBranchesPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<BranchDN>> = flow {
        val data = remoteDataSource.getBranches(query)
        emit(PageDN(items = data.list.orEmpty().map { it.toDN() }, total = data.total))
    }

    override fun getJobsPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<JobDN>> = flow {
        val data = remoteDataSource.getJobs(query)
        emit(PageDN(items = data.list.orEmpty().map { it.toDN() }, total = data.total))
    }

    override suspend fun submitInspectionRequest(
        request: SubmitInspectionRequestDN
    ): SubmitInspectionRequestResultDN {
        val result = remoteDataSource.submitInspectionRequest(request.toDTO())
        return SubmitInspectionRequestResultDN(
            id = result.request?.id
        )
    }

    override suspend fun getInspectionReportPDF(inspectionNo: String): PdfDownloadDN {
        return remoteDataSource.getInspectionReportPDF(inspectionNo).toDomain()
    }
}
