package com.tamin.taminhamrah.apiService.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceRequestDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.serialization.json.JsonElement

/**
 * "کمک هزینه مراسم ترحیم" (funeral-ceremony allowance) endpoints.
 * Auth header is added by the Ktor Auth plugin (main Ktorfit instance).
 */
internal interface FuneralAllowanceApiService {

    /** Insured + last-branch info, plus any previously registered request with a bank-account issue. */
    @GET("funeral-no-presence/getFuneralNoPresenceLoadData")
    suspend fun getFuneralAllowanceInfo(): BaseDTO<FuneralAllowanceInfoDTO>

    /**
     * Eligibility check for the deceased. `data` is a bare positional string array (native
     * `DeceasedInfoResponse`); the raw list is decoded into named fields by
     * [com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDTO.fromPositional] in the
     * remote data source — see that type's KDoc for the slot map. Callers never index it directly.
     */
    @GET("shortterm/validateFuneral/{nationalCode}")
    suspend fun validateDeceased(
        @Path("nationalCode") nationalCode: String,
    ): BaseDTO<List<String?>>

    /** Registers the funeral-allowance request. `data` is a success message string. */
    @POST("funeral-no-presence/saveShorttremFuneral")
    suspend fun submitFuneralAllowanceRequest(
        @Body request: FuneralAllowanceRequestDTO,
    ): BaseDTO<JsonElement?>

    /**
     * Re-submits a request that the branch rejected because of a bank-account problem, once the
     * insured has corrected the account. `data` is a success message string.
     */
    @POST("funeral-no-presence/confirmShorttremFuneral/{requestId}")
    suspend fun confirmAccountCorrection(
        @Path("requestId") requestId: String,
    ): BaseDTO<JsonElement?>
}
