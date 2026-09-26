package com.tamin.taminhamrah.dataSource.inspection

import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.inspection.InspectionApiService
import com.tamin.taminhamrah.model.inspection.BranchDTO
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDTO
import com.tamin.taminhamrah.model.inspection.JobDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestModelDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData

internal class InspectionRemoteDataSourceImpl(
    private val apiService: InspectionApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : InspectionRemoteDataSource {

    override suspend fun getAllInsurance(
        query: ApiQueryParamDN
    ): ListData<InspectionPerformedDTO> {
        val queries = queryBuilder.buildQuery(query)
        return errorParser.safeCall("getAllInsurance") {
            val response = apiService.getAllInsurance(queries)
            response.extractData()
        }
    }

    override suspend fun getAllManager(
        query: ApiQueryParamDN
    ): ListData<InspectionPerformedDTO> {
        val queries = queryBuilder.buildQuery(query)
        return errorParser.safeCall("getAllManager") {
            val response = apiService.getAllManager(queries)
            response.extractData()
        }
    }

    override suspend fun getBranches(
        query: ApiQueryParamDN
    ): ListData<BranchDTO> {
        val queries = queryBuilder.buildQuery(query)
        return errorParser.safeCall("getBranches") {
            val response = apiService.getBranches(queries)
            response.extractData()
        }
    }

    override suspend fun getJobs(
        query: ApiQueryParamDN
    ): ListData<JobDTO> {
        val queries = queryBuilder.buildQuery(query)
        return errorParser.safeCall("getJobs") {
            val response = apiService.getJobs(queries)
            response.extractData()
        }
    }

    override suspend fun submitInspectionRequest(
        request: SubmitInspectionRequestDTO
    ): SubmitInspectionRequestModelDTO {
        return errorParser.safeCall("submitInspectionRequest") {
            val response = apiService.submitInspectionRequest(request)
            response.extractData()
        }
    }

    override suspend fun getInspectionReportPDF(inspectionNo: String): PdfDownloadDTO {
        return errorParser.safeCall("getInspectionReportPDF") {
            val response = apiService.getInspectionReportPDF(inspectionNo)
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = response.body()
                )
            )
        }
    }
}
