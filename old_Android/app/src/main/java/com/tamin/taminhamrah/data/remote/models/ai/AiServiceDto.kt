package com.tamin.taminhamrah.data.remote.models.ai

import com.google.gson.annotations.SerializedName

data class AiServiceDto(
    @SerializedName("message") val message: String?,
    @SerializedName("sessionId") val sessionId: String?,
    @SerializedName("lastEntity") val lastEntity: String?,
    @SerializedName("results") val results: List<ServiceModelDto?>?
) {
    fun toDomain(): AiServiceModel {
        return AiServiceModel(
            message = message,
            sessionId = sessionId,
            lastEntity = lastEntity,
            results = results?.map { it?.toDomain() }
        )
    }
}

data class ServiceModelDto(
    @SerializedName("message") val message: String?,
    @SerializedName("serviceUrl") val serviceUrl: String?,
) {
    fun toDomain(): ServiceModel {
        return ServiceModel(
            message = message,
            serviceUrl = serviceUrl,
            aiServiceModelType = AiServiceModelType.fromUrl(serviceUrl),
            data = null // Data is not present in DTO currently
        )
    }
}
