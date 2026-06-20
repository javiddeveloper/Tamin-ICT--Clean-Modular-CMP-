package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET

interface PersonalApiService {

    @GET("survivor-request/personal")
    suspend fun getPersonalInfo(
    ): BaseDTO<PersonalInfoDTO?>?

}
