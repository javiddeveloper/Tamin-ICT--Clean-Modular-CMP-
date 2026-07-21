package com.tamin.taminhamrah.ui.imagePicker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.Constants.IMAGE_URI
import com.tamin.taminhamrah.Constants.REQUEST_CODE_TAG
import com.tamin.taminhamrah.Constants.REQUEST_LUNCHER
import com.tamin.taminhamrah.Constants.TEMPID
import com.tamin.taminhamrah.data.entity.GalleryPicture
import com.tamin.taminhamrah.databinding.ActivityMultiCustomGalleryUi2Binding
import com.tamin.taminhamrah.ui.base.ContainerBaseActivity
import com.tamin.taminhamrah.ui.dialog.CameraPermissionTextProvider
import com.tamin.taminhamrah.ui.dialog.PermissionMessageDialog
import com.tamin.taminhamrah.ui.dialog.StoragePermissionGetImageTextProvider
import com.tamin.taminhamrah.utils.PickImageUtils
import com.tamin.taminhamrah.utils.imagePicker.SpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class MultiCustomGalleryUI : ContainerBaseActivity() {
    lateinit var binding: ActivityMultiCustomGalleryUi2Binding
    var tempImageId: String? = ""
    private var previewsize: Size? = null
    private var previewBuilder: CaptureRequest.Builder? = null
    private var previewSession: CameraCaptureSession? = null
    private val myCameraPermissionRequestID: Int = 1242
    private var mCameraManager: CameraManager? = null
    private var cameraDevice: CameraDevice? = null
    private var requestCode = REQUEST_LUNCHER
    private var isCameraPermissionGranted = false
    private var isStoragePermissionGranted = false


    private val adapter by lazy {
        GalleryPicturesAdapter(pictures, 10)
    }
    private val galleryViewModel: GalleryViewModel by viewModels()

    private val pictures by lazy {
        ArrayList<GalleryPicture>(galleryViewModel.getGallerySize(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiCustomGalleryUi2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)
        tempImageId = intent.getStringExtra(TEMPID)
        this.requestCode = intent.getIntExtra(REQUEST_CODE_TAG, REQUEST_LUNCHER)
        requestCameraAndStoragePermission()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            fabGoToCamera.setOnClickListener {
                getCamera()
            }
            fabCameraLarge.setOnClickListener {
                getCamera()
            }
            fabGoToFolder.setOnClickListener {
                getFolder()
            }
            folderLayout.root.setOnClickListener {
                getFolder()
            }
        }
    }

    private fun getFolder() {
        val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (isPermissionsAllowed(readImagePermission)) {
            resultImageLaunch.launch(getGalleryIntent())
        } else {
            permissionLauncher.launch(arrayOf(readImagePermission))
        }
    }

    private fun getCamera() {
        val permission = Manifest.permission.CAMERA
        if (isPermissionsAllowed(permission)) {
            resultImageLaunch.launch(getCameraIntent(tempImageId))
        } else {
            permissionLauncher.launch(arrayOf(permission))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        closeCamera()
    }

    private fun closeCamera() {
        cameraDevice?.close()
    }

    override fun onResume() {
        super.onResume()
        cameraDevice?.let {
            openCamera()
        }
    }

    private fun setPreviewCamera() {
        val permission = Manifest.permission.CAMERA
        if (checkCameraHardware() && PickImageUtils.hasPermission(this, permission)) {
            binding.preview.surfaceTextureListener = surfaceTextureListener
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            println("Bala surface changed")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }

    private fun checkCameraHardware(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun requestCameraAndStoragePermission() {
        val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        val permissions = arrayOf(readImagePermission, Manifest.permission.CAMERA)
        if (PickImageUtils.hasPermissionsOfList(this, permissions)) {
            fillRecycler()
            setPreviewCamera()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun isPermissionsAllowed(permission: String): Boolean {
        return when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    PickImageUtils.hasPermission(this, permission) -> {
                true
            }

            else -> {
                false
            }
        }
    }

    private fun fillRecycler() {
        val layoutManager = GridLayoutManager(this, 3)
        binding.apply {
            rv.layoutManager = layoutManager
            rv.addItemDecoration(SpaceItemDecoration(8))
            rv.adapter = adapter
        }
        adapter.setOnClickListener { position ->
            //    try {
            //       pictures[position]
            intent.putExtra(IMAGE_URI, pictures[position].path)
            setResult(requestCode, intent)
            finish()
//            } catch (ed: ArrayIndexOutOfBoundsException) {
//                ed.printStackTrace()
//            }
        }

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager.findLastVisibleItemPosition() == pictures.lastIndex) {
                    loadPictures()
                }
            }
        })

        loadPictures()
    }


    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                if (result.data?.data == null) {
                    setResult(requestCode, result.data)
                    finish()
                } else {
                    val intent = Intent()
                    intent.putExtra(IMAGE_URI, result.data?.data.toString())
                    setResult(requestCode, intent)
                    finish()
                }
            }
        }

    private fun getCameraIntent(tempImageId: String?): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = "$packageName.provider"
            val photoURI = FileProvider.getUriForFile(
                this, authority, getImageFile(
                    tempImageId, isCamera = true
                )
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getImageFile(tempImageId)))
        }
        return intent
    }

    private fun getImageFile(
        tempImageId: String?,
        deleteIfExists: Boolean = true,
        isCamera: Boolean = false
    ): File {

        val folder =
            File("${getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tempImage/")
        folder.mkdirs()
        val file = File(folder, "Image_Imp_$tempImageId${if (isCamera) ".jpg" else ""}")
        if (file.exists() && deleteIfExists)
            file.delete()
        file.createNewFile()
        return file
    }

    private fun getGalleryIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        return intent
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadPictures() {
        val pageSize = 20
        galleryViewModel.getImagesFromGallery(this, pageSize) {
            if (it.isNotEmpty()) {
                pictures.addAll(it)
                adapter.notifyItemRangeInserted(pictures.size, it.size)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            fillRecycler()
    }


    fun openCamera() {
        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = getCameraID()
        val characteristics = mCameraManager?.getCameraCharacteristics(cameraId)
        val map = characteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        previewsize = map?.getOutputSizes(SurfaceTexture::class.java)?.get(0)

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                myCameraPermissionRequestID
            )
        } else {
            try {
                mCameraManager?.openCamera(cameraId, stateCallback, null)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startCamera()
        }

        override fun onDisconnected(camera: CameraDevice) {

        }

        override fun onError(camera: CameraDevice, error: Int) {

        }
    }

    private fun getCameraID(): String {
        var cameraId = "0"
        val requiredLensFacing = CameraCharacteristics.LENS_FACING_BACK
        if (mCameraManager != null) {
            try {
                for (id in this.mCameraManager!!.cameraIdList) {
                    val cameraChars: CameraCharacteristics =
                        this.mCameraManager!!.getCameraCharacteristics(id)
                    val facing: Int? = cameraChars.get(CameraCharacteristics.LENS_FACING)
                    println("Bala CameraID: $id with characteristics = $facing")
                    if (facing != null && facing == requiredLensFacing) {
                        cameraId = id
                    }
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
        println("Bala camera ID returned = $cameraId")
        return cameraId
    }

    fun startCamera() {
        if (cameraDevice == null || !binding.preview.isAvailable || previewsize == null) {
            return
        }
        val texture = binding.preview.surfaceTexture ?: return
        texture.setDefaultBufferSize(previewsize!!.getWidth(), previewsize!!.getHeight())
        val surface = Surface(texture)
        try {
            cameraDevice?.let {
                previewBuilder = it.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

            }
        } catch (e: Exception) {
            Timber.tag("TAG").v("startCamera exception = " + e)
        }

        previewBuilder?.addTarget(surface)
        try {
            cameraDevice?.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        previewSession = session
                        getChangedPreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                },
                null
            )
        } catch (e: Exception) {
            Timber.tag("TAG").v("startCamera exception = " + e)
        }

    }

    fun getChangedPreview() {
        if (cameraDevice == null && previewBuilder == null && previewSession == null) {
            return
        }
        previewBuilder?.let { previewBuilder ->
            previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            val thread = HandlerThread("changed Preview")
            thread.start()
            val handler = Handler(thread.looper)
            println("Bala changed preview")
            try {
                previewSession?.setRepeatingRequest(previewBuilder.build(), null, handler)
            } catch (e: Exception) {
                Timber.tag("getChangedPreview").v("getChangedPreview exception = " + e)
            }
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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    permissionDialog.showPermissionDialog(
                                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                            perm.key
                                        ),
                                        permissionTextProvider = StoragePermissionGetImageTextProvider(),
                                        onCancelClicked = { super.onBackPressed() },
                                        onOkClicked = { super.onBackPressed() },
                                    )
                                    permissionDialog.createDialog().show(supportFragmentManager,"permissionDialog")

                                } else {
                                    super.onBackPressed()
                                }
                            } else {
                                fillRecycler()
                            }
                        }

                        "android.permission.CAMERA" -> {
                            if (!perm.value) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    permissionDialog.showPermissionDialog(
                                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                            perm.key
                                        ),
                                        permissionTextProvider = CameraPermissionTextProvider(),
                                        onCancelClicked = { super.onBackPressed() },
                                        onOkClicked = { super.onBackPressed() },
                                    )
                                    permissionDialog.createDialog().show(supportFragmentManager,"permissionDialog")
                                }

                                binding.apply {
                                    cameraDisable.visibility = View.VISIBLE
                                    fabCameraLarge.visibility = View.GONE
                                    preview.visibility = View.GONE
                                }

                            } else {
                                binding.apply {
                                    cameraDisable.visibility = View.GONE
                                    fabGoToCamera.visibility = View.VISIBLE
                                    preview.visibility = View.VISIBLE
                                }
                                openCamera()
                                setPreviewCamera()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.tag("permissionLauncher").v("permissionLauncher exception = " + e)
            }
        }

}



