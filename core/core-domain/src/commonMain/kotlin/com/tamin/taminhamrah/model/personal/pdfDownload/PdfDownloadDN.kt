package com.tamin.taminhamrah.model.personal.pdfDownload

import io.ktor.utils.io.ByteReadChannel

data class PdfDownloadDN(val pdf: InputStreamDN?)

data class InputStreamDN(
    val pdf: ByteReadChannel?
)
