package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class BeneficiaryResponse : ListDataModel<Beneficiary>()

data class Beneficiary(
    var bankCode: String = "",
    var bankName: String = ""
)

fun Beneficiary.asDomainModel(): MenuModel {
    return MenuModel(
        id = this.bankCode,
        title = this.bankName
    )
}

fun List<Beneficiary>.asDomainModel(): List<MenuModel> {
    return map {
        it.asDomainModel()
    }
}