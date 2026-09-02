package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoPR
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.model.workshop.DebitReasonDN
import com.tamin.taminhamrah.model.workshop.DebitReasonPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractPR
import com.tamin.taminhamrah.model.workshop.LegalRepresentativePR
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopPR
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetPR
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryPR
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDN
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocPR
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberPR
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toJalaliDateLabel

/**
 * Domain → presentation for کارگاه‌های کارفرما.
 *
 * This is the only place workshop data is formatted: Jalali dates get their separators, numbers
 * become Persian digits, amounts gain their «ریال», and anything the service left out becomes the
 * design's dash. Doing it here rather than in a composable is what keeps a card free of
 * per-recomposition string work — and what lets a preview call the same functions the screen does.
 */

// ------------------------------------------------------------------ کارگاه‌های کارفرما

fun EmployerAgreementDN.toPresentation(): WorkshopPR = with(workshop) {
    WorkshopPR(
        workshopId = workshopId,
        branchCode = branchCode,
        hasIdentity = hasIdentity,
        name = name.orDash(),
        codeLabel = workshopId.orDashDigits(),
        status = WorkshopActivityStatus.fromCode(statusCode),
        statusLabel = statusDescription.orDash(),
        employerType = characterDescription.orDash(),
        startDate = this@toPresentation.startDate.orDashDate(),
        activityType = workshopTypeDescription.orDash(),
        branchOfficeCode = branchOfficeCode.orDashDigits(),
        branchOfficeName = branchOfficeName.orDash(),
        registerDate = registerDate.orDashDate(),
        approveDate = approveDate.orDashDate(),
    )
}

// ------------------------------------------------------------------------ ردیف‌های پیمان

/**
 * A تعهدنامه‌دار row: seven fields, from the agreement envelope and the workshop nested in it.
 *
 * ردیف پیمان comes from the agreement's own `pymseq`, *not* from the workshop's `contractRow` —
 * the two are different columns and the نام کارگاه on this card belongs to the workshop while the
 * ردیف belongs to the agreement.
 */
fun EmployerAgreementDN.toContractRow(): ContractRowPR = ContractRowPR(
    workshopId = workshop.workshopId,
    branchCode = workshop.branchCode,
    name = workshop.name.orDash(),
    rowLabel = contractRow.orDashDigits(),
    workshopCodeLabel = workshop.workshopId.orDashDigits(),
    commitmentDate = startDate.orDashDate(),
    mobile = mobile.orDashDigits(),
    email = email.orDash(),
    // Blank, not dashed: the card drops the tile entirely rather than drawing a dash across it.
    address = workshop.address,
)

/** A بدون تعهدنامه row: the same card with the contact block absent, because the data is. */
fun WorkshopContractDN.toContractRow(): ContractRowPR = ContractRowPR(
    workshopId = workshopId,
    branchCode = branchCode,
    name = workshopName.orDash(),
    rowLabel = contractRow.orDashDigits(),
    workshopCodeLabel = workshopId.orDashDigits(),
    commitmentDate = startDate.orDashDate(),
)

// -------------------------------------------------------------------------- برگ پرداخت‌ها

fun PaymentSheetDN.toPresentation(): PaymentSheetPR = PaymentSheetPR(
    debitNumber = debitNumber.orDashDigits(),
    agreementRow = agreementRow.orDashDigits(),
    amount = amount.orDashAmount(),
    collectDate = collectDate.orDashTimestamp(),
    issueDate = docDate.orDashTimestamp(),
    status = status,
    statusLabel = statusDescription.orDash(),
    debitReason = debitCreateReasonDescription.orDash(),
    payKind = payKindDescription.orDash(),
    documentNumber = documentNumber.orDashDigits(),
)

fun DebitReasonDN.toPresentation(): DebitReasonPR = DebitReasonPR(
    code = code,
    title = title.orDash(),
)

// ------------------------------------------------------- بدهی کارگاه / اعتراض

fun WorkShopDebtDN.toPresentation(): WorkShopDebtPR = WorkShopDebtPR(
    debitNumber = debitNumber,
    debitNumberLabel = debitNumber.orDashDigits(),
    agreementRow = agreementRow.orDashDigits(),
    notifyDate = orderRecipeDate.orDashDate(),
    customerCode = customerCode.orDashDigits(),
    amount = debitAmount.orDashAmount(),
    remainingAmount = debitRemain.orDashAmount(),
    fromDate = debitStartDate.orDashDate(),
    toDate = debitEndDate.orDashDate(),
    hasPrimaryVote = hasPrimaryVote,
    primaryVoteNumber = primaryVoteNumber.orDashDigits(),
    primaryVoteDate = primaryVoteDate.orDashDate(),
    objectionKind = objectionKind,
    objectionSeqNo = seqNo,
)

fun WorkshopDemandDocDN.toPresentation(): WorkshopDemandDocPR = WorkshopDemandDocPR(
    docNumber = docNumber,
    docNumberLabel = docNumber.orDashDigits(),
    docDate = docDate.orDashDate(),
    docType = docTypeDescription.orDash(),
    step = debitStepDescription.orDash(),
    state = debitStateDescription.orDash(),
    isViewable = isViewable,
)

fun WorkshopDebtInquiryDN.toPresentation(): WorkshopDebtInquiryPR = WorkshopDebtInquiryPR(
    result = result.orDash(),
    date = date.orDashDate(),
    definitiveDebt = definitiveDebt.orDashAmount(),
    divisibleDebt = divisibleDebt.orDashAmount(),
    indivisibleDebt = indivisibleDebt.orDashAmount(),
)

