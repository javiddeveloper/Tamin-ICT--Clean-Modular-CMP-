package com.tamin.taminhamrah.data.entity

import android.net.Uri

data class ImageModel(
    val image: String? = null,
    val title: String? = null,
    val resImg:Int = 0,
    val isSelected:Boolean = false,
    val uri: Uri?=null
)