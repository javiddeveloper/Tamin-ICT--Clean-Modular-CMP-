package com.tamin.taminhamrah.data.remote.models.services.contract

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class ContractResponseNew : ListDataModel<ContractInfoNew>()

@Parcelize
data class ContractInfoNew (
    var supportType: String? = null,
    var contractDate: String? = null,
    var contractRow: String? = null,
    var assignName: String? = null,
    var createUserFullName: @RawValue Any? = null,
    var managerName:@RawValue Any? = null,
    var branch: Branch? = null,
    var creatorSource: @RawValue Any? = null,
    var character: String? = null,
    var copyContractRequest: @RawValue Any? = null,
    var complementaryContractType: @RawValue Any? = null,
    var isConfirmed:Boolean?= false,
    var model: Model? = null,
    var contractAssignName: String? = null,
    var incomOpDate: @RawValue Any? = null,
    var assignersWorkshop: AssignersWorkshop? = null,
    var confidential: @RawValue Any? = null,
    var contractAssignType: String? = null,
    var certificateDate: @RawValue Any? = null,
    var workshop: Workshop? = null,
    var contractStartDate: String? = null,
    var contractNumber: String? = null,
    var incomUserId: @RawValue Any? = null,
    var contractAddendumState: @RawValue Any? = null,
    var debitNumber: String? = null,
    var contractAmount: Long = 0,
    var status :Int?= 0,
    var contractAddress: String? = null,
    var contractDescription: @RawValue Any? = null,
    var employerName: @RawValue Any? = null,
    var licenseDate :Int?= 0,
//    var confirmed :Boolean? = false,
    var contractCreationDecodedDate: String? = null,
    var licenseNumber: @RawValue Any? = null,
    var workshopPremiumRate: WorkshopPremiumRate? = null,
    var claimOpDate: @RawValue Any? = null,
    var contractSequence: String? = null,
    var createDate: String? = null,
    var eserviceRequestId: @RawValue Any? = null,
    var contractStatus: ContractStatus? = null,
    var contractAddendumLst: @RawValue Any? = null,
    var contractSubject: String? = null,
    var legalId: String? = null,
    var contractEndDate: String? = null,
    var fullName: @RawValue Any? = null,
    var isNew: @RawValue Any? = null,
    var birthDate: @RawValue Any? = null,
    var contractAssignRow: String? = null,
    var claimUserId: @RawValue Any? = null,
    var nationalId: @RawValue Any? = null,
    var fromOtherBranch: @RawValue Any? = null,
    var certificateNumber: @RawValue Any? = null,
    var createdBy: String? = null,
    var parentContract: @RawValue Any? = null,
    var ignoreInquiry: @RawValue Any? = null,
    var contractAssignCode: String? = null,
):Parcelable{

    fun createKeyValue(item: ContractInfoNew): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("کد کارگاه", item.workshop?.workshopId ?: "-"))
        keyValueList.add(KeyValueModel("نام پیمانکار", item.workshop?.workshopName ?: "-"))
        keyValueList.add(KeyValueModel("شماره قرارداد", item.contractNumber ?: "-"))
        keyValueList.add(KeyValueModel("ردیف پیمان", item.contractRow ?: "-"))
        keyValueList.add(KeyValueModel("وضعیت", contractStatus?.contractStatusDescription ?: "-"))
        keyValueList.add(KeyValueModel("شعبه", item.branch?.organizationName ?: "-"))
        return keyValueList
    }
    fun createKeyValueDetails(item: ContractInfoNew): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نوع پیمان", item.workshop?.workshopUnemployedStat ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ قرارداد", Utility.getDateSeparator(item.contractDate)))
        keyValueList.add(KeyValueModel("شروع قرارداد", Utility.getDateSeparator(item.contractStartDate)))
        keyValueList.add(KeyValueModel("پایان قرارداد", Utility.getDateSeparator(item.contractEndDate)))
        keyValueList.add(KeyValueModel("نرخ حق بیمه", item.workshopPremiumRate?.rateDesc ?: "-"))
        keyValueList.add(KeyValueModel("شناسه واگذارنده", item.model?.nationalCode ?: "-"))
        keyValueList.add(KeyValueModel("نام واگذارنده", item.model?.name ?: "-"))
//        keyValueList.add(KeyValueModel("مبلغ اولیه قرارداد", item.branch?.organizationName ?: "-"))
        return keyValueList
    }
}


