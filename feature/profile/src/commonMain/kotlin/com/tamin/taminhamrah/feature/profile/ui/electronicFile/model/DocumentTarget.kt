package com.tamin.taminhamrah.feature.profile.ui.electronicFile.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.ui.documentExtension
import com.tamin.taminhamrah.ui.forFullDocument

/** What opens when a document card is tapped. */
@Immutable
sealed interface DocumentTarget {
    /** A scanned document the server renders as PDF; the bytes are downloaded before display. */
    data class Pdf(val url: String, val fileName: String) : DocumentTarget

    /** A plain image, shown straight from its URL. */
    data class Image(val url: String, val title: String) : DocumentTarget
}

/** Thumbnail URLs carry this segment; the full document lives under a sibling of it. */
private const val FULL_PDF_SEGMENT = "full-pdf"
private const val FULL_IMAGE_SEGMENT = "full"

/** Extensions the server renders as PDF. `tiff` is here because the old `.tif` check matched it. */
private val PDF_EXTENSIONS = setOf("pdf", "tif", "tiff")

/**
 * Where this document's full version lives, or null when there is no thumbnail to derive it from.
 *
 * The server decides the shape: a thumbnail whose own extension is `.pdf`, `.tif` or `.tiff` is
 * served as a PDF under `full-pdf`; everything else is an image under `full`. Both swap the
 * `thumbs` segment, leaving host, path and query as the server built them.
 *
 * Image is the default rather than PDF, matching the previous app: it is what an unrecognized
 * extension was treated as there, and sending an image to the PDF downloader fails to open.
 */
fun ElectronicFilePR.documentTarget(): DocumentTarget? {
    if (thumb.isBlank()) return null

    return if (thumb.documentExtension() in PDF_EXTENSIONS) {
        DocumentTarget.Pdf(url = thumb.forFullDocument(FULL_PDF_SEGMENT), fileName = name)
    } else {
        DocumentTarget.Image(url = thumb.forFullDocument(FULL_IMAGE_SEGMENT), title = name)
    }
}
