package com.tamin.taminhamrah.data.entity

import com.tamin.taminhamrah.utils.ConvertDate

data class BankAccountModel(
    var id: Long? = null,
    var accountNumber: String? = null,
    var bankCode: String? = null,
    var bankName: String? = null,
    var accountCode: String? = null,
    var accountName: String? = null,
    var dateOfStart: Long? = null,
    var dateOfFinish: Long? = null) {

    fun createKeyValue(list: List<BankAccountModel>): List<KeyValueModel> {
        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValue(it)
        }
        return keyValueList
    }

    fun createKeyValue(item: BankAccountModel): List<KeyValueModel> {
        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("شماره حساب", item.accountNumber ?: "-"))
        keyValueList.add(KeyValueModel("بانک", item.bankName ?: "-"))
        keyValueList.add(KeyValueModel("نوع حساب", item.accountName ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ شروع", item.dateOfStart?.let {
            item.getPersianDate(it)
        }?: "-"))
        keyValueList.add(KeyValueModel("تاریخ پایان", item.dateOfFinish?.let {
            item.getPersianDate(
                it
            )
        } ?: "-"))
        return keyValueList
    }

    fun getPersianDate(timeStamp: Long): String {
        return ConvertDate.convertTimestampToPersianDate(timeStamp)
    }
}
