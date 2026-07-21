package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate

class BankAccountResponse : ListDataModel<BankAccount>()

data class BankAccount(
    var dateOfFinish: Long? = null,
    var creationTime: String? = null,
    var lastModificationTime: String? = null,
    var lastModifiedBy: String? = null,
    var shebaNumber: Any? = null,
    var accounttype: AccountType? = null,
    var personal: Int? = null,
    var accountNumber: String? = null,
    var isValidAccount: Boolean? = null,
    var confirmed: Boolean? = null,
    var branch: Any? = null,
    var organizationId: String? = null,
    var bank: BankInfo? = null,
    var deleted: Any? = null,
    var accountstatus: Any? = null,
    var createdBy: String? = null,
    var dateOfStart: Long? = null,
    var lastModifiedUser: Any? = null,
    var id: Long? = null,
    var createdUser: Any? = null
) {
    fun getBankCode(): String {
        return bank?.bankCode ?: "-"
    }

    fun getBankName(): String {
        return bank?.bankName ?: "-"
    }

    fun getAccountCode(): String {
        return accounttype?.accountCode ?: "-"
    }

    fun getAccountName(): String {
        return accounttype?.accountName ?: "-"
    }

    fun createKeyValue(list: List<BankAccount>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValue(it)
        }

        return keyValueList
    }

    fun createKeyValue(item: BankAccount): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

        keyValueList.add(KeyValueModel("شماره حساب", item.accountNumber ?: "-"))
        keyValueList.add(KeyValueModel("بانک", getBankName()))
        keyValueList.add(KeyValueModel("نوع حساب", getAccountName()))
        keyValueList.add(KeyValueModel("تاریخ شروع", item.dateOfStart?.let {
            item.getPersianDate(
                it
            )
        }
            ?: "-"))

        keyValueList.add(KeyValueModel("تاریخ پایان", item.dateOfFinish?.let {
            item.getPersianDate(
                it
            )
        }
            ?: "-"))

        return keyValueList
    }

    fun getPersianDate(timeStamp: Long): String {
        return ConvertDate.convertTimestampToPersianDate(timeStamp)
    }


    inner class AccountType(
        var statusDate: Any? = null,
        var accountCode: String? = null,
        var accountName: String? = null,
        var status: String? = null
    )

    inner class BankInfo(
        var statusDate: String? = null,
        var bankCode: String? = null,
        var bankName: String? = null,
        var status: String? = null
    )
}
/*

fun BankAccountResponse.asDomainModel(): BankAccountModel {
    return BankAccountModel(
        id = this.id,
        accountNumber = this.accountNumber,
        bankCode = this.bank?.bankCode,
        bankName = this.bank?.bankName,
        accountCode = this.accounttype?.accountCode,
        accountName = this.accounttype?.accountName,
        dateOfStart = this.dateOfStart,
        dateOfFinish = this.dateOfFinish
    )
}

fun List<BankAccountResponse>.asDomainModel(): List<BankAccountModel> {
    return map {
        it.asDomainModel()
    }
}

*/
