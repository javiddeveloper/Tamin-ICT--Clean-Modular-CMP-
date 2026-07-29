package com.tamin.taminhamrah.data.remote.models.services.occurrence

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


data class WorkshopSpecificationResponse(var data: WorkshopSpecification? = null) :
    BaseResponseNew()

class WorkshopSpecification {
    // Setter Methods
    // Getter Methods
    var sswn: String? = null
    var employerNationalCodes: String? = null
    var nation: Nation? = null
    var workshopRequests: String? = null
    var branch: Branch? = null
    var character: Character? = null
    var branchTitle: String? = null
    var workshopType: WorkshopType? = null
    var workshopApproveDate: String? = null
    var lastAddress: String? = null
    var incomOpDate: Float = 0f
    var inclusionDate: String? = null
    var brhCode: String? = null
    var period: Period? = null
    var activityName: String? = null
    var directorOrg: Any? = null
    var workshopRegisterDate: String? = null
    var workshopActivity: WorkshopActivity? = null
    var workshopStatus: WorkshopStatus? = null
    var incomUserId: String? = null
    var branchCode: String? = null
    var grade: String? = null
    var sendListPeriod: SendListPeriod? = null
    var placeTypeCode: String? = null
    var status: Float = 0f
    var parentWorkshop: String? = null
    var buildRequest: String? = null
    var workshopName: String? = null
    var sendListMethod: SendListMethod? = null
    var employerName: String? = null
    var workshopUnemployedStat: String? = null
    var actitvityCode: String? = null
    var decodedCreateDate: String? = null
    var lastAddressPostCode: String? = null
    var webServiceResultStatus: String? = null
    var recognizeMethod: RecognizeMethod? = null
    var workshopLicenseMainList: String? = null
    var claimOpDate: Float = 0f
    var createDate: String? = null
    var isNew: String? = null
    var userId: String? = null
    var claimUserId: String? = null
    var legalWorkshop: LegalWorkshop? = null
    var workshopKhalaf: String? = null
    var special: String? = null
    var workshopRate: WorkshopRate? = null
    var trade: String? = null
    var fromOtherBranch: String? = null
    var workshopId: String? = null
}

class WorkshopRate {
    // Setter Methods
    // Getter Methods
    var abnormalInsuranceRate: Float = 0f
    var statusDate: String? = null
    var maxGovernmentPourcentage: Float = 0f
    var rateCode: String? = null
    var normalEmployerInsuranceRate: Float = 0f
    var unemploymentRate: Float = 0f
    var rateDesc: String? = null
    var governmentPourcentage: Float = 0f
    var sendListResp: Float = 0f
    var abnormalEmployerInsuranceRate: Float = 0f
    var normalInsuranceRate: Float = 0f
    var lisenceControl: String? = null
    var status: String? = null
}

class LegalWorkshop {
    // Setter Methods
    // Getter Methods
    var establishmentDate: String? = null
    var workshop: String? = null
    var workshopName: String? = null
    var lastChangeDate: String? = null
    var publicWorkshopType: PublicWorkshopType? = null
    var legalFullInfo: LegalFullInfo? = null
    var legalWorkshopType: LegalWorkshopType? = null
    var createdt: Float = 0f
    var bank: String? = null
    var nationalId: String? = null
    var editdt: String? = null
    var registrationDate: Float = 0f
    var accountNumer: String? = null
    var id: Float = 0f
    var workshopCode: String? = null
    var registartionNumber: String? = null
    var brand: String? = null
    var parentNationalId: String? = null
    var createuid: String? = null
    var edituid: String? = null
    var transientWorkshopId: String? = null
    var statusCode: String? = null
    var workshopBranch: String? = null
}

class LegalWorkshopType {
    // Setter Methods
    // Getter Methods
    var code: String? = null
    var description: String? = null
}

class LegalFullInfo {
    // Setter Methods
    // Getter Methods
    var residency: String? = null
    var nationalCode: String? = null
    var branchList: String? = null
    var newService: String? = null
    var establishmentDate: String? = null
    var isBankRupt: Boolean = false
    var settleDate: String? = null
    var legalPersonType: String? = null
    var unitId: String? = null
    var id: String? = null
    var registerNumber: String? = null
    var state: String? = null
    var parentLegalPerson: String? = null
    var registerDate: String? = null
    var address: String? = null
    var followUpNo: String? = null
    var isSettle: Boolean = false
    var lastChangeDate: String? = null
    var isBreakUp: Boolean = false
    var breakUpDate: String? = null
    var message: String? = null
    var isBranch: Boolean = false
    var registerUnit: String? = null
    var bankRuptcyDate: String? = null
    var name: String? = null
    var successful1: Boolean = false
    var postCode: String? = null
}

class PublicWorkshopType {
    // Setter Methods
    // Getter Methods
    var code: String? = null
    var legalType: String? = null
    var description: String? = null
}

class RecognizeMethod {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var code: String? = null
    var description: String? = null
    var status: String? = null
}

class SendListMethod {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var methodDesc: String? = null
    var methodCode: String? = null
    var status: String? = null
}

class SendListPeriod {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var periodDesc: String? = null
    var periodCode: String? = null
    var status: String? = null
}

class WorkshopStatus {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var workshopStatusCode: String? = null
    var workshopStatusDesc: String? = null
    var status: String? = null
}

class WorkshopActivity {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var activityCode: String? = null
    var activityDesc: String? = null
    var orderCase: String? = null
    var status: String? = null
}

class Period {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var actionPeriodDesc: String? = null
    var actionPeriodCode: String? = null
    var status: String? = null
}

class WorkshopType {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var workshoptypeCode: String? = null
    var workshoptypeDesc: String? = null
    var status: String? = null
}

class Character {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var characterDesc: String? = null
    var characterCode: String? = null
    var status: String? = null
}

class Branch {
    // Setter Methods
    // Getter Methods
    var parent: Parent? = null
    var organizationCustomerType: String? = null
    var code: String? = null
    var organizationName: String? = null
    var children: ArrayList<Any> = ArrayList()
    var entityId: String? = null
    var organizationStatus: String? = null
    var organizationDetail: String? = null
}

class Parent {
    // Setter Methods
    // Getter Methods
    var parent: Parent? = null
    var organizationCustomerType: String? = null
    var code: String? = null
    var organizationName: String? = null
    var children: String? = null
    var entityId: String? = null
    var organizationStatus: String? = null
    var organizationDetail: String? = null
}

class Nation {
    // Setter Methods
    // Getter Methods
    var statusDate: String? = null
    var nationDesc: String? = null
    var nationCode: String? = null
    var status: String? = null
}

