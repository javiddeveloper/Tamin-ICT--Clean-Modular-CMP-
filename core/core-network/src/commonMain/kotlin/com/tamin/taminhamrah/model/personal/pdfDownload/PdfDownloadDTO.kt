package com.tamin.taminhamrah.model.personal.pdfDownload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PdfDownloadDTO(@SerialName("pdf") val pdf: InputStreamDTO? = null)
