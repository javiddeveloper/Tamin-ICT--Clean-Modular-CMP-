package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class BranchDetailResponse : ListDataModel<BranchDetailModel>()
data class BranchDetailModel(
    val branchAddress: String? = null,
    val cityCode: String? = null,
    val code: String? = null,
    val maxCode: String? = null,
    val minCode: String? = null,
    val name: String? = null,
    val status: String? = null,
    val type: String? = null
)