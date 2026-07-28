package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable

/**
 * The device's copy of a downloaded PDF: what has been kept, and where new ones go.
 *
 * Platform-specific: Android uses public Downloads (MediaStore) with a notification; iOS uses the
 * Downloads/Documents directory with a local notification. [rememberPdfSaver] also requests
 * notification permission. Neither side touches ktor — [TaminPdfViewer] drains the download's
 * channel once and hands the bytes over.
 */
interface PdfSaver {

    /**
     * The PDF kept under [fileName] from an earlier download, or null if there is none.
     *
     * A hit means the viewer can render straight from the device and the caller never has to ask
     * the network for it again.
     */
    suspend fun load(fileName: String): ByteArray?

    /**
     * Makes sure [fileName] is on the device and tells the person so.
     *
     * Already there: nothing is written — no duplicate lands in Downloads — and the notification
     * says it was downloaded before. Otherwise [bytes] are written and it reports a finished
     * download.
     */
    suspend fun save(fileName: String, bytes: ByteArray)
}

/** The platform [PdfSaver], scoped to the current composition (and requests notification permission). */
@Composable
expect fun rememberPdfSaver(): PdfSaver

/** Notification body, shared by both platforms so the two apps say the same thing. */
internal const val DOWNLOAD_DONE_MESSAGE = "دانلود انجام شد"
internal const val ALREADY_DOWNLOADED_MESSAGE = "قبلاً دانلود شده است"
