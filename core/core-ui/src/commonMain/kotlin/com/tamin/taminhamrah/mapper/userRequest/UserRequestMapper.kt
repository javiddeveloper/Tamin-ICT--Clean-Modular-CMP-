package com.tamin.taminhamrah.mapper.userRequest

import com.tamin.taminhamrah.model.userRequest.Article16DetailDN
import com.tamin.taminhamrah.model.userRequest.Article16DetailPR
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDN
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailPR
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionDetailDN
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionDetailPR
import com.tamin.taminhamrah.model.userRequest.IllDayDetailDN
import com.tamin.taminhamrah.model.userRequest.IllDayDetailPR
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorPR
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuidePR
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsPR
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR

fun UserRequestDN.toPresentation(): UserRequestPR {
    return UserRequestPR(
        id = id,
        refCode = refCode ?: "",
        title = title ?: "",
        comment = comment ?: "",
        creationTime = creationTime?.toString() ?: "0",
        createByName = createByName ?: "",
        statusDesc = status?.requestDesc ?: "",
        statusCode = status?.requestCode ?: "",
        requestTypeId = requestType?.id ?: 0L,
        requestTypeTitle = requestType?.title ?: "",
        requestDetails = requestDetails,
        details = details?.toPresentation(),
    )
}

fun UserRequestDetailsDN.toPresentation(): UserRequestDetailsPR {
    return UserRequestDetailsPR(
        deferredInstallment = deferredInstallment?.toPresentation(),
        illDay = illDay?.toPresentation(),
        article16 = article16?.toPresentation(),
        followUpObjection = followUpObjection?.toPresentation(),
    )
}

fun DeferredInstallmentDetailDN.toPresentation(): DeferredInstallmentDetailPR = DeferredInstallmentDetailPR(
    borrowerName = borrowerName,
    borrowerNationalCode = borrowerNationalCode,
    borrowerBirthDate = borrowerBirthDate,
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
)

fun IllDayDetailDN.toPresentation(): IllDayDetailPR = IllDayDetailPR(
    startDate = startDate,
    endDate = endDate,
    employerName = employerName,
    amount = amount,
)

fun Article16DetailDN.toPresentation(): Article16DetailPR = Article16DetailPR(
    meetingDate = meetingDate,
    result = result,
)

fun FollowUpObjectionDetailDN.toPresentation(): FollowUpObjectionDetailPR = FollowUpObjectionDetailPR(
    objectionDate = objectionDate,
    reason = reason,
)

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
        creationTime = creationTime?.toString().orEmpty(),
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

