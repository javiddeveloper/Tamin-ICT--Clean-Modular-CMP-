package com.tamin.taminhamrah.data.remote.models.services.performedInspections

import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate

class InspectionResponse : ListDataModel<InspectionInfo>()

data class InspectionInfo(
    var inspectionNo: String? = null,
    var insuranceNo: String? = null,
    var workshopNo: String? = null,
    var workshopName: String? = null,
    var relationType: String? = null,
    var inspectionDate: Long = 0,
    var activityDesc: String? = null,
    var branchdesc: String? = null,
    var objectable: String? = null,
    var branchCode: String? = null
) {

    fun createKeyValue(list: List<InspectionInfo>): List<KeyValueModel> {
        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValue(it)
        }
        return keyValueList
    }

    fun createKeyValue(item: InspectionInfo): List<KeyValueModel> {
        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(
            KeyValueModel(
                "شناسه بازرسی",
                item.inspectionNo ?: "-",
                _isValueBold = true
            )
        )
        keyValueList.add(KeyValueModel("شماره بیمه", item.insuranceNo ?: "-"))
        keyValueList.add(KeyValueModel("شماره کارگاه", item.workshopNo ?: "-"))
        keyValueList.add(KeyValueModel("نام کارگاه", item.workshopName ?: "-"))
        keyValueList.add(KeyValueModel("نوع ارتباط با کارگاه", item.relationType ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ بازرسی", getPersianDate(item.inspectionDate)))
        keyValueList.add(KeyValueModel("نوع فعالیت", item.activityDesc ?: "-"))
        keyValueList.add(KeyValueModel("شعبه تأمین اجتماعی", item.branchdesc ?: "-"))
        return keyValueList
    }

    fun getPersianDate(timeStamp: Long): String {
        return ConvertDate.convertTimestampToPersianDate(timeStamp)
    }

}
