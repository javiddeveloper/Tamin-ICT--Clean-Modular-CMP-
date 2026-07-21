package com.tamin.taminhamrah.data.remote.models.services.workshop

import android.content.Context
import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.DecimalFormat

class InstallmentListResponse: ListDataModel<WorkShopDebtModel>()

@Parcelize
data class WorkShopDebtModel(
    val serialNo: Long? = null,
    val debitNumber: String? = null,
    val workshopId: String? = null,
    val peymanSequence: String? = null,
    val letterNumber: String? = null,
    val letterDate: String? = null,
    val installmentType: String? = null,
    val installmentSate: String? = null,
    val installmentAmount: Long? = null,
    val installmentNumber: Int? = null,
    val createUserId: String? = null,
    val createDate: String? = null,
    val debitStartDate: String? = null,
    val debitEndDate: String? = null,
    val dateFirstInstallment: String? = null,
    val stepCat: String? = null,
    val firstInstallment: Long? = null,
    val firstInstallmentPer: String? = null,
    val eachInstallment: Long? = null,
    val branchCode: String? = null,
    val garantyType: String? = null,
    val refId: String? = null,
    val payAghsat: Long? = null,
    val finishCode: String? = null,
    val closingTime: String? = null,
    val debitInstallmentDetail: String? = null,
    val message: String? = null,
    @IgnoredOnParcel @ApplicationContext val context: Context? = null
) : Parcelable {
    fun allDebt() = formatter.format(installmentAmount).toString()
    fun getFormattedLetterDate() = getFormattedDate(letterDate)

    @IgnoredOnParcel
    val formatter by lazy { DecimalFormat("#,###") }
    fun getInstallmentNumberValue() = installmentNumber.toString()
    fun firstInstallment() = formatter.format(firstInstallment).toString()
    fun getOtherInstallmentAmount() = formatter.format(eachInstallment).toString()
    fun getStartDebtDate() = getFormattedDate(debitStartDate)
    fun getEndDebtDate() = getFormattedDate(debitEndDate)
    fun getFirstPaymentDate() = getFormattedDate(dateFirstInstallment)
    fun getStatusRequest(): String {
        return when (finishCode) {
            "0" -> {
                "در انتظار پرداخت اقساط"
            }

            "1","5" -> {
                "پرداخت گردید"
            }

            "2" -> {
                "در انتظار تایید"
            }

            "3" -> {
                "تایید درخواست"
            }

            "4" -> {
                "رد درخواست"
            }
            else->{"_"}
        }
    }

    private fun getFormattedDate(date: String?) =
        if (date != null && date.length > 7) "${date.substring(0, 4)}/${date.substring(4, 6)}/${
            date.substring(
                6,
                8
            )
        }" else "-"
}
