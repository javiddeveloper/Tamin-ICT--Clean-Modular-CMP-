package com.tamin.taminhamrah.data.remote.models.services.construction

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.formattedDate

class WorkersPaymentInfoResponse : ListDataModel<WorkersPaymentInfo>()

data class WorkersPayDebitResponse(
    var data: WorkersPayDebit? = null
) : BaseResponseNew()
/*{"status":200,
    "family":"SUCCESSFUL",
    "reason":"OK",
    "data":
      {
        "total":1,
        "list":["https://tfh.tamin.ir/view/#/payment/f357d2fd-5525-4f96-829b-3b492c7ac516",
        "f357d2fd-5525-4f96-829b-3b492c7ac516",
        "fb4aebab65e8a03ed38487e16b498578053f43442a9b20bbe3a2303c29c4712a4464f0be59bef6bdcf18ec0dae5819aa663c2797bf7aee9c248b8bbd3feca9d7e1a3f338503d742a16fd42b4294a8bd7d54b721f2d7829f87b5ba9c11bb0e4d92278214275866baca208be37a3fe86c1b1201c828d32c3e5"]
    }
}*/
//https://tfh.tamin.ir/api/v1.1/payment/ticket/current-user/f357d2fd-5525-4f96-829b-3b492c7ac516
data class WorkersPayDebit(
    val total: Int,
    val list: List<String> = emptyList()
) {
    fun ticket() = list.elementAtOrNull(1)
    fun url() = list.elementAtOrNull(0)
    fun paymentInfo() = list.elementAtOrNull(3)
}

data class WorkersPaymentInfo(
    val pay: Boolean = false,
    val payable: Boolean = false,
    val fines: Boolean = false,
    var year: String? = null,
    var month: String? = null,
    var days: String? = null,
    var professional: String? = null,
    var professionalTitle: String? = null,
    var rate: String? = null,
    var amount: Long = 0,
    var amountFines: String? = null,
    var fromDate: Long = 0,
    var toDate: Long = 0,
    var payDay: Long = 0,
    @SerializedName("dastMoazd")
    var salary: Long = 0,
    var fromDatePersian: String? = null,
    var toDatePersian: String? = null,
    var intDate: Int = 0,
    var paymentDate: String? = null,
    var monthTitle: String? = null,
    var type: String? = null,
    var fromDateNotConfirm: String? = null,
    var fromDateToDate: String? = null,
    var payableDes: String? = null,
    var paymentStatus: String? = null
) {

    fun getTitleDays() = "تعداد روز : $days"

    private val keyValueList = mutableListOf<KeyValueModel>()

    fun exportKayValue(): List<KeyValueModel> {
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.from_date,
                _value = fromDatePersian.formattedDate()
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.to_date,
                _value = toDatePersian.formattedDate()
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.label_job_title,
                _value = professionalTitle ?: "_"
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.job_code,
                _value = professional ?: "_"
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.employment_rate,
                _value = rate ?: "_"
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.label_insurance,
                _value = Utility.getRialWithSeparator(amount),
                _textColor = EnumTextColor.BLUE,
                _isValueBold = true
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.fines,
                _value = "${Utility.getNumberWithSeparatorForStringValue(amountFines)} ریال ",
                _textColor = EnumTextColor.NORMAL,
                _isValueBold = true
            )
        )


        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.label_total_pay,
                _value = Utility.getRialWithSeparator(finalAmount()),
                _textColor = EnumTextColor.GREEN
            )
        )

        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.label_payment_date,
                _value = "$payDay".formattedDate()
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.payable,
                _value = payableDes ?: "_"
            )
        )
        return keyValueList
    }

    private fun finalAmount(): Long {
        var finalAmount = amount
        if (amountFines != null) {
            finalAmount += amountFines!!.toLong()
        }
        return finalAmount
    }

    fun makePayDebitRequest(): WorkersPayDebitRequest {
        return WorkersPayDebitRequest(
            amount = finalAmount(),
            listOf(fromDatePersian),
            listOf(fromDateToDate)
        )
    }
}

data class WorkersPayDebitRequest(
    val amount: Long,
    val dates: List<String?> = emptyList(),
    val fromToDate: List<String?> = emptyList()
)