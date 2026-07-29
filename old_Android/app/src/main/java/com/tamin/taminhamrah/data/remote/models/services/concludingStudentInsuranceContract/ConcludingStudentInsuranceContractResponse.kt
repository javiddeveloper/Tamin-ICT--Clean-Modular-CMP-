package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate

class ConcludingStudentInsuranceContractResponse
    (var data:CreateContractModel? = null): BaseResponseNew()

data class CreateContractModel(
    val insuranceId: String = "-",
    val mobileNumber: String = "-",
    val personalInfo: PersonalInfo?=null,
   public val lastContact: LastContact? = null
){
    fun getPersianDate(timeStamp: Long?): String {
        return ConvertDate.convertTimestampToPersianDate(timeStamp ?: 0)
    }

    fun createKeyValue(item: CreateContractModel): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نام و نام خانوادگی", "${item.personalInfo?.firstName} ${item.personalInfo?.lastName}" ?: "-"))
        keyValueList.add(KeyValueModel("کد ملی", item.personalInfo?.nationalId?: "-"))
        keyValueList.add(KeyValueModel("شماره بیمه", item.insuranceId ?: "0"))

        return keyValueList
    }
}

data class PersonalInfo(
    val firstName: String = "-",
    val lastName: String = "-",
    val fatherName: String = "-",
    val nationalId: String = "-",
    val dateOfBirth: Long = 0,
    val gender: Gender? = null,
    val idCardNumber: String = "-",
    val cityOfBirth: CityOfBirth? = null,
    val cityOfIssue: CityOfIssue? = null,
    val ssn: String? = null
    )

data class LastContact(
    val address: String? = null,
  //  val city:String? = null,
    val mobile: String? = null,
    val phoneNumber: String? = null,
    val zipCode: String? =null
)

data class CityOfBirth(
    val title:String = "-",
    val description:String = "-"
)

data class Gender(
    val genderCode:String = "-",
    val genderDesc:String = "-"
)

data class CityOfIssue(
    val description:String = "-",
    val title:String = "-",
)

