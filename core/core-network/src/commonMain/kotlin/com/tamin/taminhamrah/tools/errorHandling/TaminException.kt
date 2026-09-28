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
    val navigateBack: Boolean = false,
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
    val navigateBack: Boolean = false,
) : Exception()

fun TaminApiException.toSingleLineMessage(): String {
    val separator = if (this.title.isEmpty() || this.subtitle.isNullOrEmpty()) "" else ", "

    return "${this.title.removeSuffix(".")}$separator${(this.subtitle ?: "")}"
}

fun Throwable.toSingleLineMessage() = this.asTaminApiException().toSingleLineMessage()

/**
 * A classified failure that was never parsed — thrown outside a data source's catch, e.g. while a
 * repository drains a download's [io.ktor.client.statement.HttpStatement] — is parsed here instead
 * of being discarded as "something went wrong".
 */
fun Throwable.asTaminApiException(): TaminApiException = when (this) {
    is TaminApiException -> this
    is TaminErrorUriException -> ErrorParserImpl().parseGeneralError(this)
    else -> TaminApiException(
        title = "مشکلی پیش آمده، لطفا بعدا سعی کنید",
        cause = TaminErrorUriException(ErrorUri.UNKNOWN),
    )
}

fun Throwable.getTaminErrorUri() = (this.asTaminApiException().cause as TaminErrorUriException).uri

/**
 * Which [ErrorUri] this failure was classified as, or null when it carries none.
 *
 * The null-safe counterpart of [getTaminErrorUri], which casts and therefore throws for anything
 * that is not a parsed API failure — a caller that only wants to tell a connection problem from a
 * server's answer must not have to risk that.
 */
fun Throwable.taminErrorUriOrNull(): ErrorUri? =
    (this as? TaminErrorUriException)?.uri
        ?: ((this as? TaminApiException)?.cause as? TaminErrorUriException)?.uri

fun Throwable.shouldNavigateBack(): Boolean = when (this) {
    is TaminApiException -> navigateBack
    is TaminErrorUriException -> navigateBack
    else -> (cause as? TaminApiException)?.navigateBack == true ||
        (cause as? TaminErrorUriException)?.navigateBack == true
}

fun ErrorUri.toApiException(parser: ErrorParser = ErrorParserImpl()): TaminApiException =
    parser.parseGeneralError(TaminErrorUriException(this))

fun ErrorUri.toSingleLineMessage(parser: ErrorParser = ErrorParserImpl()): String =
    this.toApiException(parser).toSingleLineMessage()
