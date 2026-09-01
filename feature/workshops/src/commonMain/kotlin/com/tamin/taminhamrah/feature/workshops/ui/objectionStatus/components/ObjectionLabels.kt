package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components

import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType

/**
 * Display text for پیگیری وضعیت اعتراض's two code-driven enums.
 *
 * Purely code-driven — the service sends a status/type *code*, not a label — so, like
 * `PaymentSheetStatus.labelRes` in `PaymentSheetsScreen.kt`, the text lives in the UI layer rather
 * than the PR. Plain strings here rather than `stringResource` because both the list card and the
 * summary header (list, sms, document packages) need them outside any one screen's composable.
 */
internal fun WorkShopObjectionStatus.label(): String = when (this) {
    WorkShopObjectionStatus.SUBMITTED -> "ثبت درخواست"
    WorkShopObjectionStatus.CALCULATION_REVIEW -> "بازنگری محاسبات"
    WorkShopObjectionStatus.BOARD_REVIEW -> "طرح در هیئت"
    WorkShopObjectionStatus.RECALCULATED -> "تجدید محاسبه شده"
    WorkShopObjectionStatus.TIME_ALLOCATED -> "تخصیص زمان"
    WorkShopObjectionStatus.APPROVED -> "تایید رای"
    WorkShopObjectionStatus.UNKNOWN -> "نامشخص"
}

internal fun WorkShopObjectionType.label(): String = when (this) {
    WorkShopObjectionType.ESTIMATE -> "اعتراض به بدهی برآوردی"
    WorkShopObjectionType.PRIMARY_VOTE -> "اعتراض به رای هیئت بدوی"
    WorkShopObjectionType.ARTICLE_SIXTEEN -> "درخواست رسیدگی به بدهی قطعی (مادهٔ ۱۶)"
    WorkShopObjectionType.UNKNOWN -> "نوع نامشخص"
}
