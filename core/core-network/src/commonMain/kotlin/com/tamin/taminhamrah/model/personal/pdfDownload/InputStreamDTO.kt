package com.tamin.taminhamrah.model.personal.pdfDownload

import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InputStreamDTO(
    @SerialName("pdf") val pdf: ByteReadChannel? = null
)
