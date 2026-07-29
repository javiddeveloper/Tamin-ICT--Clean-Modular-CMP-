package com.tamin.taminhamrah.ui.home.services.pensionSurvivor

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.SURVIVOR_ITEM
import com.tamin.taminhamrah.Constants.SURVIVOR_REQUEST_KEY
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.ConfirmSurvivorListResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.DeceasedInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SurvivorModel
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SurvivorResponse
import com.tamin.taminhamrah.databinding.FragmentPensionSurvivorBinding
import com.tamin.taminhamrah.databinding.StepSurvivorDeceasedBinding
import com.tamin.taminhamrah.databinding.StepSurvivorFinalConfirmBinding
import com.tamin.taminhamrah.databinding.StepSurvivorInfoBinding
import com.tamin.taminhamrah.databinding.StepSurvivorRulesBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.adapter.SurvivorAdapter
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody

@AndroidEntryPoint
class PensionSurvivorFragment :
    BaseFragment<FragmentPensionSurvivorBinding, PensionSurvivorViewModel>(),
    StepperLayout.NextStepClickListener, StepperLayout.PreviousStepClickListener {
    //Class Variables
    override val mViewModel: PensionSurvivorViewModel by viewModels()
    private val deceasedInfoAdapter by lazy {
        ExpandableListAdapter(expandingIndex = 3)
    }
    private val onSurvivorItemClick by lazy {
        object : AdapterInterface.OnItemClickListener<SurvivorModel> {
            override fun onItemClick(item: SurvivorModel, transitionView: View?, tag: String?) {
                if (mViewModel.dataModel.userNationalId == item.userInfo.personal?.nationalId) {
                    item.userInfo.dependentDocumentList.clear()
                    item.userInfo.dependentDocumentList.addAll(mViewModel.dataModel.deceaseDocumentList)
                }
                handlePageDestination(R.id.action_survivor_to_dependent_info,
                    bundle = Bundle().apply { putParcelable(SURVIVOR_ITEM, item) })
            }
        }
    }
    private val survivorAdapter by lazy {
        SurvivorAdapter(onSurvivorItemClick)
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
                            R.id.action_pension_survivor_to_image,
                            Bundle().apply {
                                putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                                putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                            }
                        )
                    }
                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.deceaseDocumentList
                            .apply {
                                remove(item)
                                documentListAdapter.setItems(this)
                            }
                        checkFileUploadedSize()
                    }
                }
            }
        }
    }
    val documentListAdapter: ImagePreviewAdapter by lazy { ImagePreviewAdapter(onItemDocumentClick) }
    val resultImageLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.REQUEST_LUNCHER) {
                val imageUri = if (result.data != null)
                    Uri.parse(result?.data?.extras?.getString(Constants.IMAGE_URI))
                else
                    null
                //capture image from camera imageUri is null
                if (imageUri == null || !isDuplicateImage(imageUri,
                        mViewModel.dataModel.deceaseDocumentList)
                )
                    provideImageForUpload(mViewModel.dataModel.tempImageType, imageUri)
                else
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.error_select_repeat_pic))
            }
        }

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_pension_survivor

    override fun setupObserver() {
        mViewModel.mldRegistrationInfo.observe(this, ::onRegistrationUserInfoResponse)
        mViewModel.mldDeceasedInfo.observe(this, ::onDeceasedInfoResponse)
        mViewModel.mldUploadImage.observe(this, ::onUploadImageResponse)
        mViewModel.mldSurvivorList.observe(this, ::onSurvivorsResponse)
        mViewModel.mldConfirmSurvivorList.observe(this, ::onConfirmSurvivorsListResponse)
        mViewModel.mldPDF.observe(this, ::onPdfResponse)
        mViewModel.mldFinalConfirm.observe(this, ::onSubmitFinalResponse)
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupToolbar(appBar = appbar,
                appbarBackgroundImage = appbarBackgroundImage.imageBackground,
                moreViews = null)

            setFragmentResultListener(SURVIVOR_REQUEST_KEY) { _, bundle ->
                val result = bundle.getParcelable(SURVIVOR_ITEM) as? SurvivorModel
                if (result != null) {
                    mViewModel.dataModel.survivorList.forEach { survivorModel ->
                        if (survivorModel.userInfo.personal?.nationalId == result.userInfo.personal?.nationalId) {
                            survivorModel.userInfo.personal = result.userInfo.personal
                            survivorModel.userInfo.isCompleted = true
                        }
                    }
                    survivorAdapter.notifyItemRangeChanged(0, mViewModel.dataModel.survivorList.size)
                } else
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR, getString(R.string.error_save_info))
            }
        }
    }

    override fun getData() {
        mViewModel.getRegistrationUserInfo()
    }

    override fun onClick() {
    }

    override fun chooseImage(requestCode: Int) {
        showDialog(title = getString(R.string.decease_document),
            callBack = object : MenuInterface.OnResult {
                override fun onResult(itemResult: MenuModel) {
                    mViewModel.dataModel.tempImageType = itemResult.id.toString()
                    mViewModel.dataModel.tempImageName = itemResult.title ?: ""
                    val intent = Intent(activity, MultiCustomGalleryUI::class.java)
                    intent.putExtra(Constants.TEMPID, itemResult.id)
                    resultImageLaunch.launch(intent)
                }
            })
    }
    //Listeners
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepperLayout?.apply {
            when (stepIndex) {
                2 -> {
                    view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                    (getStepLayoutBindingByStep(2) as? StepSurvivorDeceasedBinding)?.apply {
                        if (deceasedStepper.nextButtonTitle==getString(R.string.inquiry_info))
                        deceasedInfoAdapter.setItems(emptyList())
                        if (cbConfirmHistory.isChecked && mViewModel.dataModel.deceaseDocumentList.size == 3) {
                            if (mViewModel.dataModel.survivorList.isEmpty())
                                mViewModel.getSurvivorList(edNationalIdDeceased.getValueNationalCode())
                            else
                                nextStep()
                        } else {
                            if (edNationalIdDeceased.getValueNationalCode(false).isBlank()
                                || edNationalIdDeceased.getLayout().isErrorEnabled
                            ) {
                                edNationalIdDeceased.getLayout().error =
                                    getString(R.string.error_not_valid_national_code)
                            } else {
                                mViewModel.dataModel.deceasedNationalId =
                                    edNationalIdDeceased.getValueNationalCode()
                                mViewModel.getDeceasedInfo(mViewModel.dataModel.deceasedNationalId)
                            }
                        }

                    }
                }
                3 -> {
                    if (mViewModel.dataModel.requestId == 0)
                        mViewModel.confirmSurvivorsList()
                    else
                        nextStep()
                }
                4 -> {
                    mViewModel.submitFinalSurvivorPension(requestId = mViewModel.dataModel.requestId)
                }
                else -> {
                    nextStep()
                }
            }
        }
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepperLayout?.previousStep()
    }

    private fun onRegistrationUserInfoResponse(result: IdentityInfoResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.apply {
                userNationalId = result.data?.nationalId ?: ""
                genderDesc = if (result.data?.gender == "01") getString(R.string.gentleman) else getString(
                        R.string.lady)
                userName = "${result.data?.firstName} ${result.data?.lastName}"
            }
            initialStepper()
        }
    }

    private fun onDeceasedInfoResponse(result: DeceasedInfoResponse) {
        if (result.isSuccess) {
            if (result.data.deadDate == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.INFO,
                    getString(R.string.error_no_information_death_deceased)
                )
                return
            } else {
                mViewModel.dataModel.deceaseDocumentList.clear()
                documentListAdapter.setItems(mViewModel.dataModel.deceaseDocumentList)
                mViewModel.dataModel.branchCode = result.data.branchCode ?: "_"
                mViewModel.dataModel.deceasedInsuranceId = result.data.insuranceId ?: "_"
                mViewModel.dataModel.deceasedInfoList.clear()
                mViewModel.dataModel.deceasedInfoList.addAll(result.data.getDeceasedInfo())
                deceasedInfoAdapter.setItems(mViewModel.dataModel.deceasedInfoList)
                (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(2) as? StepSurvivorDeceasedBinding)?.apply {
                    setVisibilityDeceased(this)
                }
            }
        }
    }

    private fun onUploadImageResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.apply {
                loadImageInfo(guid = result.guid, list = deceaseDocumentList)
                documentListAdapter.setItems(deceaseDocumentList)
                checkFileUploadedSize()
            }
        }
    }

    private fun onSurvivorsResponse(result: SurvivorResponse) {
        if (result.isSuccess) {
            if (!result.data?.list.isNullOrEmpty()) {
                mViewModel.dataModel.survivorList.clear()
                mViewModel.dataModel.survivorList.addAll(result.data?.list ?: emptyList())
                mViewModel.dataModel.survivorList.forEach { item ->
                    val res = Utility.getTendencyResId(tendencyCode = item.userInfo.relation.tendency?.tendencyCode
                            ?: "0", genderCode = item.userInfo.personal?.gender?.genderCode ?: "0")
                    item.userInfo.personal?.apply {
                        relationShip = if (res != 0) getString(res) else "_"
                        age = item.userInfo.personal?.dateOfBirth?.let { Utility.getAge(birthDate = it) } ?:0
                        branchCode = mViewModel.dataModel.branchCode
                        deceasedInsuranceId = mViewModel.dataModel.deceasedInsuranceId
                        deceasedNationalID = mViewModel.dataModel.deceasedNationalId
                    }
                }
                survivorAdapter.setItems(mViewModel.dataModel.survivorList)
                viewDataBinding?.stepperLayout?.nextStep()
            } else {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data))
            }
        }
    }

    private fun onConfirmSurvivorsListResponse(result: ConfirmSurvivorListResponse) {
        if (result.isSuccess) {
            if (!result.data?.list.isNullOrEmpty()) {
                mViewModel.dataModel.requestId = result.data?.list?.get(0)?.request?.id ?: 0
                viewDataBinding?.apply {
                    stepperLayout.nextStep()
                }
            } else
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.INFO,
                    getString(R.string.error_not_exist_survivor_info))
        }
    }

    private fun onPdfResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val title = Utility.getToolbarTitle(arguments)
            val file = Utility.writeByteStreamToDisk(title, requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file))
                return
            } else {
                handlePageDestination(R.id.action_pension_survivor_to_pdf_viewer, Bundle().apply {
                    putString(PdfViewerActivity.ARG_TITLE, title)
                    putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
                })
            }
        }
    }

    private fun onSubmitFinalResponse(result: GeneralRes) {
        if (result.isSuccess) {
            showAlertDialog(MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.request_has_been_successfully_submitted),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
        }
    }

    //Utils
    private fun initialStepper(initialStep: Int = 1) {
        viewDataBinding?.stepperLayout?.apply {
            initial(arrayListOf<ViewBinding>(
                initialRulesStep(),
                initialDeceasedStep(),
                initialSurvivorInfoStep(),
                initialFinalConfirmStep()), initialStep)
            onNextStepClickListener = this@PensionSurvivorFragment
            onPreviousStepClickListener = this@PensionSurvivorFragment
        }
    }

    //step1
    private fun initialRulesStep() =
        StepSurvivorRulesBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true).apply {
            tvDescRules.descTxt.text = getString(R.string.desc_view_rules_pension_survivor)

            val str = getString(R.string.desc_rules_pension_survivor, mViewModel.dataModel.genderDesc,
                    UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.userName))
            tvDescCommitment.text = HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)

            cbConfirmRulesSurvivor.setOnCheckedChangeListener { _, isChecked ->
                rulesStepper.nextStepEnable = isChecked
                mViewModel.dataModel.isConfirmRules = isChecked
            }
            cbConfirmRulesSurvivor.isChecked = mViewModel.dataModel.isConfirmRules

            btnShowRules.setOnClickListener {
                handlePageDestination(R.id.action_pension_survivor_to_pdf,
                    Bundle().apply {
                        putString(Constants.TOOLBAR_TITLE, getString(R.string.rules_title))
                        putString(PdfViewerActivity.ARG_PDF_FILE_ASSET,
                            "rulesAndRegulationsHtmlFile/rule_pension_survivor.pdf")
                    })
            }
        }

    //step2
    private fun initialDeceasedStep() =
        StepSurvivorDeceasedBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout, true).apply {
            deceasedInfoRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = deceasedInfoAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(requireContext()))
            }
            docRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = documentListAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.VerticalItemSetMarginDecoration(topMargin = 10, bottomMargin = 10))
            }
            btnModifyNationalId.setOnClickListener {
                btnModifyNationalId.isVisible=false
                edNationalIdDeceased.getInput().apply {
                    isClickable = true
                    isEnabled = true
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black))
                    setText("")
                }
                groupDetailInfo.isVisible=false
            }
            tvDescUpload.descTxt.apply {
                text = getString(R.string.upload_all_deceased_doc)
                setTextColor(ContextCompat.getColor(context, R.color.text_color_dialog_blue))
            }
            btnShowDetailDeceased.setOnClickListener {
                deceasedInfoAdapter.toggleMinifyMode()
                btnShowDetailDeceased.text = if (deceasedInfoAdapter.isMinifyMode())
                    getString(R.string.show_detail)
                else
                    getString(R.string.hide_detail)
            }

            edNationalIdDeceased.getInput().doAfterTextChanged {
                edNationalIdDeceased.getLayout().isErrorEnabled = false
                cbConfirmHistory.isVisible = false
                tvDescCommitment.isVisible = false
                deceasedStepper.nextStepEnable = true
                deceasedStepper.nextButtonTitle = getString(R.string.inquiry_info)
            }

            itemAddDoc.root.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                chooseImage()
            }

            cbConfirmHistory.setOnCheckedChangeListener { _, isChecked ->
                deceasedStepper.nextStepEnable = isChecked
                mViewModel.dataModel.isConfirmDeceasedRules = isChecked
            }
            cbConfirmHistory.isChecked = mViewModel.dataModel.isConfirmDeceasedRules

            if (mViewModel.dataModel.deceaseDocumentList.size != 0) {
                documentListAdapter.setItems(mViewModel.dataModel.deceaseDocumentList)
                if (mViewModel.dataModel.deceaseDocumentList.size == 3) {
                    cbConfirmHistory.isVisible = mViewModel.dataModel.isConfirmDeceasedRules
                    tvDescCommitment.text = getText(R.string.desc_confirm_history_deceased)
                    tvDescCommitment.isVisible = true
                    cbConfirmHistory.isChecked = mViewModel.dataModel.isConfirmDeceasedRules
                    deceasedStepper.nextStepEnable = mViewModel.dataModel.isConfirmDeceasedRules
                }
            }
            if (mViewModel.dataModel.deceasedInfoList.size != 0) {
                deceasedInfoAdapter.setItems(mViewModel.dataModel.deceasedInfoList)
                setVisibilityDeceased(this)
            }
            if (mViewModel.dataModel.deceasedNationalId.isNotBlank())
                edNationalIdDeceased.setTextWidget(mViewModel.dataModel.deceasedNationalId)
        }

    //step3
    private fun initialSurvivorInfoStep() =
        StepSurvivorInfoBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout, true).apply {
            recyclerInfo.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = survivorAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.VerticalItemSetMarginDecoration(topMargin = 10, bottomMargin = 10))
            }
            if (mViewModel.dataModel.survivorList.size != 0)
                survivorAdapter.setItems(mViewModel.dataModel.survivorList)
        }

    //step4
    private fun initialFinalConfirmStep() =
        StepSurvivorFinalConfirmBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout, true).apply {
            val str = getString(R.string.desc_final_confirm_pension_survivor,
                UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.userName),
                UiUtils.createTextColorOrangeAndBold(getString(R.string.nine_months_after_death)),
                UiUtils.createTextColorGreenAndBold(getString(R.string.inheritance_certificate)))
            tvDescCommitment.text = HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
            cbConfirmRules.setOnCheckedChangeListener { _, isChecked ->
                finalConfirmStepper.nextStepEnable = isChecked
            }
            btnShowPdf.setOnClickListener {
                if (mViewModel.dataModel.requestId != 0)
                    mViewModel.getFinalSurvivorPensionPDF()
                else
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_receive_pdf_file))
            }

        }

    private fun checkFileUploadedSize() {
        mViewModel.dataModel.apply {
            (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(2) as?
                    StepSurvivorDeceasedBinding)?.apply {
                val isShow = deceaseDocumentList.size == 3
                cbConfirmHistory.isVisible = isShow
                tvDescCommitment.isVisible = isShow
                itemAddDoc.root.isVisible = !isShow
                if (!isShow) {
                    deceasedStepper.nextStepEnable = false
                    cbConfirmHistory.isChecked = false
                }
            }
        }
    }

    private fun setVisibilityDeceased(
        binding: StepSurvivorDeceasedBinding,
    ) {
        binding.apply {
            edNationalIdDeceased.getInput().apply {
                isClickable = false
                isEnabled = false
                backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.lineColor))
            }
            groupDetailInfo.isVisible = true
            btnModifyNationalId.isVisible=true
            deceasedStepper.nextStepEnable = false
            deceasedStepper.nextButtonTitle = getString(R.string.label_confirm)
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

    private fun showDialog(
        title: String,
        callBack: MenuInterface.OnResult,
    ) {
        MenuDialogFragment().apply {
            val bundle = Bundle()
            bundle.putString(MenuDialogFragment.ARG_MENU_TITLE, title)
            arguments = bundle
            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                            pagingSourceFactory = { LocalPagingSource(mViewModel.dataModel.deceasedDocsTitle) }).flow.cachedIn(
                            lifecycleScope)
                            .collectLatest { pagingData -> updateData(pagingData) }
                    }
                }
            }, callBack)

        }.show(childFragmentManager, "PensionSurvivorFragment")
    }

}