package com.tamin.taminhamrah.apiService.inspection

import com.tamin.taminhamrah.model.inspection.BranchDTO
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDTO
import com.tamin.taminhamrah.model.inspection.JobDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestModelDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.model.utils.ListData
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

internal interface InspectionApiService {
    @GET("inspection-header/get-all-insurance")
    suspend fun getAllInsurance(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<InspectionPerformedDTO>>

    @GET("inspection-header/get-all-manager")
    suspend fun getAllManager(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<InspectionPerformedDTO>>

    @GET("proxy/models/branch")
    suspend fun getBranches(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<BranchDTO>>

    @GET("baseinfo/job")
    suspend fun getJobs(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<JobDTO>>

    @POST("inspection-request")
    suspend fun submitInspectionRequest(
        @Body request: SubmitInspectionRequestDTO
    ): BaseDTO<SubmitInspectionRequestModelDTO>

    @Streaming
    @GET("inspection-report/{inspectionNo}")
    suspend fun getInspectionReportPDF(
        @Path("inspectionNo") inspectionNo: String
    ): HttpStatement
}
