package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.posix.memcpy
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
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
    // Ask for local-notification permission when the viewer opens; the file saves regardless.
    LaunchedEffect(Unit) {
        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound,
            ) { _, _ -> }
    }
    return remember { IosPdfSaver() }
}

private const val DOWNLOAD_SUBDIR = "TaminICT"

private class IosPdfSaver : PdfSaver {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun load(fileName: String): ByteArray? = withContext(Dispatchers.Default) {
        val path = filePath(fileName) ?: return@withContext null
        val data = NSData.dataWithContentsOfFile(path) ?: return@withContext null
        val size = data.length.toInt()
        if (size == 0) return@withContext null
        ByteArray(size).also { bytes ->
            bytes.usePinned { memcpy(it.addressOf(0), data.bytes, data.length) }
        }
    }

    override suspend fun save(fileName: String, bytes: ByteArray): String? {
        if (bytes.isEmpty()) return null
        val message = withContext(Dispatchers.Default) {
            val path = filePath(fileName) ?: return@withContext null
            when {
                // An earlier download is kept as it is rather than written over.
                NSFileManager.defaultManager.fileExistsAtPath(path) -> ALREADY_DOWNLOADED_MESSAGE
                write(path, bytes) -> DOWNLOAD_DONE_MESSAGE
                else -> null
            }
        }
        notify(fileName, message ?: return null)
        return message
    }

    /** Where the PDF lives: a TaminICT directory under Downloads, or Documents if there is none. */
    @OptIn(ExperimentalForeignApi::class)
    private fun filePath(fileName: String): String? {
        val baseDir = (
            NSSearchPathForDirectoriesInDomains(NSDownloadsDirectory, NSUserDomainMask, true).firstOrNull()
                ?: NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).firstOrNull()
            ) as? String ?: return null
        val taminDir = "$baseDir/$DOWNLOAD_SUBDIR"
        NSFileManager.defaultManager.createDirectoryAtPath(
            path = taminDir,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
        return "$taminDir/$fileName"
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun write(path: String, bytes: ByteArray): Boolean {
        val data = bytes.usePinned {
            NSData.create(bytes = it.addressOf(0), length = bytes.size.convert())
        }
        return data.writeToFile(path, atomically = true)
    }

    private fun notify(fileName: String, message: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle(fileName)
            setBody(message)
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
