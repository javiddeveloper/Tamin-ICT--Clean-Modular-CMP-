/*
*
* @author: Javid Sattar 
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.data.remote.models.ai

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.utils.extentions.randomUUID

data class LawsAiSearchResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("results") val results: List<ChatModelResponse?>?
)

data class ChatModelResponse(
    @SerializedName("body") val body: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("idx") val idx: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("reference") val reference: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("source_file") val sourceFile: String?,
)

fun LawsAiSearchResponse.asDomainModel() =LawsAiSearchModel(
    message = message,
    results = results?.map { it?.asDomainModel() },
)

fun ChatModelResponse.asDomainModel() = ChatModel(
    body = body,
    content = content,
    idx = idx,
    name = name,
    reference = reference,
    score = score,
    sourceFile = sourceFile,
)


data class LawsAiSearchModel(
    override val id: String = randomUUID(),
    val message: String?,
    val results: List<ChatModel?>?
): AiChatModel()

data class ChatModel(
    override val id: String = randomUUID(),
    val body: String?,
    val content: String?,
    val idx: Int?,
    val name: String?,
    val reference: String?,
    val score: Double?,
    val sourceFile: String?,
): AiChatModel()
