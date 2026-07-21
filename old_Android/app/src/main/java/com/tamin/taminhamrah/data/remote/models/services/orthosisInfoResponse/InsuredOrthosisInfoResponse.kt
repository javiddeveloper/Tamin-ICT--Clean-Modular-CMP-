package com.tamin.taminhamrah.data.remote.models.services.orthosisInfoResponse

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class InsuredOrthosisInfoResponse(val data : InsuredOrthosisInfoModel ? = null) : BaseResponseNew()

data class InsuredOrthosisInfoModel(
    val bankAccount: String? = null,
    val bankName: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val branchWorkshop: List<BranchWorkshopOrthosisInfo>?=null,
    val insuranceFirstName: String? = null,
    val insuranceLastName: String? = null,
    val insuranceStatus: String? = null,
    val insuranceStatusDesc: String? = null,
    val insuranceType: String? = null,
    val insuranceTypeDesc: String? = null,
    val mobilNumber: String? = null,
    val nationalCode: String? = null,
    val risuid: String? = null
)

data class BranchWorkshopOrthosisInfo(
    val branchCode: String? = null,
    val branchName: String? = null,
    val workshopCode: String? = null,
    val workshopName: String? = null
)

fun InsuredOrthosisInfoModel.asDomainModel(): List<MenuModel> {
    val list = arrayListOf<MenuModel>()
    branchWorkshop?.let {
        branchWorkshop.forEach {
            list.add(MenuModel(id = it.branchCode, title = it.branchName + "-" + it.workshopName))
        }
    }
    return list
}