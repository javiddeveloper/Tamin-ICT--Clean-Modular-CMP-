package com.tamin.taminhamrah.data.remote.models.user

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.MyRequestModel
import com.tamin.taminhamrah.data.entity.RequestType
import com.tamin.taminhamrah.data.entity.Status
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate

class MyRequestListResponse : ListDataModel<MyRequestItem>()

data class MyRequestItem(
    var id: Long? = null,
    var createdBy: String? = null,
    var creationTime: Long? = null,
    var lastModifiedBy: Any? = null,
    var lastModificationTime: Long? = null,
    var refCode: String? = null,
    var userName: String? = null,
    var status: Status? = null,
    var title: String? = null,
    var comment: String? = null,
    var template: Any? = null,
    var requestType: RequestType? = null,
    var deliverCode: String? = null,
    var refrenceid: String? = null,
    var requestDetails: Any? = null,
    var requestChid: Any? = null,
    var fullName: Any? = null,
    var createByName: String? = null
) {
    fun getRequestInfo() : List<KeyValueModel> {
        return listOf(
            KeyValueModel(_keyStringResId = R.string.tracing_num , _value = refCode?:"_"),
            KeyValueModel(_keyStringResId = R.string.label_request_title , _value = title?:"_"),
            KeyValueModel(_keyStringResId = R.string.label_description , _value = comment?:"_"),
            KeyValueModel(_keyStringResId = R.string.request_date , _value = getPersianDate(creationTime)?:"_"),
            KeyValueModel(_keyStringResId = R.string.request_status , _value = status?.requestDesc?:"_", _textColor =getColor(status?.requestCode) ),
        )
    }
    fun getPersianDate(timeStamp: Long?): String {
        return timeStamp?.let { ConvertDate.convertTimestampToPersianDate(timeStamp) } ?: ""
    }

    fun getColor(code: String?): EnumTextColor {
        code?.let {
            when (code.toInt()) {
                2 -> { //یش پردازش
                    EnumTextColor.BLUE
                }
                9 -> { //تحویل شده
                    EnumTextColor.LIGHT_GREEN
                }
                16 -> { //تکمیل رسیدگی
                    EnumTextColor.BLUE_GREEN
                }
                18 -> {//تایید نهایی
                    EnumTextColor.GREEN
                }
                19 -> {//عدم تایید
                    EnumTextColor.RED
                }
                21 -> {//نقص مدارک ارسالی
                    EnumTextColor.RED
                }
                else -> EnumTextColor.NORMAL

            }
        }
      return EnumTextColor.NORMAL
    }
}

fun MyRequestItem.asDomainModel(): MyRequestModel {
    return MyRequestModel(
        id = this.id,
        title = this.title,
        creationTime = this.creationTime,
        requestType = this.requestType,
        refCode = this.refCode,
        status = this.status,
        deliverCode = this.deliverCode
    )
}

fun List<MyRequestItem>.asDomainModel(): List<MyRequestModel> {
    return map {
        it.asDomainModel()
    }
}

