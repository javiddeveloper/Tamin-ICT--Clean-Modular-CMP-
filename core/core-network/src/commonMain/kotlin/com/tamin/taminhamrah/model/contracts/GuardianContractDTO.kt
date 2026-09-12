package com.tamin.taminhamrah.model.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuardianShipDetailDTO(
    @SerialName("proCode") val proCode: String,
    @SerialName("guid") val guid: String,
    @SerialName("guidName") val guidName: String,
    @SerialName("nid") val nid: String,
    @SerialName("fullName") val fullName: String,
    @SerialName("protectorLetterNo") val protectorLetterNo: String,
    @SerialName("protectorLetterDate") val protectorLetterDate: String,
)

@Serializable
data class OptionalMakeContractRequestDTO(
    @SerialName("brchCodeNew") val brchCodeNew: String,
    @SerialName("cityCode") val cityCode: String,
    @SerialName("cntDrmn") val cntDrmn: String,
    @SerialName("premiumRateCode") val premiumRateCode: String,
    @SerialName("provinceCode") val provinceCode: String,
)

@Serializable
data class ContractByGuardianRequestDTO(
    @SerialName("contract") val contract: FreelanceMakeContractRequestDTO,
    @SerialName("protector") val protector: GuardianShipDetailDTO,
)

@Serializable
data class OptionalContractByGuardianRequestDTO(
    @SerialName("contract") val contract: OptionalMakeContractRequestDTO,
    @SerialName("protector") val protector: GuardianShipDetailDTO,
)

/** Legacy empty optional update body (`UpdateOptionalContract`). */
@Serializable
data class UpdateOptionalContractDTO(
    @SerialName("provinceCode") val provinceCode: String = "",
)

@Serializable
data class UpdateOptionalContractByGuardianRequestDTO(
    @SerialName("contract") val contract: UpdateOptionalContractDTO = UpdateOptionalContractDTO(),
    @SerialName("protector") val protector: GuardianShipDetailDTO,
)
