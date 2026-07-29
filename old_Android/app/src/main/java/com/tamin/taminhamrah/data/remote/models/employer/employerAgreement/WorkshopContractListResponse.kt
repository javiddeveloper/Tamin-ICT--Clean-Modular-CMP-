package com.tamin.taminhamrah.data.remote.models.employer.employerAgreement

import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class WorkshopContractListResponse : ListDataModel<WorkshopContract>()
data class WorkshopContract(
    var wokshop: Any? = null,
    var workshop: WorkshopInfo? = null,
    var workshopType: Any? = null,
    var contractRow: String? = null,
    var postalCode: String? = null,
    var tel: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var mobileNo: String? = null,
    var email: String? = null,
    var nationalCode: String? = null,
    var mastType: Any? = null,
    var roleType: Any? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var createUserId: Any? = null,
    var createDate: String? = null,
    var ticketCode: Any? = null
){
    fun getTitle(str:String?) = str?:"-"

    fun getLocalDate(dateStr:String?) = Utility.getDateSeparator(dateStr)
}

data class WorkshopInfo (
    var workshopId: String? = null,
    var branchCode: String? = null,
    var workshopName: String? = null,
    var workshopApproveDate: String? = null,
    var actitvityCode: String? = null,
    var character: String? = null,
    var sendListPeriod: Any? = null,
    var workshopUnemployedStat: String? = null,
    var inclusionDate: String? = null,
    var brhCode: String? = null,
    var workshopRegisterDate: String? = null,
    var userId: String? = null,
    var createDate: String? = null,
    var claimOpDate: Any? = null,
    var claimUserId: Any? = null,
    var incomOpDate: Any? = null,
    var incomUserId: Any? = null,
    var status: Any? = null,
    var workshopKhalaf: Any? = null,
    var fromOtherBranch: Any? = null,
    var workshopStatus: String? = null,
    var webServiceResultStatus: Any? = null,
    var sswn: Any? = null,
    var isNew: Any? = null,
    var parentWorkshop: Any? = null,
    var activityName: Any? = null,
    var lastAddress: Any? = null,
    var employerName: Any? = null,
    var decodedCreateDate: Any? = null,
    var sendListPeriUnemployedStat: String? = null
)