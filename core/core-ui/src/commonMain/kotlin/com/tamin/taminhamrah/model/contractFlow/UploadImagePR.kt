package com.tamin.taminhamrah.model.contractFlow

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UploadImagePR(
    val imageId: String,
    val fileName: String,
    val description: String,
)
