package com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor

import androidx.room.Ignore
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.isNumericString

data class DeceasedInfoResponse(var data: DeceasedInfoDataModel = DeceasedInfoDataModel()) :
    BaseResponseNew()

data class DeceasedInfoDataModel(
    val branchCode: String?=null ,
    val branchName: String?=null,
    val deadDate: String?=null,
    val insuranceId: String?=null,
    val pensionerId: String?=null,
    val personal: Personal? = Personal(),
    @Ignore()
    var yearsAge: String?=null,
    @Ignore()
    var monthsAge: String?=null,
    @Ignore()
    var daysAge: String?=null,
) {
    fun getDeceasedInfo() = mutableListOf(
        KeyValueModel(_keyStringResId = R.string.full_name,
            _value = "${personal?.firstName?:"_"} ${personal?.lastName?:"_"}"),
        KeyValueModel(_keyStringResId = R.string.father_name, _value = personal?.fatherName ?:"_"),
        KeyValueModel(_keyStringResId = R.string.insurance_num, _value = insuranceId?:"_"),
        KeyValueModel(_keyStringResId = R.string.identity_number, _value = personal?.idCardNumber ?:"_"),
        KeyValueModel(_keyStringResId = R.string.gender_title,
            _valueStringResId = if (personal?.gender == "01") R.string.man else R.string.woman),
        KeyValueModel(_keyStringResId = R.string.birthdate,
            _value = if (personal?.dateOfBirth != 0L) ConvertDate.convertTimestampToPersianDate(personal?.dateOfBirth ?:0) else ""),
        KeyValueModel(_keyStringResId = R.string.age, _value = "$yearsAge سال و $monthsAge ماه و $daysAge روز"),
        KeyValueModel(_keyStringResId = R.string.label_place_issue, _value = personal?.cityOfIssueDesc ?:"_"),
        KeyValueModel(_keyStringResId = R.string.last_branch, _value = branchCode?:"_"),
        KeyValueModel(_keyStringResId = R.string.last_branch_name, _value = branchName?:"_"),
        KeyValueModel(_keyStringResId = R.string.decease_date,
            _value = if (deadDate?.isNumericString() == true) Utility.getDateSeparator(deadDate) else ""),
        KeyValueModel(_keyStringResId = R.string.previous_pension_id, _value = pensionerId?:"_"))
}

data class Personal(
    val cityOfIssueDesc: String? =null,
    val dateOfBirth: Long? =null,
    val fatherName: String? =null,
    val firstName: String? =null,
    val lastName: String? =null,
    val nationalId: String? =null,
    val gender: String? =null,
    val idCardNumber: String? =null,
)

