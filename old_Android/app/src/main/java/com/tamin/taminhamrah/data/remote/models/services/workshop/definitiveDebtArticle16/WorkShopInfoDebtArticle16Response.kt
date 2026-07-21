package com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class WorkShopInfoDebtArticle16Response(val data: WorkShopInfoDebtArticle16Model?=null):BaseResponseNew()
data class WorkShopInfoDebtArticle16Model(
    val character: String? = null,
    val employerName: String? = null,
    val lastAddress: String? = null,
    val workshopId: String? = null,
    val workshopName: String? = null,
    val branchCode:String? = null
)