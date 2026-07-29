package com.tamin.taminhamrah.data.remote.models.services.workshop

import com.tamin.taminhamrah.data.entity.WorkshopStackHolderModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class WorkshopStackHolderResponse : ListDataModel<WorkshopStackHolder>()

data class WorkshopStackHolder(
    var endDate: Any? = null,
    var stackId: Int? = null,
    var workshopNationalCode: Any? = null,
    var mobile: String? = null,
    var birthDate: Long? = null,
    var branch: Branch? = null,
    var telephon: String? = null,
    var percent: Any? = null,
    var userId: String? = null,
    var personalRegistrationOffice: PersonalRegistrationOffice? = null,
    var nationalId: String? = null,
    var accessCode: Any? = null,
    var officeStatus: Any? = null,
    var workshopId: WorkshopId? = null,
    var stackType: String? = null,
    var startDate: Long? = null,
    var email: String? = null,
)

data class Child(
    var parent: Parent? = null,
    var actKey: Int? = null,
    var code: String? = null,
    var organizationName: String? = null,
    var children: Any? = null,
    var entityId: String? = null,
    var type: String? = null
)

data class DirectorOrg(
    var statusDate: String? = null,
    var directorDesc: String? = null,
    var directorCode: String? = null,
    var status: String? = null
)

data class LegalWorkshop(
    var establishmentDate: Any? = null,
    var workshop: Any? = null,
    var workshopName: Any? = null,
    var lastChangeDate: Any? = null,
    var publicWorkshopType: Any? = null,
    var legalFullInfo: Any? = null,
    var legalWorkshopType: LegalWorkshopType? = null,
    var createdt: Long? = null,
    var bank: Any? = null,
    var nationalId: String? = null,
    var editdt: Any? = null,
    var registrationDate: Any? = null,
    var accountNumer: Any? = null,
    var id: Int? = null,
    var workshopCode: Any? = null,
    var registartionNumber: Any? = null,
    var brand: Any? = null,
    var parentNationalId: Any? = null,
    var createuid: String? = null,
    var edituid: Any? = null,
    var statusCode: Any? = null,
    var workshopBranch: Any? = null
)

data class LegalWorkshopType(
    var code: String? = null,
    var description: String? = null,
)

data class Nation(

    var statusDate: String? = null,
    var nationDesc: String? = null,
    var nationCode: String? = null,
    var status: String? = null

)

data class PersonalRegistrationOffice(
    var deadDate: Any? = null,
    var lastName: String? = null,
    var fatherName: String? = null,
    var deadStat: Boolean? = null,
    var idCardSerial1: String? = null,
    var gender: String? = null,
    var creationTime: Long? = null,
    var idCardSerial2: String? = null,
    var lastModificationTime: Long? = null,
    var lastModifiedBy: String? = null,
    var deadRegisterDate: Any? = null,
    var dateOfBirth: Long? = null,
    var birthDate: String? = null,
    var firstName: String? = null,
    var nationalId: String? = null,
    var createdBy: String? = null,
    var idCardNumber: String? = null,
    var insuranceId: Any? = null,
    var id: Int? = null
)

data class SendListMethod(
    var statusDate: String? = null,
    var methodDesc: String? = null,
    var methodCode: String? = null,
    var status: String? = null
)

data class WorkshopId(
    var sswn: Any? = null,
    var parentWorkshop: Any? = null,
    var buildRequest: Any? = null,
    var nation: Nation? = null,
    var workshopName: String? = null,
    var sendListMethod: SendListMethod? = null,
    var employerName: Any? = null,
    var workshopUnemployedStat: String? = null,
    var workshopRequests: Any? = null,
    var actitvityCode: String? = null,
    var decodedCreateDate: Any? = null,
    var branch: Any? = null,
    var character: Character? = null,
    var webServiceResultStatus: Any? = null,
    var branchTitle: Any? = null,
    var recognizeMethod: RecognizeMethod? = null,
    var workshopType: WorkshopType? = null,
    var workshopApproveDate: String? = null,
    var lastAddress: Any? = null,
    var claimOpDate: Any? = null,
    var incomOpDate: Any? = null,
    var inclusionDate: String? = null,
    var createDate: String? = null,
    var brhCode: String? = null,
    var period: Period? = null,
    var activityName: Any? = null,
    var directorOrg: DirectorOrg? = null,
    var workshopRegisterDate: String? = null,
    var workshopActivity: Any? = null,
    var isNew: Any? = null,
    var workshopStatus: WorkshopStatus? = null,
    var userId: String? = null,
    var incomUserId: Any? = null,
    var claimUserId: Any? = null,
    var legalWorkshop: LegalWorkshop? = null,
    var branchCode: String? = null,
    var workshopKhalaf: String? = null,
    var workshopRate: WorkshopRate? = null,
    var trade: Any? = null,
    var fromOtherBranch: Any? = null,
    var grade: Any? = null,
    var sendListPeriod: SendListPeriod? = null,
    var workshopId: String? = null,
    var placeTypeCode: Any? = null,
    var status: Any? = null
)

fun WorkshopStackHolder.asDomainModel(): WorkshopStackHolderModel {
    return WorkshopStackHolderModel(
        nationalCode = this.nationalId,
        fullName = "${this.personalRegistrationOffice?.firstName} ${this.personalRegistrationOffice?.lastName}",
        fatherName = this.personalRegistrationOffice?.fatherName,
        birthDate = this.birthDate,
        stackType = this.stackType

    )
}

fun List<WorkshopStackHolder>.asDomainModel(): List<WorkshopStackHolderModel> {
    return map {
        it.asDomainModel()
    }
}