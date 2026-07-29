package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.entity.MenuModel

data class BranchModel(
    val branchCode: String? = null,
    val branchName: String? = null,
    val workshopCode: String? = null,
    val workshopName: String? = null
)

fun BranchModel.asDomainModel(): MenuModel {
    return MenuModel(
        id = this.branchCode?:"",
        title = this.branchName + "-" +this.workshopName
    )
}

fun List<BranchModel>.asDomainModel(): List<MenuModel> {
    return map {
        it.asDomainModel()
    }
}
