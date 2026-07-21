package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadImageStatusForUiModel(
    var uiClickable: Boolean = true,
    var showLoading: Boolean = false,
    var status :EnumStausUploadImage =EnumStausUploadImage.SUCCESS
) : Parcelable



enum class EnumStausUploadImage{
    ERROR,SUCCESS,LOADING
}