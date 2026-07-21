package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.services.workshop.BranchWorkShop

class WeddingPresentResponse (val data :WeddingPresentModel? = null):BaseResponseNew()
data class WeddingPresentModel(
    var request: WeddingPresentRequest? = null,

    var insuranceType: String? = null,

    var insuranceTypeDesc: String? = null,

    var insuranceStatus: String? = null,

    var insuranceStatusDesc: String? = null,

    var workshopCode: String? = null,

    var workshopName: String? = null,

    var requestHelpType: String? = null,

    var serviceDate: String? = null,

    var resultMessage: String? = null,

    var branchCode: String? = null,

    var branchName: String? = null,

    var bankAccount: String? = null,

    var bankName: String? = null,

    var shorttemRequestId: String? = null,

    var payDocNo: String? = null,

    var payment: String? = null,

    var requestFileList: ArrayList<Any>? = null,

    var serviceDateTimeStamp: String? = null,

    var risuid: String? = null,

    var nationalCode: String? = null,

    var insuranceFirstName: String? = null,

    var insuranceLastName: String? = null,

    var mobilNumber: String? = null,

    var requestHelpTypeDesc: String? = null,

    var stringDocFiles: String? = null,

    var branchWorkshop: List<BranchWorkShop>? = null,

    var requestedBrchName: String? = null,

    var requestFileList1: String? = null,

    var genderCode: String? = null,

    var flag: Boolean? = null,

    var partnerNationalId: String? = null,

    var weddingTimestamp: Int? = null,

    var consequential: String? = null
)

fun WeddingPresentModel.asRequestInput(): MarriageGiftReq {
    return MarriageGiftReq(
        request = this.request,
        requestFileList = this.requestFileList,
        risuid = this.risuid,
        insuranceFirstName = this.insuranceFirstName,
        insuranceLastName = this.insuranceLastName,
        nationalCode = this.nationalCode,
        requestHelpType = this.requestHelpType,
        mobilNumber = this.mobilNumber,
        serviceDateTimeStamp = this.serviceDateTimeStamp,
        branchCode = this.branchCode,
        branchName = this.branchName
    )
}
