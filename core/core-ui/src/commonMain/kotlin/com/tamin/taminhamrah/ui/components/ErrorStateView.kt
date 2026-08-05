package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_close
import taminx.core.core_ui.action_retry
import taminx.core.core_ui.error_title
import taminx.core.core_ui.ic_tamin_cross

/**
 * How a failed request tells the user, everywhere in the app.
 *
 * A dialog rather than a panel in the page: a failure is the only thing worth attending to when it
 * happens, and every screen showing one the same way means the user learns it once.
 *
 * Built on [TaminConfirmationDialog] and the shared buttons rather than on its own layout, so it
 * inherits the dialog language the health screens already established and cannot drift from it.
 * [message] is the service's own wording and carries the detail; the title only names the state.
 *
 * [onRetry] is optional. Without it the dialog offers only a way out, which is right for a failure
 * that repeating cannot fix.
 */
@Composable
fun ErrorStateView(
    message: String?,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (message == null) return

    // Keyed on the message, so dismissing one failure never swallows the next one. Kept here
    // rather than in each screen: four screens hand-rolling the same flag is four chances to
    // forget the key and leave a user staring at a page that silently failed.
    var dismissed by remember(message) { mutableStateOf(false) }
    if (dismissed) return

    val colors = LocalTaminColors.current
    val onDismiss = { dismissed = true }

    TaminConfirmationDialog(
        title = stringResource(Res.string.error_title),
        description = message,
        icon = vectorResource(Res.drawable.ic_tamin_cross),
        iconTint = colors.dangerText,
        iconBackground = colors.dangerBg,
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            if (onRetry != null) {
                TaminFilledButton(
                    text = stringResource(Res.string.action_retry),
                    onClick = {
                        onDismiss()
                        onRetry()
                    },
                    background = SolidColor(colors.blueText),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.action_close),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}

@PreviewRtlTheme
@Composable
fun PreviewErrorStateViewWithRetry() {
    PreviewRtlThemeContent {
        ErrorStateView(
            message = "در دریافت اطلاعات مشکلی پیش آمد. لطفاً دوباره تلاش کنید.",
            onRetry = {},
        )
    }
}

/** Without a retry — the shape a screen gets when repeating the request cannot help. */
@PreviewRtlTheme
@Composable
fun PreviewErrorStateViewWithoutRetry() {
    PreviewRtlThemeContent {
        ErrorStateView(
            message = "این سرویس در حال حاضر در دسترس نیست.",
        )
    }
}
