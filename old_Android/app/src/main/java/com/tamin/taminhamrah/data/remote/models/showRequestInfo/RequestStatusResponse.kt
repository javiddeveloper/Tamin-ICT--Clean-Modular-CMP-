package com.tamin.taminhamrah.data.remote.models.showRequestInfo

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class RequestStatusResponse : ListDataModel<RequestStatusModel>()
data class RequestStatusModel(
    @SerializedName("date_acc")
    val processingTimeStamp: Long? = null,
    @SerializedName("process_result")
    val processResult: String? = null,
    @SerializedName("rejectReson")
    val rejectReason: String? = null
)