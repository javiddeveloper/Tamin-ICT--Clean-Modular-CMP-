package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class DependantsResponse : ListDataModel<DependantsModel>()

data class DependantsModel(
    val bankAccount: String? = null,
    val bankName: String? = null,
    val bletenddate: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val brithDate: String? = null,
    val cityName: String? = null,
    val nationCode: String? = null,
    val relationShip: String? = null,
    val relationShipCode: String? = null,
    val risuFatherName: String? = null,
    val risuFname: String? = null,
    val risuId: String? = null,
    val risuIdNo: String? = null,
    val risuLName: String? = null,
    val workShopName: String? = null,
    val workShopNo: String? = null
) {
    fun getDate(date:String) = Utility.getDateSeparator(date)
}



