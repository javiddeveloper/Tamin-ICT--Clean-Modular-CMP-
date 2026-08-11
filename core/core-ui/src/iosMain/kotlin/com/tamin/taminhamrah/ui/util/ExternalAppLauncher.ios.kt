package com.tamin.taminhamrah.ui.util

import platform.Foundation.NSCharacterSet
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPopoverPresentationController
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.popoverPresentationController

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

    actual fun shareText(text: String, title: String?) {
        val controller = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null,
        )
        // Presented from whatever is on top, so this works from a sheet or a dialog as well as the
        // page. On iPad the sheet is a popover and needs an anchor or UIKit raises: the root view
        // is the honest one here, since the button that triggered it is not reachable from common.
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        var presenter = root
        while (presenter.presentedViewController != null) {
            presenter = presenter.presentedViewController ?: break
        }
        controller.popoverPresentationController?.sourceView = presenter.view
        presenter.presentViewController(controller, animated = true, completion = null)
    }
}
