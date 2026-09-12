package com.tamin.taminhamrah.apiService.weddingPresent

import com.tamin.taminhamrah.model.weddingPresent.ShortTermMarriageRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import kotlinx.serialization.json.JsonElement

interface WeddingPresentApiService {

    @GET("shortterm-request/getNoPresenceLoadData")
    suspend fun getWeddingPresentInfo(): BaseDTO<WeddingPresentInfoDTO>

    @POST("stp-no-presence/saveShorttremMariage")
    suspend fun submitWeddingPresent(
        @Body request: ShortTermMarriageRequestDTO,
    ): BaseDTO<JsonElement?>
}
