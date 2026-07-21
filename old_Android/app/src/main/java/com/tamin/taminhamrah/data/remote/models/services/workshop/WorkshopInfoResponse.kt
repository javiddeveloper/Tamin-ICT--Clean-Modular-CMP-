package com.tamin.taminhamrah.data.remote.models.services.workshop

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.entity.WorkshopInfoModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class WorkshopInfoResponse : ListDataModel<WorkshopInfo>()

data class WorkshopInfo(
    var sswn: Any? = null,
    var parentWorkshop: Any? = null,
    var buildRequest: Any? = null,
    var nation: Nation2? = null,
    var workshopName: String? = null,
    var sendListMethod: Any? = null,
    var employerName: String? = null,
    var workshopUnemployedStat: String? = null,
    var workshopRequests: Any? = null,
    var actitvityCode: String? = null,
    var decodedCreateDate: Any? = null,
    var branch: Branch? = null,
    var character: Character? = null,
    var webServiceResultStatus: Any? = null,
    var recognizeMethod: RecognizeMethod? = null,
    var workshopType: WorkshopType? = null,
    var workshopApproveDate: String? = null,
    var lastAddress: String? = null,
    var claimOpDate: Any? = null,
    var incomOpDate: Any? = null,
    var inclusionDate: Any? = null,
    var createDate: String? = null,
    var brhCode: String? = null,
    var period: Period? = null,
    @SerializedName(value = "activityName", alternate = ["activityDesc"])
    var activityName: String? = null,
    var directorOrg: Any? = null,
    var workshopRegisterDate: String? = null,
    var workshopActivity: Any? = null,
    var isNew: String? = null,
    var workshopStatus: WorkshopStatus? = null,
    var userId: String? = null,
    var incomUserId: Any? = null,
    var claimUserId: Any? = null,
    var legalWorkshop: Any? = null,
    var branchCode: String? = null,
    var workshopKhalaf: Any? = null,
    var workshopRate: WorkshopRate? = null,
    var trade: Any? = null,
    var fromOtherBranch: Any? = null,
    var grade: Any? = null,
    var sendListPeriod: SendListPeriod? = null,
    var workshopId: String? = null,
    var placeTypeCode: Any? = null,
    var status: Any? = null,
    var branchName: String? = null,

    var contractRow: String? = null
) : java.io.Serializable {
    fun getTitle(title: String?): String {
        val defTitle = if (title == contractRow) "ندارد" else "-"
        return title ?: defTitle
    }

    fun getOrganizationName(): String {

        return branch?.organizationName ?: "-"
    }

    fun getAgreementRow() = if (contractRow.isNullOrBlank()) "-" else contractRow
    fun branchName() = if (branchName.isNullOrBlank()) "-" else branchName
    fun branchCode() = if (branchCode.isNullOrBlank()) "-" else branchCode
    fun workshopId() = if (workshopId.isNullOrBlank()) "-" else workshopId
    fun workshopName() = if (workshopName.isNullOrBlank()) "-" else workshopName


}


fun WorkshopInfo.asDomainModel(): WorkshopInfoModel {
    return WorkshopInfoModel(
        workshopId = this.workshopId,
        workshopName = this.workshopName ?: "-",
        activityName = this.activityName ?: "-",
        activityCode = this.actitvityCode,
        branchCode = this.branchCode,
        branchName = this.branchName,
        organizationName = this.branch?.organizationName ?: "-",
        employerName = this.employerName ?: "-",
        lastAddress = this.lastAddress ?: "-",
        contractRow = this.contractRow ?: "ندارد"
    )
}

fun List<WorkshopInfo>.asDomainModel(): List<WorkshopInfoModel> {
    return map {
        it.asDomainModel()
    }
}

data class Branch(
    var parent: Parent? = null,
    var actKey: Int? = null,
    var code: String? = null,
    var organizationName: String? = null,
    var children: List<Child>? = null,
    var entityId: String? = null,
    var type: String? = null
)

data class Character(
    var statusDate: String? = null,
    var characterDesc: String? = null,
    var characterCode: String? = null, //01 : حقیقی  وو 02 : حقوقی
    var status: String? = null
)

data class Nation2(
    var statusDate: String? = null,
    var nationDesc: String? = null,
    var nationCode: String? = null,
    var status: String? = null
)

data class Parent(
    var parent: Parent? = null,
    var actKey: Int? = null,
    var code: String? = null,
    var organizationName: String? = null,
    var children: Any? = null,
    var entityId: String? = null,
    var type: String? = null

)

data class Period(
    var statusDate: String? = null,
    var actionPeriodDesc: String? = null,
    var actionPeriodCode: String? = null,
    var status: String? = null
)

data class SendListPeriod(
    var statusDate: String? = null,
    var periodDesc: String? = null,
    var periodCode: String? = null,
    var status: String? = null
)

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
)

data class WorkshopStatus(
    var statusDate: String? = null,
    var workshopStatusCode: String? = null,
    var workshopStatusDesc: String? = null,
    var status: String? = null
)

data class WorkshopType(
    var statusDate: String? = null,
    var workshoptypeCode: String? = null,
    var workshoptypeDesc: String? = null,
    var status: String? = null
)

data class RecognizeMethod(
    var code: String? = null,
    var description: String? = null,
    var status: String? = null,
    var statusDate: String? = null
)