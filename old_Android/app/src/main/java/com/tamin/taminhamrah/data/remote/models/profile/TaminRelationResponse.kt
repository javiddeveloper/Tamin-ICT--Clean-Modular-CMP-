package com.tamin.taminhamrah.data.remote.models.profile

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


data class TaminRelationResponse(
    var data: TaminRelation? = null
) : BaseResponseNew()

data class TaminRelation(
    var id: Int? = null,
    var nationalId: String? = null,
    var insuranceId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var fatherName: String? = null,
    var identityId: String? = null,
    var birthDate: String? = null,
    var otherDesc: String? = null,
    var workshopId: String? = null,
    var workshopName: String? = null,
    var lastMonthWork: String? = null,
    var isuType: String? = null,
    var isuStatus: String? = null,
    var isuTypeDesc: String? = null,
    var isuStatusDesc: String? = null,
    var relationType: String? = null,
    var relationTypeDesc: String? = null,
    var relationWithTaminId: String? = null,
    var relationStartDate: String? = null,
    var brhCode: String? = null,
    var brhName: String? = null,
    var brhAdress: String? = null,
    var address: Any? = null,
    var tell: Any? = null,
    var workAddress: String? = null,
    var workTel: String? = null,
    var employerMobile: String? = null,
    var employerName: String? = null,
    var bookletDate: String? = null,
    var idCityName: String? = null,
    var parentRisuId: Any? = null,
    var parentNationalId: Any? = null,
    var pensionerId: Any? = null,
    var parentLastName: Any? = null,
    var parentFirstName: Any? = null,
    var parentFatherName: Any? = null,
    var parentIdNumber: Any? = null,
    var parentBirthDate: Any? = null,
    var parentIdCityName: Any? = null,
    var dependenceType: String? = null,
    var noBooklet: String? = null,
    var haveDarman: String? = null,
    var isuCityCode: Any? = null,
    var isuCityName: Any? = null,
    var idCityCode: Any? = null,
    var parentDeathDate: Any? = null
) {
    fun fullName(): String {
        return "$firstName $lastName"
    }
}


/*


fun TaminRelationResponse.asDomainModel(): UserInfo {
    return UserInfo(
        fullName = "${this.firstName}\" \"${this.lastName}",
        nationalID = this.nationalId,
        insuranceNumber = this.insuranceId
    )
}

fun List<TaminRelationResponse>.asDomainModel(): List<UserInfo> {
    return map {
        it.asDomainModel()
    }
}
*/
