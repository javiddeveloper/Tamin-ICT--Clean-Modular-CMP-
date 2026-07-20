package com.tamin.taminhamrah.ui.system

import androidx.compose.runtime.Composable

/**
 * Requests dark or light status-bar icons for as long as this composable stays in the
 * tree, restoring the previous appearance when it leaves.
 *
 * A screen that draws a dark header behind the status bar must ask for [darkIcons] =
 * `false`, otherwise the clock, signal and battery sink into the header. Scoping it to the
 * screen rather than the whole app matters while only some screens have dark headers —
 * the rest still need the default dark-on-light icons.
 *
 * @param darkIcons `true` for dark icons on a light background, `false` for the reverse.
 */
@Composable
expect fun StatusBarIcons(darkIcons: Boolean)
