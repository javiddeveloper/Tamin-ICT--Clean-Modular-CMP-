package com.tamin.taminhamrah.data.remote.models.refreshtoken

import com.google.gson.annotations.SerializedName

data class RevokeResponse(
    @SerializedName("status")
    val status: String = ""
)