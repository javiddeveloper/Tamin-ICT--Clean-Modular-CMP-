package com.tamin.taminhamrah.utils.extentions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.filter
import com.project.jetpack.paging3.FooterAdapter
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.MAX_UPLOAD_SIZE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumStausUploadImage
import com.tamin.taminhamrah.data.entity.ImagePathModel
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadImageStatusForUiModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.databinding.ItemAddPicBinding
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.EndOfPaginationListener
import com.tamin.taminhamrah.ui.appinterface.ImagePickerResult
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.PictureSelectorFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.LoadingView
import com.tamin.taminhamrah.utils.PickImageUtils
import com.tamin.taminhamrah.utils.compression.Compression
import com.tamin.taminhamrah.widget.CustomRecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream


fun Fragment.openImageTypeMenu(
    itemList: PagingData<MenuModel>,
    onResultCallBack: MenuInterface.OnResult,
    isSearchEnable: Boolean=false,
    meuTitle: String = "",
    isRemoteList: Boolean = false
) {
    val dialog = MenuDialogFragment.newInstance(isRemoteData = isRemoteList, menuTitle = meuTitle)
    /*  val bundle = Bundle()
      dialog.arguments = bundle*/
    dialog.setMenuListener(object : MenuInterface.OnFetchData {
        override fun onFetch() {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                dialog.updateData(itemList)
            }
        }
    }, object : MenuInterface.OnResult {
        override fun onResult(itemResult: MenuModel) {
            onResultCallBack.onResult(itemResult)
        }
    }, searchListener = object :MenuInterface.OnSearch{
        override fun onSearch(str: String) {
            if (isSearchEnable)
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    dialog.updateData(itemList.filter { it.title?.contains(str)?:false})
                }
        }
    })
    dialog.show(childFragmentManager, "uyliuyy898")
}


fun BaseFragment<*, *>.openMediaChooserDialog(
    id: String,
    title: String,
    resultImageLaunch: ActivityResultLauncher<Intent>,
    permCameraReqLauncher: ActivityResultLauncher<Array<String>>,
    permGalleryReqLauncher: ActivityResultLauncher<Array<String>>
) {
    val dialog = PictureSelectorFragment()
    val bundle = Bundle()
    bundle.putString(PictureSelectorFragment.ARG_TITLE, title)
    dialog.arguments = bundle
    dialog.setListener(object : PictureSelectorFragment.OnButtonClick {
        override fun onCameraButtonClick() {

            if (isPermissionsAllowed(Manifest.permission.CAMERA)) {
                resultImageLaunch.launch(getCameraIntent(id))
            } else {
                permCameraReqLauncher.launch(
                    arrayOf(Manifest.permission.CAMERA)
                )
            }
        }

        override fun onGalleryButtonClick() {
            if (isPermissionsAllowed(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                resultImageLaunch.launch(getGalleryIntent())
            } else {
                permGalleryReqLauncher.launch(
                    arrayOf(Manifest.permission.CAMERA)
                )
            }
        }
    })
    dialog.setStopListener(object : AdapterInterface.OnStopDialogListener {
        override fun onStop() {
        }
    })
    dialog.show(childFragmentManager, "uyliuyy898")
}

fun BaseFragment<*, *>.handleImageRequest(
    tempImageId: String?,
    data: Intent?,
    pathResult: ImagePickerResult.GetPathIamge,
) {
    try {
        val inputFile = if (data?.data == null) getImageFile(
            tempImageId,
            deleteIfExists = false,
            isCamera = true
        ) else getGalleryFile(data.data!!)
        inputFile?.let {
            if (inputFile.length() >= MAX_UPLOAD_SIZE) {
                Compression(requireActivity()).compress(
                    inputFile,
                    object : ImagePickerResult.CompressionUri {
                        override fun onResultFile(file: File) {
                            pathResult.OnResult(arrayListOf(ImagePathModel(data?.data, file.path)))
                        }
                    })
            } else {
                pathResult.OnResult(arrayListOf(ImagePathModel(data?.data, inputFile.path)))

            }
        }
    } catch (e: Exception) {
        showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.image_upload_error)
        )
        Timber.tag("handleImageRequest: ").e("Error" + e.localizedMessage.toString())
    }

}

