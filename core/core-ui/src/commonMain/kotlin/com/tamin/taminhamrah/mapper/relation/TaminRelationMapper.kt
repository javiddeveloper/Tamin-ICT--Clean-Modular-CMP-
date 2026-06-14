package com.tamin.taminhamrah.mapper.relation

import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.relation.TaminRelationPR


fun TaminRelationDN.toPresentation(): TaminRelationPR {
    return TaminRelationPR(
        id = this.id ?: 0,
        nationalId = this.nationalId ?: "",
        insuranceId = this.insuranceId ?: "",
        firstName = this.firstName ?: "",
        lastName = this.lastName ?: "",
        fullName = this.fullName.ifEmpty { "نامشخص" },
        fatherName = this.fatherName ?: "",
        identityId = this.identityId ?: "",
        birthDate = this.birthDate ?: "",
        otherDesc = this.otherDesc ?: "",
        workshopId = this.workshopId ?: "",
        workshopName = this.workshopName ?: "",
        lastMonthWork = this.lastMonthWork ?: "",
        isuType = this.isuType ?: "",
        isuStatus = this.isuStatus ?: "",
        isuTypeDesc = this.isuTypeDesc ?: "",
        isuStatusDesc = this.isuStatusDesc ?: "",
        relationType = this.relationType ?: "",
        relationTypeDesc = this.relationTypeDesc ?: "",
        relationWithTaminId = this.relationWithTaminId ?: "",
        relationStartDate = this.relationStartDate ?: "",
        brhCode = this.brhCode ?: "",
        brhName = this.brhName ?: "",
        brhAdress = this.brhAdress ?: "",
        address = this.address ?: "",
        tell = this.tell ?: "",
        workAddress = this.workAddress ?: "",
        workTel = this.workTel ?: "",
        employerMobile = this.employerMobile ?: "",
        employerName = this.employerName ?: "",
        bookletDate = this.bookletDate ?: "",
        idCityName = this.idCityName ?: "",
        parentRisuId = this.parentRisuId ?: "",
        parentNationalId = this.parentNationalId ?: "",
        pensionerId = this.pensionerId ?: "",
        parentLastName = this.parentLastName ?: "",
        parentFirstName = this.parentFirstName ?: "",
        parentFatherName = this.parentFatherName ?: "",
        parentIdNumber = this.parentIdNumber ?: "",
        parentBirthDate = this.parentBirthDate ?: "",
        parentIdCityName = this.parentIdCityName ?: "",
        dependenceType = this.dependenceType ?: "",
        noBooklet = this.noBooklet ?: "",
        haveDarman = this.haveDarman ?: "",
        isuCityCode = this.isuCityCode ?: "",
        isuCityName = this.isuCityName ?: "",
        idCityCode = this.idCityCode ?: "",
        parentDeathDate = this.parentDeathDate ?: ""
    )
}
