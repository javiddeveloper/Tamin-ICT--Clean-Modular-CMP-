package com.tamin.taminhamrah.ui.system

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun StatusBarIcons(darkIcons: Boolean) {
    val view = LocalView.current
    // Previews render without a window; touching the insets controller there would throw.
    if (view.isInEditMode) return

    DisposableEffect(view, darkIcons) {
        val window = view.context.findActivity()?.window
            ?: return@DisposableEffect onDispose { }

        val controller = WindowCompat.getInsetsController(window, view)
        val previous = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = darkIcons

        // Restore on the way out, so leaving a dark-header screen does not strand the
        // rest of the app with icons the wrong way round.
        onDispose { controller.isAppearanceLightStatusBars = previous }
    }
}

/** Compose hands out a ContextWrapper, so the Activity has to be unwrapped. */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