fun BaseFragment<*, *>.handleImageRequest(
    tempImageId: String?,
    imageUri: Uri?,
    pathResult: ImagePickerResult.GetPathIamge,
) {
    try {
        val inputFile = if (imageUri == null) getImageFile(
            tempImageId,
            deleteIfExists = false,
            isCamera = true
        ) else getGalleryFile(imageUri)

        inputFile?.let {
            if (inputFile.length() >= MAX_UPLOAD_SIZE) {
                Compression(requireActivity()).compress(
                    inputFile,
                    object : ImagePickerResult.CompressionUri {
                        override fun onResultFile(file: File) {
                            pathResult.OnResult(arrayListOf(ImagePathModel(imageUri, file.path)))
                        }
                    })
            } else {
                pathResult.OnResult(arrayListOf(ImagePathModel(imageUri, inputFile.path)))
            }
        }
    } catch (e: Exception) {
        showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.image_upload_error)
        )

        Timber.tag("handleImageRequest: ").e("Error" + e.localizedMessage.toString())
    }
}

fun BaseFragment<*, *>.getGalleryFile(uri: Uri): File? {
    try {
        val bitMap = getBitmapFromUri(uri)
        val file = getImageFile(getFileName(uri))
        val ops = FileOutputStream(file)
        bitMap?.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            ops
        )
        ops.flush()
        ops.close()
        bitMap?.recycle()
        return file
    } catch (e: Exception) {
        showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.image_upload_error)
        )
        Timber.tag("getGalleryFile: ").e(e.localizedMessage.toString())
    }
    return null
}

fun BaseFragment<*, *>.getCameraIntent(tempImageId: String?): Intent {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val authority =
            requireContext().packageName + ".provider"
        val photoURI = FileProvider.getUriForFile(
            requireContext(), authority, getImageFile(
                tempImageId, isCamera = true
            )
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    } else {
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getImageFile(tempImageId)))
    }
    return intent
}

fun BaseFragment<*, *>.getGalleryIntent(): Intent {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    return intent
}

fun BaseFragment<*, *>.setUploadLayoutUI(
    statusUpload: EnumStausUploadImage,
    layoutUpload: ItemAddPicBinding
): Boolean {
    layoutUpload.apply {
        when (statusUpload) {
            EnumStausUploadImage.LOADING -> {
                getFromCamera.isEnabled = false
                getFromGallery.isEnabled = false
                status = UploadImageStatusForUiModel(
                    uiClickable = false,
                    showLoading = true
                )
                return false
            }
            EnumStausUploadImage.ERROR -> {
                getFromCamera.isEnabled = true
                getFromGallery.isEnabled = true
                status = UploadImageStatusForUiModel(
                    uiClickable = true,
                    showLoading = false,
                    status = EnumStausUploadImage.ERROR
                )
                return true
            }
            EnumStausUploadImage.SUCCESS -> {
                getFromCamera.isEnabled = true
                getFromGallery.isEnabled = true
                status = UploadImageStatusForUiModel(
                    uiClickable = true,
                    showLoading = false,
                    status = EnumStausUploadImage.SUCCESS
                )
                return false
            }
        }
    }
}

fun BaseFragment<*, *>.getImageFile(
    tempImageId: String?,
    deleteIfExists: Boolean = true,
    isCamera: Boolean = false
): File {

    val folder =
        File("${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tempImage/")
    folder.mkdirs()
    val file = File(folder, "Image_Imp_$tempImageId${if (isCamera) ".jpg" else ""}")
    if (file.exists() && deleteIfExists)
        file.delete()
    file.createNewFile()
    return file
}

fun BaseFragment<*, *>.getImageUri(tempImageId: String?): Uri =
    FileProvider.getUriForFile(
        requireContext(),
        BuildConfig.APPLICATION_ID + ".provider",
        getImageFile(tempImageId)
    )

fun BaseFragment<*, *>.checkPermission(
    permissions: Map<String, Boolean>,
    resultImageLaunch: ActivityResultLauncher<Intent>,
    imageType: String?,
    isCamera: Boolean
) {
    val granted = permissions.entries.all {
        it.value
    }
    if (granted) {
        resultImageLaunch.launch(if (isCamera) getCameraIntent(imageType) else getGalleryIntent())
    }
}


@SuppressLint("Range")
fun BaseFragment<*, *>.getFileName(uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor: Cursor? = requireActivity().contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result!!.lastIndexOf(File.separator)
        if (cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    Timber.tag("CompressionImageTag: ").i(" STEP1:File Name = " + result + " ")

    return result
}


/** we need to get bitmap from uri/path. Earlier we were using BitmapFactory.decodeFile(path),
but it return null for external files. So, we have to use following method to get bitmap by using SAF method.*/
fun BaseFragment<*, *>.getBitmapFromUri(uri: Uri, options: BitmapFactory.Options? = null): Bitmap? {
    val parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor = parcelFileDescriptor?.fileDescriptor
    val image: Bitmap? = if (options != null)
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
    else
        BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor?.close()
    return image
}

fun BaseFragment<*, *>.isPermissionsAllowed(permission: String): Boolean {
    return when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                PickImageUtils.hasPermission(activity as Context, permission) -> {
            true
        }
        else -> {

            false
        }
    }
}



