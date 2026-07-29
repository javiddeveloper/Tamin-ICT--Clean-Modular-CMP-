package com.tamin.taminhamrah.data.remote.models.services

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class DeferredInstallmentCertificateResponse (val data :DeferredInstallmentCertificateModel? = null):BaseResponseNew()
class DeferredInstallmentCertificateModel (
    @SerializedName("request")
    val request: RequestCertificate? = null
    ) {
    data class RequestCertificate(
        @SerializedName("refCode")
        val refCode: String? = null
    )
}
