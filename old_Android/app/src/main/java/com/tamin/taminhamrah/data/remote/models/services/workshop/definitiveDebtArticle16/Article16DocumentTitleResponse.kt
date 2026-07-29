package com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class Article16DocumentTitleResponse(
    @SerializedName("investigationItems")
    val investigationItems: List<InvestigationItem?>? = null,
) : BaseResponseNew()

data class InvestigationItem(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("value")
    val value: String? = null
)
