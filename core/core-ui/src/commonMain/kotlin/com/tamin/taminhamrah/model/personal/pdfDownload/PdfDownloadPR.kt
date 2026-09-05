package com.tamin.taminhamrah.model.personal.pdfDownload

import io.ktor.utils.io.ByteReadChannel

data class PdfDownloadPR(val pdf: InputStreamPR?)

data class InputStreamPR(
    val pdf: ByteReadChannel?
)

/**
 * Bytes already in hand, as the viewer's own model.
 *
 * For documents the service answers with a whole body rather than a stream — a static form, for
 * instance. Lives here because [PdfDownloadPR] is what knows about ktor; a feature module does
 * not have it on the classpath, and should not need it to show a PDF.
 */
fun ByteArray.asPdfDownload(): PdfDownloadPR = PdfDownloadPR(InputStreamPR(ByteReadChannel(this)))
