package com.tamin.taminhamrah.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import androidx.core.graphics.createBitmap

@Composable
internal actual fun PdfPagesView(pdfBytes: ByteArray, modifier: Modifier) {
    val context = LocalContext.current
    // Rasterizing off the main thread; recomputed only when the bytes change.
    val pages by produceState(initialValue = emptyList(), pdfBytes) {
        value = withContext(Dispatchers.Default) { renderPdf(context.cacheDir, pdfBytes) }
    }
    LazyColumn(modifier = modifier) {
        items(pages) { page ->
            Image(
                bitmap = page.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().padding(all = 8.dp),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}

/**
 * Rasterizes every page to a bitmap at [PAGE_SCALE]× the intrinsic size for legibility.
 * PdfRenderer needs a seekable file descriptor, so the bytes are spilled to a temp file first.
 */
private fun renderPdf(cacheDir: File, bytes: ByteArray): List<Bitmap> {
    if (bytes.isEmpty()) return emptyList()
    val file = File.createTempFile("tamin_pdf", ".pdf", cacheDir).apply { writeBytes(bytes) }
    return try {
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
            PdfRenderer(descriptor).use { renderer ->
                (0 until renderer.pageCount).map { index ->
                    renderer.openPage(index).use { page ->
                        val bitmap =
                            createBitmap(page.width * PAGE_SCALE, page.height * PAGE_SCALE)
                        bitmap.eraseColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                }
            }
        }
    } finally {
        file.delete()
    }
}

private const val PAGE_SCALE = 2
