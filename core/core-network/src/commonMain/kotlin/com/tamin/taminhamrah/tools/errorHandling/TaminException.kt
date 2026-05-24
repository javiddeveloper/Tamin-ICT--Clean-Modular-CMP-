/*
*
* @author: Javid Sattar 
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.core.network.tools.errorHandling

data class TaminApiException(
    val title: String,
    val subtitle: String? = null,
    override val cause: Throwable? = null
) : Exception()

data class TaminErrorUriException(
    val uri: ErrorUri,
) : Exception()

fun TaminApiException.toSingleLineMessage(): String {
    val separator = if (this.title.isEmpty() || this.subtitle.isNullOrEmpty()) "" else ", "

    return "${this.title.removeSuffix(".")}$separator${(this.subtitle ?: "")}"
}

fun Throwable.toSingleLineMessage() = this.asTaminApiException().toSingleLineMessage()
fun Throwable.asTaminApiException() = try {
    this as TaminApiException
} catch (t: Throwable) {
    TaminApiException(title = "مشکلی پیش آمده، لطفا بعدا سعی کنید", cause = TaminErrorUriException(ErrorUri.UNKNOWN))
}

fun Throwable.getTaminApiExceptionTitle() = this.asTaminApiException().title
fun Throwable.getTaminApiExceptionSubtitle() = this.asTaminApiException().subtitle
fun Throwable.getTaminErrorUri() = (this.asTaminApiException().cause as TaminErrorUriException).uri
