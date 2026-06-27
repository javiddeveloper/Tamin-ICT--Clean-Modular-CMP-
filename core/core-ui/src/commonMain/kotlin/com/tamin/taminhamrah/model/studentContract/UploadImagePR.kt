package com.tamin.taminhamrah.model.studentContract

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UploadImagePR(
    val imageId: String,
    val fileName: String,
    val description: String,
)
