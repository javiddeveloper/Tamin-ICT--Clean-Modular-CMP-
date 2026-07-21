package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.data.remote.models.ListDataModel


class BranchesInfoListResponse : ListDataModel<BranchesInfoListModel>()

data class BranchesInfoListModel(
    val branchAddress: String? = "-",
    val cityCode: String = "-",
    val code: String = "-",
    val maxCode: String = "-",
    val minCode: String = "-",
    val name: String? = "-",
    val status: String = "-",
    val type: String = "-"
) {
    fun getTitle() =
        "${if (name.isNullOrBlank()) "نامشخص" else name} - ${if (branchAddress.isNullOrBlank()) "" else branchAddress}"
}
