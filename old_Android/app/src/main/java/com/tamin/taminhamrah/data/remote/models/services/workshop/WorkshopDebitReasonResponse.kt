package com.tamin.taminhamrah.data.remote.models.services.workshop

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class WorkshopDebitReasonResponse : ListDataModel<WorkshopDebitReason>()

data class WorkshopDebitReason(
    var amtStatus: String? = null,
    var debitCreateReasonCode: String? = null,
    var debitCreateReasonDesc: String? = null,
    var ordCase: String? = null,
    var status: String? = null,
    var statusStDate: String? = null
)

fun WorkshopDebitReason.asDomainModel(): MenuModel {
    return MenuModel(
        title = debitCreateReasonDesc,
        id = debitCreateReasonCode
    )
}

fun List<WorkshopDebitReason>.asDomainModel(): List<MenuModel> {
    return map {
        it.asDomainModel()
    }
}
