package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit.objectionableDebit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.DebitObjectionResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt
import com.tamin.taminhamrah.databinding.FragmentDebitObjectionBinding
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.PictureSelectorFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopSearchDialogFragment
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.PickImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DebitObjectionFragment :
    BaseFragment<FragmentDebitObjectionBinding, WorkshopInfoViewModel>(),
    AdapterInterface.OnItemClickListener<UploadedImageModel>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    override val mViewModel: WorkshopInfoViewModel by viewModels()
    lateinit var listAdapter: ImagePreviewAdapter

    companion object {
        const val DEBIT_ITEM = "DEBIT_ITEM"
    }

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == Constants.REQUEST_LUNCHER) {
                    var imageUri: Uri? = null

                    if (result.data != null)
                        imageUri = Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI))

                    if (!isDuplicateImage(imageUri, mViewModel.fileListUploaded))
                        provideImageForUpload(mViewModel.tempImageType, imageUri)
                    else
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.WARNING,
                            getString(R.string.error_select_repeat_pic)
                        )
                }
            } catch (e: Exception) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.image_upload_error)
                )
            }
        }

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_debit_objection
    }

    override fun setupObserver() {
        mViewModel.mldUploadImage.observe(this, ::showResultUploadedImage)
        mViewModel.mldDebitObjectionResult.observe(this, ::showObjectionResult)
    }

    override fun initView() {

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )

        viewDataBinding?.apply {
            listAdapter = ImagePreviewAdapter(this@DebitObjectionFragment)
            recyclerDocs.apply {
                this.adapter = listAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                }
            }
            (arguments?.getParcelable(DEBIT_ITEM) as? WorkShopDebt)?.let {
                recyclerInfo.apply {
                    if (itemDecorationCount == 0) {
                        addItemDecoration(UiUtils.createDivider(this.context))
                    }
                    adapter = KeyValueAdapter().apply {
                        setItems(it.createKeyValueObjectionInfo(it))
                    }
                }
            }
        }
    }

    override fun chooseImage(requestCode: Int) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {

            this@DebitObjectionFragment.lifecycleScope.launchWhenCreated {
                mViewModel.getObjectionType().collectLatest { pagingData ->
                    val result = pagingData.map {
                        MenuModel(title = it.name, id = it.value)
                    }
                    openImageTypeMenu(result, onResultCallBack = object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            mViewModel.fileListUploaded.apply {
                                for (i in 0 until size) {
                                    if (get(i).imageType == itemResult.id) {
                                        removeAt(i)
                                        break
                                    }
                                }

                                mViewModel.tempImageType = itemResult.id.toString()
                                mViewModel.tempImageName = itemResult.title ?: ""
                                val intent = Intent(activity, MultiCustomGalleryUI::class.java)
                                intent.putExtra(Constants.TEMPID, mViewModel.tempImageType)
                                intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)

                                resultImageLaunch.launch(intent)
                            }
                        }
                    })

                }
            }
        }
    }

    private fun openObjectionTypeMenu() {

        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

        val dialog =
            MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_add_document))
        dialog.setMenuListener(object : MenuInterface.OnFetchData {

            override fun onFetch() {
                this@DebitObjectionFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getObjectionType().collectLatest { pagingData ->
                        val result = pagingData.map {
                            MenuModel(title = it.name, id = it.value)
                        }
                        dialog.updateData(result)
                    }
                }
            }

        }, object : MenuInterface.OnResult {

            override fun onResult(itemResult: MenuModel) {
                openMediaChooserDialog(itemResult.title, itemResult.id)
            }
        })

        dialog.setStopListenr(object : AdapterInterface.OnStopDialogListener {
            override fun onStop() {

            }
        })
        dialog.show(childFragmentManager, "ruyeltiyut")

    }

    var fileList = ArrayList<UploadedImageModel?>()
    private fun openMediaChooserDialog(title: String?, id: String?) {

        val dialog = PictureSelectorFragment()
        val bundle = Bundle()
        bundle.putString(PictureSelectorFragment.ARG_TITLE, title)
        dialog.arguments = bundle
        dialog.setListener(object : PictureSelectorFragment.OnButtonClick {
            override fun onCameraButtonClick() {
                setTempImage(title, id)
                // getCamera(resultCameraLauncher2, permCameraReqLauncher2)
            }

            override fun onGalleryButtonClick() {
                setTempImage(title, id)
                //          getGallery(resultGalleryLauncher2, permGalleryReqLauncher2)
            }
        })
        dialog.setStopListener(object : AdapterInterface.OnStopDialogListener {
            override fun onStop() {
            }
        })

        dialog.show(childFragmentManager, "uyliuyy898")
    }

    private fun setTempImage(title: String?, id: String?) {
        fileList.forEach { item ->
            if (item?.imageName == title && item?.imageType == id) {
                item?.isSelected = true

                return@forEach
            }
        }
    }

    override fun getData() {
    }

    override fun onClick() {
        viewDataBinding?.apply {
            appBar.toolbar.apply {
                imgAction.setOnClickListener {

                    val dialog = WorkshopSearchDialogFragment()
                    dialog.setListener(this@DebitObjectionFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }
            }

            layoutUploadImage.root.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                chooseImage()
            }

            btnSubmit.setOnClickListener {

                if (fileList.filter { !it?.guid.isNullOrBlank() }.isEmpty()) {
                    showErrorMessage(getString(R.string.error_add_ducument))
                } else {
                    if (cbSubmit.isChecked) {
                        tvError.visibility = View.GONE


                        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                        dialog.arguments = createBundle(
                            MessageOfRequestDialogFragment.MessageType.CONFIRM,
                            getString(R.string.message_debit_objection_confirmation)
                        )
                        dialog.setDialogClickListener(object :
                            DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                mViewModel.sendDebitObjection(
                                    fileList,
                                    getDebitInfo(),
                                    getWorkshopId(),
                                    getBranchCode(),
                                    cbBankDeposit.isChecked,
                                    cbSubmit.isChecked,
                                    etObjectionDesc.getValue()
                                )
                            }

                            override fun onCancelClick() {

                            }
                        })
                        dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")

                    } else {
                        tvError.text = getString(R.string.error_check_debit_objection_submit)
                        tvError.visibility = View.VISIBLE
                    }
                }

            }
        }
    }

    private fun showErrorMessage(messageStr: String) {
        viewDataBinding?.tvError?.apply {
            text = messageStr
            visibility = View.VISIBLE
        }
    }

    private fun getWorkshopId(): String? {
        return arguments?.getString(Constants.WORKSHOP_ID)
    }

    private fun getBranchCode(): String? {
        return arguments?.getString(Constants.BRANCH_ID)
    }

    private fun getDebitInfo(): WorkShopDebt? {
        return arguments?.getParcelable(mViewModel.ARG_WORKSHOP_DEBIT)
    }

    private fun showResultUploadedImage(result: UploadImageResponse) {
        if (result.isSuccess) {
            result.let { guid ->
                fileList.forEach { item ->
                    if (item?.isSelected == true) {
                        if (item.imageUri != null) {
                            item.guid = guid.guid
                        }
                        item.isSelected = false
                    }
                }

                val resultList = ArrayList<UploadedImageModel>()
                fileList.forEach { item ->
                    if (!item?.guid.isNullOrBlank() && item != null)
                        resultList.add(item)
                }
                listAdapter.setItems(resultList)
            }
        }
    }

    private fun showObjectionResult(result: DebitObjectionResponse) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_debit_objection_success, result.data?.refId)
            )
        }
    }

    override fun onItemClick(item: UploadedImageModel, transitionView: View?, tag: String?) {

        when (tag) {
            Constants.IMAGE_PREVIEW_TAG -> {
                val bundle = Bundle()
                bundle.putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                bundle.putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                handlePageDestination(R.id.action_debit_objection_to_image_preview, bundle)
            }
            Constants.DELETE_IMAGE_TAG -> {
                val adapterList = listAdapter.getItems()
                adapterList?.remove(item)
                adapterList?.let { listAdapter.setItems(it) }
            }
        }

    }

    override fun onDialogResult(item: Map<String, String>) {
        val workshopId = item[Constants.WORKSHOP_ID] ?: ""
        val branchCode = item[Constants.BRANCH_ID] ?: ""
//        mViewModel.getWorkshopList(workshopId = workshopId, branchCode = branchCode)
    }
/*
    override fun onGalleryResult(uri: Uri?) {
        uploadImage(uri, false)
    }

    override fun onCameraResult(uri: Uri?) {
        uploadImage(uri, true)
    }*/

    private fun uploadImage(uri: Uri?, fromCamera: Boolean) {
        fileList.forEach { item ->
            if (item?.isSelected == true) {
                if (uri == null) {
                    item.isSelected = false // get image is failed
                } else {
                    val body = PickImageUtils.getImageRequestBody(requireContext(), uri, fromCamera)
                    item.imageUri = uri
                    body?.let { mViewModel.uploadImage(it) }
                }
            }
        }

    }
}

