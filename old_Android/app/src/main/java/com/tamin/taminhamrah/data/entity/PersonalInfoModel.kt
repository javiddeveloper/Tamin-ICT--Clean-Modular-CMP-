package com.tamin.taminhamrah.data.entity

data class PersonalInfoModel(
    var id: String? = null,
    var firstName: String?,
    var lastName: String?,
    var fatherName: String? = null,
    var nationalCode: String?,
    var insuranceNumber: String? = null,
    var birthDate: String? = null,
    var birthDateTimeStamp: Long? = null,
    var mobileNumber: String?,
    var zipCode: String? = null,
    var address: String? = null,
    var childInsuranceId: String?,
    var deathDate: Long?,
    var deathType: Any? = null,
    var gender: String?,
    var idCardNumber: String?,
    var phoneNumber: String?,
    var parentInfo: ParentPersonalInfo? = null
) {

    fun createKeyValue(it: PersonalInfoModel): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(
            KeyValueModel(
                "نام و نام خانوادگی",
                "${it.firstName} ${it.lastName}"
            )
        )
        keyValueList.add(KeyValueModel("نام پدر", it.fatherName ?: "-"))
        keyValueList.add(KeyValueModel("کد ملی", it.nationalCode ?: "-"))
        keyValueList.add(KeyValueModel("شماره بیمه", it.insuranceNumber ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ تولد", it.birthDate ?: "-"))
        keyValueList.add(KeyValueModel("شماره همراه", it.mobileNumber ?: ""))
        return keyValueList
    }
}

data class ParentPersonalInfo(var nationalCode: String?, var pensionId: String?)

