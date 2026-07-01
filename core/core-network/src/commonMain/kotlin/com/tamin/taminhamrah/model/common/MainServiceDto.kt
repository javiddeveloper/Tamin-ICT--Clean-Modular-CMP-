package com.tamin.taminhamrah.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainServiceDto(
    @SerialName("active") val active: Boolean? = null,
    @SerialName("hiddenForVersions") val hiddenForVersions: List<Int?> = emptyList(),
    @SerialName("icon") val icon: String? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("newService") val newService: Boolean? = null,
    @SerialName("showRole") val showRole: List<Int?> = emptyList(),
    @SerialName("sorting") val sorting: Int? = null,
    @SerialName("subtitle") val subtitle: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("status") val status: MenuServiceStatus? = MenuServiceStatus.ACTIVE,
    @SerialName("message") val message: String? = null
)

@Serializable
enum class MenuServiceStatus {
    @SerialName("active") ACTIVE,
    @SerialName("temp_disabled") TEMPORARY_DISABLED,
    @SerialName("disabled") DISABLED,
    @SerialName("completely_disabled") COMPLETELY_DISABLED,
    @SerialName("enabled_with_error") ENABLED_WITH_ERROR,
    @SerialName("web_view") WEB_VIEW
}
