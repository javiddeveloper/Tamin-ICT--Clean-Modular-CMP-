package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.mapper.inspection.toDN
import com.tamin.taminhamrah.data.mapper.inspection.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSource
import com.tamin.taminhamrah.model.inspection.BranchListDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedListDN
import com.tamin.taminhamrah.model.inspection.JobListDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository

class InspectionRepositoryImpl(
    private val remoteDataSource: InspectionRemoteDataSource
) : InspectionRepository {

    override suspend fun getAllInsurance(
        filters: List<ApiFilterDN>
    ): InspectionPerformedListDN {
        val query = ApiQueryParamDN(filters = filters, limit = 100)
        return remoteDataSource.getAllInsurance(query).toDN()
    }

    override suspend fun getBranches(
        filters: List<ApiFilterDN>
    ): BranchListDN {
        val query = ApiQueryParamDN(filters = filters, limit = 100)
        return remoteDataSource.getBranches(query).toDN()
    }

    override suspend fun getJobs(
        filters: List<ApiFilterDN>
    ): JobListDN {
        val query = ApiQueryParamDN(filters = filters, limit = 100)
        return remoteDataSource.getJobs(query).toDN()
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
