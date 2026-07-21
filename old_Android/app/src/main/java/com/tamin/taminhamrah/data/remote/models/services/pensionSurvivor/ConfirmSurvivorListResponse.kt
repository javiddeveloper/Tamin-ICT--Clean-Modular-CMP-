package com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class ConfirmSurvivorListResponse : ListDataModel<ConfirmSurvivorListModel>()

data class ConfirmSurvivorListModel (val request: RequestModel? = null)

data class RequestModel(val id : Int? = 0)