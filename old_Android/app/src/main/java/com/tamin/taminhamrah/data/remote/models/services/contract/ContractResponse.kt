package com.tamin.taminhamrah.data.remote.models.services.contract

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class ContractResponse : ListDataModel<ContractInfo>()

@Parcelize
class ContractInfo(
    var supportType: String? = null,
    var contractDate: String? = null,
    var contractRow: String? = null,
    var assignName: String? = null,
    var createUserFullName: String? = null,
    var managerName: String? = null,
    var branch:@RawValue Branch? = null,
    var creatorSource:@RawValue Any? = null,
    var character:@RawValue Any? = null,
    var copyContractRequest:@RawValue Any? = null,
    var complementaryContractType:@RawValue Any? = null,
    var isConfirmed: Boolean = false,
    var model: String? = null,
    var contractAssignName: String? = null,
    var incomOpDate: String? = null,
    var assignersWorkshop:@RawValue AssignersWorkshop? = null,
    var confidential:@RawValue Any? = null,
    var contractAssignType: String? = null,
    var certificateDate: String? = null,
    var workshop:@RawValue Workshop? = null,
    var contractStartDate: String? = null,
    var contractNumber: String? = null,
    var incomUserId: String? = null,
    var contractAddendumState: String? = null,
    var debitNumber: String? = null,
    var contractAmount: Long? = null,
    var status:@RawValue Any? = null,
    var contractAddress: String? = null,
    var contractDescription:@RawValue Any? = null,
    var employerName: String? = null,
    var licenseDate: String? = null,
    var confirmed: Boolean? = null,
    var contractCreationDecodedDate: String? = null,
    var licenseNumber: String? = null,
    var workshopPremiumRate: @RawValue WorkshopPremiumRate? = null,
    var claimOpDate: String? = null,
    var contractSequence: String? = null,
    var createDate: String? = null,
    var eserviceRequestId: String? = null,
    var contractStatus: @RawValue ContractStatus? = null,
    var contractAddendumLst:@RawValue Any? = null,
    var contractSubject: String? = null,
    var legalId: String? = null,
    var contractEndDate: String? = null,
    var fullName: String? = null,
    var isNew: Boolean? = null,
    var birthDate: String? = null,
    var contractAssignRow: String? = null,
    var claimUserId: String? = null,
    var nationalId: String? = null,
    var fromOtherBranch:@RawValue Any? = null,
    var certificateNumber: String? = null,
    var createdBy: String? = null,
    var parentContract:@RawValue Any? = null,
    var ignoreInquiry:@RawValue Any? = null,
    var contractAssignCode: String? = null,

    //for assigner Contracts:
    var assigner:@RawValue Employer? = null,
    var employer:@RawValue Employer? = null,

    ):Parcelable {

    fun createKeyValue(item: ContractInfo): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("کد گارگاه", item.workshop?.workshopId ?: "-"))
        keyValueList.add(KeyValueModel("ردیف پیمان", item.contractRow ?: "-"))
        keyValueList.add(KeyValueModel("شماره قرارداد", item.contractNumber ?: "-"))
        keyValueList.add(KeyValueModel("وضعیت", contractStatus?.contractStatusDescription ?: "-"))
        keyValueList.add(KeyValueModel("شعبه", item.branch?.organizationName ?: "-"))
        return keyValueList
    }

    fun createKeyValue2(item: ContractInfo): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(
            KeyValueModel(
                "تاریخ قرارداد",
                Utility.getDateSeparator(item.contractDate)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "شروع قرارداد",
                Utility.getDateSeparator(item.contractStartDate)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "پایان قرارداد",
                Utility.getDateSeparator(item.contractEndDate)
            )
        )
        keyValueList.add(KeyValueModel("مبلغ حق بیمه ماهانه", item.workshopPremiumRate?.rateDesc ?: "-"))
        keyValueList.add(KeyValueModel("شناسه واگذارنده", item.model ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "نام واگذارنده",
                item.assignersWorkshop?.workshopName ?: "-"
            )
        )
        keyValueList.add(
            KeyValueModel(
                "مبلغ اولیه قرارداد",
                Utility.getRialWithSeparator(item.contractAmount)
            )
        )
        return keyValueList
    }

    fun createKeyValueAssigner(item: ContractInfo): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نام پیمانکار", item.employer?.workshopName ?: "-"))
        keyValueList.add(KeyValueModel("کد گارگاه", item.employer?.workshopId ?: "-"))
        keyValueList.add(KeyValueModel("شعبه", item.employer?.branch?.organizationName ?: "-"))
        keyValueList.add(KeyValueModel("ردیف پیمان", item.contractRow ?: "-"))
        keyValueList.add(KeyValueModel("شماره قرارداد", item.contractNumber ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "تاریخ قرارداد",
                Utility.getDateSeparator(item.contractDate)
            )
        )
        return keyValueList
    }

    fun createKeyValueContractInfo(item: ContractInfo?): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("ردیف پیمان", item?.contractRow ?: "-"))
        keyValueList.add(KeyValueModel("شماره قرارداد", item?.contractNumber ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "تاریخ قرارداد",
                Utility.getDateSeparator(item?.contractDate)
            )
        )
        keyValueList.add(KeyValueModel("عنوان قرارداد", item?.contractSubject ?: "-"))
        return keyValueList
    }

    fun createKeyValueAssignerDetail(item: ContractInfo?): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نام کارگاه", item?.assigner?.workshopName ?: "-"))
        keyValueList.add(KeyValueModel("کد گارگاه", item?.assigner?.workshopId ?: "-"))
        keyValueList.add(KeyValueModel("کد/شناسه ملی", item?.assigner?.nationalId ?: "-"))
        keyValueList.add(KeyValueModel("شعبه", item?.assigner?.branch?.organizationName ?: "-"))
        keyValueList.add(KeyValueModel("آخرین آدرس کارگاه", item?.assigner?.address ?: "-"))

        return keyValueList
    }

    fun createKeyValueContractor(item: ContractInfo?): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نام کارگاه", item?.employer?.workshopName ?: "-"))
        keyValueList.add(KeyValueModel("کد گارگاه", item?.employer?.workshopId ?: "-"))
        keyValueList.add(KeyValueModel("کد/شناسه ملی", item?.employer?.nationalId ?: "-"))
        keyValueList.add(KeyValueModel("شعبه", item?.employer?.branch?.organizationName ?: "-"))
        keyValueList.add(KeyValueModel("آخرین آدرس کارگاه", item?.employer?.address ?: "-"))
        return keyValueList
    }

    @Parcelize
    data class AssignersWorkshop(
        var sswn: String? = null,
        var parentWorkshop: ParentWorkshop? = null,
        var buildRequest: @RawValue Any?  = null,
        var nation: Nation? = null,
        var workshopName: String? = null,
        var sendListMethod: @RawValue Any?  = null,
        var employerName: @RawValue Any?  = null,
        var workshopUnemployedStat: String? = null,
        var workshopRequests: @RawValue Any?  = null,
        var actitvityCode: String? = null,
        var decodedCreateDate: @RawValue Any?  = null,
        var branch: @RawValue Any?  = null,
        var character: Character? = null,
        var webServiceResultStatus: @RawValue Any?  = null,
        var branchTitle: @RawValue Any?  = null,
        var recognizeMethod: @RawValue Any?  = null,
        var workshopType: WorkshopType? = null,
        var workshopApproveDate: String? = null,
        var lastAddress: @RawValue Any?  = null,
        var claimOpDate: Long? = null,
        var incomOpDate: Long? = null,
        var inclusionDate: String? = null,
        var createDate: String? = null,
        var brhCode: String? = null,
        var period: Period? = null,
        var activityName: @RawValue Any?  = null,
        var directorOrg: @RawValue Any?  = null,
        var workshopRegisterDate: String? = null,
        var workshopActivity: @RawValue Any?  = null,
        var isNew: String? = null,
        var workshopStatus: WorkshopStatus? = null,
        var userId: String? = null,
        var incomUserId: String? = null,
        var claimUserId: String? = null,
        var legalWorkshop: LegalWorkshop? = null,
        var branchCode: String? = null,
        var workshopKhalaf: String? = null,
        var workshopRate: WorkshopRate? = null,
        var trade: @RawValue Any?  = null,
        var fromOtherBranch: String? = null,
        var grade: @RawValue Any?  = null,
        var sendListPeriod: SendListPeriod? = null,
        var workshopId: String? = null,
        var placeTypeCode: PlaceTypeCode? = null,
        var status: Int? = null
    ):Parcelable

    @Parcelize
    data class Branch(
        var parent: Parent? = null,
        var actKey: Int? = null,
        var code: String? = null,
        var organizationName: String? = null,
        var children: List<Child>? = null,
        var entityId: String? = null,
        var type: String? = null
    ):Parcelable

    @Parcelize
    data class Child(
        var parent: Parent? = null,
        var actKey: Int? = null,
        var code: String? = null,
        var organizationName: String? = null,
        var children: Child? = null,
        var entityId: String? = null,
        var type: String? = null
    ):Parcelable

    @Parcelize
    data class Character(
        var statusDate: String? = null,
        var characterDesc: String? = null,
        var characterCode: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class ContractStatus(
        var statusDate: String? = null,
        var contractStatusDescription: String? = null,
        var contractStatusCode: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class DirectorOrg(
        var statusDate: String? = null,
        var directorDesc: String? = null,
        var directorCode: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class LegalWorkshop(
        var establishmentDate: @RawValue Any?  = null,
        var workshop: @RawValue Any?  = null,
        var workshopName: @RawValue Any?  = null,
        var lastChangeDate: @RawValue Any?  = null,
        var publicWorkshopType: PublicWorkshopType? = null,
        var legalFullInfo: @RawValue Any?  = null,
        var legalWorkshopType: LegalWorkshopType? = null,
        var createdt: @RawValue Any?  = null,
        var bank: @RawValue Any?  = null,
        var nationalId: String? = null,
        var editdt: @RawValue Any?  = null,
        var registrationDate: @RawValue Any?  = null,
        var accountNumer: @RawValue Any?  = null,
        var id: Int? = null,
        var workshopCode: @RawValue Any?  = null,
        var registartionNumber: @RawValue Any?  = null,
        var brand: @RawValue Any?  = null,
        var parentNationalId: @RawValue Any?  = null,
        var createuid: @RawValue Any?  = null,
        var edituid: @RawValue Any?  = null,
        var statusCode: @RawValue Any?  = null,
        var workshopBranch: @RawValue Any?  = null
    ):Parcelable

    @Parcelize
    data class LegalWorkshopType(
        var code: String? = null,
        var description: String? = null
    ):Parcelable

    @Parcelize
    data class Nation(
        var statusDate: String? = null,
        var nationDesc: String? = null,
        var nationCode: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class Parent(
        var parent: Parent? = null,
        var actKey: Int? = null,
        var code: String? = null,
        var organizationName: String? = null,
        var children: @RawValue Any?  = null,
        var entityId: String? = null,
        var type: String? = null
    ):Parcelable

    @Parcelize
    data class ParentWorkshop(
        var sswn: String? = null,
        var parentWorkshop: @RawValue Any?  = null,
        var buildRequest: @RawValue Any?  = null,
        var nation: Nation? = null,
        var workshopName: String? = null,
        var sendListMethod: SendListMethod? = null,
        var employerName: @RawValue Any?  = null,
        var workshopUnemployedStat: String? = null,
        var workshopRequests: @RawValue Any?  = null,
        var actitvityCode: String? = null,
        var decodedCreateDate: @RawValue Any?  = null,
        var branch: @RawValue Any?  = null,
        var character: Character? = null,
        var webServiceResultStatus: @RawValue Any?  = null,
        var branchTitle: @RawValue Any?  = null,
        var recognizeMethod: RecognizeMethod? = null,
        var workshopType: WorkshopType? = null,
        var workshopApproveDate: String? = null,
        var lastAddress: @RawValue Any?  = null,
        var claimOpDate: @RawValue Any?  = null,
        var incomOpDate: @RawValue Any?  = null,
        var inclusionDate: @RawValue Any?  = null,
        var createDate: String? = null,
        var brhCode: String? = null,
        var period: Period? = null,
        var activityName: @RawValue Any?  = null,
        var directorOrg: DirectorOrg? = null,
        var workshopRegisterDate: String? = null,
        var workshopActivity: @RawValue Any?  = null,
        var isNew: @RawValue Any?  = null,
        var workshopStatus: WorkshopStatus? = null,
        var userId: String? = null,
        var incomUserId: @RawValue Any?  = null,
        var claimUserId: @RawValue Any?  = null,
        var legalWorkshop: @RawValue Any?  = null,
        var branchCode: String? = null,
        var workshopKhalaf: @RawValue Any?  = null,
        var workshopRate: WorkshopRate? = null,
        var trade: @RawValue Any?  = null,
        var fromOtherBranch: @RawValue Any?  = null,
        var grade: @RawValue Any?  = null,
        var sendListPeriod: SendListPeriod? = null,
        var workshopId: String? = null,
        var placeTypeCode: @RawValue Any?  = null,
        var status: @RawValue Any?  = null
    ):Parcelable

    @Parcelize
    data class Period(
        var statusDate: String? = null,
        var actionPeriodDesc: String? = null,
        var actionPeriodCode: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class PlaceTypeCode(
        var statusDate: String? = null,
        var adrressTypeCode: String? = null,
        var adrressTypeDesc: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class PublicWorkshopType(
        var code: String? = null,
        var legalType: String? = null,
        var description: String? = null
    ):Parcelable


    @Parcelize
    data class RecognizeMethod(
        var statusDate: String? = null,
        var code: String? = null,
        var description: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class SendListMethod(
        var statusDate: String? = null,
        var methodDesc: String? = null,
        var methodCode: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class SendListPeriod(
        var statusDate: String? = null,
        var periodDesc: String? = null,
        var periodCode: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class Workshop(
        var sswn: @RawValue Any?  = null,
        var parentWorkshop: @RawValue Any?  = null,
        var buildRequest: @RawValue Any?  = null,
        var nation: Nation? = null,
        var workshopName: String? = null,
        var sendListMethod: SendListMethod? = null,
        var employerName: @RawValue Any?  = null,
        var workshopUnemployedStat: String? = null,
        var workshopRequests: @RawValue Any?  = null,
        var actitvityCode: String? = null,
        var decodedCreateDate: @RawValue Any?  = null,
        var branch: @RawValue Any?  = null,
        var character: Character? = null,
        var webServiceResultStatus: @RawValue Any?  = null,
        var branchTitle: @RawValue String?  = null,
        var recognizeMethod: RecognizeMethod? = null,
        var workshopType: WorkshopType? = null,
        var workshopApproveDate: String? = null,
        var lastAddress: String? = null,
        var claimOpDate: @RawValue Any?  = null,
        var incomOpDate: @RawValue Any?  = null,
        var inclusionDate: String? = null,
        var createDate: String? = null,
        var brhCode: String? = null,
        var period: Period? = null,
        var activityName: @RawValue Any?  = null,
        var directorOrg: DirectorOrg? = null,
        var workshopRegisterDate: String? = null,
        var workshopActivity: @RawValue Any?  = null,
        var isNew: @RawValue Any?  = null,
        var workshopStatus: WorkshopStatus? = null,
        var userId: String? = null,
        var incomUserId: @RawValue Any?  = null,
        var claimUserId: @RawValue Any?  = null,
        var legalWorkshop: LegalWorkshop? = null,
        var branchCode: String? = null,
        var workshopKhalaf: String? = null,
        var workshopRate: WorkshopRate? = null,
        var trade: @RawValue Any?  = null,
        var fromOtherBranch: @RawValue Any?  = null,
        var grade: @RawValue Any?  = null,
        var sendListPeriod: SendListPeriod? = null,
        var workshopId: String? = null,
        var placeTypeCode: @RawValue Any?  = null,
        var status: @RawValue Any?  = null
    ):Parcelable

    @Parcelize
    data class WorkshopPremiumRate(
        var abnormalInsuranceRate: Int? = null,
        var statusDate: String? = null,
        var maxGovernmentPourcentage: Int? = null,
        var rateCode: String? = null,
        var normalEmployerInsuranceRate: Int? = null,
        var unemploymentRate: Int? = null,
        var rateDesc: String? = null,
        var governmentPourcentage: Int? = null,
        var sendListResp: Int? = null,
        var abnormalEmployerInsuranceRate: Int? = null,
        var normalInsuranceRate: Int? = null,
        var lisenceControl: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class WorkshopRate(
        var abnormalInsuranceRate: Int? = null,
        var statusDate: String? = null,
        var maxGovernmentPourcentage: Int? = null,
        var rateCode: String? = null,
        var normalEmployerInsuranceRate: Int? = null,
        var unemploymentRate: Int? = null,
        var rateDesc: String? = null,
        var governmentPourcentage: Int? = null,
        var sendListResp: Int? = null,
        var abnormalEmployerInsuranceRate: Int? = null,
        var normalInsuranceRate: Int? = null,
        var lisenceControl: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class WorkshopStatus(
        var statusDate: String? = null,
        var workshopStatusCode: String? = null,
        var workshopStatusDesc: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class WorkshopType(
        var statusDate: String? = null,
        var workshoptypeCode: String? = null,
        var workshoptypeDesc: String? = null,
        var status: String? = null
    ):Parcelable

    @Parcelize
    data class Employer(
        var address: String? = null,
        var nationalId: String? = null,
        var workshopName: String? = null,
        var workshopId: String? = null,
        var branch: Branch? = null
    ):Parcelable

}
