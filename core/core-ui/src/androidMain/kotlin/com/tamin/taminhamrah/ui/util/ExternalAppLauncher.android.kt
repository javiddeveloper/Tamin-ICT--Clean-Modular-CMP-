package com.tamin.taminhamrah.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.koin.core.context.GlobalContext

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

        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    actual fun openPhone(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    actual fun openWhatsApp(phone: String) {
        val phoneNumber = phone.filter { it.isDigit() }
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://wa.me/$phoneNumber")
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
