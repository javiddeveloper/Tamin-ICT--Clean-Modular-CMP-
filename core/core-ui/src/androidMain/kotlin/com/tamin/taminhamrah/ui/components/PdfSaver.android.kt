package com.tamin.taminhamrah.ui.components

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@Composable
actual fun rememberPdfSaver(): PdfSaver {
    val context = LocalContext.current
    return remember(context) { AndroidPdfSaver(context) }
}

private const val CHANNEL_ID = "pdf_downloads"
private const val MIME_PDF = "application/pdf"
private const val DOWNLOAD_SUBDIR = "TaminICT"

private class AndroidPdfSaver(private val context: Context) : PdfSaver {

    override suspend fun load(fileName: String): ByteArray? = withContext(Dispatchers.IO) {
        val uri = find(fileName) ?: return@withContext null
        try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: IOException) {
            // The MediaStore row outlived the file (deleted, or on an unmounted volume).
            null
        }
    }

    override suspend fun save(fileName: String, bytes: ByteArray) {
        if (bytes.isEmpty()) return
        // An earlier download is kept as it is: writing again would leave MediaStore holding
        // "name (1).pdf" beside the original.
        val existing = withContext(Dispatchers.IO) { find(fileName) }
        val uri = existing ?: withContext(Dispatchers.IO) { write(fileName, bytes) } ?: return
        notify(fileName, uri, isNew = existing == null)
    }

    private fun write(fileName: String, bytes: ByteArray): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveToDownloads(fileName, bytes)
        else saveToAppFiles(fileName, bytes)

    private fun find(fileName: String): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) findInDownloads(fileName)
        else File(appFilesDir(), fileName).takeIf { it.exists() }?.let(::uriForAppFile)

    /** API 29+: the public Downloads collection; the returned content URI opens without a FileProvider. */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloads(fileName: String, bytes: ByteArray): Uri? {
        val resolver = context.contentResolver
        val pending = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, MIME_PDF)
            put(MediaStore.Downloads.RELATIVE_PATH, downloadsRelativePath())
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = resolver.insert(downloadsCollection(), pending) ?: return null
        resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: return null
        resolver.update(uri, ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }, null, null)
        return uri
    }

    /** The same file from an earlier run, if the person has not deleted it since. */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun findInDownloads(fileName: String): Uri? {
        val collection = downloadsCollection()
        return context.contentResolver.query(
            collection,
            arrayOf(MediaStore.Downloads._ID),
            "${MediaStore.Downloads.DISPLAY_NAME}=? AND ${MediaStore.Downloads.RELATIVE_PATH} LIKE ?",
            arrayOf(fileName, "%$DOWNLOAD_SUBDIR%"),
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) ContentUris.withAppendedId(collection, cursor.getLong(0)) else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadsCollection(): Uri =
        MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    private fun downloadsRelativePath() = "${Environment.DIRECTORY_DOWNLOADS}/$DOWNLOAD_SUBDIR"

    /** Pre-29 fallback: app-specific external files (no storage permission), shared via FileProvider. */
    private fun saveToAppFiles(fileName: String, bytes: ByteArray): Uri {
        val file = File(appFilesDir(), fileName).apply { writeBytes(bytes) }
        return uriForAppFile(file)
    }

    private fun appFilesDir(): File =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), DOWNLOAD_SUBDIR)
            .apply { mkdirs() }

    private fun uriForAppFile(file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    @Suppress("MissingPermission")
    private fun notify(fileName: String, uri: Uri, isNew: Boolean) {
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
            .setContentText(if (isNew) DOWNLOAD_DONE_MESSAGE else ALREADY_DOWNLOADED_MESSAGE)
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
