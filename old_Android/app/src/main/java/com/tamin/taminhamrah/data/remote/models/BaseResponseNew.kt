package com.tamin.taminhamrah.data.remote.models

import com.tamin.taminhamrah.enums.ServiceStatus

abstract class BaseResponseNew  {

    var status: Int? = 0
    var family: String? = ""
    var reason: String? = ""
    var baseStatus: BaseStatus? = null
    var isBackToPrevious: Boolean = false
    val isSuccess: Boolean
        get() = baseStatus?.serviceStatus == ServiceStatus.SUCCESS

    val isError: Boolean
        get() = baseStatus?.serviceStatus == ServiceStatus.ERROR

    val isNeedNetwork: Boolean
        get() = baseStatus?.serviceStatus == ServiceStatus.NEED_NETWORK

    val needRefreshToken: Boolean
        get() = baseStatus?.serviceStatus == ServiceStatus.NEED_REFRESH_TOKEN


    fun getMessage() = baseStatus?.message?.message ?: ""
    fun getCode() = baseStatus?.message?.code ?: 0
}
