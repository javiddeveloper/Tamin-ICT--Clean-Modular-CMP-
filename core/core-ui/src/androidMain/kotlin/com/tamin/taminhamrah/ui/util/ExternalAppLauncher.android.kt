package com.tamin.taminhamrah.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.text.HtmlCompat
import org.koin.core.context.GlobalContext
import androidx.core.net.toUri

actual class ExternalAppLauncher actual constructor() {

    private val context: Context
        get() = GlobalContext.get().get<Context>()

    actual fun openEmail(email: String, cc: String?) {
        val uriString = buildString {
            append("mailto:")
            if (email.isNotBlank()) {
                append(Uri.encode(email))
            }
            val params = mutableListOf<String>()
            if (!cc.isNullOrBlank()) {
                params += "cc=${Uri.encode(cc)}"
            }
            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }

        val intent = Intent(Intent.ACTION_SENDTO, uriString.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    actual fun openPhone(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    actual fun openWhatsApp(phone: String) {
        val phoneNumber = phone.filter { it.isDigit() }
        openUrl("https://wa.me/$phoneNumber")
    }

    actual fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        // A device with no browser throws ActivityNotFoundException; opening a web page is never
        // the point of the screen that offers it, so it fails quietly rather than taking the app.
        runCatching { context.startActivity(intent) }
    }

    actual fun shareText(text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooserTitle = HtmlCompat.fromHtml("به اشتراک گذاری", HtmlCompat.FROM_HTML_MODE_LEGACY)
        val shareIntent = Intent.createChooser(sendIntent, chooserTitle)
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        context.startActivity(shareIntent)
    }
}
