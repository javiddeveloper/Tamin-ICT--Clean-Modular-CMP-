package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import com.tamin.taminhamrah.utils.ConvertDate
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class MyRequestModel(
    var id: Long? = null,
    var createdBy: String? = null,
    var creationTime: Long? = null,
    var lastModifiedBy:@RawValue Any? = null,
    var lastModificationTime: Long? = null,
    var refCode: String? = null,
    var userName: String? = null,
    var status: Status? = null,
    var title: String? = null,
    var comment:@RawValue Any? = null,
    var template:@RawValue Any? = null,
    var requestType: RequestType? = null,
    var deliverCode: String? = null,
    var refrenceid:@RawValue Any? = null,
    var requestDetails:@RawValue Any? = null,
    var requestChid:@RawValue Any? = null,
    var fullName:@RawValue Any? = null,
    var createByName: String? = null
): Parcelable {

    fun createKeyValue(list: List<MyRequestModel>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValue(it)
        }

        return keyValueList
    }


    fun createKeyValue(item: MyRequestModel): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

//        keyValueList.add(KeyValueModel("شماره پیگیری", item.refCode ?: "-"))
//        keyValueList.add(KeyValueModel("عنوان درخواست", item.title ?: "-"))
        keyValueList.add(KeyValueModel("کد ملی", item.createdBy ?: "-"))
        keyValueList.add(KeyValueModel("توضیحات",item.requestType?.description ?: "-"))
        keyValueList.add(KeyValueModel("درخواست کننده", item.createByName ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ درخواست", item.creationTime?.let {
            item.getPersianDate(
                it
            )
        }
            ?: "-"))
        keyValueList.add(KeyValueModel("وضعیت درخواست", item.status?.requestDesc ?: "-"))
        keyValueList.add(KeyValueModel("کد تحویل", item.deliverCode ?: "-"))

        return keyValueList
    }

    fun getPersianDate(timeStamp:Long):String{
        return ConvertDate.convertTimestampToPersianDate(timeStamp)
    }
}

@Parcelize
data class RequestType(
    var createdBy:@RawValue Any? = null,
    var creationTime:@RawValue Any? = null,
    var lastModifiedBy:@RawValue Any? = null,
    var lastModificationTime:@RawValue Any? = null,
    var id: Int? = null,
    var title: String? = null,
    var description: String? = null
):Parcelable

@Parcelize
data class Status(
    var requestCode: String? = null,
    var requestDesc: String? = null
):Parcelable


