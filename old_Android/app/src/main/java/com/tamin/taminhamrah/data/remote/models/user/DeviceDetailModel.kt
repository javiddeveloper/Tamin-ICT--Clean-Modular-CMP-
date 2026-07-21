package com.tamin.taminhamrah.data.remote.models.user

data class DeviceDetailModel(
    val uuid: String,
    val serial: String,
    val model: String,
    val manufacture: String,
    val lastLoginDate: String,
    val lastLoginTimeStamp: Long,
    val lastUserType: String,
    val sdk: Int,
    val versionName: String,
    val versionCode: Int,
    val flavor: String,
    val fcmToken: String,
    val lastLoginId: String
)