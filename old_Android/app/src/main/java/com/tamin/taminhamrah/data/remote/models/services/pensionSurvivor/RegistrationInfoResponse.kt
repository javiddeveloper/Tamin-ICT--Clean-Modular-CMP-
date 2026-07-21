package com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class RegistrationInfoResponse
    (var data:RegistrationDataModel = RegistrationDataModel()): BaseResponseNew()

data class RegistrationDataModel(
    val personalInfo: PersonalInfo=PersonalInfo())

data class PersonalInfo(
    val firstName: String = "",
    val lastName: String = "",
    val gender: Gender = Gender(),
    )

data class Gender(
    val genderCode:String = "",
    val genderDesc:String = ""
)

