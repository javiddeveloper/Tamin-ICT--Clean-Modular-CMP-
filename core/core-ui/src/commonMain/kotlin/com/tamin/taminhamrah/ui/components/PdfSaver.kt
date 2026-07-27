package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable

/**
 * Saves already-decoded PDF [bytes] to the device and posts a "download complete" notification.
 *
 * [TaminPdfViewer] drains the download's channel once and hands the bytes here, so this never
 * touches ktor. Platform-specific: Android writes to public Downloads + notifies; iOS writes to
 * Documents + a local notification. [rememberPdfSaver] also requests notification permission.
 */
interface PdfSaver {
    fun save(fileName: String, bytes: ByteArray)
}

/** The platform [PdfSaver], scoped to the current composition (and requests notification permission). */
@Composable
expect fun rememberPdfSaver(): PdfSaver
