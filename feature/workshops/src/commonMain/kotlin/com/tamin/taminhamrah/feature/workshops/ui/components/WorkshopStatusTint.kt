package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.model.workshop.Article16RequestStatus
import com.tamin.taminhamrah.model.workshop.PaymentSheetStatus
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * Which of the theme's status colour pairs a workshop status is drawn in.
 *
 * The buckets are named by meaning, not by colour, so the palette can move without every screen
 * that spelled "green" having to move with it. The status *word* always comes from the service —
 * only the tint is decided here.
 */
@Immutable
enum class StatusTint { POSITIVE, WARNING, NEGATIVE, INFO, NEUTRAL }

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

val Article16RequestStatus.tint: StatusTint
    get() = when (this) {
        Article16RequestStatus.APPROVED -> StatusTint.POSITIVE
        Article16RequestStatus.DOCUMENT_DEFECT -> StatusTint.WARNING
        Article16RequestStatus.REJECTED -> StatusTint.NEGATIVE
        Article16RequestStatus.SUBMITTED -> StatusTint.INFO
        Article16RequestStatus.NONE, Article16RequestStatus.UNKNOWN -> StatusTint.NEUTRAL
    }
