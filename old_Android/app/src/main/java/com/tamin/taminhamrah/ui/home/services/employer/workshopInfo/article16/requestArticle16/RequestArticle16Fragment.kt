package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.requestArticle16

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.ObjectionPhoto
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.RegisterArticle16Response
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkShopInfoDebtArticle16Response
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListModel
import com.tamin.taminhamrah.databinding.Article16DetailsStepBinding
import com.tamin.taminhamrah.databinding.Article16RegisterRequestStepBinding
import com.tamin.taminhamrah.databinding.Article16UploadDocumentStepBinding
import com.tamin.taminhamrah.databinding.Article16WorkshopInfoStepBinding
import com.tamin.taminhamrah.databinding.FragmentRequestArticle16Binding
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.DefinitiveDebtArticle16ViewModel
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model.FilterRequestTypeEnumClass
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody

@AndroidEntryPoint
class RequestArticle16Fragment :
    BaseFragment<FragmentRequestArticle16Binding, DefinitiveDebtArticle16ViewModel>(),
    StepperLayout.NextStepClickListener, StepperLayout.PreviousStepClickListener {

    //Class Variables
    override val mViewModel: DefinitiveDebtArticle16ViewModel by viewModels()
    private val adapterList by lazy { KeyValueAdapter() }
    val documentListAdapter: ImagePreviewAdapter by lazy { ImagePreviewAdapter(onItemDocumentClick) }

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.REQUEST_LUNCHER) {
                val imageUri = if (result.data != null)
                    Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI))
                else
                    null
                //when capture image from camera imageUri is null
                if (imageUri == null || !isDuplicateImage(imageUri,
                        mViewModel.dataModel.documentList)
                )
                    provideImageForUpload(mViewModel.dataModel.tempImageType, imageUri)
                else
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.error_select_repeat_pic))
            }
        }

    val onItemDocumentClick by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?,
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        handlePageDestination(
                            R.id.action_request_article16_to_imageView,
                            Bundle().apply {
                                putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                                putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                            }
                        )
                    }
                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.documentList.remove(item)
                        documentListAdapter.setItems(mViewModel.dataModel.documentList)
                        setVisibilityUploadDocStep()
                    }
                }
            }
        }
    }

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_request_article16

    override fun setupObserver() {
        mViewModel.mldWorkshopInfo.observe(this, ::onWorkshopInfoResponse)
        mViewModel.mldUploadImage.observe(this, ::onUploadDocumentResponse)
        mViewModel.mldRegisterRequest.observe(this, ::onRequestRegisterResponse)
    }

    override fun initView() {}

    override fun getData() {
        mViewModel.dataModel.apply {
            workshopName = arguments?.getString(Constants.WORKSHOP_NAME) ?: ""
            workshopId = arguments?.getString(Constants.WORKSHOP_ID) ?: ""
            branchCode = arguments?.getString(Constants.BRANCH_ID) ?: ""
            debtNumber = arguments?.getString(Constants.DEBIT_NUMBER) ?: ""
            statusCode = arguments?.getString(Constants.STATUS_CODE) ?: ""
            detailDebtInfo =
                arguments?.getParcelable(Constants.DATA_CLASS) as? WorkshopsDebtListModel
        }

        viewDataBinding?.tvWorkshopName?.text =
            getString(R.string.accountable_debts, mViewModel.dataModel.workshopName)

        mViewModel.getWorkshopInfoDebtArticle16(workshopId = mViewModel.dataModel.workshopId,
            branchId = mViewModel.dataModel.branchCode)
    }

    override fun onClick() {
        viewDataBinding?.apply {
            imageBack.setOnClickListener {
                backButtonPress()
            }
        }
    }

    override fun chooseImage(requestCode: Int) {
        val intent = Intent(activity, MultiCustomGalleryUI::class.java)
        intent.putExtra(Constants.TEMPID, mViewModel.dataModel.tempImageType)
        resultImageLaunch.launch(intent)
    }

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int,
    ) {
        mViewModel.apply {
            uploadImage(body)
            mViewModel.dataModel.apply {
                tempImageOriginalUri = orgPath
                tempImageUri = imageUri
            }
        }
    }

    //Listeners
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.layoutStepper?.nextStep()
        when (stepIndex) {
            4 -> {
                mViewModel.dataModel.imageList.clear()
                mViewModel.dataModel.documentList.forEach {
                    mViewModel.dataModel.imageList.add(
                        ObjectionPhoto(
                            guid = it.guid,
                            type = it.imageType
                        )
                    )
                }
                mViewModel.dataModel.getRequestRegisterModel()?.let {
                    mViewModel.registrationRequestArticle16(it)
                    // Log.i( "onNextStepClick",it.toString())
                } ?: showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_general))
            }
        }
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.layoutStepper?.previousStep()
    }

    private fun onWorkshopInfoResponse(result: WorkShopInfoDebtArticle16Response) {
        if (result.isSuccess) {
            result.data?.let {
                mViewModel.dataModel.initialInfo(it)
                if (mViewModel.dataModel.statusCode == FilterRequestTypeEnumClass.DOC_VIOLATION_STATE.id)
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.WARNING,
                        getText(R.string.re_requesting_review_article16).toString())
            } ?: showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_recive_data),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)

            initStepper()
        }
    }


    private fun onUploadDocumentResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.loadImageInfo(result.guid)
            documentListAdapter.setItems(mViewModel.dataModel.documentList)
            setVisibilityUploadDocStep()
        }
    }


    private fun onRequestRegisterResponse(result: RegisterArticle16Response) {
        if (result.isSuccess) {
            val desc = getString(R.string.register_request_investigation_success,
                UiUtils.createTextColorGreenAndBold(result.data?.refId))
            showAlertDialog(MessageOfRequestDialogFragment.MessageType.SUCCESS,
                desc,
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
        }
    }

    //step1
    private fun workshopInfoStep() =
        Article16WorkshopInfoStepBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.layoutStepper, true).apply {
            tvValueEmployer.text = mViewModel.dataModel.employeeName
            tvValueWorkShopName.text = mViewModel.dataModel.workshopName
            tvValueWorkShopCode.text = mViewModel.dataModel.workshopId
            tvValueWorkshopAddress.text = mViewModel.dataModel.workshopAddress
        }

    //step2
    private fun debtDetailInfoStep() =  Article16DetailsStepBinding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.layoutStepper, true).apply {
        mViewModel.dataModel.detailDebtInfo?.let { data ->
            adapterList.setItems(data.getDetailInfoInvestigationPage())
        } ?: showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.error_recive_data),
            MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)

        itemDescDebtCertainty.descTxt.text = getString(R.string.debt_certainty)
        itemDescRecognitionClaims.descTxt.text = getString(R.string.recognition_claims)
        itemDescAppealProvince.descTxt.text = getString(R.string.appeal_province)
        itemDescAppealHeadquartersCenter.descTxt.text = getString(R.string.appeal_headquarters)
        rcvInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterList
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }

        cbArticle42.tvTitle.text = getString(R.string.article_42)
        cbArticle43.tvTitle.text = getString(R.string.article_43)
        cbArticle44.tvTitle.text = getString(R.string.article_44)

        cbArticle42.isActive = false
        cbArticle43.isActive = false
        cbArticle44.isActive = false

        mViewModel.dataModel.detailDebtInfo?.apply {
            tvValueVoteNumberClaims.text = primaryDebtNumber ?: "-"
            tvValueVoteDateClaims.text = Utility.getDateSeparator(primaryDebtDate)
            tvValueVoteNumberAppealProvince.text = renewalNumber ?: "-"
            tvValueVoteDateAppealProvince.text = Utility.getDateSeparator(renewalDate)
            tvValueVoteNumberAppealHeadquartersCenter.text = renewalNumber ?: "-"
            tvValueVoteDateAppealHeadquartersCenter.text = Utility.getDateSeparator(renewalDate)

            when (kindDoc) {
                "1" -> {
                    cbArticle42.isActive = true
                }
                "2" -> {
                    cbArticle43.isActive = true
                }
                "3" -> {
                    cbArticle44.isActive = true
                }
            }
        }


    }

    //step3
    private fun uploadDocStep() =
        Article16UploadDocumentStepBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.layoutStepper, true).apply {
            val binding = this
            recycler.adapter = documentListAdapter
            if (recycler.itemDecorationCount == 0)
                recycler.addItemDecoration(UiUtils.VerticalItemSetMarginDecoration(topMargin = 10,
                    bottomMargin = 10))

            selectDocType.getIt().setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    mViewModel.getDocumentTitleList().collectLatest { pagingData ->
                        val result = pagingData.map {
                            MenuModel(title = it.name, id = it.value)
                        }
                        if (mViewModel.dataModel.documentList.isNotEmpty()) {
                            DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
                                arguments = createBundle(
                                    MessageOfRequestDialogFragment.MessageType.WARNING,
                                    this@RequestArticle16Fragment.getString(R.string.warning_upload_desc),
                                    btnCancel = true)

                                setDialogClickListener(object :
                                    DialogClickInterface.onClickListener {
                                    override fun onConfirmClick() {
                                        showUploadDialog(binding, result)
                                    }

                                    override fun onCancelClick() {}
                                })
                            }.show(childFragmentManager, "Alert Dialog upload")
                        } else {
                            showUploadDialog(binding, result)
                        }
                    }
                }
            }
            itemAddDoc.root.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                chooseImage()
            }
        }

    private fun showUploadDialog(
        stepBinding:  Article16UploadDocumentStepBinding,
        result: PagingData<MenuModel>,
    ) {
        openImageTypeMenu(
            itemList = result,
            isSearchEnable = true,
            onResultCallBack = object : MenuInterface.OnResult {
                override fun onResult(itemResult: MenuModel) {
                    setVisibilityUploadDocStep(isShowAddItem = false)
                    itemResult.title?.let { title ->
                        stepBinding.selectDocType.setValue(title)
                    }
                    mViewModel.dataModel.apply {
                        if (tempImageType != itemResult.id) {
                            documentList.clear()
                            documentListAdapter.setItems(documentList)
                            stepBinding.stepperUploadDoc.nextStepEnable=false
                        }
                        tempImageType = itemResult.id.toString()
                        tempImageName = itemResult.title ?: ""
                        tempImageUri = null
                    }
                }
            })
    }

    //step4
    private fun registerRequest() =
        Article16RegisterRequestStepBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.layoutStepper, true).apply {
            itemDescRequest.descTxt.text = getText(R.string.register_request_investigation_desc)
            tvRules.text = getString(R.string.register_request_investigation_confirm_desc)

            cbConfirmRules.setOnCheckedChangeListener { _, isChecked ->
                this.stepperRegistration.nextStepEnable = isChecked
            }
            stepperRegistration.nextStepEnable = false //test
        }

    //Utils
    private fun initStepper() {
        viewDataBinding?.layoutStepper?.apply {
            initial(listOf(workshopInfoStep(),
                debtDetailInfoStep(),
                uploadDocStep(),
                registerRequest()))
            onNextStepClickListener = this@RequestArticle16Fragment
            onPreviousStepClickListener = this@RequestArticle16Fragment
        }
    }

    private fun setVisibilityUploadDocStep(isShowAddItem: Boolean = true) {
        ((viewDataBinding?.layoutStepper?.getStepLayoutBindingByStep(3)) as? Article16UploadDocumentStepBinding)?.apply {
            mViewModel.dataModel.documentList.also { list ->
                stepperUploadDoc.nextStepEnable = (list.size >= 1)
                when {
                    (list.size < 10) -> {
                        itemAddDoc.root.visibility = View.VISIBLE
                    }
                    !isShowAddItem || list.size >= 10 -> {
                        itemAddDoc.root.visibility = View.GONE
                    }

                }
            }
        }
    }
}