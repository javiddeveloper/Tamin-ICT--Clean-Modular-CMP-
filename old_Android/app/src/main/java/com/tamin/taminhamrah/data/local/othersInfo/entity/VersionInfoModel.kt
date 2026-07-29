package com.tamin.taminhamrah.data.local.othersInfo.entity

data class VersionInfoModel(
    val versionName: String,
    val versionCode: Int,
    val releaseDate: String,
    val newFeatures: List<String>,
    val debug: List<String>
)

data class VersionInfoSavingModel(val list:List<VersionInfoModel> = ArrayList())