fun BaseFragment<*, *>.provideImageForUpload(
    imageType: String?,
    imageUri: Uri?,
    requestCode: Int = Constants.REQUEST_LUNCHER
) {
    handleImageRequest(imageType, imageUri, object : ImagePickerResult.GetPathIamge {
        override fun OnResult(listPath: ArrayList<ImagePathModel>) {
            val inputFile = File(listPath[0].compressionPath!!)
            // tempImageUri = Uri.fromFile(inputFile)
            val body = PickImageUtils.getImageBody(inputFile.path)
            // uploadImage(body, listPath[0].orgUri)
            //  uploadImage(body, listPath[0].orgUri)
            uploadImage(body, listPath[0].orgUri, Uri.fromFile(inputFile), requestCode)
        }
    })
}


fun BaseFragment<*, *>.isDuplicateImage(
    intent: Intent?,
    fileListUploaded: java.util.ArrayList<UploadedImageModel>
): Boolean {
    try {
        intent?.data?.let { selectedUriPic ->
            val newPic = getBitmapFromUri(Uri.fromFile(getGalleryFile(selectedUriPic)))
            if (fileListUploaded.isNotEmpty()) {
                fileListUploaded.forEach { item ->
                    item.orgUri?.let { uri ->
                        val oldPic = getBitmapFromUri(Uri.fromFile(getGalleryFile(uri)))
                        newPic?.let {
                            if (it.sameAs(oldPic)) {
                                oldPic?.recycle()
                                newPic.recycle()
                                return true
                            } else {
                                oldPic?.recycle()
                            }
                        }
                    }
                }
            }
            newPic?.recycle()
        }
        return false
    } catch (e: Exception) {
        Timber.tag("isDuplicateImage: ").e(e.localizedMessage.toString())
        return false
    }

}

fun BaseFragment<*, *>.isDuplicateImage(
    imageUri: Uri?,
    fileListUploaded: java.util.ArrayList<UploadedImageModel>
): Boolean {
    try {
        imageUri?.let { selectedUriPic ->
            val newPic = getBitmapFromUri(Uri.fromFile(getGalleryFile(selectedUriPic)))
            if (fileListUploaded.isNotEmpty()) {
                fileListUploaded.forEach { item ->
                    item.orgUri?.let { uri ->
                        val oldPic = getBitmapFromUri(Uri.fromFile(getGalleryFile(uri)))
                        newPic?.let {
                            if (it.sameAs(oldPic)) {
                                oldPic?.recycle()
                                newPic.recycle()
                                return true
                            } else {
                                oldPic?.recycle()
                            }
                        }
                    }
                }
            }
            newPic?.recycle()
        }
        return false
    } catch (e: Exception) {
        Timber.tag("isDuplicateImage: ").e(e.localizedMessage.toString())
        return false
    }

}

fun Fragment.navigateUp() {
    if (mayNavigate()) findNavController().navigateUp()
}

fun Fragment.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {


    if (mayNavigate()) findNavController().navigate(
        resId, args,
        navOptions, navigatorExtras
    )
}

/**
 * Navigates only if this is safely possible; when this Fragment is still the current destination.
 */
fun Fragment.navigateSafe(
    deepLink: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    if (mayNavigate()) findNavController().navigate(deepLink, navOptions, navigatorExtras)
}

/**
 * Navigates only if this is safely possible; when this Fragment is still the current destination.
 */
fun Fragment.navigateSafe(directions: NavDirections, navOptions: NavOptions? = null) {
    if (mayNavigate()) findNavController().navigate(directions, navOptions)
}

/**
 * Navigates only if this is safely possible; when this Fragment is still the current destination.
 */
fun Fragment.navigateSafe(
    directions: NavDirections,
    navigatorExtras: Navigator.Extras
) {
    if (mayNavigate()) findNavController().navigate(directions, navigatorExtras)
}

/**
 * Returns true if the navigation controller is still pointing at 'this' fragment, or false if it already navigated away.
 */
fun Fragment.mayNavigate(): Boolean {

    val navController = findNavController()
    val destinationIdInNavController = navController.currentDestination?.id

    // add tag_navigation_destination_id to your ids.xml so that it's unique:
    val destinationIdOfThisFragment =
        view?.getTag(R.id.nav_host_fragment) ?: destinationIdInNavController

    // check that the navigation graph is still in 'this' fragment, if not then the app already navigated:
    return if (destinationIdInNavController == destinationIdOfThisFragment) {
        view?.setTag(R.id.nav_host_fragment, destinationIdOfThisFragment)
        true
    } else {
        Timber.tag("FragmentExtensions")
            .d("May not navigate: current destination is not the current fragment.")
        false
    }
}


