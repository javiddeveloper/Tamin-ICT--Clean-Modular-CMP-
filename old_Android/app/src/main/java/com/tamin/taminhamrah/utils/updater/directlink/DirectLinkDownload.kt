package com.tamin.taminhamrah.utils.updater.directlink

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import androidx.fragment.app.FragmentManager
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Progress
import com.tamin.taminhamrah.utils.updater.dialog.UpdateInProgressDialog
import com.tamin.taminhamrah.utils.updater.utils.InstallAPKUtil
import com.tamin.taminhamrah.utils.updater.utils.PermissionUtils
import com.tamin.taminhamrah.utils.updater.utils.UnknownSourceInstallRequest
import timber.log.Timber
import java.io.File

/**
 * starts a download manager and downloads apk
 * also shows a loading indicator showing the apk is downloading
 * after download finishes , opens install page
 */
class DirectLinkDownload {

    private var downloadId: Int = 0
    var dialog: UpdateInProgressDialog? = null

    private val APK_NAME = "hamrahTamin${System.currentTimeMillis()}.apk"

    private fun installApk(context: Context) {

        dismissAlertDialog()

        val downloadedApkFile = File(getPath(context), APK_NAME)
        if (!downloadedApkFile.exists()) {
            Timber.tag(TAG).d("Downloaded file not found at: ${downloadedApkFile.absolutePath}")
        } else {
            InstallAPKUtil().installAPK(
                context,
                downloadedApkFile.absolutePath,
                Build.VERSION.SDK_INT
            )
        }
    }

    /**
     * Checks for needed permissions and tries to download the apk
     */
    fun getApk(url: String, context: Activity?, fm: FragmentManager) {
        checkNotNull(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            downloadApk(url, context, fm)
        } else {
            val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permissionChecker = PermissionUtils()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !context.packageManager.canRequestPackageInstalls()) {
                UnknownSourceInstallRequest().showRequest(context)
            }

            if (permissionChecker.isPermissionGranted(permission[0], context)) {
                downloadApk(url, context, fm)
            } else {
                permissionChecker.getPermission(context, permission)
            }
        }
    }

    private fun downloadApk(url: String, context: Context, fm: FragmentManager) {
        deleteExistingFiles(context)
        showDownloadDialog(context, fm)
        downloadApkFile(url, context)
    }

    private fun downloadApkFile(url: String, context: Context) {
        val dirPath = getPath(context)

        downloadId = PRDownloader.download(url, dirPath, APK_NAME)
            .build()
            .setOnProgressListener { progress: Progress ->
                val downloadPercent: Float =
                    (progress.currentBytes.toFloat() / progress.totalBytes.toFloat()) * 100
                updateDownloadDialog(downloadPercent.toInt())
                Timber.tag(TAG)
                    .e("total=" + progress.totalBytes + "  download=" + progress.currentBytes + " (${downloadPercent.toInt()}%)")
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Timber.tag(TAG).e("download complete for $APK_NAME")
                    installApk(context)
                }

                override fun onError(error: Error?) {
                    Timber.tag(TAG).e("download error: ${error?.serverErrorMessage} - ${error?.connectionException?.message}")
                    dialog?.isCancelable = true
                }
            })
        Timber.tag(TAG).i("PRDownloader download started with ID: $downloadId for $APK_NAME to $dirPath")
    }

    private fun deleteExistingFiles(context: Context) {
        val directory = File(getPath(context))
        if (!directory.exists()) {
            directory.mkdirs() // Use mkdirs to create parent directories if needed
            Timber.tag(TAG).d("Created directory: ${directory.absolutePath}")
        } else {
            Timber.tag(TAG).d("Directory exists: ${directory.absolutePath}")
            directory.listFiles()?.forEach { file ->
                if (file.isFile && file.name.startsWith("hamrahTamin") && file.name.endsWith(".apk")) {
                    if (file.delete()) {
                        Timber.tag(TAG).d("Deleted old APK: ${file.name}")
                    } else {
                        Timber.tag(TAG).w("Failed to delete old APK: ${file.name}")
                    }
                }
            }
        }
    }

    private fun showDownloadDialog(context: Context, fm: FragmentManager) {
        dialog = UpdateInProgressDialog.getInstance(object :
            UpdateInProgressDialog.CancelDownloadClickListener {
            override fun onCancelDownloadClick() {
                if (downloadId != 0) {
                    PRDownloader.cancel(downloadId)
                    Timber.tag(TAG).i("Download cancelled with ID: $downloadId")
                }
                dialog?.dismiss()
            }
        })
        dialog?.isCancelable = false // Prevent accidental dismissal during download
        dialog?.show(fm, UpdateInProgressDialog.javaClass.simpleName)
    }

    private fun updateDownloadDialog(progress: Int) {
        dialog?.updateProgress(progress)
    }

    private fun dismissAlertDialog() {
        dialog?.dismiss()
        dialog = null // Clean up dialog instance
    }

    private fun getPath(context: Context) =
        // Consistently use this path for downloads
        Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).absolutePath + "/UpdateTamin"

    companion object {
        private const val TAG = "DirectLinkDownload"
    }
}