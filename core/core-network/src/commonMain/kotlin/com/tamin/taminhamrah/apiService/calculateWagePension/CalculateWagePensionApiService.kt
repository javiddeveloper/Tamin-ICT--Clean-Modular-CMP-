package com.tamin.taminhamrah.apiService.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface CalculateWagePensionApiService {

    @GET("multiple-workshops/personal-info")
    suspend fun getPersonalInfo(): BaseDTO<MultipleWorkshopPersonalInfoDTO>

    @GET("multiple-workshops/is-multiple")
    suspend fun isMultipleWorkshops(
        @Query("branchCode") branchCode: String,
        @Query("insuranceNumber") insuranceNumber: String
    ): BaseDTO<MultipleWorkshopResultDTO>

    @GET("multiple-workshops/calc")
    suspend fun calculateMultipleWorkshops(
        @Query("branchCode") branchCode: String,
        @Query("insuranceNumber") insuranceNumber: String
    ): BaseDTO<MultipleWorkshopResultDTO>
}
