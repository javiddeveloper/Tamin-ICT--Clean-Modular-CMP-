package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


class ShortTermOrthosisResponse (val data : ShortTermOrthosisModel? = null):BaseResponseNew()
data class ShortTermOrthosisModel(
    val shorttermRequest: resultMessage
)
data class resultMessage(
    val resultMessage: String
)