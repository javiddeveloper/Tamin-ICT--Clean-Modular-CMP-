package com.tamin.taminhamrah.mapper.userRequest

import com.tamin.taminhamrah.model.userRequest.Article16DetailDN
import com.tamin.taminhamrah.model.userRequest.Article16DetailPR
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDN
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailPR
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionDetailDN
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionDetailPR
import com.tamin.taminhamrah.model.userRequest.IllDayDetailDN
import com.tamin.taminhamrah.model.userRequest.IllDayDetailPR
import com.tamin.taminhamrah.model.userRequest.PregnancyDetailDN
import com.tamin.taminhamrah.model.userRequest.PregnancyDetailPR
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorPR
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuidePR
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsPR
import com.tamin.taminhamrah.model.userRequest.UserRequestListPolicy
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR
import com.tamin.taminhamrah.model.userRequest.UserRequestWorkflowStatus
import com.tamin.taminhamrah.model.userRequest.userRequestProgressPhase
import com.tamin.taminhamrah.model.userRequest.userRequestStatusTone
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toFormattedDate

fun UserRequestDN.toPresentation(): UserRequestPR {
    val typeId = requestType?.id ?: 0L
    val statusCode = status?.requestCode ?: ""
    return UserRequestPR(
        id = id,
        refCode = refCode ?: "",
        title = title ?: "",
        comment = comment ?: "",
        creationTime = creationTime?.let { PersianDateFormatter.formatTimestamp(it) } ?: "",
        createByName = createByName ?: "",
        statusDesc = status?.requestDesc ?: "",
        statusCode = statusCode,
        requestTypeId = typeId,
        requestTypeTitle = requestType?.title ?: "",
        requestDetails = requestDetails,
        details = details?.toPresentation(),
        referenceId = referenceId.orEmpty(),
        viewCapability = UserRequestListPolicy.viewCapability(typeId, statusCode),
        showErrorsAction = UserRequestListPolicy.canShowErrors(typeId, statusCode),
        statusTone = userRequestStatusTone(statusCode),
        progressPhase = userRequestProgressPhase(statusCode),
        tabCategory = UserRequestWorkflowStatus.tabCategory(statusCode),
    )
}

fun UserRequestDetailsDN.toPresentation(): UserRequestDetailsPR {
    return UserRequestDetailsPR(
        deferredInstallment = deferredInstallment?.toPresentation(),
        illDay = illDay?.toPresentation(),
        article16 = article16?.toPresentation(),
        followUpObjection = followUpObjection?.toPresentation(),
        pregnancy = pregnancy?.toPresentation(),
        rejectReason = rejectReason,
    )
}

fun DeferredInstallmentDetailDN.toPresentation(): DeferredInstallmentDetailPR = DeferredInstallmentDetailPR(
    borrowerName = borrowerName,
    borrowerNationalCode = borrowerNationalCode,
    borrowerBirthDate = borrowerBirthDateMillis
        ?.let { PersianDateFormatter.formatTimestamp(it) }
        ?: borrowerBirthDate,
    bankName = bankName,
    branchName = branchName,
    guaranteeAmount = guaranteeAmount,
    installmentCount = installmentCount,
    installmentAmount = installmentAmount,
    repaymentAmount = repaymentAmount,
    borrowerFullName = borrowerFullName,
    borrowerNationalId = borrowerNationalId,
    bank = bank,
    branch = branch,
    firstName = firstName,
    lastName = lastName,
    pensionerFirstName = pensionerFirstName,
    pensionerLastName = pensionerLastName,
    pensionerNationalId = pensionerNationalId,
    pensionerId = pensionerId,
)

