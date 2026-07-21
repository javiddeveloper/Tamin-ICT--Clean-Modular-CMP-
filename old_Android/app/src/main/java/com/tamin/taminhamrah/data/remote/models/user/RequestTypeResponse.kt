package com.tamin.taminhamrah.data.remote.models.user

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class RequestTypeResponse : ListDataModel<MyRequestType>()

data class MyRequestType(

    var createdBy: Any? = null,
    var creationTime: Any? = null,
    var description: String? = null,
    var id: Int = 0,
    var lastModificationTime: Any? = null,
    var lastModifiedBy: Any? = null,
    var title: String? = null,

    )

fun MyRequestType.asDomainModel(): MenuModel {
    return MenuModel(
        title = this.title,
        id = this.id.toString()
    )
}

fun List<MyRequestType>.asDomainModel(): List<MenuModel> {
    return map {
        it.asDomainModel()
    }
}