package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkshopMemberModel(
    var insuranceNumber: String? = null,
    var fullName: String? = null,
    var nationalCode: String? = null,
    var ssn: String? = null,
    var fatherName: String? = null,
    var nationality: String? = null,
    var relationType: String? = null,
    var workStatus: String? = null,
    var leavingWorkDate: String? = null

) : Parcelable {

    fun createKeyValue(list: List<WorkshopMemberModel>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
           createKeyValue(it)
        }

        return keyValueList
    }

    fun createKeyValue(item: WorkshopMemberModel): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("شماره بیمه", item.insuranceNumber ?: "-"))
        keyValueList.add(KeyValueModel("نام و نام خانوادگی", item.fullName ?: "-"))
        keyValueList.add(KeyValueModel("نام پدر", item.fatherName ?: "-"))
        keyValueList.add(KeyValueModel("شماره ملی", item.nationalCode ?: "-"))
        keyValueList.add(KeyValueModel("شماره شناسنامه", item.ssn ?: "-"))
        keyValueList.add(KeyValueModel("ملیت", item.nationality ?: "-"))
        keyValueList.add(KeyValueModel("نوع مشمول", item.relationType ?: "-"))
        keyValueList.add(KeyValueModel("وضعیت", item.workStatus ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ ترک کار", item.leavingWorkDate ?: "-"))
        return keyValueList
    }

}
