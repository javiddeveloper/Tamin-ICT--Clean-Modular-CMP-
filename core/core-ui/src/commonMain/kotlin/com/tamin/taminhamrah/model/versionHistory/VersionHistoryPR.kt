package com.tamin.taminhamrah.model.versionHistory

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class VersionHistoryPR(
    val versionName: String,
    val versionCode: Int,
    val releaseDate: String,
    val isLatest: Boolean,
    val newFeatures: List<String>,
    val debug: List<String>,
    val isExpanded: Boolean = false
)
