/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.tools.errorHandling

internal class ErrorParserImpl : ErrorParser {
    override fun parseGeneralError(exception: TaminErrorUriException): TaminApiException {
        val (title, subtitle) = when (exception.uri) {
            ErrorUri.INVALID_AUTH -> "خطای احراز هویت" to "لطفا دوباره وارد شوید"
            ErrorUri.FORBIDDEN -> "دسترسی غیرمجاز" to "شما اجازه دسترسی به این بخش را ندارید"
            ErrorUri.RESOURCE_NOT_FOUND -> "یافت نشد" to "اطلاعات درخواستی یافت نشد"
            ErrorUri.SERVICE_TIMEOUT -> "اتمام زمان" to "زمان درخواست به پایان رسید"
            ErrorUri.INTERNAL_ERROR -> "خطای سرور" to "لطفا بعدا تلاش کنید"
            ErrorUri.NO_CONNECTION_ERROR -> "خطای اتصال" to "لطفا اتصال اینترنت خود را بررسی کنید"
            ErrorUri.REQUESTS_LIMIT -> "درخواست زیاد" to "لطفا کمی صبر کنید"
            ErrorUri.INVALID_REQUEST -> "درخواست نامعتبر" to "اطلاعات ارسالی صحیح نیست"
            ErrorUri.INVALID_PARAMETER -> "پارامتر نامعتبر" to "لطفا اطلاعات را بررسی کنید"
            else -> "خطا" to "مشکلی پیش آمده است"
        }

        return TaminApiException(
            title = title,
            subtitle = subtitle,
            cause = exception
        )
    }
}
