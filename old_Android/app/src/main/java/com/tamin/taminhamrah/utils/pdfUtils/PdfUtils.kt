package com.tamin.taminhamrah.utils.pdfUtils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Date
import java.util.Locale

object PdfUtils {
//
//    @Throws(IOException::class)
//    fun openPDF(pdfView: PDFView, targetPdf: String) {
//        pdfView.fromFile(File(targetPdf))
//            .enableSwipe(false) // allows to block changing pages using swipe
//            .swipeHorizontal(false)
//            .enableDoubletap(true)
//            .defaultPage(0)
//            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//            .password(null)
//            .scrollHandle(null)
//            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//            .spacing(0)
//            .load()
//    }

    fun getPdfFile(context: Context, dir: File? = null, extension: String? = null): File? {
        return try {

            val ext = extension ?: ".pdf"
            val imageFileName = "PDF_${getTimestamp()}$ext"

            val storageDir = dir ?: getCameraDirectory(context)
            if (!storageDir.exists()) storageDir.mkdirs()
            val file = File(storageDir, imageFileName)
            file.createNewFile()

            file
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    private fun getTimestamp(): String {
        val timeFormat = "yyyyMMdd_HHmmssSSS"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date())
        } else {
            return "123465789"
        }
    }

    private fun getCameraDirectory(context: Context): File {
        val dir =
            context.getExternalFilesDir(Environment.DIRECTORY_DCIM) // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        return File(dir, "Camera")
    }

    fun getRealPath(context: Context, uri: Uri): String? {
        var path = getPathFromLocalUri(context, uri)
        if (path == null) {
            path = getPathFromRemoteUri(context, uri)
        }
        return path
    }

    private fun getPathFromLocalUri(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            when {
                isExternalStorageDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    return if ("primary".equals(type, ignoreCase = true)) {
                        if (split.size > 1) {
                            Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        } else {
                            Environment.getExternalStorageDirectory().toString() + "/"
                        }
                    } else {
                        val path = "storage" + "/" + docId.replace(":", "/")
                        if (File(path).exists()) {
                            path
                        } else {
                            "/storage/sdcard/" + split[1]
                        }
                    }
                }

                isDownloadsDocument(uri) -> {
                    var id = DocumentsContract.getDocumentId(uri)
                    if (id.contains(":")) {
                        id = id.split(":")[1]
                    }
                    if (id.isNotBlank()) {
                        return try {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                java.lang.Long.valueOf(id)
                            )
                            getDataColumn(context, contentUri, null, null)
                        } catch (e: NumberFormatException) {
                            Log.i("ImagePicker", e.message.toString())
                            null
                        }
                    }
                }
            }
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }
        return null
    }


    private fun getPathFromRemoteUri(context: Context, uri: Uri): String? {
        // The code below is why Java now has try-with-resources and the Files utility.
        var file: File? = null
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var success = false
        var extensions: String? = null
        try {
            val imagePath = uri.path
            if (imagePath != null && imagePath.lastIndexOf(".") != -1) {
                extensions = imagePath.substring(imagePath.lastIndexOf(".") + 1)
                extensions = ".$extensions"
            } else {
                val pathString = getRealPathFromURI(context, uri)
                extensions = pathString?.substring(pathString.lastIndexOf(".") + 1)
                extensions = ".$extensions"
                Log.e("pathString", "$pathString")
            }
            Log.e("extensions", "$extensions")
            if (extensions.equals(".pdf", true)) {
                inputStream = context.contentResolver.openInputStream(uri)
                file = getPdfFile(context, context.cacheDir, extensions)
                if (file == null) return null
                outputStream = FileOutputStream(file)
                if (inputStream != null) {
                    inputStream.copyTo(outputStream, bufferSize = 4 * 1024)
                    success = true
                }
            }
        } catch (ignored: IOException) {
        } finally {
            try {
                inputStream?.close()
            } catch (ignored: IOException) {
            }
            try {
                outputStream?.close()
            } catch (ignored: IOException) {
                // If closing the output stream fails, we cannot be sure that the
                // target file was written in full. Flushing the stream merely moves
                // the bytes into the OS, not necessarily to the file.
                success = false
            }
        }
        return if (success) file!!.path else null
    }
}

private fun getRealPathFromURI(context: Context?, contentUri: Uri?): String? {
    var splitUri = contentUri.toString().substring(contentUri.toString().lastIndexOf("/") + 1)
    val file = File(getFileName(context, contentUri)!!)
    if (file != null) {
        Log.e("filePath", "123:->>" + file.path)
        return file.path
    }
    return null
}

@SuppressLint("Range")
fun getFileName(context: Context?, uri: Uri?): String? {
    var result: String? = null
    if (uri!!.scheme == "content") {
        val cursor: Cursor = context!!.contentResolver.query(uri, null, null, null, null)!!
        try {
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor!!.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) {
            result = result!!.substring(cut + 1)
        }
    }
    Log.e("result", "" + result)
    return result
}



private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

private fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {

    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor =
            context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } catch (ex: Exception) {
    } finally {
        cursor?.close()
    }
    return null
}

