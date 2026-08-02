package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deserved_treatments")
data class DeservedTreatmentEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val nationalCode: String,
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
    val finalDesc: String?,
    val illness: String?,
    val trackingCode: String?
)

@Entity(tableName = "electronic_prescriptions")
data class ElectronicPrescriptionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val patientNationalCode: String,
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

@Entity(tableName = "electronic_prescription_details")
data class ElectronicPrescriptionDetailEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val noteHeadId: String,
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

@Entity(tableName = "electronic_prescription_prices")
data class ElectronicPrescriptionPriceEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val noteHeadId: String,
    val headInsuPayment: Long?,
    val headSsoPayment: Long?,
    val noteHeadEprescID: Long?,
    val requestPrice: Long?
)

@Entity(tableName = "dependants_under_eighteen")
data class DependantUserUnderEighteenEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val nationalCode: String,
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val id: Long?
)

@Entity(tableName = "treatment_costs")
data class TreatmentCostEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
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

@Entity(tableName = "medical_confirmations")
data class MedicalAuthoritiesEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val supportType: String?,
    val treatmentCenter: String?,
    val confirmInBranch: String?,
    val confirmStatus: String?,
    val insuranceNumber: String?,
    val nationalCode: String?,
    val firstName: String?,
    val lastName: String?,
    val outpatientRestStartDate: String?,
    val outpatientRestEndDate: String?,
    val numberOfOutpatientDays: String?,
    val hospitalizationStartDate: String?,
    val hospitalizationEndDate: String?,
    val numberOfHospitalizationDays: String?,
    val description: String?,
    val branch: String?,
    val fromDateNotConfirm: String?,
    val toDateNotConfirm: String?
)
