package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_copy
import kotlin.time.Duration.Companion.milliseconds

/**
 * A value to lift, on a tinted chip behind a dashed outline, with a copy glyph that turns into a
 * green tick for a beat once tapped — the workshop list's code chip, shared.
 *
 * [onCopy] does the clipboard write; pass `rememberCopyAction(raw, announce = false)` so the ASCII
 * value is what gets copied and the tick, not a toast, is the confirmation.
 */
@Composable
fun CopyCodeChip(
    text: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    /** False for prose — a drug name — which may wrap; codes never do (see [NumericText]). */
    numeric: Boolean = true,
    /**
     * Hoisted when a bigger target around the chip copies the same value — a whole [DetailRow] —
     * so a tap there ticks the chip just as a tap on the chip does.
     */
    state: CopyCodeChipState = rememberCopyCodeChipState(),
) {
    val colors = LocalTaminColors.current
    val isCopied = state.isCopied
    ClearCopiedAfterFeedback(state)

    Row(
        modifier = modifier
            .clip(ChipShape)
            .clickable(enabled = enabled) {
                onCopy()
                state.isCopied = true
            }
            .background(colors.blueBg)
            .dashedOutline(colors.blueBorder, ChipCorner, ChipBorderWidth)
            .padding(horizontal = ChipHorizontalPadding, vertical = ChipVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        // Text first so that right-to-left puts it on the right and the glyph on the left.
        if (numeric) {
            NumericText(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = colors.blueText,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = colors.blueText,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        Icon(
            imageVector = vectorResource(if (isCopied) Res.drawable.ic_tamin_check else Res.drawable.ic_tamin_copy),
            contentDescription = contentDescription,
            tint = if (isCopied) colors.greenText else colors.blueText,
            modifier = Modifier.size(ChipGlyphSize),
        )
    }
}

/** Whether a [CopyCodeChip] is showing its tick in place of the copy glyph. */
class CopyCodeChipState {
    var isCopied by mutableStateOf(false)
}

@Composable
fun rememberCopyCodeChipState(): CopyCodeChipState = remember { CopyCodeChipState() }

/** Puts the copy glyph back once the tick has stood in for it long enough. */
@Composable
internal fun ClearCopiedAfterFeedback(state: CopyCodeChipState) {
    val isCopied = state.isCopied
    LaunchedEffect(isCopied) {
        if (!isCopied) return@LaunchedEffect
        delay(CopiedFeedbackMillis.milliseconds)
        state.isCopied = false
    }
}

/** `padding:4px 9px; border-radius:11px; border:1.4px dashed`, with a 13px glyph. */
private val ChipCorner = 11.dp
private val ChipShape = RoundedCornerShape(ChipCorner)
private val ChipBorderWidth = 1.4.dp
private val ChipHorizontalPadding = 9.dp
private val ChipVerticalPadding = 4.dp
private val ChipGlyphSize = 13.dp

/** How long the tick stands in for the copy glyph — `setTimeout(…, 1600)` in the design. */
private const val CopiedFeedbackMillis = 1600L
