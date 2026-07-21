package com.tamin.taminhamrah.utils

import com.tamin.taminhamrah.enums.ApiStatusType


data class ResponseManager<out T>(val status: ApiStatusType, val data : T?, val message :String?) {

    companion object{
        fun <T>success(data : T?): ResponseManager<T> {
            return ResponseManager(ApiStatusType.SUCCESS,data,null)
        }

        fun <T>error(message:String,data:T?): ResponseManager<T> {
            return ResponseManager(ApiStatusType.ERROR,data,message)
        }

    }
}