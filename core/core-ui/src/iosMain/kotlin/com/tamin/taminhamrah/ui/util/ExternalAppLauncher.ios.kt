package com.tamin.taminhamrah.ui.util

import platform.Foundation.NSCharacterSet
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.UIKit.UIApplication
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters

actual class ExternalAppLauncher actual constructor() {

    private fun String.percentEncoded(): String? =
        NSString.create(string = this).stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLQueryAllowedCharacterSet)

    actual fun openEmail(email: String, cc: String?) {
        val to = email.takeIf { it.isNotBlank() } ?: return
        val ccQuery = cc?.let { "&cc=${it.percentEncoded()}" } ?: ""
        val urlString = "mailto:${to.percentEncoded()}?$ccQuery"

        val url = NSURL.URLWithString(urlString) ?: return
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    actual fun openPhone(phone: String) {
        val url = NSURL.URLWithString("tel:$phone") ?: return
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    actual fun openWhatsApp(phone: String) {
        val phoneNumber = phone.filter { it.isDigit() }
        val urlString = "https://wa.me/$phoneNumber"
        val url = NSURL.URLWithString(urlString) ?: return

        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}
