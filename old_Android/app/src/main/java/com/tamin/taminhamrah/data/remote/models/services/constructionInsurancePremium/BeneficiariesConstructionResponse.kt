package com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class BeneficiariesConstructionResponse : ListDataModel<BeneficiariesConstructionModel>()

data class BeneficiariesConstructionModel(
    val nationalCode: String? = "_",
    val ownerType: String? = "_",
    val requestNumber: Long? = 0,
    val fileNumber: Long? = 0,
    val requestDate: String? = "_",
    val name: String? = "_",
    val lastName: String? = "_",
    val mobile: String? = "_",
) {
    fun getDetailInfo() = listOf(
        KeyValueModel(_keyStringResId = R.string.first_name, _value = name ?: "_",_textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.last_name, _value = lastName ?: "_",_textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.national_code, _value = nationalCode ?: "_"),
        KeyValueModel(
            _keyStringResId = R.string.title,
            _valueStringResId = if (ownerType == "01") R.string.owner else if (ownerType == "02") R.string.applicant else R.string.label_unknown, _textColor = EnumTextColor.GREEN
        ),
        KeyValueModel(_keyStringResId = R.string.mobile, _value = mobile ?: "_"),

        KeyValueModel(_keyStringResId = R.string.file_number, _value = fileNumber.toString(),_textColor = EnumTextColor.AMBER),
        KeyValueModel(
            _keyStringResId = R.string.label_request_number,
            _value = requestNumber.toString()
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_registration_date,
            _value = Utility.getDateSeparator(requestDate?.substring(0,8)?:"_"),_textColor = EnumTextColor.BLUE)
    )
}


