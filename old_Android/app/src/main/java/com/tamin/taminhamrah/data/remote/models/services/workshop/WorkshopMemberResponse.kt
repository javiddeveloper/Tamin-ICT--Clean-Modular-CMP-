package com.tamin.taminhamrah.data.remote.models.services.workshop

import com.tamin.taminhamrah.data.entity.WorkshopMemberModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.remote.models.services.Nation

class WorkshopMemberResponse : ListDataModel<WorkshopMember>()

data class WorkshopMember(

    var workshop: WorkshopInfoResponse? = null,
    var insurance: Insurance? = null,
    var relationType: RelationType? = null,
    var leavingWorkStatus: String? = null,
    var leavingWorkDate: String? = null,
    var specialSubType: String? = null
)

data class Gender(
    var genderCode: String? = null,
    var genderDesc: String? = null,
    var status: Any? = null,
    var statusDate: Any? = null
)

data class Insurance(
    var id: String? = null,
    var lastName: String? = null,
    var firstName: String? = null,
    var fatherName: String? = null,
    var gender: Gender? = null,
    var nation: Nation? = null,
    var nationalId: String? = null,
    var idCardNumber: String? = null,
    var idCardSerial1: String? = null,
    var idCardSerial2: String? = null,
    var cityOfIssue: String? = null,
    var dateOfBirth: String? = null,
    var cityOfBirth: String? = null,
    var expCityCode: String? = null,
    var registerDate: String? = null,
    var requetNumber: Any? = null,
    var creationTime: String? = null,
    var createdBy: String? = null,
    var approveDate: String? = null,
    var customerType: String? = null,
    var recognizeMethod: RecognizeMethod? = null,
    var isuTypeCode: String? = null,
    var isuStatCode: String? = null,
    var flagTaeed: String? = null,
    var flagPrint: String? = null,
    var recogMethodDate: String? = null,
    var finalTaeed: String? = null,
    var flagSabt2: Any? = null,
    var flagTaeed2: Any? = null,
    var flagNoTaeed2: Any? = null,
    var fisuId: Any? = null,
    var ssn: String? = null,
    var branchCode: String? = null,
    var brchCode: String? = null,
    var natcin: Any? = null
)

data class RelationType(
    var relationTypeCode: String? = null,
    var relationTypeDescription: String? = null,
    var inclusiveInsurance: String? = null,
    var status: String? = null,
    var statusDate: String? = null
)

fun WorkshopMember.asDomainModel(): WorkshopMemberModel {
    return WorkshopMemberModel(
        insuranceNumber = this.insurance?.id ?:"-",
        fullName = "${this.insurance?.firstName} ${this.insurance?.lastName}",

        nationalCode = this.insurance?.nationalId ?:"-",
        ssn = this.insurance?.idCardNumber ?:"-",
        fatherName = this.insurance?.fatherName ?:"-",
        nationality = this.insurance?.nation?.nationDesc ?:"-",
        relationType = this.relationType?.relationTypeDescription ?:"-",
        workStatus = this.leavingWorkStatus ?:"-",
        leavingWorkDate = this.leavingWorkDate
    )
}

fun List<WorkshopMember>.asDomainModel(): List<WorkshopMemberModel> {
    return map {
        it.asDomainModel()
    }
}
