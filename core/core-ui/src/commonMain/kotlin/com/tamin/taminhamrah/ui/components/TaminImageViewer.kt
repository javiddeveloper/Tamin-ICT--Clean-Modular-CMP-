package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import taminx.core.core_ui.ic_warning
import taminx.core.core_ui.document_viewer_file_unavailable
import taminx.core.core_ui.ic_tamin_cross

/**
 * A document image, full screen and pinch-zoomable.
 *
 * The scale and pan are read inside `graphicsLayer`, so a pinch costs a redraw and never a
 * recomposition of the image beneath it.
 *
 * Callers that fetch the image *after* opening the viewer can opt into the same three states
 * [TaminPdfViewer] has: set [isLoading] while the fetch is in flight for a shimmer, and
 * [downloadFailed] when it comes back empty-handed for [emptyMessage]. Both default to false, which
 * is exactly the original viewer — black mat, image loader — for every caller that does not ask.
 */
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.graphics.Brush

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 5f

@Composable
fun TaminImageViewer(
    title: String,
    url: String,
    onDismiss: () -> Unit,
    background: Brush = taminTopAppBarGradient(),
    downloadFailed: Boolean = false,
    emptyMessage: String = stringResource(Res.string.document_viewer_file_unavailable),
    isLoading: Boolean = false,
) {
    val colors = LocalTaminColors.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        var scale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        val transform = rememberTransformableState { zoomChange, panChange, _ ->
            scale = (scale * zoomChange).coerceIn(MIN_SCALE, MAX_SCALE)
            if (scale > 1f) {
                offsetX += panChange.x
                offsetY += panChange.y
            } else {
                offsetX = 0f
                offsetY = 0f
            }
        }

        val hasImage = !downloadFailed && !isLoading
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Black is the mat an image is shown against; with no image to show it is just a
                // dark void, so the two other states take the page's own ground like the PDF
                // viewer's do.
                .background(if (hasImage) Color.Black else colors.bgPage),
            contentAlignment = Alignment.Center,
        ) {
            when {
                // Said rather than drawn: an empty model renders the broken-image placeholder,
                // which reads as a rendering fault instead of a document that is not there.
                downloadFailed -> EmptyStateMessage(
                    icon = vectorResource(Res.drawable.ic_warning),
                    title = emptyMessage,
                    showIconTile = true,
                )

                // Still arriving. The same card-shaped wait the PDF viewer shows.
                isLoading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.page)
                        .clip(RoundedCornerShape(CornerRadius.card))
                        .shimmer(),
                )

                else -> LoadAsyncImage(
                model = url,
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1.5f) {
                                    scale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                } else {
                                    scale = 2.5f
                                }
                            }
                        )
                    }
                    .transformable(transform)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    },
                )
            }

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
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}
