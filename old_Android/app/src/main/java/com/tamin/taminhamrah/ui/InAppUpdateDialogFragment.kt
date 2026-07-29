package com.tamin.taminhamrah.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.downloader.Error // PRDownloader import
import com.downloader.OnDownloadListener // PRDownloader import
import com.downloader.PRDownloader // PRDownloader import
import com.downloader.Progress // PRDownloader import
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.databinding.FragmentInAppUpdateDialogBinding
import com.tamin.taminhamrah.ui.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class InAppUpdateDialogFragment () :
    BaseDialogFragment<FragmentInAppUpdateDialogBinding>(FragmentInAppUpdateDialogBinding::inflate) {

    lateinit var downloadLink: String
    private var downloadId: Int = 0 // Changed from ANRequest to Int for PRDownloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloadLink = arguments?.getString(Constants.DownloadLinkTag) ?: ""

        if (downloadLink.isBlank())
            dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val destinationDirectory =
            File("${Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)}/")
        Timber.tag("internalDirectory").i("root Directory: " + destinationDirectory)

        if (!destinationDirectory.exists()) {
            Timber.tag("internalDirectory").i("create Directory: ")
            destinationDirectory.mkdirs() // Use mkdirs to create parent directories if they don't exist
        } else {
            Timber.tag("internalDirectory").i(" Directory Exist ")
        }

        val updateFile = File(destinationDirectory, "update.apk")

        if (updateFile.exists()) {
            install_update("update.apk", destinationDirectory.absolutePath)
        } else {
            // No need to createNewFile, PRDownloader will handle file creation
            Timber.tag("internalDirectory").i("update.apk does not exist, starting download.")

            viewBinding.btnCancel.setOnClickListener {
                if (downloadId != 0) {
                    PRDownloader.cancel(downloadId)
                    Timber.tag("internalDirectory").i("Download cancelled with ID: $downloadId")
                }
                dismiss()
            }

            downloadFile(downloadLink, destinationDirectory.absolutePath, "update.apk")
        }
    }

    private fun downloadFile(url: String, dirPath: String, fileName: String) {
        val downloadingTag = "update_download"

        downloadId = PRDownloader.download(url, dirPath, fileName)
            .build()
            .setOnProgressListener { progress: Progress ->
                val downloadPercent: Float =
                    (progress.currentBytes.toFloat() / progress.totalBytes.toFloat()) * 100
                viewBinding.updateProgress.progress = downloadPercent.toInt()
                Timber.tag("internalDirectory")
                    .i("total=" + progress.totalBytes + "  download=" + progress.currentBytes + " percent: ${downloadPercent.toInt()}%")
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Timber.tag("internalDirectory").i("download complete")
                    install_update(fileName, dirPath)
                }

                override fun onError(error: Error?) {
                    Timber.tag("internalDirectory").i("download error: ${error?.serverErrorMessage} - ${error?.connectionException?.message}")
                    // Optionally, re-enable cancel button or show error message to user
                    // viewBinding.btnCancel.isEnabled = true // Example
                }
            })
        Timber.tag("internalDirectory").i("Download started with ID: $downloadId for URL: $url")
    }

    private fun install_update(apk_name: String, destination: String) {
        Timber.tag("checkUpdate").i("install : " + apk_name)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build()) // Consider if this is still needed or if FileProvider is a better approach for modern Android
        Timber.tag("GGGGGGGGGG").i(destination)
        val install = Intent(Intent.ACTION_VIEW)
        val fileToInstall = File(destination, apk_name)

        // For modern Android (Nougat+), FileProvider is generally preferred for sharing files.
        // However, your existing toUri() approach might work for public directories.
        // If you encounter issues with file access, consider using FileProvider.
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider", // Ensure this matches your FileProvider authority
                fileToInstall
            )
            install.setDataAndType(contentUri, APP_INSTALL_PATH)
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            install.setDataAndType(fileToInstall.toUri(), APP_INSTALL_PATH)
        }
        */
        install.setDataAndType(fileToInstall.toUri(), APP_INSTALL_PATH)
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // May need NEW_TASK if started from non-activity context, though DialogFragment should be fine.
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Good practice to grant URI permission

        try {
            requireActivity().startActivity(install)
        } catch (e: Exception) {
            Timber.tag("checkUpdate").e(e, "Error starting install intent")
            // Handle error, e.g., show a toast to the user
        }
        dismiss() // Dismiss dialog after attempting install
    }


    companion object {
        const val APP_INSTALL_PATH = "application/vnd.android.package-archive"
        @JvmStatic
        fun newInstance(downloadLink: String) =
            InAppUpdateDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.DownloadLinkTag, downloadLink)
                }
            }
    }
}