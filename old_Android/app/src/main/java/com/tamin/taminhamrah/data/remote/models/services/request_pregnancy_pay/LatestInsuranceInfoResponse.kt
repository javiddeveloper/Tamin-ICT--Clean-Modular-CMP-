package com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class LatestInsuranceInfoResponse (var data :LatestInsuranceInfoModel?=null) : BaseResponseNew()
data class LatestInsuranceInfoModel(
        val bankAccount: String? = null,
        val bankName: String? = null,
        val branchCode: String? = null,
        val branchName: String? = null,
        val branchWorkshop: List<BranchWorkshop>? = null,
        val consequential: Any? = null,
        val flag: Boolean?=null,
        val genderCode: String? = null,
        val insuranceFirstName: String? = null,
        val insuranceLastName: String? = null,
        val insuranceStatus: Any? = null,
        val insuranceStatusDesc: String? = null,
        val insuranceType: Any? = null,
        val insuranceTypeDesc: String? = null,
        val mobilNumber: String? = null,
        val nationalCode: String? = null,
        val partnerNationalId: Any? = null,
        val payDocNo: Any? = null,
        val payment: Any? = null,
        val request: Request? = null,
        val requestFileList: Any? = null,
        val requestFileList1: Any? = null,
        val requestHelpType: String? = null,
        val requestHelpTypeDesc: String? = null,
        val requestedBrchName: Any? = null,
        val resultMessage: Any? = null,
        val risuid: String? = null,
        val serviceDate: Any? = null,
        val serviceDateTimeStamp: Int? = null,
        val shorttemRequestId: String? = null,
        val stringDocFiles: Any? = null,
        val weddingTimestamp: Int? = null,
        val workshopCode:String? = null,
        val workshopName:String? = null
    ) {
        data class BranchWorkshop(
            val branchCode: String? = null,
            val branchName: String? = null,
            val workshopCode: String? = null,
            val workshopName: String? = null
        )

        data class Request(
            val brchCode: Any? = null,
            val editDate: Any? = null,
            val editUser: Any? = null,
            val id: Any? = null,
            val refrenceCode: Any? = null,
            val requestDate: Any? = null,
            val requestType: Any? = null,
            val status: Any? = null,
            val statusId: Any? = null,
            val statusName: Any? = null,
            val systemType: Any? = null,
            val userId: Any? = null
        )
    }

fun LatestInsuranceInfoModel.asDomainModel(): List<MenuModel> {
    val list = arrayListOf<MenuModel>()
    branchWorkshop?.let {
        branchWorkshop.forEach {
            list.add(MenuModel(id = it.branchCode, title = it.branchName + "-" + it.workshopName))
        }
    }
    return list
}