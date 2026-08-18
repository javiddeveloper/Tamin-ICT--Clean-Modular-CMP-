package com.tamin.taminhamrah.ui.util

expect class ExternalAppLauncher() {
    fun openEmail(email: String, cc: String? = null)
    fun openPhone(phone: String)
    fun openWhatsApp(phone: String)

    fun shareText(text: String)
}
