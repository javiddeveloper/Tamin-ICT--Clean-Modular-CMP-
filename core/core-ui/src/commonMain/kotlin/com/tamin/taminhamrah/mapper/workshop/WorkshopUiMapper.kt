package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.model.workshop.AssignerPartyPR
import com.tamin.taminhamrah.model.workshop.BaseDocumentCategory
import com.tamin.taminhamrah.model.workshop.BaseDocumentDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentPR
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDN
import com.tamin.taminhamrah.model.workshop.ComputationalBasePR
import com.tamin.taminhamrah.model.workshop.ComputationalBaseStatus
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoPR
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.model.workshop.DebitReasonDN
import com.tamin.taminhamrah.model.workshop.DebitReasonPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerContactInfoDN
import com.tamin.taminhamrah.model.workshop.EmployerContactInfoPR
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
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowPR
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.StakeHolderRole
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractPR
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionPR
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDN
import com.tamin.taminhamrah.model.workshop.SettlementCertificatePR
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDN
import com.tamin.taminhamrah.model.workshop.SmsMessageDN
import com.tamin.taminhamrah.model.workshop.SmsMessagePR
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminOptionSheetItem
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toJalaliDateLabel
import kotlinx.collections.immutable.toImmutableList

import taminx.core.core_ui.Res
import taminx.core.core_ui.article_42
import taminx.core.core_ui.article_43
import taminx.core.core_ui.article_44

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
        // Raw, like the two identity fields above: these travel into a request body, where a
        // dash or a Persian digit would be wrong.
        characterCode = characterCode,
        legalNationalId = legalNationalId,
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

// ---------------------------------------------------------------------------- واگذارندگان

/**
 * One پیمان, and the card the list draws it as.
 *
 * The card is built from the **پیمانکار** side, never the واگذارنده: the واگذارنده is the
 * signed-in employer themselves, so a card built from it would show the user their own workshop
 * on every row.
 *
 * [ContractRowPR.mobile] and [ContractRowPR.email] stay blank rather than dashed. This endpoint
 * sends no contact columns at all — dashing them would draw two permanent «—» tiles and claim the
 * service answered "none", when it was never asked. The card drops the whole row when both are
 * blank, which is what the design does too.
 */
fun AssignerContractDN.toPresentation(): AssignerContractPR = AssignerContractPR(
    card = ContractRowPR(
        workshopId = employer.workshopId,
        branchCode = employer.branchCode,
        name = employer.workshopName.orDash(),
        rowLabel = contractRow.orDashDigits(),
        workshopCodeLabel = employer.workshopId.orDashDigits(),
        commitmentDate = contractDate.orDashDate(),
        // Blank, not dashed — the card drops the tile rather than drawing a dash across it.
        address = employer.address,
    ),
    // Raw ASCII: these two are query values, not labels. Persian digits here would address a
    // contract the service has never heard of.
    contractRow = contractRow,
    contractSequence = contractSequence,
    branchCode = branchCode,
    contractNumber = contractNumber.orDashDigits(),
    rawContractNumber = contractNumber,
    sequenceLabel = contractSequence.orDashDigits(),
    contractDate = contractDate.orDashDate(),
    contractSubject = contractSubject.orDash(),
    // Ended only once its end date is behind today. A blank or unparseable date reads as still in
    // force: a پیمان is shown as ended only when the service says when it ended.
    isFinished = (PersianDateFormatter.daysSince(contractEndDate) ?: 0) > 0,
    assigner = assigner.toPresentation(),
    employer = employer.toPresentation(),
)

fun AssignerPartyDN.toPresentation(): AssignerPartyPR = AssignerPartyPR(
    workshopName = workshopName.orDash(),
    workshopCode = workshopId.orDashDigits(),
    nationalId = nationalId.orDashDigits(),
    branchName = branchName.orDash(),
    address = address.orDash(),
)

/**
 * A موضوع کار as the picker lists it. The sheet's own option row is the whole of what it needs — a
 * code to file under and the wording to show — so no model of its own is added for it.
 */
fun SettlementSubjectDN.toPresentation(): TaminOptionSheetItem = TaminOptionSheetItem(
    id = code,
    label = description.orDash(),
)

fun SettlementCertificateDN.toPresentation(): SettlementCertificatePR = SettlementCertificatePR(
    number = number.orDashDigits(),
    date = date.orDashDate(),
)

fun ComputationalBaseDN.toPresentation(): ComputationalBasePR = ComputationalBasePR(
    letterNumber = letterNumber.orDashDigits(),
    sendDate = sendDate.orDashTimestamp(),
    amount = amount.orDashAmount(),
    amountRials = amount,
    // Blank rather than dashed: a base with no period drops that line instead of printing «— تا —».
    periodStart = PersianDateFormatter.formatTimestamp(startDate),
    periodEnd = PersianDateFormatter.formatTimestamp(endDate),
    // Zero is a real answer here and prints as ۰, unlike an amount: the row says «۰ سند» and the
    // detail screen's documents section is then legitimately empty.
    documentCount = documents.size.toString().toPersianDigits(),
    documents = documents.map { it.toPresentation() }.toImmutableList(),
    status = ComputationalBaseStatus.fromCode(statusCode),
    finalOrderNumber = finalOrderNumber.orDashDigits(),
    estimatedOrderNumber = estimatedOrderNumber.orDashDigits(),
)

fun BaseDocumentDN.toPresentation(): BaseDocumentPR = BaseDocumentPR(
    documentId = documentId,
    kind = kind,
    category = BaseDocumentCategory.fromCode(categoryCode),
)

// ---------------------------------------------- خدمات غیرحضوری کارفرما (employerEservicesAgreement)

