package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.ListDataModel


class FreelancerJobTitlesResponse: ListDataModel <FreelancerJobTitleModel>()
data class FreelancerJobTitleModel(
    @SerializedName("discrioption")
    val jobTitle: String? = null,
    val endDate: String? = null,
    val fixRank: String? = null,
    val id: Int? = null,
    val iscoCode: Any? = null,
    val jobCode: String? = null,
    val startDate: String? = null,
    val status: String? = null
)