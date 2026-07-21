package com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class PaymentSheetConstructionFilesResponse : ListDataModel<PaymentSheetConstructionFilesModel>()

data class PaymentSheetConstructionFilesModel(
    val orderNumber: String?=null,
    @SerializedName("shenase")
    val paymentCode: String?=null,
    val paymentSheetAmount: Long?=null,
    val status: String?=null,
    val paymentDate: String?=null,
    val buildingRequest: BuildingRequest?=null
    ) {
    fun getRequestInfo() = listOf(
        KeyValueModel(
            _keyStringResId = R.string.file_number,
            _value = (buildingRequest?.fileNumber ?: 0).toString(),
            _textColor = EnumTextColor.AMBER
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_request_number,
            _value = (buildingRequest?.requestNumber ?: 0).toString()
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_registration_date,
            _value = Utility.getDateSeparator(buildingRequest?.requestDate?.substring(0,8)),
            _textColor = EnumTextColor.GREEN
        ),
        KeyValueModel(
            _keyStringResId = R.string.workshop_number,
            _value = buildingRequest?.workshopId?.workshopId ?: "-"
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_branch,
            _value = (buildingRequest?.workshopId?.brhCode ?: "_")
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_debit_number,
            _value = (buildingRequest?.debitNumber ?: "_")
        ),
        KeyValueModel(
            _keyStringResId = R.string.calculated_amount,
            _value = (buildingRequest?.totalPayment ?: 0).toString()
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_payment_dead_line,
            _value = Utility.getDateSeparator(buildingRequest?.paymentDeadLine)
        )
    )
}


data class BuildingRequest(
    val debitNumber: String?=null,
    val fileNumber: Long?=null,
    val requestNumber: Long?=null,
    val requestDate: String?=null,
    val totalPayment: Long?=null,
    val paymentDeadLine: String?=null,
    val workshopId: WorkshopId?=null
)

data class WorkshopId(
    val workshopId: String?=null,
    val brhCode: String?=null
)