fun EmployerContactInfoDN.toPresentation(): EmployerContactInfoPR = EmployerContactInfoPR(
    fullName = fullName.orDash(),
    nationalCode = nationalCode.orDashDigits(),
    currentMobile = currentMobile.orDashDigits(),
    currentEmail = currentEmail.orDash(),
)

fun WorkshopWithoutContractDN.toPresentation(): WorkshopWithoutContractPR = WorkshopWithoutContractPR(
    workshopId = workshopId,
    branchCode = branchCode,
    hasIdentity = hasIdentity,
    name = name.orDash(),
    codeLabel = workshopId.orDashDigits(),
    nationalId = nationalId.orDashDigits(),
    postalCode = postalCode.orDashDigits(),
    tel = tel.orDashDigits(),
    address = address.orDash(),
    branchOfficeName = branchOfficeName.orDash(),
)

fun WorkshopContractRowDN.toPresentation(): WorkshopContractRowPR = WorkshopContractRowPR(
    contractRow = contractRow.orDashDigits(),
    fullName = fullName.orDash(),
    nationalCode = nationalCode.orDashDigits(),
    mobile = mobile.orDashDigits(),
    email = email.orDash(),
    tel = tel.orDashDigits(),
    postalCode = postalCode.orDashDigits(),
    startDate = startDate.orDashDate(),
    endDate = endDate.orDashDate(),
    workshopName = workshop.name.orDash(),
    workshopCodeLabel = workshop.workshopId.orDashDigits(),
)

fun EmployerAgreementByWorkshopDN.toPresentation(): EmployerAgreementByWorkshopPR =
    EmployerAgreementByWorkshopPR(
        workshopId = workshop.workshopId,
        branchCode = workshop.branchCode,
        paymentSequence = paymentSequence.orDashDigits(),
        workshopName = workshop.name.orDash(),
        workshopCodeLabel = workshop.workshopId.orDashDigits(),
        address = workshop.address.orDash(),
        startDate = startDate.orDashDate(),
        commitmentDate = commitmentDate.orDashDate(),
        mobile = mobile.orDashDigits(),
        email = email.orDash(),
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
    agreementRow = agreementRow,
    agreementRowLabel = agreementRow.orDashDigits(),
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
    docType = docTypeDescription.orDashProse(),
    step = debitStepDescription.orDashProse(),
    state = debitStateDescription.orDashProse(),
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
    proceedingType = when (kindDoc.trim()) {
        "1" -> Res.string.article_42
        "2" -> Res.string.article_43
        "3" -> Res.string.article_44
        else -> null
    },
)

fun ArticleSixteenWorkshopInfoDN.toPresentation(): ArticleSixteenWorkshopInfoPR = ArticleSixteenWorkshopInfoPR(
    workshopId = workshopId.orDashDigits(),
    workshopName = workshopName.orDash(),
    branchCode = branchCode.orDashDigits(),
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
    // Blank, not null: the service sends `leavingWorkDate` nullable but the domain model
    // flattens it with `orEmpty()`, so a null-check here is always false and marked every
    // member as having left.
    isEmployed = leavingWorkDate.isBlank(),
)

fun WorkshopStackHolderDN.toPresentation(): WorkshopStackHolderPR = WorkshopStackHolderPR(
    nationalId = nationalId.orDashDigits(),
    fullName = fullName.orDash(),
    fatherName = fatherName.orDash(),
    birthDate = birthDate.orDashTimestamp(),
    role = StakeHolderRole.fromCode(stackType),
    stackType = stackType.orDash(),
)

fun WorkShopObjectionDN.toPresentation(): WorkShopObjectionPR = WorkShopObjectionPR(
    seqNo = seqNo,
    workshopId = workshopId.orDashDigits(),
    debitNumber = debitNumber.orDashDigits(),
    objectionNumber = (seqNo?.toString() ?: "").orDashDigits(),
    objectionDate = objectionDate.orDashDate(),
    objectionDescription = objectionDescription.orDash(),
    voteTypeDescription = voteTypeDescription.orDash(),
    objectionType = objectionType,
    status = status,
)

fun SmsMessageDN.toPresentation(): SmsMessagePR = SmsMessagePR(
    id = id,
    description = description.orDash(),
    status = status,
)

// ------------------------------------------------------------------ formatting

/** Digits the user reads are Persian; a value the service omitted is the design's dash. */
/**
 * A description as the reader expects to see it, or a dash.
 *
 * Service descriptions arrive with round brackets — «محاسبه (اعلام نشده)» — and a bracket is
 * bidi-neutral: in a right-to-left run it is drawn with its mirror glyph, so the one the service
 * opens with reaches the screen as a closing bracket and the value reads «محاسبه )اعلام نشده(».
 * Swapping the pair cancels that. It is the reordering-safe half of the problem: both brackets are
 * bidi class ON and both mirror, so each keeps the position the algorithm gives it and only the
 * glyph changes.
 *
 * Stated here, at the presentation edge, so the domain keeps the service's own spelling.
 */
private fun String.orDashProse(): String = ifBlank { null }?.swapBrackets().orDash()

private fun String.swapBrackets(): String = map { character ->
    when (character) {
        '(' -> ')'
        ')' -> '('
        else -> character
    }
}.joinToString("")

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

/**
 * «جمع کارکرد اعلامی» — the bases' amounts added up, printed the way each row prints its own.
 *
 * A base the service sent no amount for adds nothing; when none of them has one the total is a gap,
 * not ۰ ریال.
 */
fun List<ComputationalBasePR>.declaredTotal(): String =
    mapNotNull { it.amountRials }.takeIf { it.isNotEmpty() }?.sum().orDashAmount()

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