fun IllDayDetailDN.toPresentation(): IllDayDetailPR = IllDayDetailPR(
    startDate = startDateMillis?.let { PersianDateFormatter.formatTimestamp(it) } ?: startDate,
    endDate = endDateMillis?.let { PersianDateFormatter.formatTimestamp(it) } ?: endDate,
    employerName = employerName,
    amount = amount,
    insuranceNumber = insuranceNumber,
    insuredFullName = insuredFullName,
    mobile = mobile,
    bankAccount = bankAccount,
    bankName = bankName,
    branchName = branchName,
    doctorName = doctorName,
    doctorId = doctorId,
    prescriptionDate = prescriptionDateMillis?.let { PersianDateFormatter.formatTimestamp(it) },
    relationship = relationship,
    identityNumber = identityNumber,
    identityPlace = identityPlace,
    birthDate = birthDate,
    expirationDate = expirationDate,
)

fun Article16DetailDN.toPresentation(): Article16DetailPR = Article16DetailPR(
    meetingDate = meetingDate,
    result = result,
    defectDesc = defectDesc,
)

fun FollowUpObjectionDetailDN.toPresentation(): FollowUpObjectionDetailPR = FollowUpObjectionDetailPR(
    objectionDate = objectionDate.toDisplayDate(),
    reason = reason,
    branchName = branchName,
    requestDesc = requestDesc,
    userDesc = userDesc,
    answerDate = answerDate.toDisplayDate(),
    answerTypeDesc = answerTypeDesc,
    resultDesc = resultDesc,
)

fun PregnancyDetailDN.toPresentation(): PregnancyDetailPR = PregnancyDetailPR(
    insuranceNumber = insuranceNumber,
    insuredFullName = insuredFullName,
    mobile = mobile,
    bankAccount = bankAccount,
    bankName = bankName,
    branchName = branchName,
    startDate = startDateMillis?.let { PersianDateFormatter.formatTimestamp(it) },
    endDate = endDateMillis?.let { PersianDateFormatter.formatTimestamp(it) },
    childbearingDate = childbearingDateMillis?.let { PersianDateFormatter.formatTimestamp(it) },
    doctorName = doctorName,
    doctorId = doctorId,
    restDays = restDays,
    statusDesc = statusDesc,
    typeDesc = typeDesc,
)

private fun String?.toDisplayDate(): String? {
    if (this.isNullOrBlank()) return this
    val digits = take(8)
    return if (digits.length == 8 && digits.all { it.isDigit() }) {
        digits.toFormattedDate()
    } else {
        this
    }
}

fun List<UserRequestDN>.toPresentation(): List<UserRequestPR> = map { it.toPresentation() }

fun UserRequestTypeDN.toTypePresentation(): UserRequestTypePR {
    return UserRequestTypePR(
        id = id ?: 0L,
        title = title.orEmpty(),
        description = description.orEmpty(),
    )
}

fun List<UserRequestTypeDN>.toTypePresentation(): List<UserRequestTypePR> =
    map { it.toTypePresentation() }

fun RequestErrorDN.toErrorPresentation(): RequestErrorPR {
    return RequestErrorPR(
        id = id ?: 0L,
        errorMessage = errorMessage.orEmpty(),
        errorType = errorType.orEmpty(),
        errorStatus = errorStatus.orEmpty(),
        creationTimeJalali = PersianDateFormatter.formatTimestamp(creationTime),
    )
}

fun List<RequestErrorDN>.toErrorPresentation(): List<RequestErrorPR> =
    map { it.toErrorPresentation() }

fun SmartGuideDN.toSmartGuidePresentation(): SmartGuidePR {
    return SmartGuidePR(
        id = id ?: 0L,
        question = question.orEmpty(),
        reply = reply.orEmpty(),
        requestCode = requestCode.orEmpty(),
        requestDesc = requestDesc.orEmpty(),
        isPublic = isPublic ?: true,
        title = title.orEmpty(),
        description = description.orEmpty(),
    )
}

fun List<SmartGuideDN>.toSmartGuidePresentation(): List<SmartGuidePR> =
    map { it.toSmartGuidePresentation() }

