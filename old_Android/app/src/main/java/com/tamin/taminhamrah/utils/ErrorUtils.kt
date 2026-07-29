package com.tamin.taminhamrah.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtils {

    fun getFriendlyErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "عدم دسترسی به اینترنت. لطفاً اتصال خود را بررسی کنید."
            is SocketTimeoutException -> "زمان درخواست به پایان رسید. لطفاً مجدداً تلاش کنید."
            is IOException -> "خطا در برقراری ارتباط با سرور."
            is HttpException -> {
                when (throwable.code()) {
                    400 -> "درخواست نامعتبر است."
                    401 -> "نشست کاربری منقضی شده است. لطفاً مجدداً وارد شوید."
                    403 -> "دسترسی غیرمجاز."
                    404 -> "سرویس مورد نظر یافت نشد."
                    500 -> "خطای داخلی سرور. لطفاً بعداً تلاش کنید."
                    502 -> "سرور در حال بروزرسانی است."
                    503 -> "سرویس موقتاً در دسترس نیست."
                    else -> "خطای شبکه: ${throwable.code()}"
                }
            }
            else -> throwable.message ?: "خطای نامشخص رخ داده است."
        }
    }
}
