package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class NotExistRequestsResponse : ListDataModel<NotExistRequestsModel>()

data class NotExistRequestsModel(
    val branchCode: String? = null,
    val branchName: String? = null,
    val cityCode: String? = null,
    val cityName: String? = null,
    val confirmed: Boolean = false,
    val endDate: Long? = null,
    val insuranceType: String? = null,
    val insuranceTypeDesc: String? = null,
    val provinceCode: String? = null,
    val provinceName: String? = null,
    val reqno: String? = null,
    val reqtype: String? = null,
    val risuid: String? = null,
    val rowi: String? = null,
    val rwshAddress: String? = null,
    val rwshManager: String? = null,
    val rwshid: String? = null,
    val rwshname: String? = null,
    val startDate: Long? = null,
    val userDesc: String? = null,
    val workDays: String? = null
)

fun NotExistRequestsModel.asDomainModel(): BodySaveNonExistentHistory {
    return BodySaveNonExistentHistory(
        branchCode = branchCode ?: "",
        branchName = branchName,
        cityCode = cityCode,
        cityName = cityName,
        endDate = endDate?.toString() ,
        insuranceType = insuranceType,
        insuranceTypeDesc = insuranceTypeDesc,
        provinceCode = provinceCode,
        provinceName = provinceName,
        reqno = reqno,
        reqtype = reqtype,
        rowi = rowi,
        rwshAddress = rwshAddress,
        rwshManager = rwshManager,
        rwshid = rwshid,
        rwshname = rwshname,
        startDate = startDate.toString(),
        workDays = workDays
    )

}

