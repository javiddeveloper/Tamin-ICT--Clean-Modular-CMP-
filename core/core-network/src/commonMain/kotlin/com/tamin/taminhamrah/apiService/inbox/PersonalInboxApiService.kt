package com.tamin.taminhamrah.apiService.inbox

import com.tamin.taminhamrah.model.inbox.InboxInquiryRequestDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxListDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

interface PersonalInboxApiService {

    @GET("announcement/to-user")
    suspend fun getInboxItems(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<PersonalInboxListDTO>

    @GET("announcement/size-personal-box")
    suspend fun getInboxSize(): PersonalInboxSizeDTO

    @GET("announcement/to-user/{requestId}/pdf")
    suspend fun getMyRequestPDF(
        @Path("requestId") requestId: String
    ): BaseDTO<PersonalInboxItemDTO>

    @DELETE("announcement/to-user/{requestId}")
    suspend fun deleteMyRequest(
        @Path("requestId") requestId: String
    ): BaseDTO<Unit?>

    @PUT("announcement/to-user/{requestId}")
    suspend fun inboxInquiryLicense(
        @Path("requestId") requestId: String,
        @Body body: InboxInquiryRequestDTO
    ): BaseDTO<Unit?>
}
