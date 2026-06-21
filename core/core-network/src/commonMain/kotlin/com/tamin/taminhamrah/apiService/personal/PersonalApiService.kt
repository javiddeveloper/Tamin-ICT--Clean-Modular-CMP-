package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Query

interface PersonalApiService {

    @GET("survivor-request/personal")
    suspend fun getPersonalInfo(
    ): BaseDTO<PersonalInfoDTO?>?


    @GET("survivor-request/age")
    suspend fun getAge(
        @Query("birthDate") birthDate: Long = 0L
    ): BaseDTO<AgeDTO>

    @GET("disability-request/subdominant")
    suspend fun getDisabilityDependentInfo(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<DisabilityDependentDTO>>

}
