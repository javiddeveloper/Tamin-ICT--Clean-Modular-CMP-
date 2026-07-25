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
import androidx.compose.runtime.produceState
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
 * It drains the download's channel **once** (a [io.ktor.utils.io.ByteReadChannel] is single-use),
 * then uses the same bytes for both things the user wants on «دریافت»: it renders every page inline
 * ([PdfPagesView]) and saves the file to the device + posts a notification ([rememberPdfSaver]).
 * Feature modules just pass the [PdfDownloadPR] and never touch ktor.
 */
@Composable
fun TaminPdfViewer(
    pdf: PdfDownloadPR?,
    fileName: String,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val saver = rememberPdfSaver()
    val bytes by produceState<ByteArray?>(initialValue = null, pdf) {
        val drained = try {
            pdf?.pdf?.pdf?.let { withContext(Dispatchers.Default) { it.toByteArray() } }
        } catch (_: Throwable) {
            null
        }
        // A failed download can still answer 200 with a body that isn't a PDF (an HTML/JSON error
        // page). Rendering that crashes the renderer and saving it writes garbage, so a non-PDF
        // body is treated exactly like "no file at all": the message below, no render, no save.
        value = drained?.takeIf { it.looksLikePdf() } ?: ByteArray(0)
    }

    // As soon as the bytes are ready, save to the device — the download runs alongside rendering.
    LaunchedEffect(bytes, fileName) {
        bytes?.takeIf { it.isNotEmpty() }?.let { saver.save(fileName, it) }
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
