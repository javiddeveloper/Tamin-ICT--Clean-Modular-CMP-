package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility

class CalculateFreelanceDebitResponse
    (var data: CalculateFreelanceDebit?=null) : BaseResponseNew()

data class CalculateFreelanceDebit(
    val PayPremiumDate: String? = null,
    val endDate: Long? = null,
    val insurancePremiums: Long? = null,
    val messageInformation: String? = null,
    val previousDebit: Long? = null,
    val startDate: Long? = null,
    val total: Long? = null,
) {
    fun getPersianDate(timeStamp: Long?): String {
        return ConvertDate.convertTimestampToPersianDate(timeStamp ?: 0)
    }

    fun createKeyValue()= arrayListOf(
        KeyValueModel("قابل پرداخت", Utility.getRialWithSeparator(total), _textColor = EnumTextColor.GREEN, _isValueBold = true),
        KeyValueModel("شروع دوره پرداخت", getPersianDate(startDate), _textColor = EnumTextColor.BLUE, _isValueBold = true),
        KeyValueModel("پایان دوره پرداخت", getPersianDate(endDate), _textColor = EnumTextColor.BLUE, _isValueBold = true),
        KeyValueModel("حق بیمه دوره", Utility.getRialWithSeparator(insurancePremiums)),
        KeyValueModel("بدهی گذشته", Utility.getRialWithSeparator(previousDebit), _textColor = if (previousDebit!= null && previousDebit > 0) EnumTextColor.AMBER else EnumTextColor.NORMAL),
        KeyValueModel("مهلت پرداخت", Utility.getDateSeparator(PayPremiumDate), _textColor = EnumTextColor.AMBER)
    )

}
