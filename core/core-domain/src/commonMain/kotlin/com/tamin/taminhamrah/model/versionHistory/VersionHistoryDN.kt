package com.tamin.taminhamrah.model.versionHistory

data class VersionHistoryDN(
    val versionName: String,
    val versionCode: Int,
    val releaseDate: String,
    val isLatest: Boolean,
    val newFeatures: List<String>,
    val debug: List<String>
)
