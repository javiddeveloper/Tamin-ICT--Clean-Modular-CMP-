package com.tamin.taminhamrah.apiService.contract

import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.FreeJobDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.contracts.UploadImageResponseDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.client.request.forms.MultiPartFormDataContent

interface ContractsApiService {

    @GET("special-insured-services/list-contracts-mobile")
    suspend fun getContractList(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ContractDTO>>

    @GET("special-insured-services/get-registration-info")
    suspend fun getRegistrationInfo(): BaseDTO<RegistrationInfoDTO>

    @GET("special-insured-services/branches")
    suspend fun getBranches(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<BranchDTO>>

    @GET("baseinfo/spc-premium-rate")
    suspend fun getSpcPremiumRates(): BaseDTO<ListData<PremiumRateDTO>>

    @GET("special-insured-services/freelance-get-low-high-premium/{treatmentSupportCode}/{spcRateCode}/{freeJobCode}")
    suspend fun getFreelancePremiumRange(
        @Path("treatmentSupportCode") treatmentSupportCode: String,
        @Path("spcRateCode") spcRateCode: String,
        @Path("freeJobCode") freeJobCode: String,
    ): BaseDTO<FreelancePremiumRangeDTO>

    @GET("special-insured-services/freelance-check-and-calc-salary/{monthlyPremium}/{treatmentSupportCode}/{spcRateCode}")
    suspend fun calculateFreelanceSalary(
        @Path("monthlyPremium") monthlyPremium: Long,
        @Path("treatmentSupportCode") treatmentSupportCode: String,
        @Path("spcRateCode") spcRateCode: String,
    ): BaseDTO<Long>

    @GET("special-insured-services/check-and-calc-salary/{premiumRate}")
    suspend fun calculateOptionalSalary(
        @Path("premiumRate") premiumRate: String,
    ): BaseDTO<Long>

    @GET("baseinfo/free-job-wage")
    suspend fun getFreeJobWages(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<FreeJobDTO>>

    @POST("special-insured-services/freelance-make-a-contract/{monthlyPremium}")
    suspend fun makeFreelanceContract(
        @Path("monthlyPremium") monthlyPremium: Long,
        @Body request: FreelanceMakeContractRequestDTO,
    ): BaseDTO<FreelanceContractResultDTO>

    @POST("upload-image")
    suspend fun uploadImage(
        @Body content: MultiPartFormDataContent,
    ): UploadImageResponseDTO
}
