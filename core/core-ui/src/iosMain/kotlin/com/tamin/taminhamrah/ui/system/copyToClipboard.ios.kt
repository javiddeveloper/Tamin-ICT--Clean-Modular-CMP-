package com.tamin.taminhamrah.ui.system

import platform.Foundation.NSTimer
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text

    val alert = UIAlertController.alertControllerWithTitle(
        title = null,
        message = "کپی شد",
        preferredStyle = UIAlertControllerStyleAlert
    )
    alert.addAction(UIAlertAction.actionWithTitle("OK", style = 0, handler = null))

    val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootController?.presentViewController(alert, animated = true, completion = null)

    NSTimer.scheduledTimerWithTimeInterval(1.0, repeats = false) {
        alert.dismissViewControllerAnimated(true, completion = null)
    }
}
