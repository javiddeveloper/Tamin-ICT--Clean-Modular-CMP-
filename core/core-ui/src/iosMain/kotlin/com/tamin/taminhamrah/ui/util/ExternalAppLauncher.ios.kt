package com.tamin.taminhamrah.ui.util

import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

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
        openUrl("https://wa.me/$phoneNumber")
    }

    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        if (UIApplication.sharedApplication.canOpenURL(nsUrl)) {
            UIApplication.sharedApplication.openURL(nsUrl)
        }
    }

    actual fun shareText(text: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
}
