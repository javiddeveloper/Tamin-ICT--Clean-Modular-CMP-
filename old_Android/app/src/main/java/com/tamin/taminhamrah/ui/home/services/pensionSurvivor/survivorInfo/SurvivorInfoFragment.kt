package com.tamin.taminhamrah.ui.home.services.pensionSurvivor.survivorInfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.FIRST_PAGE_IDENTITY_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.IDENTITY_INFO
import com.tamin.taminhamrah.Constants.MARRIAGE_TYPE_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.PARTNER_INFO_IDENTITY_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.PARTNER_INFO_MARRIAGE_CERTIFICATE_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.STUDY_CERTIFICATE_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.SURVIVOR_ITEM
import com.tamin.taminhamrah.Constants.SURVIVOR_REQUEST_KEY
import com.tamin.taminhamrah.Constants.WIFE_INFO_MARRIAGE_CERTIFICATE_IMAGE_TYPE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SurvivorModel
import com.tamin.taminhamrah.databinding.FragmentSurvivorInfoBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.PensionSurvivorViewModel
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model.EnumImageTypeSurvivor
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model.EnumImageTypeSurvivor.IS_DAUGHTER_OLDER_THEN_15
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model.EnumImageTypeSurvivor.IS_KID
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model.EnumImageTypeSurvivor.IS_PARENT
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model.EnumImageTypeSurvivor.IS_PARTNER
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model.EnumImageTypeSurvivor.IS_SON_OLDER_THEN_18
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model.EnumImageTypeSurvivor.IS_UNKNOWN
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.survivorInfo.identityInfo.SurvivorIdentityInfoFragment
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody

