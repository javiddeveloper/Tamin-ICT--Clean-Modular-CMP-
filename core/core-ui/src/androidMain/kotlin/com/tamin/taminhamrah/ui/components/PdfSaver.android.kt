package com.tamin.taminhamrah.ui.components

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
actual fun rememberPdfSaver(): PdfSaver {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember(context, scope) { AndroidPdfSaver(context, scope) }
}

private const val CHANNEL_ID = "pdf_downloads"
private const val MIME_PDF = "application/pdf"

private class AndroidPdfSaver(
    private val context: Context,
    private val scope: CoroutineScope,
) : PdfSaver {

    override fun save(fileName: String, bytes: ByteArray) {
        if (bytes.isEmpty()) return
        scope.launch {
            val uri = withContext(Dispatchers.IO) { write(fileName, bytes) } ?: return@launch
            notify(fileName, uri)
        }
    }

    private fun write(fileName: String, bytes: ByteArray): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveToDownloads(fileName, bytes)
        else saveToAppFiles(fileName, bytes)

    /** API 29+: the public Downloads collection; the returned content URI opens without a FileProvider. */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloads(fileName: String, bytes: ByteArray): Uri? {
        val resolver = context.contentResolver
        val pending = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, MIME_PDF)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, pending) ?: return null
        resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: return null
        resolver.update(uri, ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }, null, null)
        return uri
    }

    /** Pre-29 fallback: app-specific external files (no storage permission), shared via FileProvider. */
    private fun saveToAppFiles(fileName: String, bytes: ByteArray): Uri {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "").apply { mkdirs() }
        val file = File(dir, fileName).apply { writeBytes(bytes) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    @Suppress("MissingPermission")
    private fun notify(fileName: String, uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "دانلودها", NotificationManager.IMPORTANCE_DEFAULT),
            )
        }
        val open = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, MIME_PDF)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            fileName.hashCode(),
            open,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(fileName)
            .setContentText("دانلود انجام شد")
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(fileName.hashCode(), notification)
        } catch (_: SecurityException) {
            // Silently ignore if permission was revoked or missing
        }
    }
}
