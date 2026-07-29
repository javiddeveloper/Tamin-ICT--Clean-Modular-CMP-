package com.tamin.taminhamrah.data.remote.models.services.retirementPension

import androidx.room.Ignore
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate

data class RetirementPersonalInfoResponse(
    val data: RetirementPersonalInfoDataModel? = null,
) : BaseResponseNew()

data class RetirementPersonalInfoDataModel(
    val branch: String? = "",
    val branchName: String? = "",
    val insuranceId: String? = "",
    val mobileNumber: String? = "",
    val organizationId: String? = "",
    val personal: Personal = Personal(),
    val provinceName: String? = "",
    val work: Work? = Work(),
    @Ignore()
    var strAge: String = "",
    var verificationResult :String? = null
) {
    fun getIdentityInfo() = listOf(
        KeyValueModel(_keyStringResId = R.string.full_name,
            _value = "${personal.firstName} ${personal.lastName}"),
        KeyValueModel(_keyStringResId = R.string.insurance_num, _value = insuranceId ?: "_"),
        KeyValueModel(_keyStringResId = R.string.national_code,
            _value = personal.nationalId ?: "_"),
        KeyValueModel(_keyStringResId = R.string.father_name, _value = personal.fatherName ?: "_"),
        KeyValueModel(_keyStringResId = R.string.label_card_id,
            _value = personal.idCardNumber ?: "_"),
        KeyValueModel(_keyStringResId = R.string.gender,
            _valueStringResId = if (personal.gender.genderCode == "01") R.string.man else R.string.woman),
        KeyValueModel(_keyStringResId = R.string.birthdate,
            _value = ConvertDate.convertTimestampToPersianDate(personal.dateOfBirth ?: 0)),
        KeyValueModel(_keyStringResId = R.string.age, _value = strAge),
        KeyValueModel(_keyStringResId = R.string.issue_city,
            _value = personal.cityOfIssue.description ?: "_"),
        KeyValueModel(_keyStringResId = R.string.mobile, _value = mobileNumber ?: "_"),
    )

    fun getBranchInfo() = listOf(
        KeyValueModel(_keyStringResId = R.string.label_province_name , _value = provinceName ?: "_"),
        KeyValueModel(_keyStringResId = R.string.code_last_branch , _value = branch ?: "_"),
        KeyValueModel(_keyStringResId = R.string.name_last_branch , _value = branchName ?: "_"),
        KeyValueModel(_keyStringResId = R.string.workshop_number , _value = work?.workshopId ?: "_"),
    )

    fun loadRequestInfo() = ConfirmIdentityAndHistoryInfoRequest(
        firstName=personal.firstName ?:"",
        lastName = personal.lastName?:"",
        fatherName = personal.fatherName?:"",
        birthDate = personal.dateOfBirth?:0L,
        gender = personal.gender.genderCode?:"",
        age = strAge,
        branchCode = branch?:"",
        idNumber = personal.idCardNumber?:"",
        insuranceNumber = insuranceId?:"",
        issuePlace = personal.cityOfIssue.title?:"",
        mobileNumber = mobileNumber?:"",
        nationalCode = personal.nationalId?:"",
        workshopCode =work?.workshopId?:"",
        status = "0"
    )
}

data class Personal(
    val cityOfBirth: CityOfBirth = CityOfBirth(),
    val cityOfIssue: CityOfIssue = CityOfIssue(),
    val confirmed: Boolean? = false,
    val dateOfBirth: Long? = null,
    val fatherName: String? = "",
    val firstName: String? = "",
    val gender: Gender = Gender(),
    val idCardNumber: String? = "",
    val idCardSerial1: String? = "",
    val idCardSerial2: String? = "",
    val lastName: String? = "",
    val nation: Nation? = Nation(),
    val nationalId: String? = "",
)

data class Work(
    val job: Job? = null,
    val workshopId: String? = null,
    val workshopName: String? = null
)

data class Job(
    val jobCode: String? = null,
    val jobDescription: String? = null,
    val status: String? = null,
    val statusDate: String? = null,
)

data class CityOfBirth(
    val code: String? = "",
    val description: String? = "",
    val id: Int? = 0,
    val isDefault: Any? = Any(),
    val parent: Parent? = Parent(),
    val title: String? = "",
)

data class CityOfIssue(
    val code: String? = "",
    val description: String? = "",
    val id: Int? = 0,
    val isDefault: Any? = Any(),
    val title: String? = "",
)

data class Gender(
    val genderCode: String? = null,
    val genderDesc: String? = null,
    val status: Any? = null,
    val statusDate: Any? = null,
)

data class Nation(
    val nationCode: String? = null,
    val nationDesc: String? = null,
    val status: String? = null,
    val statusDate: String? = null,
)

data class Parent(
    val code: String? = "",
    val description: String? = "",
    val id: Int? = 0,
    val isDefault: Any? = Any(),
    val title: String? = "",
)
