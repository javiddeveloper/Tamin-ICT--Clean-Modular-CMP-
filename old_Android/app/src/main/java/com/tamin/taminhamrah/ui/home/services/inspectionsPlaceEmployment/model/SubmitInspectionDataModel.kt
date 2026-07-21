package com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.model

import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.SubmitResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserModel

data class SubmitInspectionDataModel(
    var mobile: String = "",
    var email: String = "",
    var fullName: String = "",
    var nationalId:String= "",
    var startDate:Long =0L,
    var endDate:Long =0L,
    var workShopName:String= "",
    var employerName:String= "",
    var branchCode:String= "",
    var jobTitle:String= "",
    var workshopAddress:String= "",
    var workshopId:String= "",
    var workshopTell:String= "",
    var descInspection:String= ""
){
    fun initUserInfo(data: CurrentUserModel) {
        mobile = data.mobile?:""
        nationalId = data.nationalCode?:""
        email = data.email?:""
        fullName = "${data.firstName} ${data.lastName}"
    }

    fun getRequestSubmitInspection() = SubmitResponse(
        brchCode = branchCode,
        endDate = endDate,
        startDate = startDate,
        inspectionNumberOld = "",
        insuranceId = "",
        insuranceJob = jobTitle,
        requestDescription = descInspection,
        workshopAddress = workshopAddress,
        workshopManager = employerName,
        workshopName = workShopName,
        workshopTel =workshopTell,
        workshopNumber = workshopId
    )
}
