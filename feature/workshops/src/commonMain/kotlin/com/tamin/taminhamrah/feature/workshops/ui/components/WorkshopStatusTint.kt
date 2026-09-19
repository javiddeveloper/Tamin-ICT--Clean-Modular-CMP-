package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestStatus
import com.tamin.taminhamrah.model.workshop.PaymentSheetStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.article_sixteen_status_approved
import taminx.core.core_ui.article_sixteen_status_document_defect
import taminx.core.core_ui.article_sixteen_status_none
import taminx.core.core_ui.article_sixteen_status_rejected
import taminx.core.core_ui.article_sixteen_status_submitted
import taminx.core.core_ui.article_sixteen_status_unknown

/**
 * Which of the theme's color pairs a workshop status — or an action's icon tile — is drawn in.
 *
 * The buckets are named by meaning, not by color, so the palette can move without every screen
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

val ArticleSixteenRequestStatus.label: StringResource
    get() = when (this) {
        ArticleSixteenRequestStatus.SUBMITTED -> Res.string.article_sixteen_status_submitted
        ArticleSixteenRequestStatus.DOCUMENT_DEFECT -> Res.string.article_sixteen_status_document_defect
        ArticleSixteenRequestStatus.REJECTED -> Res.string.article_sixteen_status_rejected
        ArticleSixteenRequestStatus.APPROVED -> Res.string.article_sixteen_status_approved
        ArticleSixteenRequestStatus.NONE -> Res.string.article_sixteen_status_none
        ArticleSixteenRequestStatus.UNKNOWN -> Res.string.article_sixteen_status_unknown
    }

val WorkShopObjectionStatus.tint: StatusTint
    get() = when (this) {
        WorkShopObjectionStatus.SUBMITTED,
        WorkShopObjectionStatus.TIME_ALLOCATED,
        WorkShopObjectionStatus.UNKNOWN,
        -> StatusTint.NEUTRAL
        WorkShopObjectionStatus.APPROVED -> StatusTint.POSITIVE
        WorkShopObjectionStatus.CALCULATION_REVIEW -> StatusTint.NEGATIVE
        WorkShopObjectionStatus.BOARD_REVIEW -> StatusTint.INFO
        WorkShopObjectionStatus.RECALCULATED -> StatusTint.WARNING
    }
