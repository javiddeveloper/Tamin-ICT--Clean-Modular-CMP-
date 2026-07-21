package com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class Article16RequestInfoResponse(val data : Article16RequestInfoModel?=null):BaseResponseNew()

data class Article16RequestInfoModel(
    val defectDesc: String? = null,
    val objectionPhotos: List<ObjectionPhoto>,
) {

data class ObjectionPhoto(
        @SerializedName("guid")
        val guid: String?=null,
        @SerializedName("seqNo")
        val seqNo: Int?=null,
        @SerializedName("type")
        val type: String?=null
    )
}