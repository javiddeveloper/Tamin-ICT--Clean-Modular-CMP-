package com.tamin.taminhamrah.data.remote.models.services.retirementPension

import android.os.Parcelable
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import kotlinx.parcelize.Parcelize

class RetirementRequestInfoResponse :ListDataModel<RetirementRequestInfoModel>()
@Parcelize
data class RetirementRequestInfoModel(
    val activityType: String? = null,
    val address: String? = null,
    val age: String? = null,
    val birthDate: Long? = null,
    val branchCode: String? = null,
    val fatherName: String? = null,
    val firstName: String? = null,
    val gender: String? = null,
    val insuranceNumber: String? = null,
    val issuePlace: String? = null,
    val idNumber: String? = null,
    val lastName: String? = null,
    val mobileNumber: String? = null,
    val nationalCode: String? = null,
    val phoneNumber: String? = null,
    val workshopAddress: String? = null,
    val workshopCode: String? = null,
    val workshopName: String? = null,
    val managerName: String? = null
):Parcelable{
    fun getIdentityInfo() = arrayListOf(
        KeyValueModel(_keyStringResId = R.string.full_name,  _value ="$firstName $lastName"),
        KeyValueModel(_keyStringResId = R.string.father_name,  _value =fatherName?:"-"),
        KeyValueModel(_keyStringResId = R.string.insurance_num,  _value =insuranceNumber?:"-"),
        KeyValueModel(_keyStringResId = R.string.identity_number,  _value =idNumber ?:"-"),
        KeyValueModel(_keyStringResId = R.string.gender,  _valueStringResId =if (gender == "01") R.string.man else R.string.woman),
        KeyValueModel(_keyStringResId = R.string.birthdate,  _value = ConvertDate.convertTimestampToPersianDate(birthDate ?: 0) ?:"-"),
        KeyValueModel(_keyStringResId = R.string.age,  _value =age ?:"-"),
        KeyValueModel(_keyStringResId = R.string.issue_city,  _value =issuePlace ?:"-"),
        )

    fun getWorkshopAndHomeInfo() = arrayListOf(
        KeyValueModel(_keyStringResId = R.string.label_tel,  _value =phoneNumber ?:"-"),
        KeyValueModel(_keyStringResId = R.string.mobile,  _value =mobileNumber ?:"-"),
        KeyValueModel(_keyStringResId = R.string.label_address,  _value =address ?:"-"),
        KeyValueModel(_keyStringResId = R.string.label_workshop_name,  _value =workshopName ?:"-"),
        KeyValueModel(_keyStringResId = R.string.workshop_number,  _value =workshopCode ?:"-"),
        KeyValueModel(_keyStringResId = R.string.label_workshop_address,  _value =workshopAddress ?:"-"),
        KeyValueModel(_keyStringResId = R.string.label_employer_name,  _value =managerName ?:"-"),
        KeyValueModel(_keyStringResId = R.string.workshop_activity,  _value =activityType ?:"-"),
        KeyValueModel(_keyStringResId = R.string.label_workshop_address,  _value =workshopAddress ?:"-"))
}
