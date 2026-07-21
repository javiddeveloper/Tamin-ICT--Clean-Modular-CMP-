package com.tamin.taminhamrah.data.remote.models

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: MessageModel?,
    var isBackToPrevious: Boolean = false

) {


    val isSuccess: Boolean
        get() = status == Status.SUCCESS

    val isError: Boolean
        get() = status == Status.ERROR

    val isLoading: Boolean
        get() = status == Status.LOADING
    val isNeedNetwork: Boolean
        get() = status == Status.NEED_NETWORK

    val needRefreshToken: Boolean
        get() = status == Status.NEED_REFRESH_TOKEN

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        NEED_NETWORK,
        NEED_REFRESH_TOKEN
    }

    companion object {

        fun <T> success(data: T?): Resource<T?> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(message: MessageModel? = null): Resource<T> {

            return Resource(Status.ERROR, null, message)
        }

        fun <T> needNetwork(): Resource<T> {
            return Resource(Status.NEED_NETWORK, null, null)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }


        fun <T> needRefreshToken(message: MessageModel? = null): Resource<T> {
            return Resource(Status.NEED_REFRESH_TOKEN, null, message)
        }
    }


}

data class MessageModel(var message: String = "", var code: Int = 0)