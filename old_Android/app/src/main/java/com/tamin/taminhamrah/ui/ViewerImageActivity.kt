package com.tamin.taminhamrah.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import com.otaliastudios.zoom.ZoomImageView
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.ActivityViewImageBinding
import com.tamin.taminhamrah.ui.base.ActivityViewModel
import com.tamin.taminhamrah.ui.base.ContainerBaseActivity
import com.tamin.taminhamrah.ui.dialog.PermissionMessageDialog
import com.tamin.taminhamrah.ui.dialog.StoragePermissionGetImageTextProvider
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.PickImageUtils
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class ViewerImageActivity : ContainerBaseActivity() {

    companion object {
        const val TITLE_IMAGE = "TITLE_IMAGE"
        const val URI_IMAGE = "URI_IMAGE"
        const val IS_BASE64 = "IS_BASE64"
        const val IMAGE = "IMAGE"
        const val ENABLE_BUTTON_SHARE_AND_DOWNLOAD = "ENABLE_BUTTON_SHARE_AND_DOWNLOAD"
    }

    private val minZoom = .2f
    private val maxZoom = 4f
    private val zoomLevel = .2f
    private var currentTitle = ""
    var res: Bitmap? = null

    val mViewModel: ActivityViewModel by viewModels()

    lateinit var binding: ActivityViewImageBinding
    private var imageDrawable: Drawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewImageBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)
        currentTitle = getTitleImage() ?: getString(R.string.img_title)
        setupToolbar(currentTitle)
        initClick()
        mViewModel.viewModelScope.launch {
            if (getImageUri() != null) {
                val result = setupUi()
                Timber.tag("testViewModel").i("onCreate:  token= ---  res=" + res)
                imageDrawable = (result as? SuccessResult)?.drawable
                binding.zoomImage.setImageBitmap(res)
            } else if (getImage() != null) {
                ImageUtils.loadImageBase64(binding.zoomImage, getImage())
            }
        }
    }

    private fun initClick() {
        binding.apply {
            imageBack.setOnClickListener {
                finish()
            }
            btnDownload.setOnClickListener {
                if (PickImageUtils.hasPermissionsOfList(
                        this@ViewerImageActivity,
                        getStoragePermissions()
                    )
                ) {
                    downloadFile(currentTitle)
                } else {
                    permissionLauncher.launch(getStoragePermissions())
                }
            }

            checkEnabledButton()
            btnShare.setOnClickListener {
                val uri =
                    res?.let { it1 -> ImageUtils.saveImageExternal(it1, this@ViewerImageActivity) }
                if (uri != null) {
                    uri.path?.let { it1 ->
                        Utility.shareImageFile(
                            this@ViewerImageActivity,
                            it1
                        )
                    }
                } else {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_downloaded_pdf_file)
                    )
                }
            }
            btnZoomOut.setOnClickListener {

                setNewZoom(btnZoomOut, btnZoomIn, zoomImage, true)
            }
            btnZoomIn.setOnClickListener {
                setNewZoom(btnZoomIn, btnZoomOut, zoomImage, false)
            }
        }
    }

    private fun downloadFile(title: String) {
        val uriImage =
            res?.let { ImageUtils.saveImageExternal(it, this@ViewerImageActivity) }
        uriImage?.path?.let { path ->
            val destinationFilePath = Utility.copyFileToDownloads(
                File(path),
                this@ViewerImageActivity,
                title,
                "image/jpg"
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
    }

    private suspend fun setupUi(): ImageResult {
        val loader = ImageLoader(this)
        val req = ImageRequest.Builder(this)
        if (isBASE64() == true) {
            ImageUtils.loadImageBase64(view = binding.zoomImage, getImageUri())
            val decodedString = Base64.decode(getImageUri(), Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding.zoomImage.load(decodedByte) {
                req.data(decodedByte)
                    .target { result ->
                        res = (result as BitmapDrawable).bitmap
                        binding.zoomImage
                    }
                    .addHeader(
                        Constants.AUTHENTICATION, mViewModel.getToken()
                    )
            }
        } else {

            req.data(getImageUri())
                .target { result ->
                    res = (result as BitmapDrawable).bitmap
                    binding.zoomImage
                }
                .addHeader(
                    Constants.AUTHENTICATION, mViewModel.getToken()
                )
        }
        return loader.execute(req.build())

    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            } else {
                true
            }
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

    private fun setupToolbar(toolbarTitle: String) {
        binding.tvToolbarTitle.text = toolbarTitle
    }

    private fun checkEnabledButton() {
        val isEnable = intent?.extras?.getBoolean(ENABLE_BUTTON_SHARE_AND_DOWNLOAD, false)
        if (isEnable != null && isEnable) {
            binding.btnDownload.visibility = View.VISIBLE
            binding.btnShare.visibility = View.VISIBLE
            checkFileIsExistToGallery()
        } else {
            binding.btnDownload.visibility = View.GONE
            binding.btnShare.visibility = View.GONE
        }
    }

    private fun getStoragePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun checkFileIsExistToGallery() {

    }

    private fun getTitleImage(): String? {
        return intent?.extras?.getString(TITLE_IMAGE)
    }

    private fun getImageUri(): String? {
        return intent?.extras?.getString(URI_IMAGE)
    }

    private fun isBASE64(): Boolean? {
        return intent?.extras?.getBoolean(IS_BASE64, false)
    }


    private fun getImage(): String? {
        return intent?.extras?.getString(IMAGE)
    }

    private fun setNewZoom(
        clickedBtn: AppCompatImageView,
        otherBtn: AppCompatImageView,
        imageView: ZoomImageView, isZoomOut: Boolean
    ) {
        val currentZoom = imageView.zoom
        val newZoom = if (isZoomOut) currentZoom - zoomLevel
        else currentZoom + zoomLevel
        imageView.realZoomTo(newZoom, true)
        enableButton(otherBtn)
        if (newZoom - zoomLevel < minZoom || newZoom + zoomLevel > maxZoom) {
            disableButton(clickedBtn)
        }
    }

    private fun enableButton(button: AppCompatImageView) {
        button.isClickable = true
        button.setColorFilter(
            ContextCompat.getColor(this, android.R.color.white),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun disableButton(button: AppCompatImageView) {
        button.isClickable = false
        button.setColorFilter(
            ContextCompat.getColor(this, R.color.color_icon),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            try {
                val permissionDialog = PermissionMessageDialog()
                permissions.entries.forEach { perm ->
                    when (perm.key) {
                        "android.permission.READ_EXTERNAL_STORAGE", "android.permission.READ_MEDIA_IMAGES" -> {
                            if (!perm.value) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                                downloadFile(currentTitle)
                            }
                        }
                        Manifest.permission.WRITE_EXTERNAL_STORAGE ->{

                        }
                    }
                }
            } catch (e: Exception) {
                Timber.tag("permissionLauncher").v("permissionLauncher exception = " + e)
            }
        }

}