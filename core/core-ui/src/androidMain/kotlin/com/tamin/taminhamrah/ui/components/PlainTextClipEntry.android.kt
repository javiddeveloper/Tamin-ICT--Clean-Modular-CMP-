package com.tamin.taminhamrah.ui.components

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

/** The label doubles as the text: Android shows it in the paste preview on newer versions. */
internal actual fun plainTextClipEntry(text: String): ClipEntry =
    ClipEntry(ClipData.newPlainText(text, text))
