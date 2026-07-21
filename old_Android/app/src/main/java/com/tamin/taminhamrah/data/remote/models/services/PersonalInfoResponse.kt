package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.entity.PersonalInfoModel
import com.tamin.taminhamrah.data.entity.RequestType
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate
data class PersonalInfoResponse(val data : PersonalInfoModelRes? = null ):BaseResponseNew()

class PersonalInfoModelRes(
    var request: Request? = null,
    var creationTime: Long? = null,
    var endDate: Long? = null,
    var lastModificationTime: Long? = null,
    var logicalControlStatus: Any? = null,
    var endConfirmed: Boolean? = null,
    var confirmed: Boolean? = null,
    var branch: String? = null,
    var organizationId: String? = null,
    var recognizeDate: Long? = null,
    var recognizeMethod: Any? = null,
    var dateOfStart: Long? = null,
    var endReasonType: Any? = null,
    var insuranceId: String? = null,
    var id: Long? = null,
    var newInsuranceId: Boolean? = null,
    var subDominant: SubDominant? = null,
    var edited: Boolean? = null,
    var work: Any? = null,
    var lastModifiedBy: String? = null,
    var isFirst: Boolean? = null,
    var personal: Personal? = null,
    var printed: Boolean? = null,
    var deleted: Boolean? = null,
    var createdBy: String? = null,
    var relationWithTamin: RelationWithTamin? = null,
    var provinceName: String? = null,
    var mobileNumber: String? = null,
)

class RelationWithTamin(

    var request: Request? = null,
    var creationTime: Long? = null,
    var endDate: Long? = null,
    var lastModificationTime: Long? = null,
    var logicalControlStatus: Any? = null,
    var endConfirmed: Boolean? = null,
    var confirmed: Boolean? = null,
    var branch: Any? = null,
    var organizationId: String? = null,
    var recognizeDate: Any? = null,
    var recognizeMethod: Any? = null,
    var dateOfStart: Long? = null,
    var endReasonType: Any? = null,
    var insuranceId: String? = null,
    var id2: Int? = null,
    var id: Long? = null,
    var newInsuranceId: Boolean? = null,
    var subDominant: SubDominant? = null,
    var edited: Boolean? = null,
    var work: Any? = null,
    var lastModifiedBy: String? = null,
    var isFirst: Boolean? = null,
    var personal: Int? = null,
    var printed: Boolean? = null,
    var deleted: Boolean? = null,
    var createdBy: String? = null,
    var relationWithTamin: RelationWithTamin? = null
)

class Personal(
    var lastName: String? = null,
    var fatherName: String? = null,
    var country: Country? = null,
    var cityOfIssue: City? = null,
    var idCardSerial1: String? = null,
    var gender: Gender2? = null,
    var creationTime: Long? = null,
    var idCardSerial2: String? = null,
    var nation: Nation? = null,
    var lastModificationTime: Long? = null,
    var militaryService: Any? = null,
    var saveMethod: Int? = null,
    var educations: List<Any>? = null,
    var baseBloadGroup: Any? = null,
    var confirmed: Boolean? = null,
    var ssn: String? = null,
    var cityOfBirth: City? = null,
    var marriage: Marriage? = null,
    var id2: Int? = null,
    var id: Long? = null,
    var forienRisuid: Any? = null,
    var personalLogs: List<Any>? = null,
    var dateOfDead: Long? = null,
    var languages: List<Any>? = null,
    var medicalExamination: List<Any>? = null,
    var lastModifiedBy: String? = null,
    var dateOfBirth: Long? = null,
    var relatives: List<Any>? = null,
    var firstName: String? = null,
    var foreignId: Any? = null,
    var nationalId: String? = null,
    var createdBy: String? = null,
    var idCardNumber: String? = null,
    var relationWithTamins: Any? = null,
    var accounts: List<Any>? = null,
    var contacts: List<Contact>? = null,
    var isForien: Any? = null
)

class BailExpireControl(
    var statusDate: String? = null,
    var code: String? = null,
    var description: String? = null,
    var status: String? = null

)

class BailType(
    var statusDate: String? = null,
    var code: String? = null,
    var description: String? = null,
    var status: String? = null

)

class Request(
    var deadDate: Any? = null,
    var requestType: RequestType? = null,
    var processStatus: ProcessStatus? = null,
    var creationTime: Long? = null,
    var lastModificationTime: Long? = null,
    var createdByUser: Any? = null,
    var lastModifiedBy: Any? = null,
    var personal: Int? = null,
    var dateOfBirth: Long? = null,
    var requestGroup: Any? = null,
    var portalRequestId: Any? = null,
    var ssn: String? = null,
    var foreignId: Any? = null,
    var deleted: Boolean? = null,
    var nationalId: String? = null,
    var isSendToBranch: Boolean? = null,
    var processId: Any? = null,
    var createdBy: String? = null,
    var organization: Any? = null,
    var modifiedByUser: Any? = null,
    var id: Long? = null,
    var forienRisuid: Any? = null,
    var mainOrganization: String? = null,
    var requestFrom: RequestFrom? = null
)

