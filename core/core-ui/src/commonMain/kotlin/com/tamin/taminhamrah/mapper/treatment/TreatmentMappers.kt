package com.tamin.taminhamrah.mapper.treatment

import com.tamin.taminhamrah.ui.normalizeArabicLetters
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.orZero
import com.tamin.taminhamrah.ui.toLongStringOrZero
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.util.toJalaliDateLabel
import kotlin.jvm.JvmName

fun DeservedTreatmentDN.toPresentation(): DeservedTreatmentPR {
    return DeservedTreatmentPR(
        id = id ?: 0,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        fullName = "${firstName ?: ""} ${lastName ?: ""}".trim().ifEmpty { "نامشخص" },
        nationalId = nationalId ?: "",
        natCode = natCode ?: "",
        birthDate = birthDate ?: "",
        brhCode = brhCode ?: "",
        brhName = brhName ?: "نامشخص",
        dependenceType = dependenceType ?: "",
        fatherName = fatherName ?: "",
        feranshiz = feranshiz ?: "",
        gender = gender ?: "",
        healthBookletDate = healthBookletDate?.toString() ?: "",
        insuranceType = insuranceType ?: "",
        lastBookletDate = lastBookletDate ?: "",
        parentRisuid = parentRisuid ?: "",
        provinceCode = provinceCode ?: "",
        provinceName = provinceName ?: "نامشخص",
        regWorkshopId = regWorkshopId ?: "",
        regWorkshopName = regWorkshopName ?: "",
        risuid = risuid ?: "",
        message = message ?: "",
        finalDesc = finalDesc ?: "",
        illness = illness ?: "",
        trackingCode = trackingCode ?: ""
    )
}

@JvmName("toDeservedPresentation")
fun List<DeservedTreatmentDN>.toPresentation(): List<DeservedTreatmentPR> {
    return this.map { it.toPresentation() }
}

fun ElectronicPrescriptionDN.toPresentation(): ElectronicPrescriptionPR {
    return ElectronicPrescriptionPR(
        id = id ?: "",
        docId = docId ?: "",
        docName = docName ?: "نامشخص",
        flagSata = flagSata ?: "",
        location = location ?: "",
        noteHeadEprescID = noteHeadEprescID?.toString() ?: "",
        patientID = patientID ?: "",
        patientName = patientName ?: "نامشخص",
        prescDate = prescDate ?: "",
        prescName = prescName ?: "نامشخص",
        specDesc = specDesc ?: "",
        prescType = prescType ?: "",
        trackingCode = trackingCode?.toString() ?: ""
    )
}

@JvmName("toPrescriptionPresentation")
fun List<ElectronicPrescriptionDN>.toPresentation(): List<ElectronicPrescriptionPR> {
    return this.map { it.toPresentation() }
}

fun ElectronicPrescriptionDetailDN.toPresentation(): ElectronicPrescriptionDetailPR {
    return ElectronicPrescriptionDetailPR(
        sumPriceItem = sumPriceItem?.toString() ?: "0",
        ssoPayment = ssoPayment?.toString() ?: "0",
        insurancePayment = insurancePayment?.toString() ?: "0",
        serviceQuantity = serviceQuantity?.toString() ?: "0",
        noteHeadEprescID = noteHeadEprescID?.toString() ?: "",
        serverCode = serverCode ?: "",
        serverName = serverName ?: "",
        serviceName = serviceName ?: "نامشخص",
        drugInst = drugInst ?: "",
        registerDate = registerDate ?: "",
        drugInstruction = drugInstruction ?: "",
        deliveredNo = deliveredNo?.toString() ?: "0",
        drugAmount = drugAmount ?: ""
    )
}

@JvmName("toDetailPresentation")
fun List<ElectronicPrescriptionDetailDN>.toPresentation(): List<ElectronicPrescriptionDetailPR> {
    return this.map { it.toPresentation() }
}

fun ElectronicPrescriptionPriceDN.toPresentation(): ElectronicPrescriptionPricePR {
    return ElectronicPrescriptionPricePR(
        headInsuPayment = headInsuPayment?.toString() ?: "0",
        headSsoPayment = headSsoPayment?.toString() ?: "0",
        noteHeadEprescID = noteHeadEprescID?.toString() ?: "",
        requestPrice = requestPrice?.toString() ?: "0"
    )
}

