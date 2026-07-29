package com.tamin.taminhamrah.data.entity

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadedImageModel(
    var guid: String? = null,
    var imageType: String? = null,
    var imageName: String? = null,
    var imageUri: Uri? = null,
    var orgUri: Uri? = null,
    var isSelected: Boolean = false,
    var desc: String? = null,
    var isBase64: Boolean = false,
    var Base64Value: String? = null
): Parcelable