// ---------------------------------------------- نام نویسی غیر حضوری بیمه شده

fun WorkshopNewMemberDN.toPresentation(): WorkshopNewMemberPR = WorkshopNewMemberPR(
    personalId = personalId,
    requestId = request?.requestId,
    fullName = fullName.orDash(),
    nationalId = nationalId.orDashDigits(),
    birthDate = birthDate.orDashTimestamp(),
    insuranceNumber = insuranceNumber.orDashDigits(),
    registerDate = request?.creationTime.orDashTimestamp(),
    referenceCode = request?.referenceCode.orEmpty(),
    statusLabel = request?.statusDescription.orDash(),
    isDraft = isDraft,
    canConfirm = canConfirm,
    // Raw, for re-opening the draft: the form edits the names apart and re-seeds its pickers from
    // the codes, none of which survives formatting for display.
    firstName = firstName,
    lastName = lastName,
    cityOfBirthId = cityOfBirthId.orEmpty(),
    cityOfIssueId = cityOfIssueId.orEmpty(),
    jobCode = job,
    startDate = startDate.orDashTimestamp(),
)

// ------------------------------------------------------------------- ماده ۱۶

fun WorkshopsDebtListModelDN.toPresentation(): ArticleSixteenDebtPR = ArticleSixteenDebtPR(
    debitNumber = debitNumber,
    debitNumberLabel = debitNumber.orDashDigits(),
    amount = debitAmount.orDashAmount(),
    remainingAmount = debitRemain.orDashAmount(),
    fromDate = debitStartDate.orDashDate(),
    toDate = debitEndDate.orDashDate(),
    agreementRow = agreementRow.orDashDigits(),
    executiveNotifyDate = executiveNotifyDate,
    executiveNotifyDateLabel = executiveNotifyDate.orDashDate(),
    status = status,
    seqNo = seqNo,
)

fun ArticleSixteenWorkshopInfoDN.toPresentation(): ArticleSixteenWorkshopInfoPR = ArticleSixteenWorkshopInfoPR(
    workshopId = workshopId.orDashDigits(),
    workshopName = workshopName.orDash(),
    employerName = employerName.orDash(),
    character = character.orDash(),
    address = address.orDash(),
)

// --------------------------------------------------------- کارکنان / ذینفعان

fun WorkshopMemberDN.toPresentation(): WorkshopMemberPR = WorkshopMemberPR(
    insuranceNumber = insuranceNumber.orDashDigits(),
    fullName = fullName.orDash(),
    nationalId = nationalId.orDashDigits(),
    idCardNumber = idCardNumber.orDashDigits(),
    fatherName = fatherName.orDash(),
    nationality = nationDescription.orDash(),
    relationType = relationTypeDescription.orDash(),
    leavingWorkStatus = leavingWorkStatus.orDash(),
    leavingWorkDate = leavingWorkDate.orDashDate(),
    isEmployed = leavingWorkDate == null,
)

fun WorkshopStackHolderDN.toPresentation(): WorkshopStackHolderPR = WorkshopStackHolderPR(
    nationalId = nationalId.orDashDigits(),
    fullName = fullName.orDash(),
    fatherName = fatherName.orDash(),
    birthDate = birthDate.orDashTimestamp(),
    stackType = stackType.orDash(),
)

// ------------------------------------------------------------------ formatting

/** Digits the user reads are Persian; a value the service omitted is the design's dash. */
private fun String.orDashDigits(): String = ifBlank { null }?.toPersianDigits().orDash()

/** Compact Jalali (`14050131`) renders as `۱۴۰۵/۰۱/۳۱`; an absent date is a dash. */
private fun String.orDashDate(): String = ifBlank { null }?.toJalaliDateLabel().orDash()

/** Epoch millis render as a Jalali date; the formatter returns blank for null, so it dashes. */
private fun Long?.orDashTimestamp(): String = PersianDateFormatter.formatTimestamp(this).orDash()

/**
 * An amount reads as grouped Persian digits with «ریال».
 *
 * A missing amount dashes rather than printing ۰ — a debt the service did not report and a debt of
 * zero are different answers, and the dash is the one that does not claim a figure.
 */
private fun Long?.orDashAmount(): String = this?.let { "${it.toPriceFormat()} ریال" }.orDash()

fun LegalRepresentativeWorkshopDN.toPresentation(): LegalRepresentativeWorkshopPR {
    return LegalRepresentativeWorkshopPR(
        workshopId = workshopId,
        branchCode = branchCode,
        workshopName = workshopName ?: "",
        branchName = branchName,
        special = special,
        representativeCount = representativeCount,
    )
}

fun LegalRepresentativeDN.toPresentation(contractRows: List<String> = emptyList()): LegalRepresentativePR {
    return LegalRepresentativePR(
        stakeId = stakeId,
        nationalId = nationalId,
        mobile = mobile,
        fullName = fullName,
        hasElectronicNotification = hasElectronicNotification,
        hasInternetList = hasInternetList,
        hasInsuredRegistration = hasInsuredRegistration,
        startDateLabel = PersianDateFormatter.formatTimestamp(startDate),
        workshopId = workshopId,
        branchCode = branchCode,
        special = special,
        contractRows = contractRows,
    )
}

fun LegalRepresentativeContractDN.toPresentation(): LegalRepresentativeContractPR {
    return LegalRepresentativeContractPR(
        contractRow = contractRow,
        title = title,
    )
}
