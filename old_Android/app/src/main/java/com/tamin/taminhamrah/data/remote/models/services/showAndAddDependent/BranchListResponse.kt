package com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class BranchListResponse: BaseResponseNew() {
    val data: List<BranchModel>? = null

    fun getBranchList(): List<MenuModel> {
        return data?.map{
            MenuModel(
                id = it.branchCode ?: "",
                title = it.branchName + "-" + it.workshopName
            )
        } ?: emptyList()
    }
}

data class BranchModel(
    val branchCode: String = "" ,
    val branchName: String = "" ,
    val workshopCode: String = "" ,
    val workshopName: String = ""
)