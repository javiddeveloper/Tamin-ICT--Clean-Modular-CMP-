package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopDN
import com.tamin.taminhamrah.model.workshop.RelationWithTaminDTO
import com.tamin.taminhamrah.model.workshop.PersonalRegistrationDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.AssignerContractDTO
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerPartyDTO
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDTO
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDN
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDocumentDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.ArticleSixteenPhotoDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestStatus
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionRequestDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionResultDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveResultDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitReasonDN
import com.tamin.taminhamrah.model.workshop.DebitReasonDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmissionDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmitRequestDTO
import com.tamin.taminhamrah.model.workshop.EmployerCommitmentInfoDTO
import com.tamin.taminhamrah.model.workshop.EmployerContactInfoDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRequestDN
import com.tamin.taminhamrah.model.workshop.NewMemberRequestDTO
import com.tamin.taminhamrah.model.workshop.ObjectionDocumentDN
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.model.workshop.ObjectionPhotoDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetStatus
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.SmsMessageDN
import com.tamin.taminhamrah.model.workshop.SmsMessageDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDN
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
import com.tamin.taminhamrah.model.workshop.withTypeSlots

/**
 * Wire → domain for everything under کارگاه‌های کارفرما.
 *
 * Absent text collapses to `""` here, once, so no screen repeats `?: ""`; absent *amounts* stay
 * null, because a debt of zero and a debt the service did not report are different things.
 */

/** Wraps one page of rows with the server's grand total, so callers can page without guessing. */
fun <D, T> ListData<D>.toDomainPage(map: (D) -> T): PagedListDN<T> = PagedListDN(
    items = list?.map(map).orEmpty(),
    total = total,
)

// ------------------------------------------------------------------ کارگاه‌های کارفرما

fun EmployerAgreementDTO.toDomain(): EmployerAgreementDN = EmployerAgreementDN(
    contractRow = contractRow.orEmpty(),
    startDate = startDate.orEmpty(),
    commitmentDate = commitmentDate.orEmpty(),
    email = email.orEmpty(),
    mobile = mobile.orEmpty(),
    workshop = workshop?.toDomain() ?: WorkshopSummaryDN(),
)

fun EmployerWorkshopDTO.toDomain(): WorkshopSummaryDN = WorkshopSummaryDN(
    workshopId = workshopId.orEmpty(),
    branchCode = branchCode.orEmpty(),
    name = workshopName.orEmpty(),
    employerName = employerName.orEmpty(),
    activityName = activityName.orEmpty(),
    address = lastAddress.orEmpty(),
    registerDate = workshopRegisterDate.orEmpty(),
    approveDate = workshopApproveDate.orEmpty(),
    contractRow = contractRow.orEmpty(),
    // The nested `branch` object is absent on the employer-agreement service, which names the
    // same office flat; without the fallback the card's شعبه cell reads "-".
    branchOfficeCode = branch?.code ?: brhCode.orEmpty(),
    branchOfficeName = branch?.organizationName ?: branchTitle.orEmpty(),
    characterCode = character?.characterCode.orEmpty(),
    legalNationalId = legalWorkshop?.nationalId.orEmpty(),
    characterDescription = character?.characterDesc.orEmpty(),
    workshopTypeDescription = workshopType?.workshopTypeDesc.orEmpty(),
    statusCode = workshopStatus?.workshopStatusCode.orEmpty(),
    statusDescription = workshopStatus?.workshopStatusDesc.orEmpty(),
    branchTitle = branchTitle.orEmpty()
)

// ---------------------------------------------- خدمات غیرحضوری کارفرما (employerServicesAgreement)

fun EmployerCommitmentInfoDTO.toDomain(): EmployerContactInfoDN = EmployerContactInfoDN(
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    nationalCode = nationalCode.orEmpty(),
    currentMobile = mobile.orEmpty(),
    currentEmail = email.orEmpty(),
)

fun WorkshopWithoutContractDTO.toDomain(): WorkshopWithoutContractDN = WorkshopWithoutContractDN(
    workshopId = workshopId.orEmpty(),
    branchCode = branchCode.orEmpty(),
    name = workshopName.orEmpty(),
    nationalId = nationalId.orEmpty(),
    postalCode = postalCode.orEmpty(),
    tel = tel.orEmpty(),
    address = address.orEmpty(),
    branchOfficeName = organization?.organizationName.orEmpty(),
    branchOfficeCode = organization?.code.orEmpty(),
)

