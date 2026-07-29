package com.tamin.taminhamrah.data.remote.models.user

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class InboxSizeResponse(
    var usage: Float = 0F,
    var total: Int = 0
) : BaseResponseNew()