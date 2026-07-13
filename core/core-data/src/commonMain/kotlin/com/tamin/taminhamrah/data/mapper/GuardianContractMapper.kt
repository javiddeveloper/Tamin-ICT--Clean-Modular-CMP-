package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.contracts.ContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.GuardianShipDetailDN
import com.tamin.taminhamrah.model.contracts.GuardianShipDetailDTO
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDTO
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.OptionalMakeContractRequestDN
import com.tamin.taminhamrah.model.contracts.OptionalMakeContractRequestDTO

internal fun GuardianShipDetailDN.toDto(): GuardianShipDetailDTO =
    GuardianShipDetailDTO(
        proCode = proCode,
        guid = guid,
        guidName = guidName,
        nid = nid,
        fullName = fullName,
        protectorLetterNo = protectorLetterNo,
        protectorLetterDate = protectorLetterDate,
    )

internal fun OptionalMakeContractRequestDN.toDto(): OptionalMakeContractRequestDTO =
    OptionalMakeContractRequestDTO(
        brchCodeNew = brchCodeNew,
        cityCode = cityCode,
        cntDrmn = cntDrmn,
        premiumRateCode = premiumRateCode,
        provinceCode = provinceCode,
    )

internal fun FreelanceContractByGuardianParams.toDto(): ContractByGuardianRequestDTO =
    ContractByGuardianRequestDTO(
        contract = contract.toDto(),
        protector = protector.toDto(),
    )

internal fun OptionalContractByGuardianParams.toDto(): OptionalContractByGuardianRequestDTO =
    OptionalContractByGuardianRequestDTO(
        contract = contract.toDto(),
        protector = protector.toDto(),
    )

internal fun InsurancePaymentDTO.toDomain(): InsurancePaymentDN =
    InsurancePaymentDN(
        paymentTicket = paymentTicket,
        paymentUrl = paymentUrl,
        responseMessage = responseMessage,
        succeed = succeed,
    )
