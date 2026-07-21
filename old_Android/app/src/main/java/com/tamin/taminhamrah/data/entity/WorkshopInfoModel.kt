package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkshopInfoModel(
    var workshopId: String? = null,
    var workshopName: String? = null,
    var activityName: String? = null,
    var activityCode: String? = null,
    var branchCode: String? = null,
    var branchName: String? = null,
    var organizationName: String? = null,
    var employerName: String? = null,
    var lastAddress: String? = null,
    var contractRow: String? = null

) : Parcelable {

    fun createKeyValue(list: List<WorkshopInfoModel>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            keyValueList.add(KeyValueModel("کد کارگاه", it.workshopId ?: "-"))
            keyValueList.add(KeyValueModel("نام کارگاه", it.workshopName ?: "-"))
            keyValueList.add(KeyValueModel("فعالیت", it.activityName ?: "-"))
            keyValueList.add(KeyValueModel("کد شعبه", it.branchCode ?: "-"))
            keyValueList.add(KeyValueModel("نام شعبه", it.organizationName ?: "-"))
        }

        return keyValueList
    }

    fun createKeyValue(item: WorkshopInfoModel): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

        keyValueList.add(KeyValueModel("کد کارگاه", item.workshopId ?: "-"))
        keyValueList.add(KeyValueModel("نام کارگاه", item.workshopName ?: "-"))
        keyValueList.add(KeyValueModel("فعالیت", item.activityName ?: "-"))
        keyValueList.add(KeyValueModel("کد شعبه", item.branchCode ?: "-"))
        keyValueList.add(KeyValueModel("نام شعبه", item.organizationName ?: "-"))
        return keyValueList
    }

}
