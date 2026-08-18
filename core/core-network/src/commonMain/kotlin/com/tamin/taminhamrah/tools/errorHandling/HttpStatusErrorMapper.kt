package com.tamin.taminhamrah.tools.errorHandling

import com.tamin.taminhamrah.tools.looksLikeArabicScript

internal data class MappedHttpError(
    val uri: ErrorUri,
    val userMessage: String,
    val navigateBack: Boolean = false
)

/**
 * User-facing copy ported from old_android `BaseRemoteDataSource.handleError`.
 * HTML `<br/>` from the legacy strings is a newline so Compose can render it.
 */
internal object HttpErrorCopy {
    const val FORBIDDEN_VPN =
        "دسترسی به سرویس مورد نظر مقدور نمی‌باشد؛ در صورت استفاده از هرگونه VPN یا پروکسی، لطفا آن را غیرفعال و مجدد تلاش نمائید."

    const val NOT_FOUND = "در این لحظه اطلاعات مورد نظر یافت نشد"

    const val LOGIN_AGAIN =
        "ورود شما با خطا مواجه شده است. لطفا دوباره با کلیک دکمه، وارد حساب کاربری خود شوید."

    const val BANK_ACCOUNT =
        "شما فاقد شماره حساب بانکی می باشید. لطفا نسبت به ثبت شماره حساب بانکی جدید (منوی حساب کاربری) اقدام نمایید"

    const val SABTE_AHVAL =
        "ارتباط بر خط با سازمان ثبت احوال قطع می باشد، لطفا دقایقی دیگر مجددا تلاش نمایید."

    const val OPEN_REQUEST =
        "شما دارای درخواست در حال بررسی می باشید و امکان ثبت درخواست جدید وجود ندارد"

    const val SURVIVOR_ALREADY_SAVED = "درخواست برای بازمانده مورد نظر قبلا ثبت گردیده است."

    const val DISABILITY_COMMISSION_NOT_READY =
        "نتیجه رای کمیسیون پزشکی هنوز صادر نشده است و امکان ثبت درخواست نمی باشد."

    const val DISABILITY_SAVED_BEFORE =
        "درخواست قبلا ذخیره شده است.در صورتیکه اطلاعات هنوز به شعبه ارسال نشده باشد امکان مشاهده و ادامه مراحل از طریق منوی \"کارتابل/درخواست‌های من/عملیات/مشاهده\" وجود دارد."

    const val DISABILITY_HISTORY_NOT_FOUND = "اطلاعات سابقه یافت نشد امکان ادامه وجود ندارد."

    const val DISABILITY_COMMISSION_NOT_POSSIBLE =
        "شما دارای حکم مستمری فعال می باشید و ثبت درخواست از کارافتادگی مقدور نیست."

    const val BAD_GATEWAY =
        "کاربر گرامی با عرض پوزش اختلالی در سیستم رخ داده است ، لطفاً دقایقی دیگر دوباره تلاش فرمایید.."

    const val SERVICE_UNAVAILABLE =
        "در حال حاضر امکان برقراری ارتباط با سرویس دهنده مرکزی وجود ندارد. پس از چند دقیقه، مجددا تلاش نمائید."

    const val GATEWAY_TIMEOUT =
        "سرویس دهنده مرکزی در حال بروزرسانی می‌باشد.  پس از چند دقیقه، مجددا تلاش نمائید."

    const val SYSTEM_UPDATING =
        "کاربر گرامی سیستم در حال بروزرسانی می باشد ، لطفاً در روزهای آتی مجدداً مراجعه فرمایید."

    const val GENERIC_SERVER =
        "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست.\n" +
            "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:\n" +
            "_امکان اتصال به اینترنت وجود ندارد.\n" +
            "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است.\n" +
            "_اطلاعات ثبت شده برای شما دارای اشکال است."

    const val INVALID_REQUEST = "اطلاعات ارسالی صحیح نیست"
}

/**
 * Maps an HTTP status plus the raw backend payload to a localized user message.
 * 401 is classified only — token refresh stays in the Ktor Auth plugin.
 */
internal object HttpStatusErrorMapper {

