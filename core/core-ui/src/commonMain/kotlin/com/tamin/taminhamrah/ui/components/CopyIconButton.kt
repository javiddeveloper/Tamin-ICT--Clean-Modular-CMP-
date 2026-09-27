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
import taminx.core.core_ui.ic_tamin_check
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
    /**
     * The glyph turns into a green tick for a beat once copied — as [CopyCodeChip] does — and the
     * tick is the confirmation, not a toast. Hoist it when an enclosing row copies [value], and
     * build that row's action with `rememberCopyAction(value, copiedState = …)` so its tap ticks
     * the glyph too.
     */
    copiedState: CopyCodeChipState = rememberCopyCodeChipState(),
) {
    val copy = rememberCopyAction(value, copiedState = copiedState)
    val copyAction = stringResource(Res.string.action_copy)
    val isCopied = copiedState.isCopied
    ClearCopiedAfterFeedback(copiedState)

    Icon(
        imageVector = vectorResource(if (isCopied) Res.drawable.ic_tamin_check else Res.drawable.ic_tamin_copy),
        contentDescription = if (interactive) {
            label?.let { "$copyAction $it" } ?: copyAction
        } else {
            null
        },
        tint = if (isCopied) LocalTaminColors.current.greenText else tint,
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
 *
 * The write is suspending on the current clipboard API, so it runs in the composition's scope; the
 * confirmation follows it rather than racing it.
 *
 * @param announce false for a caller that confirms the copy itself — a chip that swaps its glyph
 * for a tick, say. It also keeps that caller off [LocalToaster], which has no default value on
 * purpose, so the component still composes in a preview with no `AppToastHost` above it.
 * @param copiedState the tick to show instead of a toast — the one handed to the [CopyIconButton]
 * or [CopyCodeChip] beside the row, so a tap anywhere on the row ticks that glyph.
 */
@Composable
fun rememberCopyAction(
    value: String,
    announce: Boolean = true,
    copiedState: CopyCodeChipState? = null,
): () -> Unit {
    val clipboard = LocalClipboard.current
    val toaster = if (announce && copiedState == null) LocalToaster.current else null
    val copiedMessage = stringResource(Res.string.action_copied)
    val scope = rememberCoroutineScope()
    return remember(value, clipboard, toaster, copiedMessage, scope, copiedState) {
        {
            copiedState?.isCopied = true
            scope.launch {
                clipboard.setClipEntry(plainTextClipEntry(value))
                toaster?.success(copiedMessage)
            }
        }
    }
}
