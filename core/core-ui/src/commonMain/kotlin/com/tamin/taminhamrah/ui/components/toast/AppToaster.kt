package com.tamin.taminhamrah.ui.components.toast // adjust to your actual core-ui package, e.g. ir.tamin.hamrah.core.ui.toast

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import kotlin.time.Duration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * CompositionLocal that exposes the app-wide [ToasterState].
 * Not provided a default value on purpose — if a screen tries to read it without
 * [AppToastHost] wrapping it somewhere above, it should fail loudly, not silently
 * create a no-op toaster.
 */
val LocalToaster = compositionLocalOf<ToasterState> {
    error("No ToasterState provided. Wrap your app content in AppToastHost {}.")
}

/**
 * Call this ONCE near the root of your app (e.g. right inside your theme wrapper or
 * top-level Scaffold, above your NavHost), instead of declaring Toaster() on every screen.
 *
 * Example:
 * ```
 * TaminHamrahTheme {
 *     AppToastHost {
 *         NavHost(...) { ... }
 *     }
 * }
 * ```
 */
@Composable
fun AppToastHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val toaster = rememberToasterState()

    CompositionLocalProvider(LocalToaster provides toaster) {
        content()
    }

    Toaster(
        state = toaster,
        modifier = modifier,
        showCloseButton = true,
        alignment = Alignment.TopCenter,
        richColors = true,
        darkTheme = isSystemInDarkTheme(),
        messageSlot = { toast ->
            Text(
                text = toast.message.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = LocalToastContentColor.current,
            )
        },
        iconSlot = { toast ->
            val tint = LocalToastContentColor.current
            // The gap between the glyph and the message: this slot replaces the library's default,
            // which carried it, so without it the icon sits against the first word.
            val gap = Modifier.padding(end = Spacing.md)
            when (toast.type) {
                ToastType.Success -> androidx.compose.material3.Icon(Icons.Default.CheckCircle, contentDescription = null, tint = tint, modifier = gap)
                ToastType.Error   -> androidx.compose.material3.Icon(Icons.Default.Error, contentDescription = null, tint = tint, modifier = gap)
                ToastType.Info    -> androidx.compose.material3.Icon(Icons.Default.Info, contentDescription = null, tint = tint, modifier = gap)
                ToastType.Warning -> androidx.compose.material3.Icon(Icons.Default.Warning, contentDescription = null, tint = tint, modifier = gap)
                ToastType.Normal  -> {} // no icon
            }
        },
    )
}

/**
 * Shortcut helpers so you can write `toaster.success("...")`, `toaster.error("...")`,
 * `toaster.info("...")`, `toaster.warning("...")` instead of
 * `toaster.show("...", type = ToastType.Success)` everywhere.
 *
 * Usage:
 * ```
 * val toaster = LocalToaster.current
 * toaster.success("ذخیره شد")
 * toaster.error("خطا رخ داد")
 * toaster.info("در حال همگام‌سازی...")
 * toaster.warning("باتری کم است")
 * ```
 */
fun ToasterState.success(
    message: Any,
    id: Any = currentNanoTime(),
    icon: Any? = null,
    action: Any? = null,
    duration: Duration = ToasterDefaults.DurationDefault,
): Toast = show(
    message = message,
    id = id,
    icon = icon,
    action = action,
    type = ToastType.Success,
    duration = duration,
)

fun ToasterState.error(
    message: Any,
    id: Any = currentNanoTime(),
    icon: Any? = null,
    action: Any? = null,
    duration: Duration = ToasterDefaults.DurationDefault,
): Toast = show(
    message = message,
    id = id,
    icon = icon,
    action = action,
    type = ToastType.Error,
    duration = duration,
)

fun ToasterState.info(
    message: Any,
    id: Any = currentNanoTime(),
    icon: Any? = null,
    action: Any? = null,
    duration: Duration = ToasterDefaults.DurationDefault,
): Toast = show(
    message = message,
    id = id,
    icon = icon,
    action = action,
    type = ToastType.Info,
    duration = duration,
)

fun ToasterState.warning(
    message: Any,
    id: Any = currentNanoTime(),
    icon: Any? = null,
    action: Any? = null,
    duration: Duration = ToasterDefaults.DurationDefault,
): Toast = show(
    message = message,
    id = id,
    icon = icon,
    action = action,
    type = ToastType.Warning,
    duration = duration,
)
