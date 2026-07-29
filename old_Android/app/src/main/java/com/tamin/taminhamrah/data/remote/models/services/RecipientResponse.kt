package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class RecipientResponse : ListDataModel<Recipient>()

data class Recipient(
    var recipientCode: String = "",
    var recipientName: String = ""
)

fun Recipient.asDomainModel(): MenuModel {
    return MenuModel(
        id = this.recipientCode,
        title = this.recipientName
    )
}

fun List<Recipient>.asDomainModel(): List<MenuModel> {
    return map {
        it.asDomainModel()
    }
}