package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import com.tamin.taminhamrah.utils.ConvertDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkshopStackHolderModel(
    var nationalCode: String? = null,
    var fullName: String? = null,
    var fatherName: String? = null,
    var birthDate: Long? = null,
    var stackType: String? = null

) : Parcelable {

    fun createKeyValue(list: List<WorkshopStackHolderModel>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValue(it)
        }

        return keyValueList
    }

    fun createKeyValue(item: WorkshopStackHolderModel): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("شماره ملی", item.nationalCode ?: "-"))
        keyValueList.add(KeyValueModel("نام و نام خانوادگی", item.fullName ?: "-"))
        keyValueList.add(KeyValueModel("نام پدر", item.fatherName ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ تولد", getPersianDate(item.birthDate)))
        keyValueList.add(KeyValueModel("سمت", getStackTypeDescription()))

        return keyValueList
    }

    fun getStackTypeDescription(): String {

        return when(stackType){
            "1"->{
                "اعضای هیئت مدیره"
            }
            "2"->{
                "صاحبان امضا"
            }
            "3"->{
                "مدیرعامل"
            }
            "4"->{
                "نماینده"
            }
            else->stackType?:""
        }
    }

    fun getPersianDate(timeStamp: Long?): String {
        return if (timeStamp == null) "-"
        else ConvertDate.convertTimestampToPersianDate(timeStamp.toString())
    }


}