    fun map(status: Int, rawMessage: String?, cause: String?): MappedHttpError {
        val raw = sanitizeRaw(rawMessage)
        return when (status) {
            401 -> MappedHttpError(
                uri = ErrorUri.INVALID_AUTH,
                userMessage = arabicOr(raw, "لطفا دوباره وارد شوید")
            )
            403 -> MappedHttpError(
                uri = ErrorUri.FORBIDDEN,
                userMessage = HttpErrorCopy.FORBIDDEN_VPN,
                navigateBack = true
            )
            404 -> MappedHttpError(
                uri = ErrorUri.RESOURCE_NOT_FOUND,
                userMessage = HttpErrorCopy.NOT_FOUND
            )
            400 -> MappedHttpError(
                uri = ErrorUri.INVALID_REQUEST,
                userMessage = mapClientError(raw)
            )
            422 -> MappedHttpError(
                uri = ErrorUri.INVALID_REQUEST,
                userMessage = mapUnprocessable(raw)
            )
            408 -> MappedHttpError(
                uri = ErrorUri.SERVICE_TIMEOUT,
                userMessage = "زمان درخواست به پایان رسید. لطفاً مجدداً تلاش کنید."
            )
            500 -> MappedHttpError(
                uri = ErrorUri.INTERNAL_ERROR,
                userMessage = mapInternalServerError(raw, cause)
            )
            502 -> MappedHttpError(
                uri = ErrorUri.SERVER_SERVICE_UNAVAILABLE,
                userMessage = HttpErrorCopy.BAD_GATEWAY
            )
            503 -> MappedHttpError(
                uri = ErrorUri.SERVICE_UNAVAILABLE,
                userMessage = mapServiceUnavailable(raw)
            )
            504 -> MappedHttpError(
                uri = ErrorUri.SERVICE_TIMEOUT,
                userMessage = arabicOr(raw, HttpErrorCopy.GATEWAY_TIMEOUT)
            )
            in 400..499 -> MappedHttpError(
                uri = ErrorUri.INVALID_REQUEST,
                userMessage = arabicOr(raw, HttpErrorCopy.INVALID_REQUEST)
            )
            in 500..599 -> MappedHttpError(
                uri = ErrorUri.INTERNAL_ERROR,
                userMessage = arabicOr(raw, HttpErrorCopy.GENERIC_SERVER)
            )
            else -> MappedHttpError(
                uri = ErrorUri.UNKNOWN,
                userMessage = arabicOr(raw, HttpErrorCopy.GENERIC_SERVER)
            )
        }
    }

    private fun mapClientError(raw: String?): String {
        val message = raw.orEmpty()
        return when {
            message.contains("pension.disability.commission.notready.exception") ->
                HttpErrorCopy.DISABILITY_COMMISSION_NOT_READY
            message.contains("pension.disability.saved.before.exception") ->
                HttpErrorCopy.DISABILITY_SAVED_BEFORE
            message.contains("pension.disability.history.not.found.exception") ->
                HttpErrorCopy.DISABILITY_HISTORY_NOT_FOUND
            message.contains("pension.disability.commission.not.possible.exception") ->
                HttpErrorCopy.DISABILITY_COMMISSION_NOT_POSSIBLE
            isInvalidGrant(message) -> HttpErrorCopy.LOGIN_AGAIN
            else -> arabicOr(raw, HttpErrorCopy.INVALID_REQUEST)
        }
    }

    private fun mapUnprocessable(raw: String?): String {
        val message = raw.orEmpty()
        return when {
            isInvalidGrant(message) -> HttpErrorCopy.LOGIN_AGAIN
            else -> arabicOr(raw, HttpErrorCopy.INVALID_REQUEST)
        }
    }

    private fun mapInternalServerError(raw: String?, cause: String?): String {
        val message = raw.orEmpty()
        return when {
            message.contains("شما فاقد شماره حساب بانکی می باشید") -> HttpErrorCopy.BANK_ACCOUNT
            cause == "ProxyProcessingException" -> arabicOr(raw, HttpErrorCopy.GENERIC_SERVER)
            isInternalServerErrorLiteral(message) -> HttpErrorCopy.GENERIC_SERVER
            message.contains("sso.to.sa.connection.exception") -> HttpErrorCopy.SABTE_AHVAL
            message.contains("pension.request.current.request.open") -> HttpErrorCopy.OPEN_REQUEST
            message.contains("pension.survivor.request.survivor.already.saved") ->
                HttpErrorCopy.SURVIVOR_ALREADY_SAVED
            else -> arabicOr(raw, HttpErrorCopy.GENERIC_SERVER)
        }
    }

    private fun mapServiceUnavailable(raw: String?): String {
        val message = raw.orEmpty()
        return when {
            message.contains("سازمان تامین اجتماعی") ||
                message.contains("(&#1593;&#1583;&#1605; &#1583;&#1587;&#1578;&#1585;&#1587;&#1740;)") ->
                HttpErrorCopy.SYSTEM_UPDATING
            else -> arabicOr(raw, HttpErrorCopy.SERVICE_UNAVAILABLE)
        }
    }

    private fun isInvalidGrant(message: String): Boolean =
        message.contains("Failed to validate code challenge claim.") ||
            message.contains("invalid_grant")

    private fun isInternalServerErrorLiteral(message: String): Boolean =
        message.contains("INTERNAL_SERVER_ERROR") ||
            message.contains("Internal Server Error", ignoreCase = true)

    private fun arabicOr(raw: String?, fallback: String): String =
        raw?.takeIf { it.looksLikeArabicScript() } ?: fallback

    private fun sanitizeRaw(rawMessage: String?): String? {
        val trimmed = rawMessage?.trim().orEmpty()
        return trimmed.takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
    }
}
