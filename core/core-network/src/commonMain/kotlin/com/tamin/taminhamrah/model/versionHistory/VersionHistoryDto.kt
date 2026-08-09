package com.tamin.taminhamrah.model.versionHistory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionHistoryListDto(
    @SerialName("list") val list: List<VersionHistoryDto> = emptyList()
)

@Serializable
data class VersionHistoryDto(
    @SerialName("versionName") val versionName: String,
    @SerialName("versionCode") val versionCode: Int,
    @SerialName("releaseDate") val releaseDate: String,
    @SerialName("newFeatures") val newFeatures: List<String> = emptyList(),
    @SerialName("debug") val debug: List<String> = emptyList()
)
