package com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class InquiryRegistryResponse(val data : RegistryDataModel= RegistryDataModel()):BaseResponseNew()
data class RegistryDataModel(
    val age: Int = 0 ,
    val birthDate: String = "" ,
    val fatherName: String = "" ,
    val firstName: String = "" ,
    val lastName: String = "" ,
    val nationalId: String = "",
    val gender: String = "",
    @SerializedName("insuranceId")
    val registryConfirmState: String = ""
)