package com.tamin.taminhamrah.model.treatment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElectronicPrescriptionDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("docID") val docId: String? = null,
    @SerialName("docName") val docName: String? = null,
    @SerialName("flagSata") val flagSata: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("noteHeadEprescID") val noteHeadEprescID: Long? = null,
    @SerialName("patientID") val patientID: String? = null,
    @SerialName("patientName") val patientName: String? = null,
    @SerialName("prescDate") val prescDate: String? = null,
    @SerialName("prescName") val prescName: String? = null,
    @SerialName("specDesc") val specDesc: String? = null,
    @SerialName("prescType") val prescType: String? = null,
    @SerialName("trackingCode") val trackingCode: Long? = null
)

@Serializable
data class ElectronicPrescriptionDetailDTO(
    @SerialName("sumPriceItem") val sumPriceItem: Long? = null,
    @SerialName("ssoPayment") val ssoPayment: Long? = null,
    @SerialName("insuPayment") val insurancePayment: Long? = null,
    @SerialName("srvQty") val serviceQuantity: Int? = null,
    @SerialName("noteHeadEprescID") val noteHeadEprescID: Long? = null,
    @SerialName("wsSrvCode") val serverCode: String? = null,
    @SerialName("userName") val serverName: String? = null,
    @SerialName("serviceName") val serviceName: String? = null,
    @SerialName("drugInst") val drugInst: String? = null,
    @SerialName("regdate") val registerDate: String? = null,
    @SerialName("drugInstruction") val drugInstruction: String? = null,
    @SerialName("deliveredNo") val deliveredNo: Int? = null,
    @SerialName("drugAmnt") val drugAmount: String? = null
)

@Serializable
data class ElectronicPrescriptionPriceDTO(
    @SerialName("headInsuPayment") val headInsuPayment: Long? = null,
    @SerialName("headSsoPayment") val headSsoPayment: Long? = null,
    @SerialName("noteHeadEprescID") val noteHeadEprescID: Long? = null,
    @SerialName("requestPrice") val requestPrice: Long? = null
)
