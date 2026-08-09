package com.tamin.taminhamrah.model.treatment

data class DeservedTreatmentDN(
    val birthDate: String?,
    val brhCode: String?,
    val brhName: String?,
    val dependenceType: String?,
    val fatherName: String?,
    val feranshiz: String?,
    val firstName: String?,
    val gender: String?,
    val healthBookletDate: Long?,
    val id: Int?,
    val idNumber: String?,
    val insuranceType: String?,
    val lastBookletDate: String?,
    val lastName: String?,
    val natCode: String?,
    val nationalId: String?,
    val parentRisuid: String?,
    val provinceCode: String?,
    val provinceName: String?,
    val regWorkshopId: String?,
    val regWorkshopName: String?,
    val risuid: String?,
    val message: String?,
    /** Blank/null while treatment support is active; the refusal reason when it is not. */
    val finalDesc: String?,
    val illness: String?,
    val trackingCode: String?
)

data class ElectronicPrescriptionDN(
    val id: String?,
    val docId: String?,
    val docName: String?,
    val flagSata: String?,
    val location: String?,
    val noteHeadEprescID: Long?,
    val patientID: String?,
    val patientName: String?,
    val prescDate: String?,
    val prescName: String?,
    val specDesc: String?,
    val prescType: String?,
    val trackingCode: Long?
)

data class ElectronicPrescriptionDetailDN(
    val sumPriceItem: Long?,
    val ssoPayment: Long?,
    val insurancePayment: Long?,
    val serviceQuantity: Int?,
    val noteHeadEprescID: Long?,
    val serverCode: String?,
    val serverName: String?,
    val serviceName: String?,
    val drugInst: String?,
    val registerDate: String?,
    val drugInstruction: String?,
    val deliveredNo: Int?,
    val drugAmount: String?
)

data class ElectronicPrescriptionPriceDN(
    val headInsuPayment: Long?,
    val headSsoPayment: Long?,
    val noteHeadEprescID: Long?,
    val requestPrice: Long?
)

data class DependantUserUnderEighteenDN(
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val id: Long?
)

data class TreatmentCostDN(
    val accountNumber: String?,
    val bimeCode: String?,
    val datePaz: String?,
    val famil: String?,
    val healthcenterName: String?,
    val mainNational: String?,
    val maliCode: String?,
    val name: String?,
    val nameAsli: String?,
    val nameFamil: String?,
    val noPazir: String?,
    val payNatCode: String?,
    val payOtherService: String?,
    val payPrice: String?,
    val payService: String?,
    val payStatus: String?,
    val payType: String?,
    val province: String?,
    val rahgiriCode: String?,
    val releaseDate: String?,
    val repId: Int?,
    val serviceDate: String?,
    val status: String?,
    val statusDesc: String?,
    val payStatusDesc: String?,
    val estimatePayDate: String?,
    val returnReason: String?
)
