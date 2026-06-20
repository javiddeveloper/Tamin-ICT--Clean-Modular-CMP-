package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface PersonalApiService {

    @GET("survivor-request/personal")
    suspend fun getPersonalInfo(
    ): BaseDTO<PersonalInfoDTO?>?

    @GET("survivor-request/national-id")
    suspend fun getDeceasedInfo(
        @Query("id") nationalId: String
    ): BaseDTO<DeceasedInfoDTO>

}
