package com.tamin.taminhamrah.apiService.contract

import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.model.contracts.CancelContractRequestDTO
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.ContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.ContractStateDTO
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.FreeJobDTO
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDTO
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDTO
import com.tamin.taminhamrah.model.contracts.UploadImageResponseDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpStatement
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

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

    @POST("special-insured-services/make-a-contract/{selectedSalary}")
    suspend fun makeContract(
        @Path("selectedSalary") selectedSalary: Long,
        @Body request: FreelanceMakeContractRequestDTO,
    ): BaseDTO<FreelanceContractResultDTO>

    @POST("special-insured-services/freelance-make-a-contract-protector/{selectedSalary}")
    suspend fun makeFreelanceContractByGuardian(
        @Path("selectedSalary") selectedSalary: Long,
        @Body request: ContractByGuardianRequestDTO,
    ): BaseDTO<FreelanceContractResultDTO>

    @POST("special-insured-services/make-a-contract-by-protector/{selectedSalary}")
    suspend fun makeOptionalContractByGuardian(
        @Path("selectedSalary") selectedSalary: Long,
        @Body request: OptionalContractByGuardianRequestDTO,
    ): BaseDTO<FreelanceContractResultDTO>

    @GET("sep/online-payment-mobile")
    suspend fun getInsurancePayment(
        @Query("start-date") startDate: Long,
        @Query("end-date") endDate: Long,
        @Query("amount") amount: Long,
        @Query("systemType") systemType: String,
        @Query("redirectUri") redirectUri: String,
        @Query("paramPage") paramPage: String,
        @Query("month") month: Int,
        @Query("url") redirectUrl: String,
    ): BaseDTO<InsurancePaymentDTO>

    @GET("sep/online-payment-widthout-back")
    suspend fun checkInsurancePaymentStatus(
        @Query("systemType") systemType: String,
    ): BaseDTO<JsonElement>

    @POST("upload-image")
    suspend fun uploadImage(
        @Body content: MultiPartFormDataContent,
    ): UploadImageResponseDTO

    @POST("special-insured-services/save-contact")
    suspend fun saveContact(
        @Body request: SaveContactRequestDTO,
    ): BaseDTO<JsonElement>

    // ---- امور قراردادها و پرداخت (contract operations) ----

    /** علت خاتمه قرارداد — reasons list shown before cancelling a contract. */
    @GET("special-insured-services/list-self-contract-state")
    suspend fun getContractStates(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<ContractStateDTO>>

    /** غیرفعال کردن قرارداد for a بیمه اختیاری contract. Path segment is the reason code. */
    @PUT("special-insured-services/update-self-contract-state/{stateCode}")
    suspend fun cancelOptionalContract(
        @Path("stateCode") stateCode: Int,
        @Body request: CancelContractRequestDTO,
    ): BaseDTO<JsonElement>

    /** غیرفعال کردن قرارداد for a حرف و مشاغل آزاد contract. Path segment is the reason code. */
    @PUT("special-insured-services/freelance-update-self-contract-state/{stateCode}")
    suspend fun cancelFreelanceContract(
        @Path("stateCode") stateCode: Int,
        @Body request: CancelContractRequestDTO,
    ): BaseDTO<JsonElement>

    /** مشاهده پرداخت‌ها — payment history head for one contract (rows are positional arrays). */
    @GET("special-insured-services/freelance-payment-history-head-with-contractNumber/{contractNumber}")
    suspend fun getContractPaymentHistory(
        @Path("contractNumber") contractNumber: String,
    ): BaseDTO<ListData<JsonArray>>

    /** مشاهده قرارداد PDF — بیمه اختیاری. `timestamp` is a cache-busting path segment. */
    @Streaming
    @GET("special-insured-services/contract-report/{timestamp}")
    suspend fun getOptionalContractReport(
        @Path("timestamp") timestamp: Long,
    ): HttpStatement

    /** مشاهده قرارداد PDF — حرف و مشاغل آزاد. */
    @Streaming
    @GET("special-insured-services/freelance-contract-report/{timestamp}")
    suspend fun getFreelanceContractReport(
        @Path("timestamp") timestamp: Long,
    ): HttpStatement

    /** مشاهده قرارداد PDF — تکمیل سوابق کسری از ماه. */
    @Streaming
    @GET("fraction-special-insured-services/contract-report/{timestamp}")
    suspend fun getFractionContractReport(
        @Path("timestamp") timestamp: Long,
    ): HttpStatement
}