@Parcelize
data class AssignersWorkshop (
    var sswn: @RawValue Any? = null,
    var parentWorkshop: @RawValue Any? = null,
    var buildRequest: @RawValue Any? = null,
    var employerNationalCodes: @RawValue Any? = null,
    var nation: Nation? = null,
    var workshopName: String? = null,
    var sendListMethod: SendListMethod? = null,
    var employerName: @RawValue Any? = null,
    var workshopUnemployedStat: String? = null,
    var workshopRequests: @RawValue Any? = null,
    var actitvityCode: String? = null,
    var decodedCreateDate: @RawValue Any? = null,
    var branch: @RawValue Any? = null,
    var character: Character? = null,
    var webServiceResultStatus: @RawValue Any? = null,
    var branchTitle: @RawValue Any? = null,
    var recognizeMethod: RecognizeMethod? = null,
    var workshopType: WorkshopType? = null,
    var workshopLicenseMainList: @RawValue Any? = null,
    var workshopApproveDate: String? = null,
    var lastAddress: @RawValue Any? = null,
    var claimOpDate: @RawValue Any? = null,
    var incomOpDate: @RawValue Any? = null,
    var inclusionDate: String? = null,
    var createDate: String? = null,
    var brhCode: String? = null,
    var period: Period? = null,
    var activityName: @RawValue Any? = null,
    var directorOrg: DirectorOrg? = null,
    var workshopRegisterDate: String? = null,
    var workshopActivity: @RawValue Any? = null,
    var isNew: @RawValue Any? = null,
    var workshopStatus: WorkshopStatus? = null,
    var userId: String? = null,
    var incomUserId: @RawValue Any? = null,
    var claimUserId: @RawValue Any? = null,
    var legalWorkshop: @RawValue Any? = null,
    var branchCode: String? = null,
    var workshopKhalaf: @RawValue Any? = null,
    var workshopRate: WorkshopRate? = null,
    var trade: @RawValue Any? = null,
    var fromOtherBranch: @RawValue Any? = null,
    var grade: @RawValue Any? = null,
    var sendListPeriod: SendListPeriod? = null,
    var workshopId: String? = null,
    var placeTypeCode: @RawValue Any? = null,
    var status: @RawValue Any? = null,
):Parcelable

@Parcelize
data class Branch (
    var parent: Parent? = null,
    var actKey :Int?= 0,
    var code: String? = null,
    var organizationName: String? = null,
    var children: ArrayList<Child>? = null,
    var entityId: String? = null,
    var type: String? = null,
):Parcelable

