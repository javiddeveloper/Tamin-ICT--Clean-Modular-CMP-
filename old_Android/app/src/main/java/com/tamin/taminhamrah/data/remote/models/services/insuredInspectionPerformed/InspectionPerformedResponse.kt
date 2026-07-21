package com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import kotlinx.parcelize.Parcelize

class InspectionPerformedResponse : ListDataModel<InspectionPerformedModel>()

@Parcelize
data class InspectionPerformedModel(
    val activityDesc: String? = null,
    val branchCode: String? = null,
    val branchdesc: String? = null,
    val inspectionDate: Long? = null,
    val inspectionNo: String? = null,
    val insuranceNo: String? = null,
    val objectable: String? = null,
    val relationType: String? = null,
    val workshopName: String? = null,
    val workshopNo: String? = null,
    val nationalCode: String? = null
) : Parcelable {
    fun getInspectionDatePersian(): String {
        return ConvertDate.convertTimestampToPersianDate(inspectionDate ?: 0)
    }


    fun createKeyValue(list: List<InspectionPerformedModel>): List<KeyValueModel> {
        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValue(it)
        }
        return keyValueList
    }

    fun createKeyValue(item: InspectionPerformedModel): List<KeyValueModel> {
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
        keyValueList.add(
            KeyValueModel(
                "تاریخ بازرسی",
                ConvertDate.convertTimestampToPersianDate(item.inspectionDate ?: 0)
            )
        )
        keyValueList.add(KeyValueModel("نوع فعالیت", item.activityDesc ?: "-"))
        keyValueList.add(KeyValueModel("شعبه تأمین اجتماعی", item.branchdesc ?: "-"))
        keyValueList.add(KeyValueModel("کد ملی", item.nationalCode ?: "-"))
        return keyValueList
    }
}

