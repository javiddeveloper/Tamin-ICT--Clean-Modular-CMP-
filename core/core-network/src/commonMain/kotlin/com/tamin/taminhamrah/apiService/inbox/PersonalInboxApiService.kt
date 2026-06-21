package com.tamin.taminhamrah.apiService.inbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxListDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface PersonalInboxApiService {

    @GET("announcement/to-user")
    suspend fun getInboxItems(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<PersonalInboxListDTO>

    @GET("announcement/size-personal-box")
    suspend fun getInboxSize(): PersonalInboxSizeDTO
}