@Parcelize
data class Character (
    var statusDate: String? = null,
    var characterDesc: String? = null,
    var characterCode: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class Child (
    var parent: Parent? = null,
    var actKey:Int? = 0,
    var code: String? = null,
    var organizationName: String? = null,
    var children: @RawValue Any? = null,
    var entityId: String? = null,
    var type: String? = null,
):Parcelable

@Parcelize
data class ContractStatus (
    var statusDate: String? = null,
    var contractStatusDescription: String? = null,
    var contractStatusCode: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class DirectorOrg (
    var statusDate: String? = null,
    var directorDesc: String? = null,
    var directorCode: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class LegalWorkshop (
    var establishmentDate: String? = null,
    var workshop: @RawValue Any? = null,
    var workshopName: @RawValue Any? = null,
    var lastChangeDate: String? = null,
    var publicWorkshopType: PublicWorkshopType? = null,
    var legalFullInfo: @RawValue Any? = null,
    var legalWorkshopType: LegalWorkshopType? = null,
    var createdt: @RawValue Any? = null,
    var bank: @RawValue Any? = null,
    var nationalId: String? = null,
    var editdt: @RawValue Any? = null,
    var registrationDate: @RawValue Any? = null,
    var accountNumer: @RawValue Any? = null,
    var id :Long?= 0,
    var workshopCode: @RawValue Any? = null,
    var registartionNumber: String? = null,
    var brand: @RawValue Any? = null,
    var parentNationalId: @RawValue Any? = null,
    var createuid: String? = null,
    var edituid: @RawValue Any? = null,
    var transientWorkshopId: @RawValue Any? = null,
    var statusCode: String? = null,
    var workshopBranch: @RawValue Any? = null,
):Parcelable

@Parcelize
data class LegalWorkshopType (
    var code: String? = null,
    var description: String? = null,
):Parcelable

@Parcelize
data class Model (
    var residency: @RawValue Any? = null,
    var nationalCode: String? = null,
    var branchList: @RawValue Any? = null,
    var newService:Boolean? = false,
    var establishmentDate: String? = null,
    var isBankRupt:Boolean? = false,
    var settleDate: @RawValue Any? = null,
    var legalPersonType: String? = null,
    var unitId: @RawValue Any? = null,
    var id: String? = null,
    var registerNumber: String? = null,
    var state: String? = null,
    var parentLegalPerson: @RawValue Any? = null,
    var registerDate: String? = null,
    var address: String? = null,
    var followUpNo: String? = null,
    var isSettle:Boolean? = false,
    var lastChangeDate: String? = null,
    var isBreakUp:Boolean? = false,
    var breakUpDate: @RawValue Any? = null,
    var message: @RawValue Any? = null,
    var isBranch :Boolean? = false,
    var registerUnit: @RawValue Any? = null,
    var bankRuptcyDate: @RawValue Any? = null,
    var name: String? = null,
    var successful1:Boolean? = false,
    var postCode: String? = null,
):Parcelable

@Parcelize
data class Nation (
    var statusDate: String? = null,
    var nationDesc: String? = null,
    var nationCode: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class Parent (
    var parent: Parent? = null,
    var actKey:Int? = 0,
    var code: String? = null,
    var organizationName: String? = null,
    var children: @RawValue Any? = null,
    var entityId: String? = null,
    var type: String? = null,
):Parcelable

@Parcelize
data class Period (
    var statusDate: String? = null,
    var actionPeriodDesc: String? = null,
    var actionPeriodCode: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class PublicWorkshopType (
    var code: String? = null,
    var legalType: String? = null,
    var description: String? = null,
):Parcelable

@Parcelize
data class RecognizeMethod (
    var statusDate: String? = null,
    var code: String? = null,
    var description: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class SendListMethod (
    var statusDate: String? = null,
    var methodDesc: String? = null,
    var methodCode: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class SendListPeriod (
    var statusDate: String? = null,
    var periodDesc: String? = null,
    var periodCode: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class Workshop (
    var sswn: String? = null,
    var parentWorkshop: @RawValue Any? = null,
    var buildRequest: @RawValue Any? = null,
    var employerNationalCodes: @RawValue Any? = null,
    var nation: Nation? = null,
    var workshopName: String? = null,
    var sendListMethod: SendListMethod? = null,
    var employerName: @RawValue Any? = null,
    var workshopUnemployedStat: String? = null,
    var workshopRequests: @RawValue Any? = null,
    var actitvityCode: String? = null,
    var decodedCreateDate: @RawValue Any? = null,
    var branch: @RawValue Any? = null,
    var character: Character? = null,
    var webServiceResultStatus: @RawValue Any? = null,
    var branchTitle: @RawValue Any? = null,
    var recognizeMethod: RecognizeMethod? = null,
    var workshopType: WorkshopType? = null,
    var workshopLicenseMainList: @RawValue Any? = null,
    var workshopApproveDate: String? = null,
    var lastAddress: @RawValue Any? = null,
    var claimOpDate: @RawValue Any? = null,
    var incomOpDate: @RawValue Any? = null,
    var inclusionDate: String? = null,
    var createDate: String? = null,
    var brhCode: String? = null,
    var period: Period? = null,
    var activityName: @RawValue Any? = null,
    var directorOrg: DirectorOrg? = null,
    var workshopRegisterDate: String? = null,
    var workshopActivity: @RawValue Any? = null,
    var isNew: @RawValue Any? = null,
    var workshopStatus: WorkshopStatus? = null,
    var userId: String? = null,
    var incomUserId: @RawValue Any? = null,
    var claimUserId: @RawValue Any? = null,
    var legalWorkshop: LegalWorkshop? = null,
    var branchCode: String? = null,
    var workshopKhalaf: String? = null,
    var workshopRate: WorkshopRate? = null,
    var trade: @RawValue Any? = null,
    var fromOtherBranch: @RawValue Any? = null,
    var grade: @RawValue Any? = null,
    var sendListPeriod: SendListPeriod? = null,
    var workshopId: String? = null,
    var placeTypeCode: @RawValue Any? = null,
    var status: @RawValue Any? = null,
):Parcelable

@Parcelize
data class WorkshopPremiumRate (
    var abnormalInsuranceRate :Int?= 0,
    var statusDate: String? = null,
    var maxGovernmentPourcentage:Int?= 0,
    var rateCode: String? = null,
    var normalEmployerInsuranceRate :Int?= 0,
    var unemploymentRate :Int?= 0,
    var rateDesc: String? = null,
    var governmentPourcentage :Int?= 0,
    var sendListResp :Int?= 0,
    var abnormalEmployerInsuranceRate :Int?= 0,
    var normalInsuranceRate:Int?= 0,
    var lisenceControl: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class WorkshopRate (
    var abnormalInsuranceRate :Int?= 0,
    var statusDate: String? = null,
    var maxGovernmentPourcentage :Int?= 0,
    var rateCode: String? = null,
    var normalEmployerInsuranceRate :Int?= 0,
    var unemploymentRate :Int?= 0,
    var rateDesc: String? = null,
    var governmentPourcentage :Int?= 0,
    var sendListResp :Int?= 0,
    var abnormalEmployerInsuranceRate :Int?= 0,
    var normalInsuranceRate :Int?= 0,
    var lisenceControl: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class WorkshopStatus (
    var statusDate: String? = null,
    var workshopStatusCode: String? = null,
    var workshopStatusDesc: String? = null,
    var status: String? = null,
):Parcelable

@Parcelize
data class WorkshopType (
    var statusDate: String? = null,
    var workshoptypeCode: String? = null,
    var workshoptypeDesc: String? = null,
    var status: String? = null,
):Parcelable


