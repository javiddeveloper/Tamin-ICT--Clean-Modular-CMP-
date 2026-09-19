package com.tamin.taminhamrah.apiService.fractionContract

import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDTO
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDTO
import com.tamin.taminhamrah.model.fractionContract.MakeFractionContractRequestDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface FractionContractApiService {

    @GET("fraction-special-insured-services/check-age-and-history")
    suspend fun checkAgeAndHistory(): BaseDTO<FractionEligibilityDTO?>

    @POST("fraction-special-insured-services/make-a-contract")
    suspend fun makeFractionContract(
        @Body request: MakeFractionContractRequestDTO,
    ): BaseDTO<FractionContractResultDTO>
}
