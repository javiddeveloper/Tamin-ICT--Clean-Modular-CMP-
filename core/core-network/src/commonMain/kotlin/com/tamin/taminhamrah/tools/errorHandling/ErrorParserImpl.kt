/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.tools.errorHandling

internal class ErrorParserImpl : ErrorParser {
    override fun parseGeneralError(exception: TaminErrorUriException): TaminApiException {
        // The backend sent its own (already localized) message via the
        // hasError/problems envelope (see BaseDTO.problemMessage) - prefer
        // that verbatim over the generic per-ErrorUri copy below, since it's
        // usually specific and actionable (e.g. field validation errors).
        exception.serverMessage?.takeIf { it.isNotBlank() }?.let { serverMessage ->
            return TaminApiException(
                title = "خطا",
                subtitle = serverMessage,
                cause = exception,
                navigateBack = exception.navigateBack
            )
        }

        val (title, subtitle) = when (exception.uri) {
            ErrorUri.INVALID_AUTH -> "خطای احراز هویت" to "لطفا دوباره وارد شوید"
            ErrorUri.FORBIDDEN -> "خطا" to HttpErrorCopy.FORBIDDEN_VPN
            ErrorUri.RESOURCE_NOT_FOUND -> "یافت نشد" to "اطلاعات درخواستی یافت نشد"
            ErrorUri.SERVICE_TIMEOUT -> "اتمام زمان" to "زمان درخواست به پایان رسید"
            ErrorUri.INTERNAL_ERROR -> "خطای سرور" to "لطفا بعدا تلاش کنید"
            ErrorUri.NO_CONNECTION_ERROR -> "خطای اتصال" to "لطفا اتصال اینترنت خود را بررسی کنید"
            ErrorUri.REQUESTS_LIMIT -> "درخواست زیاد" to "لطفا کمی صبر کنید"
            ErrorUri.INVALID_REQUEST -> "درخواست نامعتبر" to "اطلاعات ارسالی صحیح نیست"
            ErrorUri.INVALID_PARAMETER -> "پارامتر نامعتبر" to "لطفا اطلاعات را بررسی کنید"
            ErrorUri.ERROR_LOAD_PRESCRIPTION_LIST -> "خطا در دریافت سوابق درمانی" to "لطفا بعدا تلاش کنید"
            ErrorUri.ERROR_LOAD_PRESCRIPTION_DETAIL -> "خطا در دریافت جزئیات نسخه" to "اطلاعات نسخه قابل دریافت نیست"
            ErrorUri.ERROR_RECEIVE_PRESCRIPTION_FILE -> "خطا در دریافت فایل" to "لطفا بعدا تلاش کنید"
            ErrorUri.ERROR_LOAD_DESERVED_TREATMENT -> "خطا در دریافت وضعیت استحقاق درمان" to "لطفا بعدا تلاش کنید"
            ErrorUri.ERROR_LOAD_DEPENDANTS -> "خطا در دریافت لیست همراهان زیر ۱۸ سال" to "لطفا بعدا تلاش کنید"
            ErrorUri.ERROR_LOAD_HEALTH_PROFILE -> "خطا در دریافت پرونده سلامت" to "لطفا بعدا تلاش کنید"
            ErrorUri.FEATURE_UNAVAILABLE -> "عدم دسترسی به سرویس" to "این سرویس در حال حاضر در دسترس نیست."
            ErrorUri.FEATURE_TEMPORARILY_UNAVAILABLE -> "عدم دسترسی موقت" to "این سرویس موقتاً در دسترس نیست."
            ErrorUri.SERVER_PROBLEM -> "خطا" to "اطلاعات ارسالی صحیح نیست"
            else -> "خطا" to "مشکلی پیش آمده است"
        }

        return TaminApiException(
            title = title,
            subtitle = subtitle,
            cause = exception,
            navigateBack = exception.navigateBack
        )
    }
}
