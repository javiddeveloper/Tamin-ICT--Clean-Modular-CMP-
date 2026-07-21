package com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility
import kotlinx.parcelize.Parcelize

class WorkshopsDebtListResponse:ListDataModel<WorkshopsDebtListModel>()

@Parcelize
data class WorkshopsDebtListModel(

    @SerializedName("bikariAmount")
    val indebtednessAmount: Int? = null,

    @SerializedName("bimehAmount")
    val insuranceAmount: Int? = null,

    val debitAmount: Int? = null,
    val debitCreateReasonCode: String? = null,
    val debitEndDate: String? = null,
    val debitNumber: String? = null,
    val debitRemain: Int? = null,
    val debitStartDate: String? = null,
    val debitStatCode: String? = null,
    val debitStepCode: String? = null,

    @SerializedName("docDateBadvi")
    val primaryDebtDate: String? = null,

    @SerializedName("docDateEblaghEjra")
    val dateExecutiveNotification: String? = null,

    @SerializedName("docDateEblaghEkhtar")
    val dateWarningNotification: String? = null,

    @SerializedName("docDateEjra")
    val dateImplementation: String? = null,

    @SerializedName("docDateEkhtar")
    val warningDate: String? = null,

    @SerializedName("docDateTajdid")
    val renewalDate: String? = null,

    @SerializedName("docNoTajdid")
    val renewalNumber: String? = null,

    @SerializedName("docNoBadvi")
    val primaryDebtNumber: String? = null,

    @SerializedName("docNoEjra")
    val executiveNumber: String? = null,

    @SerializedName("jarimehAmount")
    val fineAmount: Int? = null,

    val kindDoc: String? = null,

    @SerializedName("mastCustomerCode")
    val customerCode: String? = null,

    @SerializedName("mastCustomerTypeCode")
    val customerTypeCode: String? = null,

    @SerializedName("peymanSequence")
    val agreementRow: String? = null,

    val rowNum: Int? = null,

    @SerializedName("sayerAmount")
    val otherAmount: Int? = null,

    val seqNo:Long?=null,

    @SerializedName("stepCat")
    val stepCat: String? = null,

    val status:String?=null
):Parcelable {
    fun getDetailInfoMainPage(): List<KeyValueModel> {
        val value: Int
        var textColor = EnumTextColor.NORMAL
        when (status) {
            "1" -> {
                value = R.string.submit_request_state
                textColor = EnumTextColor.BLUE
            }
            "7" -> {
                value = R.string.doc_violation_state
                textColor = EnumTextColor.AMBER
            }
            "8" -> {
                value = R.string.reject_request_state
                textColor = EnumTextColor.RED
            }
            "9" -> {
                value = R.string.confirm_request_state
                textColor = EnumTextColor.GREEN
            }
            else -> {
                value = R.string.unknown_state
            }
        }
        return listOf(
            KeyValueModel(_keyStringResId = R.string.account_code, _value = debitNumber ?: "-"),
            KeyValueModel(_keyStringResId = R.string.debit_amount_rial,
                _value = Utility.getNumberWithSeparatorForStringValue(debitAmount.toString()),
                _textColor = EnumTextColor.BLUE),
            KeyValueModel(_keyStringResId = R.string.debit_amount_residue_rial,
                _value = Utility.getNumberWithSeparatorForStringValue(debitRemain.toString()),
                _textColor = EnumTextColor.AMBER),
            KeyValueModel(_keyStringResId = R.string.label_date_from,
                _value = Utility.getDateSeparator(debitStartDate)),
            KeyValueModel(_keyStringResId = R.string.label_date_to,
                _value = Utility.getDateSeparator(debitEndDate)),
            KeyValueModel(_keyStringResId = R.string.label_agreement_row,
                _value = agreementRow ?: "_"),
            KeyValueModel(_keyStringResId = R.string.request_status,
                _valueStringResId = value,
                _textColor = textColor),

            )
    }
     fun getDetailInfoInvestigationPage() = listOf(
         KeyValueModel(_keyStringResId = R.string.account_code, _value = debitNumber ?: "-"),
         KeyValueModel(_keyStringResId = R.string.label_agreement_row, _value = agreementRow ?: "_"),
         KeyValueModel(_keyStringResId = R.string.debt_period_from, _value = Utility.getDateSeparator(debitStartDate), _textColor = EnumTextColor.BLUE),
         KeyValueModel(_keyStringResId = R.string.debt_period_up_to, _value = Utility.getDateSeparator(debitEndDate) , _textColor = EnumTextColor.BLUE),
         KeyValueModel(_keyStringResId = R.string.debt_step, _value = debitStepCode ?:"_", _textColor = EnumTextColor.BLUE),
         KeyValueModel(_keyStringResId = R.string.edict_number, _value = executiveNumber ?:"_" ),
         KeyValueModel(_keyStringResId = R.string.edict_date, _value = Utility.getDateSeparator(dateImplementation)),
         KeyValueModel(_keyStringResId = R.string.date_delivery, _value = Utility.getDateSeparator(dateExecutiveNotification), _textColor = EnumTextColor.AMBER),
     )

}

