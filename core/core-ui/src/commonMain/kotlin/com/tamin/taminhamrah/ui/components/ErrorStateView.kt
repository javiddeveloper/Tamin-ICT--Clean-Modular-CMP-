package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
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
 * [onDismiss] is where closing the dialog leads, and every caller sends it back: acknowledging a
 * failure should leave the screen, not strand the user on a page that never loaded.
 *
 * [onRetry] is optional. Without it the dialog offers only a way out, which is right for a failure
 * that repeating cannot fix. It needs no dismissal of its own — every reducer clears `error` when a
 * load begins, so the dialog closes itself and returns if the retry fails too.
 */
@Composable
fun ErrorStateView(
    message: String?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (message == null) return

    val colors = LocalTaminColors.current

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
                    onClick = onRetry,
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
            onDismiss = {},
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
            onDismiss = {},
        )
    }
}
