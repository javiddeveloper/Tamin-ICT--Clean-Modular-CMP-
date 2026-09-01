package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestStatus
import com.tamin.taminhamrah.model.workshop.PaymentSheetStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * Which of the theme's colour pairs a workshop status — or an action's icon tile — is drawn in.
 *
 * The buckets are named by meaning, not by colour, so the palette can move without every screen
 * that spelled "green" having to move with it. The status *word* always comes from the service —
 * only the tint is decided here.
 */
@Immutable
enum class StatusTint { POSITIVE, WARNING, NEGATIVE, INFO, NEUTRAL, TEAL, MINT, PURPLE }

/** The container/content pair this tint draws with, resolved from the theme. */
@Composable
fun StatusTint.colors(): Pair<Color, Color> {
    val palette = LocalTaminColors.current
    return when (this) {
        StatusTint.POSITIVE -> palette.greenBg to palette.greenText
        StatusTint.WARNING -> palette.orangeBg to palette.orangeText
        StatusTint.NEGATIVE -> palette.dangerBg to palette.dangerText
        StatusTint.INFO -> palette.blueBg to palette.blueText
        StatusTint.NEUTRAL -> palette.chipBg to palette.textSecondary
        StatusTint.TEAL -> palette.tealBg to palette.teal
        StatusTint.MINT -> palette.mintBg to palette.mintText
        StatusTint.PURPLE -> palette.fuchsiaBlueBg to palette.fuchsiaBlue
    }
}

/** فعال is positive, نیمه فعال cautionary, and everything else — unknown included — negative. */
val WorkshopActivityStatus.tint: StatusTint
    get() = when (this) {
        WorkshopActivityStatus.ACTIVE -> StatusTint.POSITIVE
        WorkshopActivityStatus.SEMI_ACTIVE -> StatusTint.WARNING
        WorkshopActivityStatus.INACTIVE -> StatusTint.NEGATIVE
    }

val PaymentSheetStatus.tint: StatusTint
    get() = when (this) {
        PaymentSheetStatus.COLLECTED -> StatusTint.POSITIVE
        PaymentSheetStatus.EFFECTIVE -> StatusTint.WARNING
        PaymentSheetStatus.VOID -> StatusTint.NEGATIVE
        PaymentSheetStatus.UNKNOWN -> StatusTint.NEUTRAL
    }

val ArticleSixteenRequestStatus.tint: StatusTint
    get() = when (this) {
        ArticleSixteenRequestStatus.APPROVED -> StatusTint.POSITIVE
        ArticleSixteenRequestStatus.DOCUMENT_DEFECT -> StatusTint.WARNING
        ArticleSixteenRequestStatus.REJECTED -> StatusTint.NEGATIVE
        ArticleSixteenRequestStatus.SUBMITTED -> StatusTint.INFO
        ArticleSixteenRequestStatus.NONE, ArticleSixteenRequestStatus.UNKNOWN -> StatusTint.NEUTRAL
    }

/**
 * پیگیری وضعیت اعتراض status colors, matching the legacy app's own `gridStatusTypeColor`
 * (1/5/6 = neutral, 2 = red, 3 = blue, 4 = amber). APPROVED (6, "تایید رای") is deliberately left
 * neutral rather than given a positive/green tint — that is the legacy behavior, not an oversight,
 * so don't "fix" it into [StatusTint.POSITIVE].
 */
val WorkShopObjectionStatus.tint: StatusTint
    get() = when (this) {
        WorkShopObjectionStatus.SUBMITTED,
        WorkShopObjectionStatus.TIME_ALLOCATED,
        WorkShopObjectionStatus.APPROVED,
        WorkShopObjectionStatus.UNKNOWN,
        -> StatusTint.NEUTRAL
        WorkShopObjectionStatus.CALCULATION_REVIEW -> StatusTint.NEGATIVE
        WorkShopObjectionStatus.BOARD_REVIEW -> StatusTint.INFO
        WorkShopObjectionStatus.RECALCULATED -> StatusTint.WARNING
    }
