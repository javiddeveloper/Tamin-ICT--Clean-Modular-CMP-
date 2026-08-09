/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.tools.errorHandling

data class TaminApiException(
    val title: String,
    val subtitle: String? = null,
    override val cause: Throwable? = null,
) : Exception(title) {
    override val message: String?
        get() = toSingleLineMessage()
}

data class TaminErrorUriException(
    val uri: ErrorUri,
    // Present when the backend sent its own (already localized) message via
    // the `hasError`/`problems` envelope, e.g. BaseDTO.problemMessage.
    // When set, ErrorParser prefers this over the generic per-ErrorUri copy.
    val serverMessage: String? = null,
    val errorCode: Int? = null,
) : Exception()

fun TaminApiException.toSingleLineMessage(): String {
    val separator = if (this.title.isEmpty() || this.subtitle.isNullOrEmpty()) "" else ", "

    return "${this.title.removeSuffix(".")}$separator${(this.subtitle ?: "")}"
}

fun Throwable.toSingleLineMessage() = this.asTaminApiException().toSingleLineMessage()
fun Throwable.asTaminApiException() = try {
    this as TaminApiException
} catch (t: Throwable) {
    TaminApiException(title = "مشکلی پیش آمده، لطفا بعدا سعی کنید", cause = TaminErrorUriException(
        ErrorUri.UNKNOWN))
}

fun Throwable.getTaminApiExceptionTitle() = this.asTaminApiException().title
fun Throwable.getTaminApiExceptionSubtitle() = this.asTaminApiException().subtitle
fun Throwable.getTaminErrorUri() = (this.asTaminApiException().cause as TaminErrorUriException).uri
fun Throwable.getServerErrorCode() = (this.asTaminApiException().cause as? TaminErrorUriException)?.errorCode

fun ErrorUri.toApiException(parser: ErrorParser = ErrorParserImpl()): TaminApiException =
    parser.parseGeneralError(TaminErrorUriException(this))

fun ErrorUri.toSingleLineMessage(parser: ErrorParser = ErrorParserImpl()): String =
    this.toApiException(parser).toSingleLineMessage()
