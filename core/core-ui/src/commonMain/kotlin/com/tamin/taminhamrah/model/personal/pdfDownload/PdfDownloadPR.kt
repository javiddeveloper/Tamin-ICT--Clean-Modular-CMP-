package com.tamin.taminhamrah.model.personal.pdfDownload

import io.ktor.utils.io.ByteReadChannel

data class PdfDownloadPR(val pdf: InputStreamPR?)

data class InputStreamPR(
    val pdf: ByteReadChannel?
)
