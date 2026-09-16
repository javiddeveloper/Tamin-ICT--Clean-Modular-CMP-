package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.borderTrace
import com.tamin.taminhamrah.ui.components.rememberBorderTracePhase
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_card_collapse
import taminx.core.core_ui.workshop_card_expand

/**
 * One record on any of the کارگاه service lists.
 *
 * Every one of those screens draws the same card — a stack of label/value cells, an optional row
 * of actions, and an optional «جزئیات بیشتر» that unfolds the rest — so the geometry lives here
 * once. What differs per screen is only which cells and which buttons, and those are the caller's.
 *
 * [isExpanded] null means the card has no toggle at all, which is how اسناد مطالبه and
 * استعلام بدهی are drawn.
 */
@Composable
fun WorkshopRecordCard(
    modifier: Modifier = Modifier,
    isExpanded: Boolean? = null,
    onToggle: () -> Unit = {},
    expandLabel: StringResource = Res.string.workshop_card_expand,
    collapseLabel: StringResource = Res.string.workshop_card_collapse,
    buttons: (@Composable RowScope.() -> Unit)? = null,
    cells: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.cardCorner)
            .padding(
                start = WorkshopDimens.cardHorizontalPadding,
                end = WorkshopDimens.cardHorizontalPadding,
                top = WorkshopDimens.cardTopPadding,
                bottom = WorkshopDimens.cardBottomPadding,
            ),
    ) {
        cells()

        if (buttons != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = WorkshopDimens.cardButtonsTopMargin),
                horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.cardButtonGap),
            ) {
                buttons()
            }
        }

        if (isExpanded != null) {
            CardExpandToggle(
                isExpanded = isExpanded,
                onToggle = onToggle,
                expandLabel = expandLabel,
                collapseLabel = collapseLabel,
            )
        }
    }
}

/**
 * How a card's action reads. The design gives each a fill, a text color and an outline.
 *
 * [NEUTRAL], [INFO], [SUCCESS_SOFT] and [TEAL] are the quieter tinted chips واگذارندگان draws: page
 * gray, pale blue, pale green, and a white chip in teal.
 */
@Immutable
enum class WorkshopCardButtonTone {
    PRIMARY,
    OUTLINE,
    SUCCESS,
    DANGER,
    ALERT,
    NOTICE,
    DISABLED,
    NEUTRAL,
    INFO,
    SUCCESS_SOFT,
    TEAL,
}

/**
 * One action inside a [WorkshopRecordCard].
 *
 * Shorter and tighter than a page-level button — `min-height:42px; radius:13px; 12px/700` — and
 * always sharing the row's width equally with its siblings.
 *
 * @param icon a glyph ahead of the label. Null, the default, draws the label alone.
 */
@Composable
fun RowScope.WorkshopCardButton(
    text: String,
    tone: WorkshopCardButtonTone,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    /**
     * The action's request is running: the outline is traced in the label's own color, and taps are
     * dropped, since another would only start the same request again.
     */
    isLoading: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val textStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
    val slot = modifier.weight(1f).widthIn(min = WorkshopDimens.cardButtonMinWidth)
    val tracePhase = if (isLoading) rememberBorderTracePhase() else null
    val action = if (isLoading) NoAction else onClick

    val gradient: Brush? = when (tone) {
        WorkshopCardButtonTone.PRIMARY -> colors.buttonGradient
        WorkshopCardButtonTone.SUCCESS -> colors.successGradient
        WorkshopCardButtonTone.ALERT -> colors.alertGradient
        else -> null
    }

    if (gradient != null) {
        TaminPrimaryButton(
            text = text,
            onClick = action,
            icon = icon,
            iconAtStart = true,
            background = gradient,
            height = WorkshopDimens.cardButtonHeight,
            shape = CardButtonShape,
            textStyle = textStyle,
            modifier = slot.cardButtonTrace(tracePhase, Color.White),
        )
        return
    }

    // The rest are a fill plus an outline, which is what the outlined button already is; only the
    // three colors change per tone.
    val (container, content, border) = when (tone) {
        WorkshopCardButtonTone.OUTLINE ->
            Triple(colors.bgSurface, colors.blueText, colors.blueBorder)

        WorkshopCardButtonTone.DANGER ->
            Triple(colors.dangerBg, colors.dangerText, colors.dangerText.copy(alpha = WorkshopDimens.cardButtonOutlineAlpha))

        WorkshopCardButtonTone.NOTICE ->
            Triple(colors.orangeBg, colors.orangeText, colors.orangeText.copy(alpha = WorkshopDimens.cardButtonOutlineAlpha))

        WorkshopCardButtonTone.NEUTRAL -> Triple(colors.bgPage, colors.textTertiary, colors.border)
        WorkshopCardButtonTone.INFO -> Triple(colors.blueBg, colors.blueText, colors.blueBorder)
        WorkshopCardButtonTone.SUCCESS_SOFT -> Triple(colors.greenBg, colors.greenText, colors.greenBorder)
        WorkshopCardButtonTone.TEAL -> Triple(colors.bgSurface, colors.tealText, colors.border)

        else -> Triple(colors.bgSurface, colors.textMuted, colors.border)
    }

    TaminOutlinedButton(
        text = text,
        onClick = action,
        icon = icon,
        enabled = tone != WorkshopCardButtonTone.DISABLED,
        shape = CardButtonShape,
        height = WorkshopDimens.cardButtonHeight,
        borderWidth = WorkshopDimens.cardButtonBorderWidth,
        borderColor = border,
        containerColor = container,
        contentColor = content,
        textStyle = textStyle,
        modifier = slot.cardButtonTrace(tracePhase, content),
    )
}

/** Traces a card button's outline in [color] while [phase] runs, and leaves it alone otherwise. */
private fun Modifier.cardButtonTrace(phase: (() -> Float)?, color: Color): Modifier = when (phase) {
    null -> this
    else -> borderTrace(phase, color, WorkshopDimens.cardButtonBorderWidth, CornerRadius.listRow)
}

private val NoAction: () -> Unit = {}

private val CardButtonShape = RoundedCornerShape(CornerRadius.listRow)
