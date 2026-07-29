package com.tamin.taminhamrah.data.remote.models.services.occurrence

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


data class OfficePersonalInfoResponse(var data: OfficePersonalInfo? = null) : BaseResponseNew()


class OfficePersonalInfo {
    // Setter Methods
    // Getter Methods
    var id: Float = 0f
    var nationalId: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var idCardNumber: String? = null
    var idCardSerial1: String? = null
    var idCardSerial2: String? = null
    var fatherName: String? = null
    var birthDate: String? = null
    var gender: String? = null
    var deadStat: Boolean = false
    var insuranceId: String? = null
    var deadDate: String? = null
    var deadRegisterDate: String? = null
    var creationTime: String? = null
    var lastModificationTime: String? = null
    var createdBy: String? = null
    var lastModifiedBy: String? = null
    var age: Float = 0f
}