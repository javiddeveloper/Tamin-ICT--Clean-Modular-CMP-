package com.tamin.taminhamrah.data.remote.models.services

import android.text.BidiFormatter
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class PayRollResponse : ListDataModel<PayRollModel>()
data class PayRollModel(
    var id: Int? = null,
    var clpType: String? = null,
    var tprDesc: String? = null,
    var sumAmount: Long? = null,
    var infDesc: Any? = null,
    var textNumber: String? = null,
    var sumPay: Long? = null,
    var hisYear: String? = null,
    var hisMon: String? = null,
    var hisDay: String? = null,
    var hisYearPlus: String? = null,
    var hisMonPlus: String? = null,
    var hisDayPlus: String? = null
)

fun PayRollModel.asDomainModel(): KeyValueModel {
    return KeyValueModel(
        _key = this.tprDesc ?: "",
        _value = Utility.getRialWithSeparator(this.sumAmount),
        _type = this.clpType ?: "1"
    )
}

fun getPaymentList(list: List<PayRollModel>): List<KeyValueModel> {
    val itemList = ArrayList<KeyValueModel>()
    list.forEach {item->
        val amount = item.sumAmount ?: 0L
        if (item.clpType == "1" && amount>0) {
            itemList.add(
                KeyValueModel(
                    _key = item.tprDesc ?: "",
                    _value = Utility.getRialWithSeparator(amount)
                )
            )
        }
    }
    return itemList
}

fun getPaymentListAi(list: List<PayRollModel>): List<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel> {
    val itemList = ArrayList<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel>()
    list.forEach {item->
        val amount = item.sumAmount ?: 0L
        if (item.clpType == "1" && amount>0) {
            itemList.add(
                com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                    _key = item.tprDesc ?: "",
                    _value = Utility.getRialWithSeparator(amount)
                )
            )
        }
    }
    return itemList
}

fun getLoanList(list: List<PayRollModel>): List<KeyValueModel> {
    val itemList = ArrayList<KeyValueModel>()
    list.forEach {
        if (it.clpType == "3") {
            itemList.add(
                KeyValueModel(
                    _key = it.tprDesc ?: "",
                    _value = BidiFormatter.getInstance(true).unicodeWrap(Utility.getRialWithSeparator(it.sumAmount)) //for show negative number in persian text
                )
            )
        }
    }
    return itemList
}
fun getLoanListAi(list: List<PayRollModel>): List<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel> {
    val itemList = ArrayList<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel>()
    list.forEach {
        if (it.clpType == "3") {
            itemList.add(
                com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                    _key = it.tprDesc ?: "",
                    _value = BidiFormatter.getInstance(true).unicodeWrap(Utility.getRialWithSeparator(it.sumAmount)) //for show negative number in persian text
                )
            )
        }
    }
    return itemList
}

fun getDeductionList(list: List<PayRollModel>): List<KeyValueModel> {
    val itemList = ArrayList<KeyValueModel>()
    list.forEach {
        val deductionAmount = it.sumAmount.toString().replace("-","")
            if (it.clpType == "2") {
            itemList.add(
                KeyValueModel(
                    _key = it.tprDesc ?: "",
                    _value = "${Utility.getNumberWithSeparatorForStringValue(deductionAmount)}- ریال"
                )
            )
        }
    }
    return itemList
}
fun getDeductionListAi(list: List<PayRollModel>): List<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel> {
    val itemList = ArrayList<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel>()
    list.forEach {
        val deductionAmount = it.sumAmount.toString().replace("-","")
            if (it.clpType == "2") {
            itemList.add(
                com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                    _key = it.tprDesc ?: "",
                    _value = "${Utility.getNumberWithSeparatorForStringValue(deductionAmount)}- ریال"
                )
            )
        }
    }
    return itemList
}
