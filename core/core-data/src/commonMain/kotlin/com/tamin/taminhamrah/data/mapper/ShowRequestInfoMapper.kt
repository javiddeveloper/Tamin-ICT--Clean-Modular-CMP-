package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.userRequest.ArticleSixteenDetailDN
import com.tamin.taminhamrah.model.userRequest.ArticleSixteenRequestInfoDTO
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDN
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentInfoDTO
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionDetailDN
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionHistoryDTO
import com.tamin.taminhamrah.model.userRequest.IllDayDetailDN
import com.tamin.taminhamrah.model.userRequest.PregnancyDetailDN
import com.tamin.taminhamrah.model.userRequest.PregnancyLookupDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestInfoDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestStatusDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDocumentDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds

fun ShortTermRequestInfoDTO.toDetails(
    requestTypeId: Long,
    status: ShortTermRequestStatusDTO?,
    pregnancyStatus: List<PregnancyLookupDTO>,
    pregnancyTypes: List<PregnancyLookupDTO>,
): UserRequestDetailsDN {
    return when (requestTypeId) {
        UserRequestTypeIds.ILL_DAY -> UserRequestDetailsDN(
            illDay = toIllDayDetail(),
            rejectReason = status?.rejectReason,
            documents = shorttermIllness?.firstOrNull()?.shorttermRequest.toDocuments(),
        )
        UserRequestTypeIds.ORTHOTICS_PROSTHESIS -> UserRequestDetailsDN(
            illDay = toOrthoticsDetail(),
            rejectReason = status?.rejectReason,
            documents = shorttermArutz?.firstOrNull()?.shorttermRequest.toDocuments(),
        )
        UserRequestTypeIds.PREGNANCY -> UserRequestDetailsDN(
            pregnancy = toPregnancyDetail(pregnancyStatus, pregnancyTypes),
            rejectReason = status?.rejectReason,
            documents = shorttermPragnent?.firstOrNull()?.shorttermRequest.toDocuments(),
        )
        else -> UserRequestDetailsDN(rejectReason = status?.rejectReason)
    }
}

private fun ShortTermRequestDTO?.toDocuments(): List<UserRequestDocumentDN> {
    return this?.fileList.orEmpty().mapNotNull { file ->
        val guid = file.documentFile?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        UserRequestDocumentDN(guid = guid, documentType = file.documentType)
    }
}

fun DeferredInstallmentInfoDTO.toDetails(): UserRequestDetailsDN {
    val fullName = listOfNotNull(firstName, lastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { null }
    return UserRequestDetailsDN(
        deferredInstallment = DeferredInstallmentDetailDN(
            borrowerName = fullName,
            borrowerFullName = fullName,
            borrowerNationalCode = nationalId,
            borrowerNationalId = nationalId,
            firstName = firstName,
            lastName = lastName,
            borrowerBirthDateMillis = birthDate,
            bankName = bank?.bankName,
            bank = bank?.bankName,
            branchName = bankBranch,
            branch = bankBranch,
            installmentAmount = installmentAmount,
            installmentCount = installmentCount,
            repaymentAmount = loanAmount,
            guaranteeAmount = guaranteeAmount,
            pensionerFirstName = userFirstName,
            pensionerLastName = userLastName,
            pensionerNationalId = pensionerNationalId,
            pensionerId = pensionerId,
        )
    )
}

fun ArticleSixteenRequestInfoDTO.toDetails(): UserRequestDetailsDN {
    return UserRequestDetailsDN(
        articleSixteen = ArticleSixteenDetailDN(
            defectDesc = defectDesc,
            result = defectDesc,
        ),
        documents = objectionPhotos.orEmpty().mapNotNull { photo ->
            val guid = photo.guid?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            UserRequestDocumentDN(guid = guid, documentType = photo.type)
        },
    )
}

fun FollowUpObjectionHistoryDTO.toDetails(): UserRequestDetailsDN {
    return UserRequestDetailsDN(
        followUpObjection = FollowUpObjectionDetailDN(
            objectionDate = requestDate,
            reason = requestDesc,
            branchName = branchName,
            requestDesc = requestDesc,
            userDesc = userDesc,
            answerDate = answerDate,
            answerTypeDesc = answerTypeDesc,
            resultDesc = resultDesc,
        )
    )
}

private fun ShortTermRequestInfoDTO.toIllDayDetail(): IllDayDetailDN? {
    val illness = shorttermIllness?.firstOrNull() ?: return null
    val request = illness.shorttermRequest
    return IllDayDetailDN(
        startDateMillis = illness.bimSDate,
        endDateMillis = illness.bimEdate,
        insuranceNumber = risuid,
        insuredFullName = risuFullName,
        mobile = mobile,
        bankAccount = request?.bankAccount,
        bankName = request?.bankName,
        branchName = request?.branchName,
        doctorName = illness.bimDrname,
        doctorId = illness.bimDrid,
        employerName = illness.bimDrname,
    )
}

private fun ShortTermRequestInfoDTO.toOrthoticsDetail(): IllDayDetailDN? {
    val orthotics = shorttermArutz?.firstOrNull() ?: return null
    val request = orthotics.shorttermRequest
    val dependent = consequential?.firstOrNull()
    return IllDayDetailDN(
        startDateMillis = orthotics.bimSDate,
        endDateMillis = orthotics.bimEdate,
        insuranceNumber = dependent?.risuId ?: risuid,
        insuredFullName = listOfNotNull(dependent?.risuLName, dependent?.risuFname)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { risuFullName },
        mobile = mobile,
        bankAccount = request?.bankAccount,
        bankName = request?.bankName,
        branchName = request?.branchName,
        doctorName = orthotics.bimDrname,
        doctorId = orthotics.bimDrid,
        prescriptionDateMillis = orthotics.useTaj,
        relationship = dependent?.relationship,
        identityNumber = dependent?.risuIdNo,
        identityPlace = dependent?.cityName,
        birthDate = dependent?.birthDate,
        expirationDate = dependent?.bletEndDate,
        employerName = orthotics.bimDrname,
    )
}

private fun ShortTermRequestInfoDTO.toPregnancyDetail(
    pregnancyStatus: List<PregnancyLookupDTO>,
    pregnancyTypes: List<PregnancyLookupDTO>,
): PregnancyDetailDN? {
    val pregnancy = shorttermPragnent?.firstOrNull() ?: return null
    val request = pregnancy.shorttermRequest
    val statusDesc = pregnancyStatus.firstOrNull { it.code == pregnancy.barType }?.name
    val typeDesc = pregnancyTypes.firstOrNull { it.code == pregnancy.barChild }?.name
    return PregnancyDetailDN(
        insuranceNumber = risuid,
        insuredFullName = risuFullName,
        mobile = mobile,
        bankAccount = request?.bankAccount,
        bankName = request?.bankName,
        branchName = request?.branchName,
        startDateMillis = pregnancy.startDate,
        endDateMillis = pregnancy.endDate,
        childbearingDateMillis = pregnancy.barDemDat,
        doctorName = pregnancy.drName,
        doctorId = pregnancy.barDrid,
        restDays = pregnancy.barDd,
        statusDesc = statusDesc,
        typeDesc = typeDesc,
    )
}