fun WorkshopContractRowDTO.toDomain(): WorkshopContractRowDN = WorkshopContractRowDN(
    contractRow = contractRow.orEmpty(),
    startDate = startDate.orEmpty(),
    endDate = endDate.orEmpty(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    mobile = mobile.orEmpty(),
    email = email.orEmpty(),
    nationalCode = nationalCode.orEmpty(),
    tel = tel.orEmpty(),
    postalCode = postalCode.orEmpty(),
    workshop = workshop?.toDomain() ?: WorkshopSummaryDN(),
)

fun EmployerAgreementByWorkshopDTO.toDomain(): EmployerAgreementByWorkshopDN = EmployerAgreementByWorkshopDN(
    paymentSequence = paymentSequence.orEmpty(),
    startDate = startDate.orEmpty(),
    commitmentDate = commitmentDate.orEmpty(),
    email = email.orEmpty(),
    mobile = mobile.orEmpty(),
    workshop = workshop?.toDomain() ?: WorkshopSummaryDN(),
)

fun EmployerAgreementSubmissionDN.toDto(): EmployerAgreementSubmitRequestDTO =
    EmployerAgreementSubmitRequestDTO(
        mobile = mobile,
        email = email,
        ticketCode = ticketCode,
    )

// ------------------------------------------------------------------------ ردیف‌های پیمان

/**
 * The lean contract row flattens its nested workshop, because the card reads three fields from it
 * and nothing downstream needs the object.
 */
fun WorkshopContractDTO.toDomain(): WorkshopContractDN = WorkshopContractDN(
    contractRow = contractRow.orEmpty(),
    startDate = startDate.orEmpty(),
    workshopId = workshop?.workshopId.orEmpty(),
    branchCode = workshop?.branchCode.orEmpty(),
    workshopName = workshop?.workshopName.orEmpty(),
)

// ---------------------------------------------------------------------------- واگذارندگان

fun AssignerContractDTO.toDomain(): AssignerContractDN = AssignerContractDN(
    contractRow = contractRow.orEmpty(),
    contractSequence = contractSequence.orEmpty(),
    contractNumber = contractNumber.orEmpty(),
    contractDate = contractDate.orEmpty(),
    contractSubject = contractSubject.orEmpty(),
    // Two sides, never folded together: `assigner` is the signed-in employer's own کارگاه and
    // `employer` is the پیمانکار. Swapping them puts the user's own workshop on every card and
    // sends the bases call looking for the wrong contract.
    assigner = assigner?.toDomain() ?: AssignerPartyDN(),
    employer = employer?.toDomain() ?: AssignerPartyDN(),
)

fun AssignerPartyDTO.toDomain(): AssignerPartyDN = AssignerPartyDN(
    workshopId = workshopId.orEmpty(),
    workshopName = workshopName.orEmpty(),
    nationalId = nationalId.orEmpty(),
    address = address.orEmpty(),
    branchCode = branch?.code.orEmpty(),
    branchName = branch?.organizationName.orEmpty(),
)

fun ComputationalBaseDTO.toDomain(): ComputationalBaseDN = ComputationalBaseDN(
    letterNumber = letterNumber.orEmpty(),
    sendDate = sendDate,
    amount = amount,
    documents = documents.orEmpty().map { it.toDomain() },
)

fun ComputationalBaseDocumentDTO.toDomain(): BaseDocumentDN = BaseDocumentDN(
    documentId = documentId.orEmpty(),
    kind = BaseDocumentKind.fromType(documentType),
    categoryCode = documentCode.orEmpty(),
)

// ------------------------------------------------------------------------ برگ پرداخت‌ها

fun PaymentSheetDTO.toDomain(): PaymentSheetDN = PaymentSheetDN(
    debitNumber = debitNumber.orEmpty(),
    agreementRow = agreementRow.orEmpty(),
    payId = payId.orEmpty(),
    amount = amount,
    docDate = docDate,
    collectDate = cardDate,
    status = PaymentSheetStatus.fromCode(statusCode),
    statusDescription = statusDesc.orEmpty(),
    debitCreateReasonDescription = debitCreateReasonDesc.orEmpty(),
    payKindDescription = payKindDesc.orEmpty(),
    documentNumber = documentNumber.orEmpty(),
)

fun DebitReasonDTO.toDomain(): DebitReasonDN = DebitReasonDN(
    code = code.orEmpty(),
    title = title.orEmpty(),
)

// ------------------------------------------------------- بدهی کارگاه / اعتراض

fun WorkShopDebtDTO.toDomain(): WorkShopDebtDN = WorkShopDebtDN(
    debitNumber = debitNumber.orEmpty(),
    orderRecipeDate = orderRecipeDate.orEmpty(),
    customerCode = mastCustomerCode.orEmpty(),
    customerTypeCode = mastCustomerTypeCode.orEmpty(),
    agreementRow = peymanSequence.orEmpty(),
    debitAmount = debitAmount,
    debitRemain = debitRemain,
    debitStartDate = debitStartDate.orEmpty(),
    debitEndDate = debitEndDate.orEmpty(),
    debitStepCode = debitStepCode.orEmpty(),
    debitStatCode = debitStatCode.orEmpty(),
    debitCreateReasonDescription = debitCreateReasonDesc.orEmpty(),
    primaryVoteNumber = primaryVoteNumber.orEmpty(),
    primaryVoteDate = primaryVoteDate.orEmpty(),
    executiveNotifyDate = executiveNotifyDate.orEmpty(),
    seqNo = seqNo,
)

fun WorkshopDemandDocDTO.toDomain(): WorkshopDemandDocDN = WorkshopDemandDocDN(
    docNumber = docNumber.orEmpty(),
    docDate = docDate.orEmpty(),
    docTypeDescription = docTypeDescription.orEmpty(),
    debitStepDescription = debitStepDesc.orEmpty(),
    debitStateDescription = debitStateDesc.orEmpty(),
)

/**
 * The three amounts arrive as strings that are not guaranteed numeric — the service answers some
 * inquiries with a sentence instead of a figure. `toLongOrNull` keeps that case as "no amount"
 * rather than throwing inside the mapper, which is what the old client did.
 */
fun WorkshopDebtInquiryDTO.toDomain(): WorkshopDebtInquiryDN = WorkshopDebtInquiryDN(
    result = result.orEmpty(),
    date = date.orEmpty(),
    definitiveDebt = definitiveDebt?.trim()?.toLongOrNull(),
    divisibleDebt = divisibleDebt?.trim()?.toLongOrNull(),
    indivisibleDebt = indivisibleDebt?.trim()?.toLongOrNull(),
)

fun DebitPaymentPreCheckDTO.toDomain(): DebitPaymentPreCheckDN = DebitPaymentPreCheckDN(
    allowed = functionResult == PAYMENT_ALLOWED,
    days = days.orEmpty(),
)

private const val PAYMENT_ALLOWED = "1"

/**
 * The service answers with a ticket and a URL, and the payment page is addressed by ticket alone.
 * When the ticket field is empty the ticket is the last segment of the URL — the same fallback the
 * old client used, and the reason a payment still worked when only one of the two arrived.
 */
//private const val TFH_PAYMENT_PAGE = "https://tfh.tamin.ir/view/#/payment/"
//
//fun DebitPaymentDTO.toDomain(): DebitPaymentDN {
//    val ticket = paymentTicket?.takeIf { it.isNotBlank() }
//        ?: paymentUrl?.trimEnd('/')?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
//    return DebitPaymentDN(
//        succeeded = succeed == true,
//        message = responseMessage.orEmpty(),
//        paymentPageUrl = ticket?.let { TFH_PAYMENT_PAGE + it }.orEmpty(),
//        ticket = ticket.orEmpty(),
//    )
//}
fun DebitPaymentDTO.toDomain(): DebitPaymentDN = DebitPaymentDN(
    succeeded = succeed == true,
    message = responseMessage.orEmpty(),
    paymentTicket = paymentTicket?.takeIf { it.isNotBlank() }
        ?: paymentUrl?.trimEnd('/')?.substringAfterLast('/').orEmpty(),
)

fun DebitPaymentRequestDN.toDto(): DebitPaymentRequestDTO = DebitPaymentRequestDTO(
    branchCode = branchCode,
    workshopId = workshopId,
    debitNumber = debitNumber,
    // Empty rather than absent when the debt has none: the old client coalesces the service's
    // null before building its request, so the key is always on the wire.
    agreementRow = agreementRow,
    // "1"/"0", not "true"/"false".
    deposit = if (deposit) DEPOSIT_YES else DEPOSIT_NO,
    // Only a حقوقی workshop has one. Blank is not the same as absent here: the service expects
    // the key present and null, so an empty id becomes null rather than "".
    nationalId = legalNationalId.takeIf { characterCode == CHARACTER_LEGAL && it.isNotBlank() },
    nationalType = characterCode,
)

private const val DEPOSIT_YES = "1"
private const val DEPOSIT_NO = "0"

/** `02` حقوقی — the only character that carries a national id of its own. */
private const val CHARACTER_LEGAL = "02"

/**
 * The wire value of `objectionType` on `objection-save` — a Persian label, not a code. The same
 * field on `debit-comitte-save` is the code `"3"` instead; two endpoints, two conventions, so both
 * are spelled out here rather than shared.
 *
 * The old client always sent [ObjectionKind.FILED]'s label ("مشاهده اعتراض"), because it read a
 * constant field that shadowed the computed kind. The kind actually being filed is sent instead.
 */
private fun ObjectionKind.wireLabel(): String = when (this) {
    ObjectionKind.ESTIMATE -> "اعتراض به بدهی برآوردی"
    ObjectionKind.PRIMARY_VOTE -> "اعتراض به رای هیئت بدوی"
    ObjectionKind.FILED -> "مشاهده اعتراض"
}

/** `objectionType` on the ماده ۱۶ request is fixed at `3`, as the e-services site sends it. */
private const val ARTICLE_SIXTEEN_OBJECTION_TYPE = "3"

/**
 * The objection body.
 *
 * `seporde`/`status` are `"1"`/`"0"` strings, and the `typeN` slots are filled from the same
 * document list as `objectionPhotos` so the two cannot disagree.
 */
fun DebitObjectionRequestDN.toDto(): DebitObjectionSaveRequestDTO {
    val photos = documents.map { ObjectionPhotoDTO(guid = it.guid, type = it.typeCode) }
    return DebitObjectionSaveRequestDTO(
        workshopId = workshopId,
        branchCode = branchCode,
        debitNumber = debt.debitNumber,
        debitStepCode = debt.debitStepCode,
        debitStatCode = debt.debitStatCode,
        agreementRow = debt.agreementRow,
        primaryVoteNumber = debt.primaryVoteNumber,
        primaryVoteDate = debt.primaryVoteDate,
        objectionDescription = description,
        objectionType = debt.objectionKind.wireLabel(),
        deposit = deposit.toFlag(),
        status = confirmed.toFlag(),
    ).withTypeSlots(photos)
}

private fun Boolean.toFlag(): String = if (this) "1" else "0"

fun DebitObjectionSaveResultDTO.toDomain(): DebitObjectionResultDN = DebitObjectionResultDN(
    seqNo = seqNo,
    referenceCode = refId.orEmpty(),
)

// ---------------------------------------------- نام نویسی غیر حضوری بیمه شده

fun WorkshopNewMemberDTO.toDomain(): WorkshopNewMemberDN = WorkshopNewMemberDN(
    relationId = id,
    personalId = personal?.id,
    insuranceNumber = insuranceId.orEmpty(),
    nationalId = personal?.nationalId.orEmpty(),
    firstName = personal?.firstName.orEmpty(),
    lastName = personal?.lastName.orEmpty(),
    birthDate = personal?.dateOfBirth,
    cityOfBirthId = personal?.cityOfBirthId,
    cityOfIssueId = personal?.cityOfIssueId,
    startDate = startDate,
    job = job.orEmpty(),
    request = personal?.request?.toDomain(),
)

fun NewMemberRequestDTO.toDomain(): NewMemberRequestDN = NewMemberRequestDN(
    requestId = id,
    creationTime = creationTime,
    referenceCode = refCode.orEmpty(),
    statusCode = status?.requestCode.orEmpty(),
    statusDescription = status?.requestDesc.orEmpty(),
)

// ------------------------------------------------------------------- ماده ۱۶

fun WorkshopsDebtListModelDTO.toDomain(): WorkshopsDebtListModelDN = WorkshopsDebtListModelDN(
    debitNumber = debitNumber.orEmpty(),
    debitAmount = debitAmount,
    debitRemain = debitRemain,
    debitStartDate = debitStartDate.orEmpty(),
    debitEndDate = debitEndDate.orEmpty(),
    debitStepCode = debitStepCode.orEmpty(),
    agreementRow = agreementRow.orEmpty(),
    customerTypeCode = customerTypeCode.orEmpty(),
    insuranceAmount = insuranceAmount,
    unemploymentAmount = unemploymentAmount,
    fineAmount = fineAmount,
    otherAmount = otherAmount,
    kindDoc = kindDoc.orEmpty(),
    executiveNumber = executiveNumber.orEmpty(),
    executiveDate = executiveDate.orEmpty(),
    executiveNotifyDate = executiveNotifyDate.orEmpty(),
    primaryVoteNumber = primaryVoteNumber.orEmpty(),
    primaryVoteDate = primaryVoteDate.orEmpty(),
    seqNo = seqNo,
    status = ArticleSixteenRequestStatus.fromCode(status),
)

fun ArticleSixteenWorkshopInfoDTO.toDomain(): ArticleSixteenWorkshopInfoDN = ArticleSixteenWorkshopInfoDN(
    workshopId = workshopId.orEmpty(),
    workshopName = workshopName.orEmpty(),
    branchCode = branchCode.orEmpty(),
    employerName = employerName.orEmpty(),
    character = character.orEmpty(),
    address = lastAddress.orEmpty(),
)

fun ArticleSixteenRequestInfoDTO.toDomain(): ArticleSixteenRequestInfoDN = ArticleSixteenRequestInfoDN(
    defectDescription = defectDescription.orEmpty(),
    documents = objectionPhotos.mapNotNull { it.toDomainOrNull() },
)

/** A document with no guid cannot be addressed, so it is dropped rather than carried as blank. */
private fun ArticleSixteenPhotoDTO.toDomainOrNull(): ObjectionDocumentDN? =
    guid?.takeIf { it.isNotBlank() }?.let { ObjectionDocumentDN(guid = it, typeCode = type.orEmpty()) }

fun ArticleSixteenSaveRequestDN.toDto(): ArticleSixteenSaveRequestDTO =
    ArticleSixteenSaveRequestDTO(
        workshopId = workshopId,
        branchCode = branchCode,
        debitNumber = debt.debitNumber,
        debitStartDate = debt.debitStartDate,
        debitEndDate = debt.debitEndDate,
        debitStepCode = debt.debitStepCode,
        agreementRow = debt.agreementRow,
        customerTypeCode = debt.customerTypeCode,
        insuranceAmount = debt.insuranceAmount,
        unemploymentAmount = debt.unemploymentAmount,
        fineAmount = debt.fineAmount,
        otherAmount = debt.otherAmount,
        kindDoc = debt.kindDoc,
        orderNumber = debt.executiveNumber,
        orderDate = debt.executiveDate,
        executiveNotifyDate = debt.executiveNotifyDate,
        objectionType = ARTICLE_SIXTEEN_OBJECTION_TYPE,
        objectionPhotos = documents.map { ObjectionPhotoDTO(guid = it.guid, type = it.typeCode) },
    )

fun ArticleSixteenSaveResultDTO.toDomain(): ArticleSixteenSaveResultDN = ArticleSixteenSaveResultDN(
    referenceCode = refId.orEmpty(),
)

// --------------------------------------------------------- کارکنان / ذینفعان

fun WorkshopMemberDTO.toDomain(): WorkshopMemberDN = WorkshopMemberDN(
    insuranceNumber = insurance?.id.orEmpty(),
    firstName = insurance?.firstName.orEmpty(),
    lastName = insurance?.lastName.orEmpty(),
    fatherName = insurance?.fatherName.orEmpty(),
    nationalId = insurance?.nationalId.orEmpty(),
    idCardNumber = insurance?.idCardNumber.orEmpty(),
    nationDescription = insurance?.nation?.nationDesc.orEmpty(),
    relationTypeDescription = relationType?.relationTypeDescription.orEmpty(),
    leavingWorkStatus = leavingWorkStatus.orEmpty(),
    leavingWorkDate = leavingWorkDate.orEmpty(),
)

fun WorkshopStackHolderDTO.toDomain(): WorkshopStackHolderDN = WorkshopStackHolderDN(
    stackId = stackId,
    nationalId = nationalId.orEmpty().ifBlank { person?.nationalId.orEmpty() },
    firstName = person?.firstName.orEmpty(),
    lastName = person?.lastName.orEmpty(),
    fatherName = person?.fatherName.orEmpty(),
    birthDate = birthDate ?: person?.dateOfBirth,
    stackType = stackType.orEmpty(),
)

// ------------------------------------------------ نام نویسی غیر حضوری — ثبت درخواست

/**
 * The registration as `POST employers` accepts it.
 *
 * `nation` and `countryId` keep the DTO's own defaults: the service requires them and this flow
 * only ever registers Iranians, which is what the old app sends too.
 */
fun NewMemberRegistrationDN.toDto(): NewMemberRegistrationDTO = NewMemberRegistrationDTO(
    personal = PersonalRegistrationDTO(
        cityOfBirthId = cityOfBirthId,
        cityOfIssueId = cityOfIssueId,
        dateOfBirth = dateOfBirth,
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId,
        id = personalId,
    ),
    relationWithTamin = RelationWithTaminDTO(
        organizationId = branchCode,
        workshopId = workshopId,
        dateOfStart = startDate,
        job = jobCode,
    ),
)

fun NewMemberRegistrationResultDTO.toDomain(): NewMemberRegistrationResultDN =
    NewMemberRegistrationResultDN(personalId = id)


fun WorkShopObjectionDTO.toDomain(): WorkShopObjectionDN = WorkShopObjectionDN(
    seqNo = seqNo,
    workshopId = workshopId.orEmpty(),
    debitNumber = debitNumber.orEmpty(),
    branchCode = branchCode.orEmpty(),
    objectionType = WorkShopObjectionType.fromCode(objectionType),
    objectionDate = objectionDate.orEmpty(),
    objectionDescription = objectionDesc.orEmpty(),
    status = WorkShopObjectionStatus.fromCode(status),
    voteTypeDescription = voteType?.description.orEmpty(),
)

fun SmsMessageDTO.toDomain(): SmsMessageDN = SmsMessageDN(
    id = id,
    description = smsDescription.takeUnless { it.isNullOrEmpty() || it.equals("null", ignoreCase = true) }.orEmpty(),
    status = WorkShopObjectionStatus.fromCode(status),
)


fun LegalRepresentativeWorkshopDTO.toDomain(): LegalRepresentativeWorkshopDN {
    return LegalRepresentativeWorkshopDN(
        workshopId = workshopId ?: "",
        branchCode = branchCode ?: "",
        workshopName = workshopName,
        branchName = branchName,
        nationalId = nationalId,
        special = special ?: false,
        representativeCount = representativeCount,
    )
}

fun LegalRepresentativeDTO.toDomain(): LegalRepresentativeDN {
    return LegalRepresentativeDN(
        stakeId = stakeId ?: 0L,
        nationalId = nationalId ?: "",
        accessCode = accessCode ?: "",
        mobile = mobile,
        fullName = fullName,
        startDate = startDate,
        workshopId = workshopId ?: "",
        workshopName = workshopName,
        branchCode = branchCode ?: "",
        special = special ?: false,
    )
}

fun LegalRepresentativeRequestDN.toDto(ticket: String): LegalRepresentativeRequestDTO {
    val accessCode = buildString {
        append(if (hasElectronicNotification) '1' else '0')
        append(if (hasInternetList) '1' else '0')
        append(if (hasInsuredRegistration) '1' else '0')
        append("00000")
    }
    return LegalRepresentativeRequestDTO(
        accessCode = accessCode,
        branchCode = branchCode,
        nationalCode = nationalCode,
        workshopId = workshopId,
        special = special,
        ticket = ticket,
        contractRows = contractRows.takeIf { special && it.isNotEmpty() },
    )
}

fun LegalRepresentativeContractDTO.toDomain(): LegalRepresentativeContractDN {
    return LegalRepresentativeContractDN(
        contractRow = contractRow ?: "",
        title = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { null },
        nationalCode = nationalCode,
    )
}
