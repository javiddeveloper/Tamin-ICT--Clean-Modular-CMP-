package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_cross

/**
 * Full-screen PDF viewer, used wherever a downloaded PDF is shown.
 *
 * It decides whether the download is needed at all. A PDF saved under [fileName] by an earlier run
 * is rendered straight off the device — [onRequestDownload] is never called, nothing is written a
 * second time, and the person is told it was already downloaded. Otherwise, it asks for the file,
 * drains the download's channel **once** (a [io.ktor.utils.io.ByteReadChannel] is single-use) and
 * uses those bytes for both jobs: rendering every page inline ([PdfPagesView]) and saving to the
 * device with a notification ([rememberPdfSaver]).
 *
 * Callers own the file's name and how it is fetched; they never touch ktor or the file system.
 *
 * @param pdf the fetched download, or null until [onRequestDownload] has produced one.
 * @param downloadFailed the requested download came back with nothing, so stop waiting for it.
 */
@Composable
fun TaminPdfViewer(
    fileName: String,
    pdf: PdfDownloadPR?,
    downloadFailed: Boolean,
    onRequestDownload: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val saver = rememberPdfSaver()
    val requestDownload by rememberUpdatedState(onRequestDownload)

    // null while the bytes are still being found, empty when there is nothing renderable.
    var bytes by remember(fileName) { mutableStateOf<ByteArray?>(null) }
    // Guards against an earlier screen's PDF still sitting in state: nothing is drained until this
    // viewer is the one that asked for a download.
    var awaitingDownload by remember(fileName) { mutableStateOf(false) }

    LaunchedEffect(fileName) {
        val saved = saver.load(fileName)
        if (saved == null) {
            awaitingDownload = true
            requestDownload()
        } else {
            bytes = saved
            // Nothing to write — this only reports that the file was downloaded before.
            saver.save(fileName, saved)
        }
    }

    LaunchedEffect(pdf, downloadFailed, awaitingDownload) {
        if (!awaitingDownload) return@LaunchedEffect
        if (pdf == null) {
            // The fetch came back empty-handed; say so rather than spin forever.
            if (downloadFailed) bytes = ByteArray(0)
            return@LaunchedEffect
        }
        val drained = try {
            pdf.pdf?.pdf?.let { withContext(Dispatchers.Default) { it.toByteArray() } }
        } catch (_: Throwable) {
            null
        }
        // A failed download can still answer 200 with a body that isn't a PDF (an HTML/JSON error
        // page). Rendering that crashes the renderer and saving it writes garbage, so a non-PDF
        // body is treated exactly like "no file at all": the message below, no render, no save.
        val usable = drained?.takeIf { it.looksLikePdf() }
        bytes = usable ?: ByteArray(0)
        usable?.let { saver.save(fileName, it) }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = colors.bgPage) {
            Column(modifier = Modifier.fillMaxSize()) {
                TaminTopAppBar(
                    title = "نمایش نسخه",
                    navigationIcon = {
                        TaminTopAppBarButton(
                            icon = vectorResource(Res.drawable.ic_tamin_cross),
                            contentDescription = "بستن",
                            onClick = onDismiss,
                        )
                    },
                )
                when (val ready = bytes) {
                    null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = colors.teal)
                    }
                    else -> if (ready.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text(text = "فایل نسخه در دسترس نیست", color = colors.textSecondary)
                        }
                    } else {
                        PdfPagesView(pdfBytes = ready, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

private fun ByteArray.looksLikePdf(): Boolean {
    if (size < 4) return false
    val pdfMagic = byteArrayOf(0x25, 0x50, 0x44, 0x46) // %PDF
    val limit = minOf(size - 3, 1024)
    for (i in 0 until limit) {
        if (this[i] == pdfMagic[0] &&
            this[i + 1] == pdfMagic[1] &&
            this[i + 2] == pdfMagic[2] &&
            this[i + 3] == pdfMagic[3]
        ) {
            return true
        }
    }
    return false
}
