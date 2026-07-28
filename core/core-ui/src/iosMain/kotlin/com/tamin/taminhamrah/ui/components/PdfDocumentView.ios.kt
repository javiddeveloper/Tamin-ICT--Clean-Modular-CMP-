package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.PDFKit.PDFDocument
import platform.PDFKit.PDFView

/**
 * PDFKit's [PDFView] already scrolls, paginates and zooms a document natively, so the whole
 * viewer is one interop view rather than a hand-rolled page rasterizer.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun PdfPagesView(pdfBytes: ByteArray, modifier: Modifier) {
    UIKitView(
        factory = {
            PDFView().apply {
                document = PDFDocument(data = pdfBytes.toNSData())
                autoScales = true
            }
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.convert())
    }
}
