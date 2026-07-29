package com.tamin.taminhamrah.data.remote.models.services.disabilityPension

import androidx.room.Ignore
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate

class  DisabilityPersonalInfoResponse (val data :DisabilityPersonalInfoDataModel? = null ) : BaseResponseNew() {

    data class DisabilityPersonalInfoDataModel(
        val branch: String? = "",
        val branchName: String? = "",
        val confirmed: Boolean? = false,
        val insuranceId: String? = "",
        val mobileNumber: String? = "",
        val personal: Personal = Personal(),
        val provinceName: String? = "",
        val work: Work? = Work(),
        @Ignore()
        var yearsAge: String = "",
        @Ignore()
        var monthsAge: String = "",
        @Ignore()
        var daysAge: String = "",
        @Ignore()
        var strAge: String = "",
    ) {
        fun getIdentityInfo() = listOf(
            KeyValueModel(_keyStringResId = R.string.full_name, _value = "${personal.firstName} ${personal.lastName}") ,
            KeyValueModel(_keyStringResId = R.string.insurance_num, _value = insuranceId ?:"_") ,
            KeyValueModel(_keyStringResId = R.string.national_code, _value = personal.nationalId ?:"_") ,
            KeyValueModel(_keyStringResId = R.string.father_name, _value = personal.fatherName ?:"_") ,
            KeyValueModel(_keyStringResId = R.string.card_id, _value = personal.idCardNumber ?:"_") ,
            KeyValueModel(_keyStringResId = R.string.gender, _valueStringResId = if (personal.gender.genderCode == "01") R.string.man else R.string.woman) ,
            KeyValueModel(_keyStringResId = R.string.birthdate, _value = ConvertDate.convertTimestampToPersianDate(personal.dateOfBirth ?: 0)) ,
            KeyValueModel(_keyStringResId = R.string.age, _value = strAge) ,
            KeyValueModel(_keyStringResId = R.string.issue_city, _value = personal.cityOfIssue.description ?:"_") ,
            KeyValueModel(_keyStringResId = R.string.mobile, _value = mobileNumber ?:"_") ,
        )

        fun getBranchInfo() = listOf(
            KeyValueModel(_keyStringResId = R.string.label_province_name , _value = provinceName ?: "_"),
            KeyValueModel(_keyStringResId = R.string.code_last_branch , _value = branch ?: "_"),
            KeyValueModel(_keyStringResId = R.string.name_last_branch , _value = branchName ?: "_"),
            KeyValueModel(_keyStringResId = R.string.label_workshop_number , _value = work?.workshopId ?: "_"),
        )
    }

    data class Personal(
        val firstName: String? = "",
        val lastName: String? = "",
        val nationalId: String? = "",
        val fatherName: String? = "",
        val idCardNumber: String? = "",
        val cityOfIssue: CityOfIssue = CityOfIssue(),
        val dateOfBirth: Long? = 0,
        val gender: Gender = Gender(),
    )

    data class CityOfIssue(
        val code: String = "",
        val description: String = "",
    )

    data class Gender(
        val genderCode: String = "",
        val genderDesc: String = "",
    )

    data class Work(
        val job: Job? = null,
        val workshopId: String? = null,
        val workshopName: Any? = null
    )

    data class Job(
        val jobCode: String? = null,
        val jobDescription: String? = null,
        val status: String? = null,
        val statusDate: String? = null
    )
}