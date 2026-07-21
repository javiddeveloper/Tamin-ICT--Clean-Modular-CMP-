package com.tamin.taminhamrah.data.remote.models.services.payment

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility
import kotlinx.parcelize.Parcelize

data class PaymentInfoResponse(
    var data: PaymentInfo? = null
) : BaseResponseNew()

@Parcelize
data class PaymentInfo(
    var affectiveAmount: Long? = 0,
    var clientId: String? = null,
    var milliSecondsToExpire: Long? = 0,
    var paymentAmount: Long? = 0,
    var paymentDesc: String? = null,
    var paymentId: String? = null,
    var paymentStatus: String? = null,
    var paymentUpdatedDate: Long? = 0,
    var redirectURL: String? = null,
    var refNum: String? = null,
    var responseCreationDate: String? = null,
    var responseTraceNo: String? = null,
    var traceDate: String? = null,
    var traceNo: String? = null,
    var transactionResultDesc: String? = null,
    var wage: Int? = 0,
    var ticket: String? = ""
) : Parcelable {

    fun getPaymentValue(): String {
        return Utility.getRialWithSeparator(affectiveAmount)
    }

    fun getPaymentPreview(): String {
        return Utility.getRialWithSeparator(paymentAmount)
    }

    fun getPaymentTitle(): String {
        return if (affectiveAmount == 0L) return "مبلغ تراکنش" else "مبلغ پرداخت"
    }

    fun getPaymentDate(): String {
        return if (traceDate == null)
            ""
        else
            ConvertDate.convertTimestampToPersianDate(traceDate!!)
    }
}