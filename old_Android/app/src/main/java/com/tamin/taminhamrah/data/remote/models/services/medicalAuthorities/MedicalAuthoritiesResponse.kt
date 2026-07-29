package com.tamin.taminhamrah.data.remote.models.services.medicalAuthorities

import androidx.annotation.ColorRes
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class MedicalAuthoritiesResponse : ListDataModel<MedicalAuthoritiesModel>()

data class MedicalAuthoritiesModel(
    @SerializedName("reqHelptype")
    var supportType: String? = null,
    @SerializedName("centerName")
    var treatmentCenter: String? = null,
    @SerializedName("confirmOk")
    var confirmInBranch: String? = null,
    @SerializedName("confirmGet")
    var confirmStatus: String? = null,

    @SerializedName("risuid")
    var insuranceNumber: String? = null,
    @SerializedName("nationalId")
    var nationalCode: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    @SerializedName("fromDate")
    var outpatientRestStartDate: String? = null,
    @SerializedName("toDate")
    var outpatientRestEndDate: String? = null,
    @SerializedName("ddSar")
    var numberOfOutpatientDays: String? = null,
    @SerializedName("sDate")
    var hospitalizationStartDate: String? = null,
    @SerializedName("eDate")
    var hospitalizationEndDate: String? = null,
    @SerializedName("ddBas")
    var numberOfHospitalizationDays: String? = null,
    @SerializedName("comment")
    var description: String? = null,
    @SerializedName("branchName")
    var branch: String? = null,
    var fromDateNotConfirm: String? = null,
    var toDateNotConfirm: String? = null
) {
    // 0- تایید شعبه  -سبز
    // 1- تایید شده - سبز
    // 2- بخشي از دوره بيماري تايئد شده - اوکر
    // 3-  قابل بررسي نمي باشد - قرمز
    // 4- تايئد نشده - قرمز
    // 5- به کميسيون پزشکي ارجاع شود - آبی
    // 6- نیاز  به تکميل مدارک-اوکر
    @ColorRes
    private fun getRelativeColor(text: String?): Int {

        if (text == null) {
            return R.color.red
        }
        if (text == "بخشي از دوره بيماري تايئد شده" || text == "بخشي از دوره بيماري تایید شده" || text == "نیاز  به تکميل مدارک") {
            return R.color.text_color_orange_light
        }

        if (text.contains("تایید") || text.contains("تائيد") || text.contains("تايئد")) {
            if (text.contains("نشده") || text.contains("عدم")) {
                return R.color.red
            }
            return R.color.green
        }
        return R.color.red
    }

    @ColorRes
    fun getConfirmStatusTextColor() = getRelativeColor(confirmStatus)

    @ColorRes
    fun getConfirmInBranchTextColor() = getRelativeColor(confirmInBranch)

    private val keyValueList = mutableListOf<KeyValueModel>()

    fun exportKayValue(): List<KeyValueModel> {
        keyValueList.add(
            KeyValueModel(_keyStringResId = R.string.support_type, _value = supportType ?: "_")
        )

        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.outpatient_rest_start_date,
                _value = Utility.getDateSeparator(outpatientRestStartDate),
                _textColor = EnumTextColor.BLUE
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.outpatient_rest_end_date,
                _value = Utility.getDateSeparator(outpatientRestEndDate),
                _textColor = EnumTextColor.BLUE
            )
        )

        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.number_of_outpatient_days,
                _value = numberOfOutpatientDays ?: "_"
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.hospitalization_start_date,
                _value = Utility.getDateSeparator(hospitalizationStartDate),
                _textColor = EnumTextColor.BLUE
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.hospitalization_end_date,
                _value = Utility.getDateSeparator(hospitalizationEndDate ),
                _textColor = EnumTextColor.BLUE
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.number_of_hospitalization_days,
                _value = numberOfHospitalizationDays ?: "_"
            )
        )
        keyValueList.add(KeyValueModel(_keyStringResId = R.string.branch, _value = branch ?: "_"))
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.from_date_not_confirm,
                _value = fromDateNotConfirm ?: "_",
                _textColor = EnumTextColor.BLUE
            )
        )
        keyValueList.add(
            KeyValueModel(
                _keyStringResId = R.string.to_date_not_confirm,
                _value = toDateNotConfirm ?: "_",
                _textColor = EnumTextColor.BLUE
            )
        )
        keyValueList.add(
            KeyValueModel(_keyStringResId = R.string.description, _value = description ?: "_")
        )

        return keyValueList
    }
}