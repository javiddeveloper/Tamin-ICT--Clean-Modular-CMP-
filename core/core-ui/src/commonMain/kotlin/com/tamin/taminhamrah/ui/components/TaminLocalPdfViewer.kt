package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.document_viewer_close
import taminx.core.core_ui.document_viewer_file_unavailable
import taminx.core.core_ui.document_viewer_title
import taminx.core.core_ui.ic_tamin_cross

/**
 * Full-screen viewer for PDFs already available as bytes (e.g. compose resource assets).
 * Unlike [TaminPdfViewer], this does not download or save to the device.
 */
@Composable
fun TaminLocalPdfViewer(
    pdfBytes: ByteArray?,
    onDismiss: () -> Unit,
    title: String = stringResource(Res.string.document_viewer_title),
    background: Brush = taminTopAppBarGradient(),
    emptyMessage: String = stringResource(Res.string.document_viewer_file_unavailable),
) {
    val colors = LocalTaminColors.current

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
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
                when (val ready = pdfBytes) {
                    null -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.page)
                            .clip(RoundedCornerShape(CornerRadius.card))
                            .shimmer(),
                    )
                    else -> if (ready.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text(text = emptyMessage, color = colors.textSecondary)
                        }
                    } else {
                        PdfPagesView(pdfBytes = ready, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}
