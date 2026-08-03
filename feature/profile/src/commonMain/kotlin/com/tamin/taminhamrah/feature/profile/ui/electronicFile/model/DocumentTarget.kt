package com.tamin.taminhamrah.feature.profile.ui.electronicFile.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR

/** What opens when a document card is tapped. */
@Immutable
sealed interface DocumentTarget {
    /** A scanned document the server renders as PDF; the bytes are downloaded before display. */
    data class Pdf(val url: String, val fileName: String) : DocumentTarget

    /** A plain image, shown straight from its URL. */
    data class Image(val url: String, val title: String) : DocumentTarget
}

/** Thumbnail URLs carry this segment; the full document lives under a sibling of it. */
private const val THUMBS_SEGMENT = "thumbs"
private const val FULL_PDF_SEGMENT = "full-pdf"
private const val FULL_IMAGE_SEGMENT = "full"

/** Extensions the server renders as PDF. `tiff` is here because the old `.tif` check matched it. */
private val PDF_EXTENSIONS = setOf("pdf", "tif", "tiff")

/**
 * The extension of the file this URL points at, lowercased, or empty when it has none.
 *
 * Query and fragment are dropped first: a `.pdf` mentioned in a query parameter says nothing about
 * the document itself, and treating it as if it did sends an image to the PDF viewer.
 */
private fun String.fileExtension(): String =
    substringBefore('?')
        .substringBefore('#')
        .substringAfterLast('.', missingDelimiterValue = "")
        .lowercase()

/**
 * Where this document's full version lives, or null when there is no thumbnail to derive it from.
 *
 * The server decides the shape: a thumbnail ending `.pdf` or `.tif` is served as a PDF under
 * `full-pdf`, anything else as an image under `full`. Both are the thumbnail URL with its
 * `thumbs` segment swapped, so the host, path and query are the server's, untouched.
 */
fun ElectronicFilePR.documentTarget(): DocumentTarget? {
    if (thumb.isBlank()) return null

    val servedAsPdf = thumb.fileExtension() in PDF_EXTENSIONS

    return if (servedAsPdf) {
        DocumentTarget.Pdf(url = thumb.replace(THUMBS_SEGMENT, FULL_PDF_SEGMENT), fileName = name)
    } else {
        DocumentTarget.Image(url = thumb.replace(THUMBS_SEGMENT, FULL_IMAGE_SEGMENT), title = name)
    }
}
