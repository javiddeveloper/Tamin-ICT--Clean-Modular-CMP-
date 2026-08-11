package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_copied
import taminx.core.core_ui.action_copy
import taminx.core.core_ui.ic_tamin_copy

/**
 * Puts [value] on the clipboard and says so.
 *
 * Sits beside whatever it copies — a tracking code, a drug name — rather than being a row of its
 * own, so the thing being copied stays the thing being read.
 *
 * [value] is what lands on the clipboard, which is not always what is on screen: a code rendered
 * in Persian digits has to be copied in the ASCII ones, or pasting it into a form that expects a
 * code produces something no service can match. Callers pass the raw value.
 */
@Composable
fun CopyIconButton(
    value: String,
    modifier: Modifier = Modifier,
    /** Names the copied thing for a screen reader — "کد رهگیری", the drug's name. */
    label: String? = null,
    tint: Color = LocalTaminColors.current.textMuted,
    /**
     * False when an enclosing row already copies [value] — a bigger, easier target. The glyph is
     * then pure affordance: no click of its own, and no content description, because the row
     * already carries the action for a screen reader.
     */
    interactive: Boolean = true,
) {
    val copy = rememberCopyAction(value)
    val copyAction = stringResource(Res.string.action_copy)

    Icon(
        imageVector = vectorResource(Res.drawable.ic_tamin_copy),
        contentDescription = if (interactive) {
            label?.let { "$copyAction $it" } ?: copyAction
        } else {
            null
        },
        tint = tint,
        modifier = modifier
            .size(IconSize.small)
            .then(if (interactive) Modifier.clickable(onClick = copy) else Modifier),
    )
}

/**
 * Copying [value] to the clipboard and announcing it, as a plain action.
 *
 * For the places where the tap target is bigger than the glyph — a whole row carrying a code, a
 * chip — so they get the same clipboard write and the same confirmation as [CopyIconButton] rather
 * than repeating both.
 */
/**
 * Copying [value] to the clipboard and announcing it, as a plain action.
 *
 * For the places where the tap target is bigger than the glyph — a whole row carrying a code, a
 * chip — so they get the same clipboard write and the same confirmation as [CopyIconButton] rather
 * than repeating both.
 *
 * The write is suspending on the current clipboard API, so it runs in the composition's scope; the
 * confirmation follows it rather than racing it.
 */
@Composable
fun rememberCopyAction(value: String): () -> Unit {
    val clipboard = LocalClipboard.current
    val toaster = LocalToaster.current
    val copiedMessage = stringResource(Res.string.action_copied)
    val scope = rememberCoroutineScope()
    return remember(value, clipboard, toaster, copiedMessage, scope) {
        {
            scope.launch {
                clipboard.setClipEntry(plainTextClipEntry(value))
                toaster.success(copiedMessage)
            }
        }
    }
}
