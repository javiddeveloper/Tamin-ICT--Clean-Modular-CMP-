package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Draws every page of an already-decoded PDF ([pdfBytes]) vertically, using each platform's native
 * engine — Android's [android.graphics.pdf.PdfRenderer], iOS's PDFKit — so no third-party PDF
 * dependency is added. [TaminPdfViewer] wraps this with channel draining, a top bar and save-to-device.
 */
@Composable
internal expect fun PdfPagesView(pdfBytes: ByteArray, modifier: Modifier = Modifier)
