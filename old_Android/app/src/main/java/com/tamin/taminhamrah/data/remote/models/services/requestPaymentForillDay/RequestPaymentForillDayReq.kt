package com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay


import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayReq

data class RequestPaymentForillDayReq(
    var bimDrid: String? = null,
    var bimDrname: String? = null,
    var bimEdateTimeStamp: Long? = null,
    var bimKind: String = "",
    var bimSdateTimeStamp: Long? = null,
    var bimWkstatus: String = "2",
    var provinceCode: String = "",
    var cityCode: String = "",
    var shorttermRequest: RequestForPregnancyPayReq.ShorttermRequest? = null
)

data class ShorttermRequest(
    val resultMessage: String? = null
)