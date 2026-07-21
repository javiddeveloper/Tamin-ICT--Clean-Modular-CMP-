package com.tamin.taminhamrah.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat.getColor
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.ActivityPdfViewerBinding
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.base.ContainerBaseActivity
import com.tamin.taminhamrah.ui.dialog.PermissionMessageDialog
import com.tamin.taminhamrah.ui.dialog.StoragePermissionGetImageTextProvider
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.PickImageUtils
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class PdfViewerActivity : ContainerBaseActivity() {

    private val minZoom = .2f
    private val maxZoom = 4f
    private val zoomLevel = .2f
    private var currentTargetPdf: String = ""
    private var toolbarTitle: String = ""

    companion object {
        const val ARG_TITLE = "TOOLBAR_TITLE"
        const val ARG_PDF_FILE_PATH = "ARG_PDF_FILE_PATH"
        const val ARG_PDF_FILE_ASSET = "ARG_PDF_FILE_ASSET"
    }

    val baseViewModel: BaseViewModel by viewModels()

    lateinit var binding: ActivityPdfViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        val title = Utility.getToolbarTitle(intent?.extras)

        try {
            val targetPdf = intent?.extras?.getString(ARG_PDF_FILE_PATH)
            targetPdf?.let { setupToolbar(title, it) }

            targetPdf?.let { initPdfViewerFromUri(it) }

            if (targetPdf == null) {
                val assetPdf = intent?.extras?.getString(ARG_PDF_FILE_ASSET)
                assetPdf?.let { initPdfViewerFromFile(createFileFromAssets(it)) }
                assetPdf?.let { setupToolbar(title, it) }
                binding.apply {
                    btnDownload.visibility = View.GONE
                    btnShare.visibility = View.GONE
                }
            }
            onClick()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Something Wrong: $e",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun createFileFromAssets(path: String): File {
       val fileName =  path.split("/").last()
        val file = File(cacheDir, fileName)
        if (file.exists()) {
            return file
        } else {
            return file.apply {
                outputStream().use { cache ->
                    assets.open(path).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }
    }


    private fun initPdfViewerFromUri(path: String) {
        initPdfViewerFromFile(File(path))
    }

    private fun initPdfViewerFromFile(file: File) {
        binding.pdfView.initWithFile(file)
    }

    private fun onClick() {
        binding.apply {
            btnDownload.setOnClickListener {
                if (PickImageUtils.hasPermissionsOfList(
                        this@PdfViewerActivity,
                        getStoragePermissions()
                    )
                ) {
                    downloadFile(currentTargetPdf, toolbarTitle)
                } else {
                    permissionLauncher.launch(getStoragePermissions())
                }
            }

            btnShare.setOnClickListener {
                Utility.sharePdfFile(this@PdfViewerActivity, currentTargetPdf)
            }

//            btnZoomOut.setOnClickListener {
//
//              setNewZoom(btnZoomOut, btnZoomIn, pdfView, true)
//            }
//
//            btnZoomIn.setOnClickListener {
//                setNewZoom(btnZoomIn, btnZoomOut, pdfView, false)
//            }
        }
    }

    private fun getStoragePermissions(): Array<String> {
       return if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
           arrayOf(READ_MEDIA_IMAGES)
       } else {
           arrayOf(
               WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE
           )
       }
   }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            try {
                val permissionDialog = PermissionMessageDialog()
                permissions.entries.forEach { perm ->
                    when (perm.key) {
                        "android.permission.READ_EXTERNAL_STORAGE", "android.permission.READ_MEDIA_IMAGES" -> {
                            if (!perm.value) {
                                if (VERSION.SDK_INT >= VERSION_CODES.M) {
                                    permissionDialog.showPermissionDialog(
                                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                            perm.key
                                        ),
                                        permissionTextProvider = StoragePermissionGetImageTextProvider(),
                                        onCancelClicked = {},
                                        onOkClicked = {},
                                    )
                                    permissionDialog.createDialog().show(supportFragmentManager,"permissionDialog")
                                } else {
                                    super.onBackPressed()
                                }
                            } else {
                                downloadFile(currentTargetPdf,toolbarTitle)
                            }
                        }
                        WRITE_EXTERNAL_STORAGE->{

                        }
                    }
                }
            } catch (e: Exception) {
                Timber.tag("permissionLauncher").v("permissionLauncher exception = " + e)
            }
        }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000)
            binding.btnDownload.callOnClick()
    }

    private fun setupToolbar(title: String, targetPdf: String) {
        binding.apply {
            toolbarTitle = title
            currentTargetPdf= targetPdf
            tvToolbarTitle.text = toolbarTitle
            imageBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun downloadFile(targetPdf: String, toolbarTitle: String) {
        val destinationFilePath = Utility.copyFileToDownloads(
            File(targetPdf),
            this@PdfViewerActivity,
            toolbarTitle
        )

        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()

        if (destinationFilePath.isNullOrBlank()) {
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_general)
            )
        } else {
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.get_file_from)
            )
        }
        dialog.show(supportFragmentManager, "Alert Dialog MessageOfRequest")
    }


//    private fun setNewZoom(
//        clickedBtn: AppCompatImageView,
//        otherBtn: AppCompatImageView,
//        pdfView: PdfRendererView?, isZoomOut: Boolean
//    ) {
//
//        if(pdfView!= null) {
//            val currentZoom = pdfView.getZoomScale()
//            val newZoom = if (isZoomOut) currentZoom - zoomLevel
//            else currentZoom + zoomLevel
//
//            pdfView.setZoom(newZoom)
//
//            enableButton(otherBtn)
//            if (newZoom - zoomLevel < minZoom || newZoom + zoomLevel > maxZoom) {
//                disableButton(clickedBtn)
//            }
//        }
//    }

    private fun disableButton(button: AppCompatImageView) {
        button.isClickable = false
        button.setColorFilter(
            getColor(this@PdfViewerActivity, R.color.color_icon),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun enableButton(button: AppCompatImageView) {
        button.isClickable = true
        button.setColorFilter(
            getColor(this@PdfViewerActivity, android.R.color.white),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

//    @Throws(IOException::class)
//    private fun openPDF(pdfView: PDFView, targetPdf: String, isAsset: Boolean = false) {
//        var viewer: PDFView.Configurator? = null
//        viewer = if (isAsset) {
//            pdfView.fromAsset(targetPdf)
//        } else {
//            pdfView.fromFile(File(targetPdf))
//        }?.also {
//            it.enableSwipe(false) // allows to block changing pages using swipe
//                .swipeHorizontal(false)
//                .enableSwipe(true)
//                .enableDoubletap(true)
//                .defaultPage(0)
//                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//                .password(null)
//                .scrollHandle(null)
//                .enableSwipe(true)
//                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//                .spacing(0)
//                .nightMode(false) // toggle night mode
//                .load()
//        }
//
//
//    }
}
//fun PdfRendererView.setZoom(scale: Float) {
//    try {
//        val method = PdfRendererView::class.java.getDeclaredMethod("zoomCenteredTo", Float::class.java, PointF::class.java)
//        method.isAccessible = true
//        method.invoke(this, scale, PointF(width / 2f, height / 2f))
//        invalidate()
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//}
