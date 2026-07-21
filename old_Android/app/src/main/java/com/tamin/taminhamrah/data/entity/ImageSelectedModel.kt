package com.tamin.taminhamrah.data.entity

import okhttp3.MultipartBody

data class ImageSelectedModel(
    var selectedpicturePath: String?= null,
    var body: MultipartBody.Part? = null,
    var error: String? = null
)



