package com.tamin.taminhamrah.data.remote.models.user

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


data class EditMobileResponse(val traceId:String?="",val data : EditMobile? = null) : BaseResponseNew()
data class EditMobile(
    var hash: String? = null,
    var expirationTime: Long? = null
)