fun <T> Fragment.setNavigationResult(key: String, value: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(
        key,
        value
    )
}

fun <T> Fragment.getNavigationResult(
    @IdRes id: Int,
    key: String,
    onResult: (result: T) -> Unit
) {
    val navBackStackEntry = findNavController().getBackStackEntry(id)

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME
            && navBackStackEntry.savedStateHandle.contains(key)
        ) {
            val result = navBackStackEntry.savedStateHandle.get<T>(key)
            result?.let(onResult)
            navBackStackEntry.savedStateHandle.remove<T>(key)
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)

    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
}

fun createBundle(
    type: MessageOfRequestDialogFragment.MessageType,
    desc: String,
    btnCancel: Boolean = false,
    titleConfirm: String? = null,
    titleId: Int? = 0,
    dismissType: MessageOfRequestDialogFragment.DismissType? = MessageOfRequestDialogFragment.DismissType.NORMAL
): Bundle {
    val bundle = Bundle()
    bundle.putSerializable(Constants.DIALOG_MESSAGE_TYPE, type)
    bundle.putString(Constants.DIALOG_DESC, desc)
    bundle.putBoolean(Constants.CANCEL_BUTTON, btnCancel)
    bundle.putString(Constants.TITLE_CONFIRM_BUTTON, titleConfirm)
    bundle.putSerializable(Constants.DISMISS_TYPE_DIALOG, dismissType)
    titleId?.let { bundle.putInt(Constants.DIALOG_TITLE, it) }
    return bundle
}

fun BaseFragment<*, *>.setupRecycler(
    recycler: CustomRecyclerView?,
    adapter: BasePagingAdapter<*, *>,
    emptyMessage: String = getString(R.string.message_empty_list),
    tag: String = "",
    listener: EndOfPaginationListener? = null
) = recycler?.apply {
    setTag(tag)
    // getRecycler().adapter = adapter
    getRecycler().adapter = adapter.withLoadStateFooter(FooterAdapter { adapter.retry() })

    adapter.addLoadStateListener { loadStates ->
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates: CombinedLoadStates ->

                updateLoadingState(if (loadStates.refresh is LoadState.Loading) LoadingState.LOADING else LoadingState.NOT_LOADING)

                if (loadStates.append is LoadState.NotLoading && loadStates.append.endOfPaginationReached) {
                    listener?.onEndOfPagination(tag)
                }


                if (loadStates.source.refresh is LoadState.NotLoading) {
                    if (adapter.itemCount < 1) {
                        recycler.showMessage(emptyMessage)
                    } else {
                        recycler.hideMessage()
                    }
                }
                if (loadStates.source.refresh is LoadState.Loading) {
                    recycler.hideMessage()
                }

                val refreshError = loadStates.refresh as? LoadState.Error
                val appendError = loadStates.append as? LoadState.Error
                val prependError = loadStates.prepend as? LoadState.Error

                refreshError?.let {
                    recycler.showMessage(emptyMessage)
                }
                appendError?.let {
                    recycler.showMessage(emptyMessage)
                }
                prependError?.let {
                    recycler.showMessage(emptyMessage)
                }
            }

        }
    }
}




fun BaseFragment<*, *>.isAppInstalled(uri: String): Boolean {
    val pm: PackageManager = requireActivity().packageManager
    try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
    }
    return false
}

fun BaseBottomSheetDialogFragment<*, *>.handleResponse(
    loadState: CombinedLoadStates,
    showError: Boolean = true,
    retry: Boolean = false,
    recycler: CustomRecyclerView?,
    loadingView: LoadingView?,
    parent: ViewGroup
) {
    when {
        loadState.refresh is LoadState.Loading /*|| loadState.append is LoadState.Loading */ -> {
            // showLoading(loadingView)
            loadingView?.showLoading(requireContext(), parent)
        }

        loadState.refresh is LoadState.Error -> {
            loadingView?.hideLoading()
//            hideLoading(loadingView)
            if (showError)
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    (loadState.refresh as LoadState.Error).error.message
                        ?: getString(R.string.message_invalide_error_null)
                )

        }

        loadState.append is LoadState.Error -> {
//            hideLoading(loadingView)
            loadingView?.hideLoading()
            if (showError)
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    (loadState.append as LoadState.Error).error.message
                        ?: getString(R.string.message_invalide_error_null)
                )

        }

        else -> {
//            hideLoading(loadingView)
            loadingView?.hideLoading()
        }


    }
}



