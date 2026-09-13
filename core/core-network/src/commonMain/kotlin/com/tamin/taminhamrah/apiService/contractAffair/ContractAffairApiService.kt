package com.tamin.taminhamrah.apiService.contractAffair

import com.tamin.taminhamrah.model.contractAffair.CancelContractRequestDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDTO
import com.tamin.taminhamrah.model.contractAffair.ContractStateDTO
import com.tamin.taminhamrah.model.contractAffair.FreelanceLastPaymentDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

/**
 * امور قراردادها و پرداخت — the standalone Ktorfit service for the
 * `feature/contractsAndPaymentAffair` module. Bound to the same `special-insured-services`
 * Ktorfit client the create-contract `ContractsApiService` uses, but kept as its own interface so
 * the affair feature owns its full networking surface.
 */
interface ContractAffairApiService {

    /** The special-insured contract list, paginated for the امور قراردادها و پرداخت screen. */
    @GET("special-insured-services/list-contracts-mobile")
    suspend fun getContractList(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<ContractDTO>>

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

    /** محاسبهٔ حق بیمه for [month] months — حرف و مشاغل آزاد. */
    @GET("special-insured-services/freelance-calc-debit/{month}")
    suspend fun getFreelanceContractDebit(
        @Path("month") month: Int,
    ): BaseDTO<ContractDebitDTO>

    /** محاسبهٔ حق بیمه for [month] months — بیمهٔ اختیاری. */
    @GET("special-insured-services/calc-debit/{month}")
    suspend fun getOptionalContractDebit(
        @Path("month") month: Int,
    ): BaseDTO<ContractDebitDTO>

    /** آخرین پرداخت — حرف و مشاغل آزاد. */
    @GET("special-insured-services/freelance-get-last-payment")
    suspend fun getFreelanceLastPayment(): BaseDTO<FreelanceLastPaymentDTO>

    /** آخرین پرداخت — بیمهٔ اختیاری. `data` is a bare timestamp. */
    @GET("special-insured-services/get-last-payment")
    suspend fun getOptionalLastPayment(): BaseDTO<Long>

    /** جزئیات برگ پرداخت — حرف و مشاغل آزاد. Rows are positional arrays. */
    @GET("special-insured-services/freelance-payment-details")
    suspend fun getFreelancePaymentDetails(
        @Query("start-date") startDate: String,
        @Query("end-date") endDate: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): BaseDTO<ListData<JsonArray>>

    /** جزئیات برگ پرداخت — بیمهٔ اختیاری. Rows are positional arrays. */
    @GET("special-insured-services/payment-details")
    suspend fun getOptionalPaymentDetails(
        @Query("start-date") startDate: String,
        @Query("end-date") endDate: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): BaseDTO<ListData<JsonArray>>
}
