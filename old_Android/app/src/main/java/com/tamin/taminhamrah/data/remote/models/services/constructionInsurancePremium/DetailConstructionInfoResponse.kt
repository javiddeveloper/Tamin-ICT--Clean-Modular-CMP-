package com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class DetailConstructionInfoResponse:ListDataModel<DetailConstructionInfoModel>()

data class DetailConstructionInfoModel(
    val nationalCode: String?="_",
    val ownerType: String?="_",
    val requestNumber: Long?=0,
    val fileNumber: Long?=0,
    val requestDate: String?="_",
    val name: String?="_",
    val lastName: String?="_",
    val mobile: String?="_",

)

