package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSDownloadsDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

@Composable
actual fun rememberPdfSaver(): PdfSaver {
    val scope = rememberCoroutineScope()
    // Ask for local-notification permission when the viewer opens; the file saves regardless.
    LaunchedEffect(Unit) {
        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound,
            ) { _, _ -> }
    }
    return remember(scope) { IosPdfSaver(scope) }
}

private class IosPdfSaver(private val scope: CoroutineScope) : PdfSaver {

    override fun save(fileName: String, bytes: ByteArray) {
        if (bytes.isEmpty()) return
        scope.launch {
            if (saveToDocuments(fileName, bytes)) notify(fileName)
        }
    }

    /** Writes the PDF into a TaminICT directory in Downloads or Documents directory. */
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun saveToDocuments(fileName: String, bytes: ByteArray): Boolean {
        val baseDir = (NSSearchPathForDirectoriesInDomains(NSDownloadsDirectory, NSUserDomainMask, true).firstOrNull()
            ?: NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).firstOrNull()) as? String
            ?: return false
        val taminDir = "$baseDir/TaminICT"
        val fileManager = NSFileManager.defaultManager
        fileManager.createDirectoryAtPath(
            path = taminDir,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
        val data = bytes.usePinned {
            NSData.create(bytes = it.addressOf(0), length = bytes.size.convert())
        }
        return data.writeToFile("$taminDir/$fileName", atomically = true)
    }

    private fun notify(fileName: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle(fileName)
            setBody("دانلود انجام شد")
        }
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 1.0,
            repeats = false,
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = fileName,
            content = content,
            trigger = trigger,
        )
        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request, withCompletionHandler = null)
    }
}
