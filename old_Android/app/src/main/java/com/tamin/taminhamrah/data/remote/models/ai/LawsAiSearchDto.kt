package com.tamin.taminhamrah.data.remote.models.ai

import com.google.gson.annotations.SerializedName

data class LawsAiSearchDto(
    @SerializedName("message") val message: String?,
    @SerializedName("results") val results: List<ChatModelDto?>?
) {
    fun toDomain(): LawsAiSearchModel {
        return LawsAiSearchModel(
            message = message,
            results = results?.map { it?.toDomain() }
        )
    }
}

data class ChatModelDto(
    @SerializedName("body") val body: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("idx") val idx: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("reference") val reference: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("sourceFile") val sourceFile: String?,
) {
    fun toDomain(): ChatModel {
        return ChatModel(
            body = body,
            content = content,
            idx = idx,
            name = name,
            reference = reference,
            score = score,
            sourceFile = sourceFile
        )
    }
}
