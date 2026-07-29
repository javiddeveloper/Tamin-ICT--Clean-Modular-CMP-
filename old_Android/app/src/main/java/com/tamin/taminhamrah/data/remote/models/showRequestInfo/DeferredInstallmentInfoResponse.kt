package com.tamin.taminhamrah.data.remote.models.showRequestInfo

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility

data class DeferredInstallmentInfoResponse(var data: DeferredInstallmentInfoModel? = null):BaseResponseNew()
data class DeferredInstallmentInfoModel(
    val firstName: String,
    val lastName: String,
    val nationalId: String,
    val birthDate: Long,
    val pensionerNationalId: String,
    val userFirstName: String,
    val userLastName: String,
    val pensionerId: String,
    val bank: Bank,
    val bankBranch: String,
    val installmentAmount: Long,
    val installmentCount: Int,
    val loanAmount: Long,
    val guaranteeAmount: Long,
){
    fun getPensionerInfo() = listOf(
        KeyValueModel(
            _keyStringResId = R.string.first_name,
            _value = userFirstName ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.last_name,
            _value = userLastName ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.national_code,
            _value = pensionerNationalId ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.pension_number,
            _value = pensionerId ?: "_"
        ))

    fun getBorrowerInfo() = listOf(
        KeyValueModel(
            _keyStringResId = R.string.first_name,
            _value = firstName ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.last_name,
            _value = lastName ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.national_code,
            _value = nationalId ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.birthdate,
            _value = ConvertDate.convertTimestampToPersianDate(birthDate) ?: "_"
        ))

    fun getLoanInfo() = listOf(
        KeyValueModel(
            _keyStringResId = R.string.label_bank,
            _value = bank.bankName ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.label_branch_name,
            _value = bankBranch ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.installment_amount,
            _value = Utility.getRialWithSeparator(installmentAmount)?: "_",
            _textColor = EnumTextColor.BLUE
        ), KeyValueModel(
            _keyStringResId = R.string.label_number_installments,
            _value = installmentCount.toString() ?: "_"
        ), KeyValueModel(
            _keyStringResId = R.string.label_refund_amount,
            _value = Utility.getRialWithSeparator(loanAmount) ?: "_",
            _textColor = EnumTextColor.GREEN
        ), KeyValueModel(
            _keyStringResId = R.string.label_guarantee_amount,
            _value =  Utility.getRialWithSeparator(guaranteeAmount) ?: "_",
            _textColor = EnumTextColor.AMBER
        ))
}

data class Bank(
    val bankName: String
)

