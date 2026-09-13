package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.ui.looksLikePdf
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

/**
 * Drains this download's channel into bytes usable bytes, or null if there was nothing to drain or
 * what came back was not a PDF (a failed download can still answer 200 with an HTML/JSON error
 * body).
 *
 * A [io.ktor.utils.io.ByteReadChannel] is single-use — call this once per [PdfDownloadPR]. Exists so
 * a feature module showing a download-only screen (no inline [PdfPagesView]) can reuse the same
 * drain-and-validate step [TaminPdfViewer] does internally, without needing ktor on its own
 * classpath.
 */
suspend fun PdfDownloadPR.drainBytesOrNull(): ByteArray? = try {
    pdf?.pdf?.let { withContext(Dispatchers.Default) { it.toByteArray() } }?.takeIf { it.looksLikePdf() }
} catch (_: Throwable) {
    null
}
