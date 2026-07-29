package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class CancelContractReasonsResponse : ListDataModel<CancelContractReasonsModel>()

data class CancelContractReasonsModel(
    @SerializedName("selfIsuContStatDesc")
    val selfIsuContStatDesc: String? = null,
    @SerializedName("selfIsuContStatDode")
    val selfIsuContStatCode: Int? = null
)