@JvmName("toPricePresentation")
fun List<ElectronicPrescriptionPriceDN>.toPresentation(): List<ElectronicPrescriptionPricePR> {
    return this.map { it.toPresentation() }
}


fun DependantUserUnderEighteenDN.toPresentation(): DependantUserUnderEighteenPR {
    return DependantUserUnderEighteenPR(
        id = id?.toString() ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        fullName = "${firstName ?: ""} ${lastName ?: ""}".trim().ifEmpty { "نامشخص" },
        nationalId = nationalId ?: ""
    )
}

@JvmName("toDependantPresentation")
fun List<DependantUserUnderEighteenDN>.toPresentation(): List<DependantUserUnderEighteenPR> {
    return this.map { it.toPresentation() }
}

fun TreatmentCostDN.toPresentation(): TreatmentCostPR {
    return TreatmentCostPR(
        repId = repId?.toString() ?: "",
        nameFamil = nameFamil ?: "نامشخص",
        healthcenterName = healthcenterName ?: "نامشخص",
        payPrice = payPrice.toLongStringOrZero(),
        payStatusDesc = payStatusDesc ?: "نامشخص",
        estimatePayDate = estimatePayDate ?: "-",
        rahgiriCode = rahgiriCode ?: "",
        serviceDate = serviceDate ?: "",
        statusDesc = statusDesc ?: "نامشخص",
        accountNumber = accountNumber ?: "",
        bimeCode = bimeCode ?: "",
        datePaz = datePaz ?: "",
        famil = famil ?: "",
        mainNational = mainNational ?: "",
        maliCode = maliCode ?: "",
        name = name ?: "",
        nameAsli = nameAsli ?: "",
        noPazir = noPazir ?: "",
        payNatCode = payNatCode ?: "",
        payOtherService = payOtherService ?: "",
        payService = payService ?: "",
        payStatus = payStatus ?: "",
        payType = payType ?: "",
        province = province ?: "",
        releaseDate = releaseDate ?: "",
        status = status ?: "",
        returnReason = returnReason ?: ""
    )
}
@JvmName("toCostPresentation")
fun List<TreatmentCostDN>.toPresentation(): List<TreatmentCostPR> {
    return this.map { it.toPresentation() }
}

fun MedicalConfirmationDN.toPresentation(): MedicalConfirmationPR {
    return MedicalConfirmationPR(
        repId = repId.orEmpty(),
        // Left blank rather than filled in here: the card substitutes its own wording from the
        // string resources, so a fallback at this layer only pre-empts it -- and defaulting
        // supportType to a *particular* support type invents data the service never sent.
        supportType = supportType.orEmpty(),
        treatmentCenter = treatmentCenter.orEmpty(),
        outpatientRestStartDate = outpatientRestStartDate.orDash().toJalaliDateLabel(),
        outpatientRestEndDate = outpatientRestEndDate.orDash().toJalaliDateLabel(),
        numberOfOutpatientDays = numberOfOutpatientDays.orZero(),
        inpatientRestStartDate = inpatientRestStartDate.orDash().toJalaliDateLabel(),
        inpatientRestEndDate = inpatientRestEndDate.orDash().toJalaliDateLabel(),
        numberOfInpatientDays = numberOfInpatientDays.orZero(),
        unapprovedFromDate = unapprovedFromDate.orEmpty().toJalaliDateLabel(),
        unapprovedToDate = unapprovedToDate.orEmpty().toJalaliDateLabel(),
        branchName = branchName.orEmpty(),
        branchStatus = branchStatus.orEmpty().normalizeArabicLetters(),
        description = description.orEmpty(),
        statusDesc = statusDesc.orEmpty().normalizeArabicLetters(),
    )
}


@JvmName("toConfirmationPresentation")
fun List<MedicalConfirmationDN>.toPresentation(): List<MedicalConfirmationPR> {
    return this.map { it.toPresentation() }
}




