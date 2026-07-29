package com.tamin.taminhamrah.data.remote.models.employer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class DebtDiscountResponse(
    @SerializedName("data")
    @Expose
    val data: Long? = null
) : BaseResponseNew()
