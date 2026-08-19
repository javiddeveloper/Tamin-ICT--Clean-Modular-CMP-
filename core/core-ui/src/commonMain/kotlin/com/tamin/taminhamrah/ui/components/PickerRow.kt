package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back

private const val CHEVRON_DOWN_DEGREES = 90f

private val IconSize = 19.dp
private val ChevronSize = 16.dp
private val PickerRowMinHeight = 56.dp
private val PickerRowPaddingVertical = 15.dp

/**
 * A read-only field that opens a picker — a date, a list, anything chosen rather than typed.
 *
 * Not a text field, but it reports a problem like one: it borrows [animatedErrorBorder] from
 * [SegmentedInputField] so a missing selection looks exactly like a bad value in the input beside
 * it, and a form built from both reads as one set of controls.
 *
 * The affordance takes the trailing edge — the left in a right-to-left layout. With no [icon] it
 * draws a bare chevron turned to point down; a plain arrow reads as "download" rather than
 * "opens a list".
 */
@Composable
fun PickerRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    /** Greys the label, for when [text] is a prompt rather than a chosen value. */
    isPlaceholder: Boolean = false,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    showChevron: Boolean = false,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .animatedErrorBorder(
                isError = isError,
                errorColor = colors.dangerText,
                normalColor = colors.textMuted,
                borderWidth = Thickness.border,
                cornerRadius = CornerRadius.lg,
            )
            .clickable(onClick = onClick)
            .heightIn(min = PickerRowMinHeight)
            .padding(horizontal = Spacing.lg, vertical = PickerRowPaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = icon ?: vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = null,
            tint = iconTint ?: colors.textMuted,
            modifier = Modifier.size(IconSize).then(
                if (icon == null) Modifier.rotate(CHEVRON_DOWN_DEGREES) else Modifier
            ),
        )
        Text(
            text = text,
            // SemiBold is the heaviest face actually imported; 700 and 800 are synthesized.
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isPlaceholder) colors.textMuted else colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (showChevron && icon != null) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(ChevronSize).rotate(CHEVRON_DOWN_DEGREES),
            )
        }
    }
}
