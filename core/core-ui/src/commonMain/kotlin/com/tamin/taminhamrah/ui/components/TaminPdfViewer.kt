package com.tamin.taminhamrah.ui.components

/**
 * Full-screen PDF viewer, used wherever a downloaded PDF is shown.
 *
 * It decides whether the download is needed at all. A PDF saved under [fileName] by an earlier run
 * is rendered straight off the device — [onRequestDownload] is never called, nothing is written a
 * second time, and the person is told it was already downloaded. Otherwise, it asks for the file,
 * drains the download's channel **once** (a [io.ktor.utils.io.ByteReadChannel] is single-use) and
 * uses those bytes for both jobs: rendering every page inline ([PdfPagesView]) and saving to the
 * device with a notification and an in-app toast ([rememberPdfSaver]).
 *
 * Callers own the file's name and how it is fetched; they never touch ktor or the file system.
 *
 * @param pdf the fetched download, or null until [onRequestDownload] has produced one.
 * @param downloadFailed the requested download came back with nothing, so stop waiting for it.
 */

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.looksLikePdf
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.document_viewer_close
import taminx.core.core_ui.document_viewer_file_unavailable
import taminx.core.core_ui.document_viewer_title
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_warning

@Composable
fun TaminPdfViewer(
    fileName: String,
    pdf: PdfDownloadPR?,
    downloadFailed: Boolean,
    onRequestDownload: () -> Unit,
    onDismiss: () -> Unit,
    title: String = stringResource(Res.string.document_viewer_title),
    background: Brush = taminTopAppBarGradient(),
    emptyMessage: String = stringResource(Res.string.document_viewer_file_unavailable),
    /**
     * Draws the empty state as the app's icon-tile block instead of a centred line of text. False —
     * the default — keeps the text every existing caller was built against.
     */
    showEmptyStateTile: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val saver = rememberPdfSaver()
    val requestDownload by rememberUpdatedState(onRequestDownload)

    // null while the bytes are still being found, empty when there is nothing renderable.
    var bytes by remember(fileName) { mutableStateOf<ByteArray?>(null) }
    // Guards against an earlier screen's PDF still sitting in state: nothing is drained until this
    // viewer is the one that asked for a download.
    var awaitingDownload by remember(fileName) { mutableStateOf(false) }
    // What the saver told the person, repeated as a toast at the top of the viewer.
    var savedNotice by remember(fileName) { mutableStateOf<String?>(null) }

    LaunchedEffect(fileName, pdf) {
        val saved = saver.load(fileName)
        if (saved == null) {
            awaitingDownload = true
            if (pdf == null) {
                requestDownload()
            }
        } else {
            bytes = saved
            // Nothing to write — this only reports that the file was downloaded before.
            savedNotice = saver.save(fileName, saved)
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
        usable?.let { savedNotice = saver.save(fileName, it) }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        // Its own host: the dialog is a separate window, so the app's toaster would draw behind it.
        Box(modifier = Modifier.fillMaxSize()) {
            AppToastHost {
                val toaster = LocalToaster.current
                LaunchedEffect(savedNotice) { savedNotice?.let { toaster.success(it) } }
                Surface(modifier = Modifier.fillMaxSize(), color = colors.bgPage) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TaminTopAppBar(
                            title = title,
                            background = background,
                            navigationIcon = {
                                TaminTopAppBarButton(
                                    icon = vectorResource(Res.drawable.ic_tamin_cross),
                                    contentDescription = stringResource(Res.string.document_viewer_close),
                                    onClick = onDismiss,
                                )
                            },
                        )
                        when (val ready = bytes) {
                            null -> Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(Spacing.page)
                                    .clip(RoundedCornerShape(CornerRadius.card))
                                    .shimmer(),
                            )
                            else -> if (ready.isEmpty()) {
                                if (showEmptyStateTile) {
                                    EmptyStateMessage(
                                        icon = vectorResource(Res.drawable.ic_warning),
                                        title = emptyMessage,
                                        showIconTile = true,
                                    )
                                } else {
                                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                                        Text(text = emptyMessage, color = colors.textSecondary)
                                    }
                                }
                            } else {
                                PdfPagesView(pdfBytes = ready, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
    }
}
