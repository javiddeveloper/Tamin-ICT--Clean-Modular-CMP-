package com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class LegalWorkshopCEOInfoResponse(
    var data: LegalWorkshopCEOInfo? = null,
) : BaseResponseNew()

data class LegalWorkshopCEOInfo(
    var id:Long? = null,
    var nationalId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: String? = null,
    var idCardSerial1: String? = null,
    var idCardSerial2: String? = null,
    var fatherName: String? = null,
    var birthDate: String? = null,
    var gender: String? = null,
    var deadStat :Boolean?= null,
    var insuranceId: Any? = null,
    var deadDate: Any? = null,
    var deadRegisterDate: Any? = null,
    var creationTime: Long? = null,
    var lastModificationTime: Long? = null,
    var createdBy: String? = null,
    var lastModifiedBy: String? = null,
    var age :Any?= null
){

}
