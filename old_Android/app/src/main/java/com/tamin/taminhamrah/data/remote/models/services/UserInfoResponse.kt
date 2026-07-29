package com.tamin.taminhamrah.data.remote.models.services
import com.tamin.taminhamrah.data.entity.UserInfo
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class UserInfoResponse(
    var data: UserInfoModel? = null
) : BaseResponseNew()


data class UserInfoModel(

    var serial1: String? = null,
    var serial2: String? = null,
    var militaryServiceCode: String? = null,
    var fatherName: String? = null,
    var lastName: String? = null,
    var creationTime: Long? = null,//timeStamp
    var lastModificationTime: Long? = null,//timeStamp
    var cityCode: String? = null,
    var socialSecurityNumber: String? = null,
    var lastModifiedBy: String? = null,
    var issueplaceName: String? = null,
    var birthDate: String? = null,
    var firstName: String? = null,
    var insuranceNumber: String? = null,
    var genderCode: String? = null,
    var createdBy: String? = null,
    var nationalID: String? = null,
    var identityNumber: String? = null,
    var countryCode: String? = null,
    var id: String? = null,
    var birthDateTimestamp: Long? = null,
    var issueplace: String? = null,
    var nationCode: String? = null
)

fun UserInfoModel.asDomainModel(): UserInfo {
    return UserInfo(
        id = this.id,
        serialnumber = " ${this.serial2} " + "/" + " ${this.serial1} ",
        militaryServiceCode = this.militaryServiceCode,
        fatherName = this.fatherName,
        fullName = "${this.firstName} ${this.lastName}",
        creationTime = this.creationTime,
        cityCode = this.cityCode,
        socialSecurityNumber = this.socialSecurityNumber,
        issueplaceName = this.issueplaceName,
        birthDate = this.birthDate,
        insuranceNumber = this.insuranceNumber,
        genderCode = this.genderCode,
        nationalID = this.nationalID,
        countryCode = this.countryCode,
        issueplace = this.issueplace,
        nationCode = this.nationCode,
        identityNumber = this.identityNumber
    )
}


fun List<UserInfoModel>.asDomainModel(): List<UserInfo> {
    return map {
        it.asDomainModel()
    }
}
