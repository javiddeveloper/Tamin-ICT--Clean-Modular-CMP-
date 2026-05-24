/*
*
* @author: Javid Sattar 
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.core.network.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainServiceDto(
    @SerialName("active") val active: Boolean?,
    @SerialName("hiddenForVersions") val hiddenForVersions: List<Int?> = emptyList(),
    @SerialName("icon") val icon: String?,
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String?,
    @SerialName("newService") val newService: Boolean?,
    @SerialName("showRole") val showRole: List<Int?> = emptyList(),
    @SerialName("sorting") val sorting: Int?,
    @SerialName("subtitle") val subtitle: String?,
    @SerialName("type") val type: Int?,
)
