package com.tamin.taminhamrah.di.interceptor

import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.utils.NetworkUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class NetworkInterceptor constructor(val networkUtil: NetworkUtil) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = if (networkUtil.isConnectedToInternet()) {
            chain.request().newBuilder()
                .header("Cache-Control", "public, max-age=" + 60)
                .build()
        } else {
            chain.request().newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                .build()
        }
        if (BuildConfig.DEBUG) {
            Timber.tag("NetworkInterceptor").i("body:${request.body}\n",)
        }
        return chain.proceed(request)
    }
}
