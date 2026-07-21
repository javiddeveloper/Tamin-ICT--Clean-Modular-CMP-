package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class PensionerIdResponse : ListDataModel<PensionIdModel>()

data class PensionIdModel(var pensionerId: String? = null)


fun PensionIdModel.asDomainModel(): MenuModel {
    return MenuModel(
        id = this.pensionerId,
        title = this.pensionerId
    )
}

fun List<PensionIdModel>.asDomainModel(): List<MenuModel> {
    return map {
        it.asDomainModel()
    }
}


fun List<PensionIdModel>?.getUserType(): String {

  val   typeUser = when {
        this == null -> EnumTypeUser.TEMPORARY.title // Handle null explicitly first
        this.isEmpty() -> EnumTypeUser.INSURED.title // Handle empty list
        else -> EnumTypeUser.PENSIONER.title
    }
    return typeUser
}