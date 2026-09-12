package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components

import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.objection_state_approved
import taminx.core.core_ui.objection_state_board_review
import taminx.core.core_ui.objection_state_calculation_review
import taminx.core.core_ui.objection_state_recalculated
import taminx.core.core_ui.objection_state_submitted
import taminx.core.core_ui.objection_state_time_allocated
import taminx.core.core_ui.objection_state_unknown
import taminx.core.core_ui.objection_type_article_sixteen
import taminx.core.core_ui.objection_type_estimate
import taminx.core.core_ui.objection_type_primary_vote
import taminx.core.core_ui.objection_type_unknown

/**
 * String resource for پیگیری وضعیت اعتراض's two code-driven enums — the service sends a
 * status/type *code*, not a label, so the text lives in the UI layer rather than the PR, matching
 * `PaymentSheetStatus.labelRes` in `PaymentSheetsScreen.kt`.
 */
internal val WorkShopObjectionStatus.labelRes: StringResource
    get() = when (this) {
        WorkShopObjectionStatus.SUBMITTED -> Res.string.objection_state_submitted
        WorkShopObjectionStatus.CALCULATION_REVIEW -> Res.string.objection_state_calculation_review
        WorkShopObjectionStatus.BOARD_REVIEW -> Res.string.objection_state_board_review
        WorkShopObjectionStatus.RECALCULATED -> Res.string.objection_state_recalculated
        WorkShopObjectionStatus.TIME_ALLOCATED -> Res.string.objection_state_time_allocated
        WorkShopObjectionStatus.APPROVED -> Res.string.objection_state_approved
        WorkShopObjectionStatus.UNKNOWN -> Res.string.objection_state_unknown
    }

internal val WorkShopObjectionType.labelRes: StringResource
    get() = when (this) {
        WorkShopObjectionType.ESTIMATE -> Res.string.objection_type_estimate
        WorkShopObjectionType.PRIMARY_VOTE -> Res.string.objection_type_primary_vote
        WorkShopObjectionType.ARTICLE_SIXTEEN -> Res.string.objection_type_article_sixteen
        WorkShopObjectionType.UNKNOWN -> Res.string.objection_type_unknown
    }
