package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.ActionMenuItem
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.RecordActionMenu
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.ic_email
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_eye
import taminx.core.core_ui.ic_tamin_medical_records
import taminx.core.core_ui.ic_tamin_misc_claims
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings

private val CardCorner = 18.dp
private val CardPaddingHorizontal = 20.dp
private val CardShadowBlur = 26.dp
private val CardShadowOffsetY = 10.dp
private val RailWidth = 3.dp
private val TopWashHeight = 26.dp

private val ChipPaddingHorizontal = 12.dp
private val ChipPaddingVertical = 5.dp
private val ChipIconSize = 14.dp
private val ChipGap = 6.dp

private val HeaderRowTop = 15.dp
private val TitleRowTop = 11.dp
private val TitleRowBottom = 14.dp
private val CodeRowTop = 2.dp
private val CodeRowBottom = 4.dp
private val RuleTop = 12.dp
private val RuleBottom = 4.dp
private val RuleGap = 1.dp
private val FooterTop = 6.dp
private val FooterBottom = 16.dp
private val FooterGap = 9.dp
private val FooterButtonCorner = 14.dp
private val FooterButtonHeight = 44.dp

private const val TOP_WASH_ALPHA = 0.05f
private const val TOGGLE_WEIGHT = 1.5f
private const val ACTIONS_WEIGHT = 1f
private const val CHEVRON_OPEN_DEGREES = -90f
private const val CHEVRON_CLOSED_DEGREES = 90f

/**
 * The record card the refund list and the personal inbox both draw.
 *
 * Five bands: a type chip with its date, a title with its status stamp, a tracking line, the
 * details behind a disclosure, and a footer of two buttons. Everything that differs between the
 * two screens is a value — the chip's wording and colors, the stamp's label, what the details
 * contain, what the actions menu offers — so neither owns a private copy of the layout.
 *
 * The rail is drawn at the *physical* right in [drawBehind] rather than aligned to an edge:
 * `End` follows the reading direction and would put it on the left of a Persian page.
 *
 * [actionsEnabled] `false` drops the actions button entirely, for a record the service has not
 * issued yet: there is nothing to open or post, and offering it would send a malformed request.
 */
