package com.tamin.taminhamrah.data.mapper.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.FUNERAL_ALLOWANCE_HELP_TYPE
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDN
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceRequestDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralShorttermRequestDTO
import com.tamin.taminhamrah.model.funeralAllowance.RegisteredFuneralRequestDN
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN

internal fun FuneralAllowanceInfoDTO.toDomain(): FuneralAllowanceInfoDN = FuneralAllowanceInfoDN(
    firstName = insuranceFirstName.orEmpty(),
    lastName = insuranceLastName.orEmpty(),
    insuranceNumber = risuid.orEmpty(),
    bankAccount = bankAccount.orEmpty(),
    bankName = bankName.orEmpty(),
    mobileNumber = mobileNumber.orEmpty(),
    branchName = branchName.orEmpty(),
    branchCode = branchCode.orEmpty(),
    nationalCode = nationalCode.orEmpty(),
    deceasedNationalId = partnerNationalId.orEmpty(),
    requestHelpType = requestHelpType?.takeIf { it.isNotBlank() } ?: FUNERAL_ALLOWANCE_HELP_TYPE,
    hasBankAccountIssue = flag,
    registeredRequest = if (flag) {
        RegisteredFuneralRequestDN(
            requestId = request?.id ?: 0L,
            deceasedNationalId = partnerNationalId.orEmpty(),
            deathTimestamp = deathTimestamp,
            requestTimestamp = request?.requestDate,
            statusName = request?.statusName.orEmpty(),
        )
    } else {
        null
    },
)

internal fun SubmitFuneralAllowanceParamsDN.toRequestDTO(): FuneralAllowanceRequestDTO =
    FuneralAllowanceRequestDTO(
        deadNationalId = deceasedNationalId,
        shorttermRequest = FuneralShorttermRequestDTO(
            branchCode = branchCode,
            branchName = branchName,
            insuranceFirstName = insuranceFirstName,
            insuranceLastName = insuranceLastName,
            mobileNumber = mobileNumber,
            nationalCode = nationalCode,
            requestHelpType = requestHelpType,
            risuid = insuranceNumber,
        ),
    )
