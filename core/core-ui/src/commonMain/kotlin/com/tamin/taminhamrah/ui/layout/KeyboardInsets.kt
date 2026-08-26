package com.tamin.taminhamrah.ui.layout

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier

/**
 * Apply on the [androidx.compose.foundation.layout.Box] that wraps a form scroll area and its
 * sticky bottom action bar overlay.
 *
 * One [imePadding] here lifts both the scroll body and the bottom bar together.
 */
fun Modifier.taminFormImeHost(): Modifier = this.imePadding()

/** Navigation-bar inset for the sticky bottom bar overlay (no extra IME padding). */
fun Modifier.taminStickyBottomBarInsets(): Modifier = this.navigationBarsPadding()
