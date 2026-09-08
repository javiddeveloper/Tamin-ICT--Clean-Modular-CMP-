package com.tamin.taminhamrah.model.contractFlow

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class GuardianFormPR(
    val nationalId: String = "",
    val letterNumber: String = "",
    val fullName: String = "",
    val letterDateFormatted: String = "",
    val letterDateEpoch: Long? = null,
    val documentGuid: String? = null,
    val documentName: String? = null,
    val documentPreviewBytes: ByteArray? = null,
    val isUploadingDocument: Boolean = false,
    val uploadError: String? = null,
) {
    val isValid: Boolean
        get() = nationalId.length == 10 &&
            letterNumber.isNotBlank() &&
            fullName.isNotBlank() &&
            letterDateFormatted.isNotBlank() &&
            (documentGuid != null || documentPreviewBytes != null)
}