@Composable
fun <T> RecordCard(
    chipLabel: String,
    chipIcon: ImageVector,
    chipContainerColor: Color,
    chipContentColor: Color,
    date: String,
    title: String,
    stampLabel: String,
    stampColor: Color,
    codeLabel: String,
    code: String,
    codeIcon: ImageVector,
    onCopyCode: () -> Unit,
    actionsLabel: String,
    actions: ImmutableList<ActionMenuItem<T>>,
    onActionSelect: (T) -> Unit,
    railBrush: Brush,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    toggleButtonContentColor: Color = LocalTaminColors.current.teal,
    dateColor: Color = LocalTaminColors.current.textMuted,
    shadowBlur: Dp = CardShadowBlur,
    shadowOffsetY: Dp = CardShadowOffsetY,
    details: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    var menuOpen by remember { mutableStateOf(false) }

    // Keyed on what it is built from. Keyed on anything else it would survive a theme change.


    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CardCorner,
                blurRadius = shadowBlur,
                offsetY = shadowOffsetY,
            )
            .clip(RoundedCornerShape(CardCorner))
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, RoundedCornerShape(CardCorner))
            .drawBehind {
                val rail = RailWidth.toPx()
                drawRect(
                    brush = railBrush,
                    topLeft = Offset(size.width - rail, 0f),
                    size = Size(rail, size.height),
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = CardPaddingHorizontal)
                .padding(top = HeaderRowTop),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(chipContainerColor)
                    .padding(
                        horizontal = ChipPaddingHorizontal,
                        vertical = ChipPaddingVertical,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ChipGap),
            ) {
                Icon(
                    imageVector = chipIcon,
                    contentDescription = null,
                    tint = chipContentColor,
                    modifier = Modifier.size(ChipIconSize),
                )
                Text(
                    text = chipLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = chipContentColor,
                )
            }
            NumericText(
                text = date,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = dateColor,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = CardPaddingHorizontal)
                .padding(top = TitleRowTop, bottom = TitleRowBottom),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            StatusStamp(label = stampLabel, color = stampColor)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = CardPaddingHorizontal)
                .padding(top = CodeRowTop, bottom = CodeRowBottom),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TrackingCodeRow(
                label = codeLabel,
                code = code,
                onCopy = onCopyCode,
                leadingIcon = codeIcon,
            )
        }

        // Two hairlines a pixel apart, which is how the design separates the head from the body.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = CardPaddingHorizontal)
                .padding(top = RuleTop, bottom = RuleBottom),
        ) {
            HorizontalDivider(thickness = Thickness.border, color = colors.border)
            Spacer(Modifier.height(RuleGap))
            HorizontalDivider(thickness = Thickness.border, color = colors.divider)
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(horizontal = CardPaddingHorizontal),
                content = details,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = CardPaddingHorizontal)
                .padding(top = FooterTop, bottom = FooterBottom),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FooterGap),
        ) {
            // Held as State, not delegated: reading it here would recompose the footer on every
            // frame of the turn. Read inside graphicsLayer, the animation costs none.
            val rotation = animateFloatAsState(
                targetValue = if (expanded) CHEVRON_OPEN_DEGREES else CHEVRON_CLOSED_DEGREES,
                label = "record-card-chevron",
            )
            TaminOutlinedButton(
                text = stringResource(
                    if (expanded) Res.string.action_hide_details else Res.string.action_show_details,
                ),
                onClick = { onExpandedChange(!expanded) },
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                iconModifier = Modifier.graphicsLayer { rotationZ = rotation.value },
                shape = RoundedCornerShape(FooterButtonCorner),
                height = FooterButtonHeight,
                borderColor = Color.Transparent,
                containerColor = colors.bgPage,
                contentColor = toggleButtonContentColor,
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.weight(TOGGLE_WEIGHT),
            )

            // The weight belongs on the Box: TaminFilledButton fills its width from the inside, so
            // an unweighted wrapper takes the whole row and starves the toggle.
            if (actionsEnabled) Box(modifier = Modifier.weight(ACTIONS_WEIGHT)) {
                TaminFilledButton(
                    text = actionsLabel,
                    onClick = { menuOpen = true },
                    // Material's own cog rather than a hand-traced one.
                    icon = Icons.Rounded.Settings,
                    iconPosition = IconPosition.End,
                    shape = RoundedCornerShape(FooterButtonCorner),
                    height = FooterButtonHeight,
                    background = colors.iconGradientSuccess,
                    textStyle = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                RecordActionMenu(
                    expanded = menuOpen,
                    items = actions,
                    onDismiss = { menuOpen = false },
                    onSelect = { action ->
                        menuOpen = false
                        onActionSelect(action)
                    },
                )
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private enum class RecordCardPreviewAction { VIEW, SEND }

/** One card, so the two previews below can only differ by [toggleButtonContentColor]. */
@Composable
private fun RecordCardPreviewSample(
    toggleButtonContentColor: Color = LocalTaminColors.current.teal,
) {
    val colors = LocalTaminColors.current
    var expanded by remember { mutableStateOf(false) }
    val actions = remember {
        persistentListOf(
            ActionMenuItem(RecordCardPreviewAction.VIEW, "مشاهده", Res.drawable.ic_tamin_eye),
            ActionMenuItem(RecordCardPreviewAction.SEND, "ارسال", Res.drawable.ic_email),
        )
    }
    val rail = remember { Brush.verticalGradient(listOf(colors.teal, colors.blueText)) }

    RecordCard(
        chipLabel = "پرونده پزشکی",
        chipIcon = vectorResource(Res.drawable.ic_tamin_medical_records),
        chipContainerColor = colors.greenBg,
        chipContentColor = colors.teal,
        date = "۱۴۰۴/۰۵/۲۷",
        title = "بیمارستان امام رضا",
        stampLabel = "پرداخت شده",
        stampColor = colors.greenText,
        codeLabel = "کد پیگیری",
        code = "۱۲۳۴۵۶۷۸",
        codeIcon = vectorResource(Res.drawable.ic_tamin_misc_claims),
        onCopyCode = {},
        actionsLabel = "عملیات",
        actions = actions,
        onActionSelect = {},
        railBrush = rail,
        expanded = expanded,
        onExpandedChange = { expanded = it },
        toggleButtonContentColor = toggleButtonContentColor,
        details = {
            DetailRow(label = "کد ملی", value = "۰۰۱۲۳۴۵۶۷۸")
            DetailRow(label = "مبلغ سهم شما", value = "۲۵۰,۰۰۰ ریال")
        },
    )
}

@PreviewRtlTheme
@Composable
private fun RecordCardPreview() {
    PreviewRtlThemeContent {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            // Default: every existing caller (treatment costs, personal inbox) keeps this.
            RecordCardPreviewSample()
            // Same card, only the toggle recolored — proves the override stays local to
            // whichever screen passes it.
            RecordCardPreviewSample(toggleButtonContentColor = LocalTaminColors.current.orangeText)
        }
    }
}

@PreviewRtlTheme
@Composable
private fun RecordCardPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            RecordCardPreviewSample()
            RecordCardPreviewSample(toggleButtonContentColor = LocalTaminColors.current.orangeText)
        }
    }
}

