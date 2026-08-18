package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import com.tamin.taminhamrah.model.userRequest.Article16DetailDTO
import com.tamin.taminhamrah.model.userRequest.Article16DetailDN
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDTO
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDN
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionDetailDTO
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionDetailDN
import com.tamin.taminhamrah.model.userRequest.IllDayDetailDTO
import com.tamin.taminhamrah.model.userRequest.IllDayDetailDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN

import com.tamin.taminhamrah.model.userRequest.RequestErrorDTO
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusDN
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDTO
import kotlinx.serialization.json.Json

private val userRequestDetailsJsonParser = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

fun UserRequestDTO.toDomain(): UserRequestDN {
    return UserRequestDN(
        id = id ?: 0L,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        status = status?.toDomain(),
        requestType = requestType?.toDomain(),
        referenceId = referenceId,
        requestDetails = requestDetails,
        details = parseUserRequestDetails(requestType?.id ?: 0L, requestDetails),
    )
}

internal fun parseUserRequestDetails(requestTypeId: Long, jsonString: String?): UserRequestDetailsDN? {
    if (jsonString.isNullOrBlank()) return null
    return try {
        when (requestTypeId) {
            22L -> userRequestDetailsJsonParser.decodeFromString<DeferredInstallmentDetailDTO>(jsonString)
                .toDomain()
            10L, 12L -> userRequestDetailsJsonParser.decodeFromString<IllDayDetailDTO>(jsonString)
                .toDomain()
            26L -> userRequestDetailsJsonParser.decodeFromString<Article16DetailDTO>(jsonString)
                .toDomain()
            8L -> userRequestDetailsJsonParser.decodeFromString<FollowUpObjectionDetailDTO>(jsonString)
                .toDomain()
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}

internal fun DeferredInstallmentDetailDTO.toDomain(): UserRequestDetailsDN = UserRequestDetailsDN(
    deferredInstallment = DeferredInstallmentDetailDN(
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
)

internal fun IllDayDetailDTO.toDomain(): UserRequestDetailsDN = UserRequestDetailsDN(
    illDay = IllDayDetailDN(
        startDate = startDate,
        endDate = endDate,
        employerName = employerName,
        amount = amount,
    )
)

internal fun Article16DetailDTO.toDomain(): UserRequestDetailsDN = UserRequestDetailsDN(
    article16 = Article16DetailDN(
        meetingDate = meetingDate,
        result = result,
    )
)

internal fun FollowUpObjectionDetailDTO.toDomain(): UserRequestDetailsDN = UserRequestDetailsDN(
    followUpObjection = FollowUpObjectionDetailDN(
        objectionDate = objectionDate,
        reason = reason,
    )
)

internal fun UserRequestDTO.toEntity(): UserRequestEntity {
    return UserRequestEntity(
        id = id ?: 0L,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        statusCode = status?.requestCode,
        statusDesc = status?.requestDesc,
        requestTypeId = requestType?.id,
        requestTypeTitle = requestType?.title,
        requestTypeDescription = requestType?.description,
        referenceId = referenceId,
    )
}

internal fun UserRequestEntity.toDomain(): UserRequestDN {
    return UserRequestDN(
        id = id,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        status = UserRequestStatusDN(
            requestCode = statusCode,
            requestDesc = statusDesc,
        ),
        requestType = UserRequestTypeDN(
            id = requestTypeId,
            title = requestTypeTitle,
            description = requestTypeDescription,
        ),
        referenceId = referenceId,
        requestDetails = null,
    )
}

internal fun UserRequestDN.toEntity(): UserRequestEntity {
    return UserRequestEntity(
        id = id,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        statusCode = status?.requestCode,
        statusDesc = status?.requestDesc,
        requestTypeId = requestType?.id,
        requestTypeTitle = requestType?.title,
        requestTypeDescription = requestType?.description,
        referenceId = referenceId,
    )
}

fun UserRequestStatusDTO.toDomain(): UserRequestStatusDN {
    return UserRequestStatusDN(
        requestCode = requestCode,
        requestDesc = requestDesc,
    )
}

fun UserRequestTypeDTO.toDomain(): UserRequestTypeDN {
    return UserRequestTypeDN(
        id = id,
        title = title,
        description = description,
    )
}

fun RequestErrorDTO.toDomain(): RequestErrorDN {
    return RequestErrorDN(
        id = id,
        errorMessage = errorMessage,
        errorType = errorType,
        errorStatus = errorStatus,
        creationTime = creationTime,
    )
}

fun SmartGuideDTO.toDomain(): SmartGuideDN {
    return SmartGuideDN(
        id = id,
        question = question,
        reply = reply,
        requestCode = requestCode ?: requestStatus?.requestCode,
        requestDesc = requestDesc ?: requestStatus?.requestDesc,
        isPublic = isPublic,
        title = title ?: requestType?.title,
        description = description ?: requestType?.description,
    )
}

