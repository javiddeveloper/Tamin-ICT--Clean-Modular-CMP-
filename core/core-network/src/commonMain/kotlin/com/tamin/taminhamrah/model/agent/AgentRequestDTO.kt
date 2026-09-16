package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

/**
 * The `data` part of an assistant prompt (search/service and search/rule), field for field what
 * the native app sent. The server expects every key to be present, nulls included, so the data
 * source encodes it with defaults and explicit nulls on.
 *
 * @param sessionId the local conversation id
 * @param lastEntity `""` when the conversation has none yet
 * @param state the server's own state from its previous answer, returned untouched
 * @param history the server's own history from its previous answer, returned untouched
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AgentRequestDTO(
    @SerialName("prompt") val prompt: String,
    @SerialName("sessionId") val sessionId: String?,
    @SerialName("lastEntity") val lastEntity: String,
    @SerialName("chatToken") val chatToken: String?,
    @SerialName("userType") val userType: String?,
    @SerialName("personal_info") val personalInfo: AgentPersonalInfoDTO?,
    @SerialName("prompt_type") val promptType: AgentPromptTypeDTO,
    @SerialName("state") val state: JsonElement?,
    @SerialName("history") val history: JsonElement = JsonArray(emptyList()),
    @EncodeDefault @SerialName("device_type") val deviceType: String = DEVICE_TYPE_MOBILE,
    @EncodeDefault @SerialName("response_type") val responseType: String = RESPONSE_TYPE_SHOW_TO_USER,
) {
    companion object {
        const val DEVICE_TYPE_MOBILE = "MOBILE"
        const val RESPONSE_TYPE_SHOW_TO_USER = "show_to_user"
    }
}

@Serializable
data class AgentPersonalInfoDTO(
    @SerialName("national_id") val nationalId: String,
    @SerialName("pensioner_id") val pensionerId: String?,
    @SerialName("first_name") val firstName: String?,
    @SerialName("last_name") val lastName: String?,
)

@Serializable
enum class AgentPromptTypeDTO {
    @SerialName("text") TEXT,
    @SerialName("voice") VOICE,
}