class Country(
    var parent: Parent? = null,
    var isDefault: Any? = null,
    var code: String? = null,
    var description: String? = null,
    var id: Int? = null,
    var title: String? = null,
    var type: Type? = null
)

class City(
    var parent: Parent? = null,
    var isDefault: Any? = null,
    var code: String? = null,
    var description: String? = null,
    var id: Int? = null,
    var title: String? = null,
    var type: Type? = null
)

class Gender2(
    var statusDate: Any? = null,
    var genderCode: String? = null,
    var genderDesc: String? = null,
    var status: Any? = null,

    )

class Marriage(
    var statusDate: Any? = null,
    var marriageCode: String? = null,
    var marriageDesc: String? = null,
    var status: Any? = null

)

class Nation(
    var statusDate: String? = null,
    var nationDesc: String? = null,
    var nationCode: String? = null,
    var status: String? = null,

    )

class Parent(
    var parent: Parent? = null,
    var isDefault: Any? = null,
    var code: String? = null,
    var description: String? = null,
    var id: Int? = null,
    var title: String? = null,
    var type: Type? = null

)

class ProcessStatus(
    var statusDesc: String? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var createdBy: Any? = null,
    var lastModifiedBy: Any? = null,
    var id: Int? = null

)

class RequestFrom(
    var requestFromDesc: String? = null,
    var requestFromId: Int? = null,
    var requestFromCode: String? = null

)

class RequestType(
    var requestTypeDesc: String? = null,
    var requestTypeId: Int? = null,
    var requestTypeCode: String? = null
)


class SubDominant(
    var howManyChildrenBefore: Any? = null,
    var bailExpireControl: BailExpireControl? = null,
    var creationTime: Long? = null,
    var lastModificationTime: Long? = null,
    var parentInsuranceId: String? = null,
    var dependentType: Any? = null,
    var lastModifiedBy: String? = null,
    var bailType: BailType? = null,
    var createdBy: String? = null,
    var relationWithTamin: Int? = null,
    var personalOfMainAudience: Any? = null,
    var id2: Int? = null,
    var dateOfExpire: Long? = null

)

class Type(
    var parent: Any? = null,
    var code: String? = null,
    var id: Int? = null,
    var title: String? = null,

    )


class Contact(
    var zipCode: String? = null,
    var dateOfFinish: Any? = null,
    var address: String? = null,
    var creationTime: Long? = null,
    var city: Any? = null,
    var lastModificationTime: Long? = null,
    var lastModifiedBy: String? = null,
    var mobile: Any? = null,
    var personal: Int? = null,
    var confirmed: Boolean? = null,
    var phoneNumber: String? = null,
    var deleted: Any? = null,
    var electronicAddress: Any? = null,
    var createdBy: String? = null,
    var dateOfStart: Long? = null,
    var lastModifiedUser: Any? = null,
    var id2: Int? = null,
    var id: Long? = null,
    var createdUser: Any? = null

)


fun PersonalInfoModelRes.asDomainModel(): PersonalInfoModel {
    return PersonalInfoModel(
        id = this.id.toString(),
        firstName = this.personal?.firstName,
        lastName = this.personal?.lastName,
        fatherName = this.personal?.fatherName,
        nationalCode = this.personal?.nationalId,
        insuranceNumber = this.insuranceId,
        birthDate = this.personal?.dateOfBirth?.let { ConvertDate.convertTimestampToPersianDate(it) }
            ?: "",
        birthDateTimeStamp = this.personal?.dateOfBirth,
        mobileNumber = this.mobileNumber,

        phoneNumber = if (this.personal?.contacts?.isNullOrEmpty() == true) ""
        else this.personal?.contacts?.get(0)?.phoneNumber ?: "",

        zipCode = if (this.personal?.contacts?.isNullOrEmpty() == true) ""
        else this.personal?.contacts?.get(0)?.zipCode ?: "",

        address = if (this.personal?.contacts?.isNullOrEmpty() == true) ""
        else this.personal?.contacts?.get(0)?.address ?: "",

        childInsuranceId = this.insuranceId,
        deathDate = this.personal?.dateOfDead,
        gender = this.personal?.gender?.genderCode,
        idCardNumber = this.personal?.idCardNumber,
        )
}

fun List<PersonalInfoModelRes>.asDomainModel(): List<PersonalInfoModel> {
    return map {
        it.asDomainModel()
    }
}
