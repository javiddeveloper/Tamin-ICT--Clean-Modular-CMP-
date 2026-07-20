/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService.agent

import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.CancelResponseDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface AgentApiService {

    /**
     * بررسی می‌کند که آیا این کاربر مجاز به استفاده از چت‌بات هست یا نه
     */
    @GET("chat-allowed")
    suspend fun checkChatAllowed(): ChatAllowedDTO

    /**
     * ارسال پرامپت متنی برای سرویس‌های بیمه‌ای
     */
    @POST("search/service")
    suspend fun sendServicePrompt(
        @Body data: io.ktor.client.request.forms.MultiPartFormDataContent
    ): PollingResponseDTO

    /**
     * ارسال پرامپت متنی برای جستجوی قوانین
     */
    @POST("search/rule")
    suspend fun sendLawPrompt(
        @Body data: io.ktor.client.request.forms.MultiPartFormDataContent
    ): PollingResponseDTO

    /**
     * پیگیری وضعیت یک درخواست ارسال‌شده (polling)
     */
    @GET("request/track/{id}")
    suspend fun trackRequest(
        @Path("id") requestId: String
    ): PollingResponseDTO

    /**
     * لغو یک درخواست در حال پردازش
     */
    @GET("request/cancel/{id}")
    suspend fun cancelRequest(
        @Path("id") requestId: String
    ): CancelResponseDTO
}
