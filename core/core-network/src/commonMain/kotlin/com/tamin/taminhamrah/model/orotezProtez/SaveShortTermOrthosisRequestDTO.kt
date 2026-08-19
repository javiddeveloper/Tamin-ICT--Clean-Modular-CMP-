package com.tamin.taminhamrah.model.orotezProtez

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveShortTermOrthosisRequestDTO(
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermOrthosisRequestDTO? = null,
    @SerialName("useNationalid") val userNationalCode: String? = null,
    @SerialName("useRel") val userRelation: String? = null,
    @SerialName("useRelationShip") val userRelationship: String? = null,
    @SerialName("useRfName") val userFirstName: String? = null,
    @SerialName("useRisuId") val userInsuredId: String? = null,
    @SerialName("useRlName") val userLastName: String? = null,
    @SerialName("useTajTimeStamp") val prescriptionDateTimeStamp: Long? = null,
)

@Serializable
data class ShortTermOrthosisRequestDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = null,
    @SerialName("insuranceLastName") val insuranceLastName: String? = null,
    @SerialName("mobilNumber") val mobileNumber: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("request") val request: ShortTermOrthosisRequestIdDTO? = null,
    @SerialName("requestFileList") val requestFileList: List<ShortTermOrthosisRequestFileDTO>? = null,
    @SerialName("requestHelpType") val requestHelpType: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("serviceDateTimeStamp") val serviceDateTimeStamp: Long? = null,
)

@Serializable
data class ShortTermOrthosisRequestIdDTO(
    @SerialName("id") val id: String? = null,
)

@Serializable
data class ShortTermOrthosisRequestFileDTO(
    @SerialName("documentFile") val documentFile: String? = null,
    @SerialName("documentType") val documentType: String? = null,
    @SerialName("editDate") val editDate: String? = null,
    @SerialName("editUser") val editUser: String? = null,
    @SerialName("id") val id: String? = null,
)
