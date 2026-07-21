package com.tamin.taminhamrah.data.remote.models.services.treatmentServices.costs

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel as AiKeyModel
import com.tamin.taminhamrah.utils.Utility

class TreatmentCostsExpensesResponse : ListDataModel<TreatmentCostsExpensesModel>()


data class TreatmentCostsExpensesModel(
    val accountNumber: String? = null,
    val bimeCode: String? = null,
    val datePaz: String? = null,
    val famil: String? = null,
    val healthcenterName: String? = null,
    val mainNational: String? = null,
    val maliCode: String? = null,
    val name: String? = null,
    val nameAsli: String? = null,
    val nameFamil: String? = null,
    val noPazir: String? = null,
    val payNatCode: String? = null,
    val payOtherService: String? = null,
    val payPrice: String? = null,
    val payService: String? = null,
    val payStatus: String? = null,
    val payType: String? = null,
    val province: String? = null,
    val rahgiriCode: String? = null,
    val releaseDate: String? = null,
    val repId: Int? = 0,
    val serviceDate: String? = null,
    val status: String? = null,
    val statusDesc: String? = null,

    val payStatusDesc: String? = null,
    val returnReason: String? = null,
    val remark: String? = null,
    val estimatePayDate: String? = null
) {
    fun getPaymentValue(strPaymentAmount: String): String {
        return Utility.getRialWithSeparator(strPaymentAmount.toLong())
    }

    fun detailTreatmentCostInfo() = listOf(
        KeyValueModel(
            _keyStringResId = R.string.label_patient_national_code,
            _value = maliCode ?: "-",
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_date_reception,
            _value = datePaz ?: "-",
            _textColor = EnumTextColor.BLUE
        ),
        KeyValueModel(
            _keyStringResId = R.string.primary_insured,
            _value = nameAsli ?: "-",
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_file_status,
            _value = statusDesc ?: "-",
            _textColor = if (status == "7") EnumTextColor.GREEN else EnumTextColor.RED

        ),
        KeyValueModel(
            _keyStringResId = R.string.reasons_return_file,
            _value = returnReason ?: "-"
        ),
        KeyValueModel(
            _keyStringResId = R.string.refund_date,
            _value = estimatePayDate ?: "-",
            _textColor = EnumTextColor.BLUE
        ),
        KeyValueModel(
            _keyStringResId = R.string.treatment_center,
            _value = healthcenterName ?: "-",
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_prosthesis_payment_title,
            _value = "${Utility.getNumberWithSeparatorForStringValue(payService)} ریال",
            _textColor = if (payService != "0") EnumTextColor.BLUE else EnumTextColor.NORMAL
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_other_medical_services_payment_title,
            _value = "${Utility.getNumberWithSeparatorForStringValue(payOtherService)} ریال",
            _textColor = if (payOtherService != "0") EnumTextColor.GREEN else EnumTextColor.NORMAL
        )
    )

    fun createKeyValue(): List<AiKeyModel> {
        val keyValuesList: MutableList<AiKeyModel> = mutableListOf()
        nameFamil?.let{keyValuesList.add(AiKeyModel("نام بیمار", it))}
        noPazir?.let{keyValuesList.add(AiKeyModel("شماره پذیرش", it))}
        maliCode?.let{keyValuesList.add(AiKeyModel("کد ملی بیمار", it))}
        datePaz?.let{keyValuesList.add(AiKeyModel("تاریخ پذیرش", getLocalDate(it)))}
        statusDesc?.let{keyValuesList.add(AiKeyModel("وضعیت پرداخت", it))}
        returnReason?.let{keyValuesList.add(AiKeyModel("دلایل برگشت پرونده از مالی", it))}
        estimatePayDate?.let{keyValuesList.add(AiKeyModel("تاریخ باز پرداخت هزینه درمان", it))}
        healthcenterName?.let{keyValuesList.add(AiKeyModel("نام مرکز درمانی", it))}
        payService?.let{keyValuesList.add(AiKeyModel("پرداختی پروتز", it))}
        payOtherService?.toLongOrNull()?.let {keyValuesList.add(AiKeyModel("پرداختی سایر خدمات پزشکی", Utility.getRialWithSeparator(it)))}

        return keyValuesList
    }


    fun getLocalDate(date: String?) = Utility.getDateSeparator(date)
}