@AndroidEntryPoint
class SurvivorInfoFragment :
    BaseFragment<FragmentSurvivorInfoBinding, PensionSurvivorViewModel>() {

    //Class Variables
    override val mViewModel: PensionSurvivorViewModel by viewModels()
    lateinit var relationType: EnumImageTypeSurvivor
    lateinit var survivorInfo: SurvivorModel
    var relationCode = ""
    val imagesList by lazy {
        ArrayList<UploadedImageModel>()
    }
    val onItemDocumentClick by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        handlePageDestination(
                            R.id.action_pension_survivor_info_to_image,
                            Bundle().apply {
                                putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                                putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                            }
                        )
                    }
                    Constants.DELETE_IMAGE_TAG -> {
                        imagesList.apply {
                            remove(item)
                            documentListAdapter.setItems(this)
                        }
                        checkImageSize()
                    }
                }
            }
        }
    }
    val documentListAdapter: ImagePreviewAdapter by lazy { ImagePreviewAdapter(onItemDocumentClick) }
    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.REQUEST_LUNCHER) {
                val imageUri = if (result.data != null)
                    Uri.parse(result?.data?.extras?.getString(Constants.IMAGE_URI))
                else
                    null
                //capture image from camera imageUri is null
                if (imageUri == null || !isDuplicateImage(imageUri, imagesList)
                )
                    provideImageForUpload(mViewModel.dataModel.tempImageType, imageUri)
                else
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.error_select_repeat_pic)
                    )
            }
        }

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_survivor_info

    override fun setupObserver() {
        mViewModel.mldUploadImage.observe(this, ::onUploadImageResponse)
        mViewModel.mldSaveSurvivorInfo.observe(this, ::onSaveSurvivorInfoResponse)
    }

    private fun onSaveSurvivorInfoResponse(result: GeneralRes) {
        if (result.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.success_save_survivor_info)
            )

            dialog.setDialogClickListener(object :
                DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    setFragmentResult(SURVIVOR_REQUEST_KEY, bundleOf(SURVIVOR_ITEM to survivorInfo))
                    dialog.dismiss()
                    requireActivity().onBackPressed()
                }

                override fun onCancelClick() {
                }
            })
            dialog.show(childFragmentManager, "SurvivorInfoFragment")
        }
    }

    override fun initView() {
        viewDataBinding?.apply {
            val info = arguments?.getParcelable(SURVIVOR_ITEM) as? SurvivorModel
            if (info == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data),
                    MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
            } else {
                tvDescUpload.descTxt.apply {
                    text = getString(R.string.upload_all_survivor_doc)
                    setTextColor(ContextCompat.getColor(context, R.color.text_color_dialog_blue))
                }
                survivorInfo = info
                survivorInfo.userInfo.apply {
                    val userName =
                        getString(R.string.space, personal?.firstName, personal?.lastName)
                    val nationalId = personal?.nationalId
                    val relationShip = personal?.relationShip?.ifBlank { "_" } ?: "_"
                    tvSurvivorInfo.text = getString(
                        R.string.survivor_info_toolbar,
                        userName,
                        nationalId,
                        relationShip
                    )
                    relationType =
                        getRelationType(
                            tendencyCode = relation.tendency?.tendencyCode ?: "0",
                            genderCode = personal?.gender?.genderCode ?: "0",
                            age = personal?.age ?: 0
                        )
                }
                recycler.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = documentListAdapter
                    if (itemDecorationCount == 0)
                        addItemDecoration(
                            UiUtils.VerticalItemSetMarginDecoration(
                                topMargin = 10,
                                bottomMargin = 10
                            )
                        )
                }
                inputMobile.getInput().doAfterTextChanged {
                    if (it?.length == 11) {
                        inputMobile.getLayout().isErrorEnabled = false
                    }
                }
                inputPhone.getInput().doAfterTextChanged {
                    if (it?.length == 10) {
                        inputPhone.getLayout().isErrorEnabled = false
                    }
                }
                inputAddress.getInput().doAfterTextChanged {
                    inputAddress.getLayout().isErrorEnabled = false
                }
            }
        }
    }

    override fun getData() {

    }

    override fun onClick() {
        viewDataBinding?.apply {
            imageBack.setOnClickListener { requireActivity().onBackPressed() }
            btnShowInfo.setOnClickListener {
                val dialog = SurvivorIdentityInfoFragment.getInstance(Bundle().apply {
                    putParcelable(
                        IDENTITY_INFO,
                        survivorInfo
                    )
                })
                dialog.show(childFragmentManager, "SurvivorIdentityInfoFragment")
            }
            itemAddDoc.root.setOnClickListener {
                chooseImage()
            }
            btnSave.setOnClickListener {
                if (checkValidInputs()) {
                    val requestModel = getSurvivorInfo()
                    if (requestModel != null)
                        mViewModel.saveSurvivorInfo(requestModel)
                    else
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_save_info)
                        )
                }
            }
        }
    }

    override fun chooseImage(requestCode: Int) {
        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val pager = Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                pagingSourceFactory = { LocalPagingSource(getNeededImagesUpload(relationType)) })

            pager.flow.cachedIn(lifecycleScope).collectLatest { pagingData ->
                openImageTypeMenu(pagingData, onResultCallBack = object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        imagesList.apply {
                            for (i in 0 until size) {
                                if (get(i).imageType == itemResult.id) {
                                    removeAt(i)
                                    break
                                }
                            }
                            mViewModel.dataModel.tempImageType = itemResult.id.toString()
                            mViewModel.dataModel.tempImageName = itemResult.title ?: ""
                            val intent = Intent(activity, MultiCustomGalleryUI::class.java)
                            intent.putExtra(Constants.TEMPID, mViewModel.dataModel.tempImageType)
                            intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)

                            resultImageLaunch.launch(intent)
                        }
                    }
                })
            }
        }
    }

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int,
    ) {
        mViewModel.uploadImage(body)
        mViewModel.dataModel.tempImageUri = imageUri
        mViewModel.dataModel.tempImageOriginalUri = orgPath
    }

    //Listeners
    private fun onUploadImageResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.apply {
                loadImageInfo(guid = result.guid, list = imagesList)
                documentListAdapter.setItems(imagesList)
                checkImageSize()
                viewDataBinding?.apply {
                    tvDescError.isVisible = false
                    imgDocError.isVisible = false
                }
            }
        }
    }

    private fun checkImageSize() {
        viewDataBinding?.itemAddDoc?.root?.isVisible =
            imagesList.size != getNeededImagesUpload(relationType).size
    }

    //Utils
    private fun checkValidInputs(): Boolean {
        viewDataBinding?.apply {
            val mobile = inputMobile.getValue(false)
            val phone = inputPhone.getValue(false)
            val address = inputAddress.getValue(false)
            when {
                mobile.isBlank() || mobile.length < 11 || mobile.take(2) != "09" -> {
                    showError(
                        inputMobile.getLayout(),
                        getString(R.string.error_input_mobile_number_is_not_valid)
                    )
                }
                phone.isBlank() || phone.length < 10 || phone.take(1) != "0" -> {
                    showError(
                        inputPhone.getLayout(),
                        getString(R.string.error_input_phone_number_is_not_valid)
                    )
                }
                address.isBlank() -> {
                    showError(inputAddress.getLayout(), getString(R.string.error_enter_address))
                }
                imagesList.size < getNeededImagesUpload(relationType).size -> {
                    tvDescError.text = getString(R.string.error_need_upload_document)
                    tvDescError.isVisible = true
                    imgDocError.isVisible = true
                }
                else -> {
                    return true
                }
            }
        }
        return false
    }

    private fun getRelationType(
        tendencyCode: String,
        genderCode: String,
        age: Int,
    ): EnumImageTypeSurvivor {
        return when (tendencyCode) {
            "106", "110" -> {
                relationCode = "06"
                IS_PARENT
            }
            "101", "104" -> {
                relationCode = "03"
                if (age >= 19)
                    IS_SON_OLDER_THEN_18
                else
                    IS_KID
            }
            "102", "105" -> {
                relationCode = "04"
                if (age >= 16)
                    IS_DAUGHTER_OLDER_THEN_15
                else
                    IS_KID
            }
            "111", "112", "117", "133", "118", "123" -> {
                when (genderCode) {
                    "01" -> {
                        relationCode = "03"
                        if (age >= 19)
                            IS_SON_OLDER_THEN_18
                        else
                            IS_KID
                    }
                    "02" -> {
                        relationCode = "04"
                        if (age >= 16)
                            IS_DAUGHTER_OLDER_THEN_15
                        else
                            IS_KID
                    }
                    else -> IS_KID
                }
            }
            "100", "103", "107", "108", "109" -> {
                relationCode = "02"
                IS_PARTNER
            }
            else -> {
                IS_UNKNOWN
            }
        }
    }

    private fun getNeededImagesUpload(imageType: EnumImageTypeSurvivor): ArrayList<MenuModel> {
        val imageTitleList: ArrayList<MenuModel> = ArrayList()
        imageTitleList.apply {
            add(
                MenuModel(
                    title = getString(R.string.first_page_identity_card),
                    id = FIRST_PAGE_IDENTITY_IMAGE_TYPE
                )
            )
            if (imageType != IS_KID)
                add(
                    MenuModel(
                        title = getString(R.string.partner_info_identity_card),
                        id = PARTNER_INFO_IDENTITY_IMAGE_TYPE
                    )
                )
        }
        when (imageType) {
            IS_SON_OLDER_THEN_18 -> {
                imageTitleList.add(
                    MenuModel(
                        title = getString(R.string.inquiry_study_certificate_page),
                        id = STUDY_CERTIFICATE_IMAGE_TYPE
                    )
                )
            }
            IS_PARTNER -> {
                imageTitleList.apply {
                    add(
                        MenuModel(
                            title = getString(R.string.wife_info_marriage_certificate),
                            id = WIFE_INFO_MARRIAGE_CERTIFICATE_IMAGE_TYPE
                        )
                    )
                    add(
                        MenuModel(
                            title = getString(R.string.husband_info_marriage_certificate),
                            id = PARTNER_INFO_MARRIAGE_CERTIFICATE_IMAGE_TYPE
                        )
                    )
                    add(
                        MenuModel(
                            title = getString(R.string.marriage_type_certificate),
                            id = MARRIAGE_TYPE_IMAGE_TYPE
                        )
                    )
                }
            }
            else -> {}
        }
        return imageTitleList
    }

    private fun showError(
        textLayout: TextInputLayout? = null,
        strError: String,
        scroll: Boolean = true,
    ) {
        textLayout?.error = strError
        if (scroll)
            viewDataBinding?.apply {
                textLayout?.requestFocus()
                nestedScrollView.scrollTo(0, textLayout?.top ?: 0)
            }
    }

    private fun getSurvivorInfo(): SaveSurvivorInfoRequest? {
        viewDataBinding?.apply {
            return mViewModel.dataModel.getSaveSurvivorInfoReqModel(
                address = inputAddress.getValue(
                    false
                ),
                mobile = inputMobile.getValue(false),
                relationCode = relationCode,
                phone = inputPhone.getValue(false),
                imagesList = imagesList,
                survivorInfo = survivorInfo
            )
        }
        return null
    }

}