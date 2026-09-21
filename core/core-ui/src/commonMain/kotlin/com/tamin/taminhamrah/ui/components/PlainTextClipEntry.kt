package com.tamin.taminhamrah.ui.components

import androidx.compose.ui.platform.ClipEntry

/**
 * A clipboard entry holding [text].
 *
 * `ClipEntry` is an `expect class` with nothing in common but its metadata — Android wraps a
 * `ClipData`, iOS wraps a pasteboard item — and neither Compose nor Compose Multiplatform ships a
 * shared way to build one from a string. So the one line that differs lives here, per platform,
 * rather than pushing every caller into platform code.
 */
expect fun plainTextClipEntry(text: String): ClipEntry
