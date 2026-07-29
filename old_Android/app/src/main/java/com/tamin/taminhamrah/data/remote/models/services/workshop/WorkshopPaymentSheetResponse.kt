package com.tamin.taminhamrah.data.remote.models.services.workshop

import androidx.annotation.ColorInt
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility

class WorkshopPaymentSheetResponse : ListDataModel<WorkshopPaymentSheet>()

data class WorkshopPaymentSheet(
    var orderNo: String? = null,
    var orderRow: String? = null,
    var payId: String? = null,
    var mastCustomerCode: String? = null,
    var rcntrow: String? = null,
    var mastCustomerName: String? = null,
    var debitCreateReasonCode: String? = null,
    var debitCreateReasonDesc: String? = null,
    var debitNo: String? = null,
    var docDate: Long? = null,
    var paySeqAmount: Long? = null,
    var orpStatusCode: String? = null,
    var orpStatusDesc: String? = null,
    var cardDate: Long? = null,
    var payKindCode: String? = null,
    var payKindDesc: String? = null,
    var ouragGno: String? = null,
    var ouragSDate: Any? = null
) {

    @ColorInt
    fun getItemColor():  Int{
        return when (orpStatusCode){
            "1"->0XFD6565
            "2"->0x3acc6c
            "3"->0xff8f00
            else->0x1B1B1B
        }
    }

    fun createKeyValue(list: List<WorkshopPaymentSheet>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValue(it)
        }

        return keyValueList
    }

    fun createKeyValue(item: WorkshopPaymentSheet): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

        keyValueList.add(KeyValueModel("شماره بدهی", item.debitNo ?: "-"))
        keyValueList.add(KeyValueModel("ردیف پیمان", item.payId ?: "-", _isValueBold = true))
        keyValueList.add(
            KeyValueModel(
                "تاریخ وصولی",
                Utility.getDateSeparator(item.docDate.toString())
            )
        )
        keyValueList.add(
            KeyValueModel(
                "مبلغ",
                Utility.getRialWithSeparator(item.paySeqAmount),
                _isValueBold = true
            )
        )
        keyValueList.add(
            KeyValueModel(
                "وضعیت",
                item.orpStatusDesc ?: "-",
                _textColor = when (item.orpStatusCode) {
                    "2" -> EnumTextColor.GREEN
                    "1" -> EnumTextColor.RED
                    else -> EnumTextColor.AMBER
                }
            )
        )
        keyValueList.add(KeyValueModel("علت ایجاد بدهی", item.debitCreateReasonDesc ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ صدور", getPersianDate(item.cardDate)))
        keyValueList.add(KeyValueModel("نحوه پرداخت", item.payKindDesc ?: "-"))
        keyValueList.add(KeyValueModel("شماره اوراق", item.ouragGno ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ سررسید", getPersianDate(item.docDate)))

        return keyValueList
    }

    fun getPersianDate(timeStamp: Long?): String {

        return ConvertDate.convertTimestampToPersianDate(timeStamp ?: 0)
    }
    fun getTitle(title:String?): String {

        return title?:"-"
    }

    fun getRialValue(value: Long?): String {

        return Utility.getRialWithSeparator(value)
    }
}


/*fun WorkshopMemberResponse.asDomainModel(): WorkshopMemberModel {
    return WorkshopMemberModel(
        insuranceNumber = this.insurance?.id ?:"-",
        fullName = "${this.insurance?.firstName} ${this.insurance?.lastName}" ?:"-",

        nationalCode = this.insurance?.nationalId ?:"-",
        ssn = this.insurance?.idCardNumber ?:"-",
        fatherName = this.insurance?.fatherName ?:"-",
        nationality = this.insurance?.nation?.nationDesc ?:"-",
        relationType = this.relationType?.relationTypeDescription ?:"-",
        workStatus = this.leavingWorkStatus ?:"-",
        leavingWorkDate = this.leavingWorkDate
    )
}

fun List<WorkshopMemberResponse>.asDomainModel(): List<WorkshopMemberModel> {
    return map {
        it.asDomainModel()
    }
}*/
