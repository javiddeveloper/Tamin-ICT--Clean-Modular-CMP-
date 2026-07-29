package com.tamin.taminhamrah.ui.home.services.retirementRequest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.RETIREMENT_AWAITING_ISSUANCE_EDICT_STATUS
import com.tamin.taminhamrah.Constants.RETIREMENT_HISTORY_CHECK_BY_BRANCH_STATUS
import com.tamin.taminhamrah.Constants.RETIREMENT_IDENTITY_INFO_CONFIRM_STATUS
import com.tamin.taminhamrah.Constants.RETIREMENT_ISSUANCE_EDICT_STATUS
import com.tamin.taminhamrah.Constants.RETIREMENT_RESIGNATION_CHECK_BY_BRANCH_STATUS
import com.tamin.taminhamrah.Constants.RETIREMENT_UPLOAD_DOC_STATUS
import com.tamin.taminhamrah.Constants.RETIREMENT_UPLOAD_RESIGNATION_STATUS
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.AuthenticationResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementConfirmIdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementPersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementRequestInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementStatusResponse
import com.tamin.taminhamrah.databinding.FragmentRetirementPensionBinding
import com.tamin.taminhamrah.databinding.StepRetirementAuthenticationBinding
import com.tamin.taminhamrah.databinding.StepRetirementBranchInfoBinding
import com.tamin.taminhamrah.databinding.StepRetirementFinalConfirmBinding
import com.tamin.taminhamrah.databinding.StepRetirementHistoryBinding
import com.tamin.taminhamrah.databinding.StepRetirementIdentityInfoBinding
import com.tamin.taminhamrah.databinding.StepRetirementIssuanceEdictBinding
import com.tamin.taminhamrah.databinding.StepRetirementRulesBinding
import com.tamin.taminhamrah.databinding.StepRetirementUploadDocBinding
import com.tamin.taminhamrah.databinding.StepRetirementUploadResignationLetterBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.home.services.retirementRequest.dialog.RetirementStatusDialogFragment
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.EnumRequestState
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.EnumRequestState.IS_CURRENT
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.EnumRequestState.IS_NOT_PASSED
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.EnumRequestState.IS_PASSED
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.EnumRetirementRequestState
import com.tamin.taminhamrah.ui.home.services.studentContract.cancelContract.CancelContractFragment
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.isNumericString
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import timber.log.Timber

@AndroidEntryPoint
class RetirementPensionFragment :
    BaseFragment<FragmentRetirementPensionBinding, RetirementPensionViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    //Class Variable
    override val mViewModel: RetirementPensionViewModel by viewModels()

    companion object {
        const val IMAGE_DOC_REQUEST_CODE = 1001
        const val IMAGE_RESIGNATION_REQUEST_CODE = 1002
    }

    private val identityAdapterInfo by lazy { ExpandableListAdapter(expandingIndex = 3) }
    private val branchInfoAdapter by lazy { KeyValueAdapter() }
    val documentListAdapter: ImagePreviewAdapter by lazy { ImagePreviewAdapter(onItemDocumentClick) }
    val resignationDocAdapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(onItemResignationClick)
    }
    val identityRequestInfoAdapter by lazy { KeyValueAdapter() }
    val workshopRequestInfoAdapter by lazy { KeyValueAdapter() }

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val imageUri = if (result.data != null)
                Uri.parse(result?.data?.extras?.getString(Constants.IMAGE_URI))
            else
                null
            //capture image from camera imageUri is null
            val list = arrayListOf<UploadedImageModel>()
            when (result.resultCode) {
                IMAGE_DOC_REQUEST_CODE -> {
                    list.clear()
                    list.addAll(mViewModel.dataModel.documentList)
                }

                IMAGE_RESIGNATION_REQUEST_CODE -> {
                    list.clear()
                    list.addAll(mViewModel.dataModel.resignationDocList)
                }

                else -> {
                    emptyList<UploadedImageModel>()
                }
            }
            if (imageUri == null || !isDuplicateImage(imageUri, list))
                provideImageForUpload(
                    mViewModel.dataModel.tempImageType,
                    imageUri,
                    result.resultCode
                )
            else
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    getString(R.string.error_select_repeat_pic)
                )
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
                            R.id.action_retirement_to_image_activity,
                            Bundle().apply {
                                putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                                putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                            }
                        )
                    }

                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.documentList.apply {
                            remove(item)
                            documentListAdapter.setItems(this)
                        }
                        checkFileUploadedSize()
                    }
                }
            }
        }
    }

    val onItemResignationClick by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?,
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        handlePageDestination(
                            R.id.action_retirement_to_image_activity,
                            Bundle().apply {
                                putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                                putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                            }
                        )
                    }

                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.resignationDocList.apply {
                            remove(item)
                            resignationDocAdapter.setItems(this)
                        }
                    }
                }
            }
        }
    }

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_retirement_pension

    override fun setupObserver() {
        mViewModel.cldCheckRetirementStatus.observe(this, ::onCheckRetirementStatus)
        mViewModel.mldUserInfo.observe(this, ::onUserInfoResponse)
        mViewModel.mldWageAndHistory.observe(this, ::onWageAndHistoryResponse)
        mViewModel.mldAuthenticationCode.observe(this, ::onAuthenticationCodeResponse)
        mViewModel.mldAuthenticationAndGetPersonalInfo.observe(
            this,
            ::onAuthenticationAndPersonalInfoResponse
        )
        mViewModel.mldUploadImage.observe(this, ::onUploadImageResponse)
        mViewModel.mldRequestInfo.observe(this, ::onRequestInfoResponse)
        mViewModel.mldUploadResignation.observe(this, ::onUploadResignationResponse)
        mViewModel.mldConfirmIdentityAndHistory.observe(this, ::onConfirmIdentityInfoResponse)
        mViewModel.mldSendRetirementDocument.observe(this, ::onSendRetirementDocumentResponse)
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupToolbar(
                appBar = appbar,
                appbarBackgroundImage = appbarBackgroundImage.imageBackground, moreViews = null
            )

            identityInfoRecycler.apply {
                adapter = identityRequestInfoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(requireContext()))
            }

            workshopInfoRecycler.apply {
                adapter = workshopRequestInfoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(requireContext()))
            }

        }
    }

    override fun getData() {
        mViewModel.checkRetirementStatus()
    }

    override fun onClick() {
    }

    override fun chooseImage(requestCode: Int) {
        MenuDialogFragment().apply {
            arguments = Bundle().apply {
                putString(
                    MenuDialogFragment.ARG_MENU_TITLE,
                    this@RetirementPensionFragment.getString(R.string.select_document_type)
                )
            }
            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                            pagingSourceFactory = { LocalPagingSource(mViewModel.dataModel.documentsTitle) }).flow.cachedIn(
                            lifecycleScope
                        )
                            .collectLatest { pagingData -> updateData(pagingData) }
                    }
                }
            }, object : MenuInterface.OnResult {
                override fun onResult(itemResult: MenuModel) {
                    mViewModel.dataModel.tempImageType = itemResult.id.toString()
                    mViewModel.dataModel.tempImageName = itemResult.title ?: ""
                    val intent = Intent(activity, MultiCustomGalleryUI::class.java)
                    intent.putExtra(Constants.TEMPID, mViewModel.dataModel.tempImageType)
                    intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)
                    resultImageLaunch.launch(intent)
                }
            })

        }.show(childFragmentManager, "PensionSurvivorFragment")
    }

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int,
    ) {
        mViewModel.uploadImage(body, requestCode)
        mViewModel.dataModel.tempImageUri = imageUri
        mViewModel.dataModel.tempImageOriginalUri = orgPath
    }

    //Listeners
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepperLayout?.apply {
            when (stepIndex) {
                5 -> {
                    val authenticationCode = mViewModel.dataModel.authenticationsCode
                    val requestInfo = mViewModel.dataModel.identityInfoNeedVerified
                    if (mViewModel.dataModel.identityInfoIsConfirmed)
                        nextStep()
                    else {
                        if (requestInfo != null && authenticationCode != null)
                            mViewModel.confirmIdentityAndHistoryInfo(
                                authenticationsCode = authenticationCode,
                                body = requestInfo
                            )
                        else
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                getString(R.string.error_recive_data)
                            )
                    }
                }

                6 -> {
                    val list =
                        mViewModel.dataModel.getSavedDocumentRequestModel(IMAGE_DOC_REQUEST_CODE)
                    val requestId = mViewModel.dataModel.requestId
                    if (!list.pensionRequestDocList.isNullOrEmpty() && requestId != null)
                        mViewModel.sendRetirementDocument(requestId, list)
                    else
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_recive_data)
                        )
                }

                8 -> {
                    val list = mViewModel.dataModel.getSavedDocumentRequestModel(
                        IMAGE_RESIGNATION_REQUEST_CODE
                    )
                    val requestId = mViewModel.dataModel.requestId
                    if (!list.pensionRequestDocList.isNullOrEmpty() && requestId != null)
                        mViewModel.sendRetirementDocument(requestId, list)
                    else
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_recive_data)
                        )
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

    private fun onCheckRetirementStatus(result: RetirementStatusResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.requestId = result.data?.requestId
            when (result.data?.requestStatusCode) {
                null -> {
                    changeRequestState(
                        step = EnumRetirementRequestState.STEP_AUTHENTICATION,
                        IS_CURRENT
                    )
                    mViewModel.getUserInfoAndUserAge()
                }

                RETIREMENT_UPLOAD_DOC_STATUS -> {
                    showConfirmDialog(desc = getString(R.string.need_upload_identity_document),
                        callbacks = object : DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                changeRequestState(
                                    step = EnumRetirementRequestState.STEP_UPLOAD_RESIGNATION_DOC,
                                    IS_CURRENT
                                )
                                initialStepper(6)
                                mViewModel.dataModel.currentState = 6
                                mViewModel.getHistoryInfo()
                            }

                            override fun onCancelClick() {
                            }
                        }
                    )
                }

                RETIREMENT_IDENTITY_INFO_CONFIRM_STATUS -> {
                    changeRequestState(
                        step = EnumRetirementRequestState.STEP_CHECK_BRANCH,
                        status = IS_CURRENT
                    )
                    showStatusDialog(desc = getString(R.string.retirement_status_check_of_branch),
                        callbacks = object : DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                showRequestInfo()
                            }

                            override fun onCancelClick() {
                            }
                        })
                }

                RETIREMENT_UPLOAD_RESIGNATION_STATUS -> {
                    showConfirmDialog(desc = getString(R.string.need_upload_resignation_document),
                        callbacks = object : DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                changeRequestState(
                                    step = EnumRetirementRequestState.STEP_UPLOAD_RESIGNATION_DOC,
                                    IS_CURRENT
                                )
                                mViewModel.dataModel.currentState = 7
                                mViewModel.getHistoryInfo()
                            }

                            override fun onCancelClick() {
                            }
                        })
                }

                RETIREMENT_HISTORY_CHECK_BY_BRANCH_STATUS -> {
                    changeRequestState(
                        step = EnumRetirementRequestState.STEP_CHECK_HISTORY_BY_BRANCH,
                        status = IS_CURRENT
                    )
                    showStatusDialog(desc = getString(R.string.retirement_status_check_of_branch),
                        callbacks = object : DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                showRequestInfo()
                            }

                            override fun onCancelClick() {
                            }
                        })
                }

                RETIREMENT_RESIGNATION_CHECK_BY_BRANCH_STATUS -> {
                    changeRequestState(
                        step = EnumRetirementRequestState.STEP_RESIGNATION_DOC_CHECK_BY_BRANCH,
                        status = IS_CURRENT
                    )
                    showStatusDialog(desc = getString(R.string.retirement_status_check_of_branch),
                        callbacks = object : DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                showRequestInfo()
                            }

                            override fun onCancelClick() {
                            }
                        })
                }

                RETIREMENT_AWAITING_ISSUANCE_EDICT_STATUS -> {
                    changeRequestState(
                        step = EnumRetirementRequestState.STEP_ISSUANCE_EDICT,
                        status = IS_CURRENT
                    )
                    showStatusDialog(desc = getString(R.string.retirement_status_awaiting_issuance_edict_desc),
                        callbacks = object : DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                showRequestInfo()
                            }

                            override fun onCancelClick() {
                            }
                        })
                }

                RETIREMENT_ISSUANCE_EDICT_STATUS -> {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.INFO,
                        getString(R.string.issuance_edict_confirmed),
                        dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                    )

                    showConfirmDialog(desc = getString(R.string.issuance_edict_confirmed),
                        callbacks = object : DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                mViewModel.dataModel.currentState = 9
                                mViewModel.getHistoryInfo()
                            }

                            override fun onCancelClick() {
                            }
                        })
                }
            }
        }
    }

    private fun onUserInfoResponse(result: IdentityInfoResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.apply {
                if (yearsAge < 42) {
                    mViewModel.hideLoading()
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_user_age_pension_request),
                        dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                    )
                } else {
                    userNationalId = result.data?.nationalId ?: ""
                    genderDesc =
                        if (result.data?.gender == "01") getString(R.string.gentleman) else getString(
                            R.string.lady
                        )
                    userName = "${result.data?.firstName} ${result.data?.lastName}"
                    mViewModel.getHistoryInfo()
                }
            }
        }
    }

    private fun onWageAndHistoryResponse(result: WageAndHistoryResponse) {
        if (result.isSuccess) {
            viewDataBinding?.apply {
                val histories = result.data?.list ?: emptyList()
                if (histories.isNotEmpty()) {
                    val historyInfo = Utility.calculateDayAndWageOfHistory(
                        histories,
                        mViewModel.dataModel.sumHistoryDays
                    )
                    tvValueAvgSalary.text = historyInfo[Constants.AVERAGE_SALARY]
                    tvValueAmountPension.text = historyInfo[Constants.ELIGIBLE_AMOUNT]
                    grHistoryInfo.visibility = View.VISIBLE

                    initialStepper(mViewModel.dataModel.currentState)
                    initialHistoryInfo()
                } else {
                    mViewModel.hideLoading()
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_recive_data),
                        dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                    )
                }
            }
        }
    }

    private fun onAuthenticationCodeResponse(result: AuthenticationResponse) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.desc_authentication_success, result.data?.mobileNumber ?: "-")
            )
            (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(2) as? StepRetirementAuthenticationBinding)?.apply {
                btnReceiveCode.isEnabled = false
            }
            mViewModel.timerStart()
        }
    }

    private fun onAuthenticationAndPersonalInfoResponse(result: RetirementPersonalInfoResponse) {
        if (result.isSuccess) {
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

            if (result.data != null) {
                val resultCode = result.data.verificationResult
                if (resultCode != null && resultCode.contains("ticketNotFound")) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_not_valid_code)
                    )
                } else {
                    mViewModel.dataModel.identityInfo.apply {
                        clear()
                        addAll(result.data.getIdentityInfo())
                        identityAdapterInfo.setItems(this)
                    }
                    mViewModel.dataModel.branchInfo.apply {
                        clear()
                        addAll(result.data.getBranchInfo())
                        branchInfoAdapter.setItems(this)
                    }
                    mViewModel.dataModel.identityInfoNeedVerified = result.data.loadRequestInfo()
                    mViewModel.dataModel.identityInfoNeedVerified?.age = mViewModel.dataModel.strAge
                    //   mViewModel.dataModel.loadRequestInfo(result.data)
                    changeRequestState(EnumRetirementRequestState.STEP_AUTHENTICATION, IS_PASSED)
                    (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(2) as? StepRetirementAuthenticationBinding)
                        ?.authenticationStepper?.nextStepEnable = true
                    viewDataBinding?.stepperLayout?.nextStep()

                }
            } else {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data)
                )
            }
        }
    }

    private fun onUploadImageResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.apply {
                loadImageInfo(guid = result.guid)
                documentListAdapter.setItems(documentList)
                checkFileUploadedSize()
            }
        }
    }


    private fun onConfirmIdentityInfoResponse(result: RetirementConfirmIdentityInfoResponse) {
        if (result.isSuccess) {
            val requestId = result.data?.request?.id
            if (requestId != null) {
                mViewModel.dataModel.requestId = requestId.toString()
            }
            mViewModel.dataModel.identityInfoIsConfirmed = true
            viewDataBinding?.stepperLayout?.nextStep()
        }
    }

    private fun onSendRetirementDocumentResponse(result: GeneralRes) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.msg_send_info_and_check_by_branch),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
        }
    }

    private fun onUploadResignationResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.apply {
                loadImageInfo(guid = result.guid, requestId = IMAGE_RESIGNATION_REQUEST_CODE)
                resignationDocAdapter.setItems(mViewModel.dataModel.resignationDocList)
            }
        }
    }

    private fun onRequestInfoResponse(result: RetirementRequestInfoResponse) {
        if (result.isSuccess) {
            val list = result.data?.list ?: emptyList()
            if (list.isNotEmpty()) {
                mViewModel.dataModel.retirementRequestInfo.apply {
                    clear()
                    addAll(list)
                }

                showRequestInfo()
            }
        }
    }

    //Utils
    private fun initialStepper(initialStep: Int = 1) {
        viewDataBinding?.stepperLayout?.apply {
            initial(
                arrayListOf(
                    initRulesStep(),
                    initAuthentication(),
                    initialIdentityInfoStep(),
                    initialBranchInfoStep(),
                    initialHistoryStep(),
                    initialUploadDocumentStep(),
                    initialUploadResignationStep(),
                    initialFinalConfirmStep(),
                    initialStatusEdict()
                ), initialStep
            )
            onNextStepClickListener = this@RetirementPensionFragment
            onPreviousStepClickListener = this@RetirementPensionFragment
        }
    }

    //Step1
    private fun initRulesStep() =
        StepRetirementRulesBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true
        ).apply {
            tvDescRules.descTxt.text = getText(R.string.desc_view_rules_retirement_request)
            val str = getString(
                R.string.desc_rules, mViewModel.dataModel.genderDesc,
                UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.userName)
            )
            tvDescCommitment.text = HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
            cbConfirmRules.setOnCheckedChangeListener { _, isChecked ->
                rulesStepper.nextStepEnable = isChecked
                mViewModel.dataModel.isConfirmRules = isChecked
            }
            cbConfirmRules.isChecked = mViewModel.dataModel.isConfirmRules
            btnShowRules.setOnClickListener {
                handlePageDestination(R.id.action_retirement_pension_to_pdf_viewer,
                    Bundle().apply {
                        putString(Constants.TOOLBAR_TITLE, getString(R.string.rules_title))
                        putString(
                            PdfViewerActivity.ARG_PDF_FILE_ASSET,
                            "rulesAndRegulationsHtmlFile/rules_retirement.pdf"
                        )
                    })
            }
        }

    //Step2
    private fun initAuthentication() =
        StepRetirementAuthenticationBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true
        ).apply {
            tvDesc.descTxt.text = getString(R.string.desc_authentication_code)

            btnReceiveCode.setOnClickListener {
                mViewModel.getAuthenticationCode()
                btnReceiveCode.isEnabled = false
            }

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                mViewModel.timerStateFlow.collectLatest { timer ->
                    if (timer.displaySeconds.isNumericString() && timer.displaySeconds != timer.textWhenStopped) {
                        val minute = (timer.secondsRemaining ?: 0) / 60
                        val second = ((timer.secondsRemaining ?: 0)) % 60
                        if (minute == 0 && second == 0) {
                            btnReceiveCode.apply {
                                text = getString(R.string.receive_code)
                                isEnabled = true
                            }
                        } else {
                            btnReceiveCode.text = String.format("%02d:%02d ثانیه", minute, second)
                        }
                    } else {
                        btnReceiveCode.apply {
                            text = getString(R.string.receive_code)
                            isEnabled = true
                        }
                    }
                }
            }

            edAuthentication.getInput().doAfterTextChanged {
                var num = 0L
                if (it?.toString().isNumericString())
                    num = it.toString().toLong()
                when {
                    it?.length == 6 -> {
                        try {
                            //NOTE: We Can not Validate Input Type Is Number
                            mViewModel.dataModel.authenticationsCode = num
                            mViewModel.authenticationAndGetPersonalInfo(num)
                        } catch (e: Exception) {
                            Timber.e(e)
                        }

                    }

                    mViewModel.dataModel.authenticationsCode != num -> {
                        authenticationStepper.nextStepEnable = false
                    }
                }
            }
        }

    //Step3
    private fun initialIdentityInfoStep() = StepRetirementIdentityInfoBinding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepperLayout, true
    ).apply {
        recyclerInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = identityAdapterInfo
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }
        btnShowDetail.setOnClickListener {
            identityAdapterInfo.toggleMinifyMode()
            btnShowDetail.text = if (identityAdapterInfo.isMinifyMode())
                getString(R.string.show_detail)
            else
                getString(R.string.hide_detail)
        }
        edLandlinePhone.getInput().doAfterTextChanged {
            cbConfirmIdentityStep.isChecked = false
            if (edLandlinePhone.getValue(false).length == 11) {
                edLandlinePhone.getLayout().isErrorEnabled = false
                mViewModel.dataModel.identityInfoNeedVerified?.phoneNumber = it.toString()
            }
        }
        edAddress.getInput().doAfterTextChanged {
            cbConfirmIdentityStep.isChecked = false
            mViewModel.dataModel.identityInfoNeedVerified?.address = it.toString()
        }
        cbConfirmIdentityStep.setOnCheckedChangeListener { _, isChecked ->
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
            if (checkValidEnterStepIdentityInfo(this))
                identityStepper.nextStepEnable = isChecked
            else
                cbConfirmIdentityStep.isChecked = false
        }
    }

    //step4
    private fun initialBranchInfoStep() =
        StepRetirementBranchInfoBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true
        ).apply {

            recyclerInfo.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = branchInfoAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(requireContext()))
            }

            edWorkshopName.getInput().doAfterTextChanged {
                cbConfirm.isChecked = false
                edWorkshopName.getLayout().isErrorEnabled = false
                mViewModel.dataModel.identityInfoNeedVerified?.workshopName = it.toString()
            }
            edWorkshopAddress.getInput().doAfterTextChanged {
                cbConfirm.isChecked = false
                mViewModel.dataModel.identityInfoNeedVerified?.workshopAddress = it.toString()
            }

            edEmployerName.getInput().doAfterTextChanged {
                cbConfirm.isChecked = false
                mViewModel.dataModel.identityInfoNeedVerified?.managerName = it.toString()
            }

            edWorkShopActivityDesk.getInput().doAfterTextChanged {
                cbConfirm.isChecked = false
                mViewModel.dataModel.identityInfoNeedVerified?.activityType = it.toString()
            }

            edWorkshopCode.getInput().doAfterTextChanged {
                cbConfirm.isChecked = false
                mViewModel.dataModel.identityInfoNeedVerified?.workshopCode = it.toString()
            }
            cbConfirm.setOnCheckedChangeListener { _, isChecked ->
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                branchInfoStepper.nextStepEnable = isChecked
            }
        }

    //step5
    private fun initialHistoryStep() =
        StepRetirementHistoryBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true
        ).apply {
            cbConfirmHistoryStep.setOnCheckedChangeListener { _, isChecked ->
                historyStepper.nextStepEnable = isChecked
            }
            btnObjection.setOnClickListener {
                handlePageDestination(R.id.action_retirement_to_objectionInsuranceHistoryFragment,
                    Bundle().apply {
                        putString(
                            Constants.TOOLBAR_TITLE,
                            getString(R.string.objection_non_existent_histories)
                        )
                        putString(Constants.TOOLBAR_ICON_IMAGE, mViewModel.getImageUrl())
                    })
            }
        }

    //step6
    private fun initialUploadDocumentStep() =
        StepRetirementUploadDocBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout, true
        ).apply {
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
            itemAddDoc.root.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                chooseImage(IMAGE_DOC_REQUEST_CODE)
            }

            if (mViewModel.dataModel.currentState == 6)
                stepperUploadDoc.previousStepVisible = false
        }

    //step7
    private fun initialUploadResignationStep() =
        StepRetirementUploadResignationLetterBinding.inflate(
            LayoutInflater.from(requireContext())
        ).apply {

            recyclerResignationItem.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = resignationDocAdapter
            }

            resignationDocAdapter.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    stepperUploadResignation.nextStepEnable = false
                    if (resignationDocAdapter.itemCount == 0)
                        itemAddResignation.root.visibility = View.VISIBLE
                    else
                        itemAddResignation.root.visibility = View.GONE
                }
            })

            itemAddResignation.root.setOnClickListener {
                mViewModel.dataModel.tempImageType = Constants.RESIGNATION_LETTER_IMAGE_TYPE
                mViewModel.dataModel.tempImageName = getString(R.string.resignation_letter)
                val intent = Intent(activity, MultiCustomGalleryUI::class.java)
                intent.putExtra(Constants.TEMPID, mViewModel.dataModel.tempImageType)
                intent.putExtra(Constants.REQUEST_CODE_TAG, IMAGE_RESIGNATION_REQUEST_CODE)
                resultImageLaunch.launch(intent)
            }
        }

    //step8
    private fun initialFinalConfirmStep() =
        StepRetirementFinalConfirmBinding.inflate(LayoutInflater.from(requireContext())).apply {
            tvDescService.descTxt.text = getText(R.string.retirement_service_final_desc)
            tvDescSendMessage.descTxt.text = getText(R.string.retirement_service_send_message_desc)

            cbFinalConfirm.setOnCheckedChangeListener { _, isChecked ->
                if (mViewModel.dataModel.resignationDocList.isEmpty()) {
                    cbFinalConfirm.isChecked = false
                } else {
                    finalConfirmStepper.nextStepEnable = isChecked
                }
            }
        }

    //step9
    private fun initialStatusEdict() =
        StepRetirementIssuanceEdictBinding.inflate(LayoutInflater.from(requireContext())).apply {
            if (mViewModel.dataModel.currentState == 9)
                issuanceEdictStepper.previousStepVisible = false

        }

    private fun checkValidEnterStepIdentityInfo(binding: StepRetirementIdentityInfoBinding): Boolean {
        binding.apply {
            val phone = edLandlinePhone.getValue(false)
            val strAddress = edAddress.getValue(false)
            when {
                phone.isBlank() -> edLandlinePhone.getLayout().error =
                    getString(R.string.error_enter_landline_phone)

                !phone.startsWith("0") -> edLandlinePhone.getLayout().error =
                    getString(R.string.error_not_valid_phone)

                phone.length < 11 -> edLandlinePhone.getLayout().error =
                    getString(R.string.error_input_length_phone)

                strAddress.isBlank() -> edAddress.getLayout().error =
                    getString(R.string.error_enter_address)

                strAddress.length < 10 ->
                    edAddress.getLayout().error = getString(R.string.error_input_length)

                !Utility.checkInputIsValidAddress(strAddress) -> {
                    edAddress.getLayout().error =
                        getString(R.string.error_input_is_address_not_valid)
                }

                else -> {
                    return true
                }
            }
            return false
        }
    }

    private fun initialHistoryInfo() {
        (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(5) as? StepRetirementHistoryBinding)?.apply {
            tvYears.text = getString(R.string.yearWithValue, mViewModel.dataModel.historyYears)
            tvMonths.text = getString(R.string.monthWithValue, mViewModel.dataModel.historyMonths)
            tvDays.text = getString(R.string.dayWithValue, mViewModel.dataModel.historyDays)
            val strWithSeparator = Utility.addSeparator(
                if (mViewModel.dataModel.sumHistoryDays.isNumericString())
                    mViewModel.dataModel.sumHistoryDays.toInt()
                else 0
            )
            val str = getString(
                R.string.dayWithValue,
                UiUtils.createTextColorBlueAndBold(strWithSeparator)
            )
            tvTotalDays.text =
                HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun checkFileUploadedSize() {
        mViewModel.dataModel.apply {
            (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(6) as? StepRetirementUploadDocBinding)?.apply {
                if (mViewModel.dataModel.documentList.size == 2) {
                    itemAddDoc.root.visibility = View.GONE
                    stepperUploadDoc.nextStepEnable = true
                } else {
                    itemAddDoc.root.visibility = View.VISIBLE
                    stepperUploadDoc.nextStepEnable = false
                }
            }
        }
    }

    private fun changeRequestState(
        step: EnumRetirementRequestState,
        status: EnumRequestState? = IS_NOT_PASSED,
    ) {
        mViewModel.dataModel.requestStatesList.also { list ->
            list[step.index].state = status
            if (step.index != 6 && status == IS_PASSED)
                list[step.index + 1].state = IS_CURRENT

            if (step.index != 0 && status == IS_CURRENT) {
                for (i in 0..list.size) {
                    if (i == step.index)
                        break
                    list[i].state = IS_PASSED
                }
            }
        }
    }

    private fun showConfirmDialog(desc: String, callbacks: DialogClickInterface.onClickListener) {
        DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
            arguments = createBundle(MessageOfRequestDialogFragment.MessageType.INFO, desc)

            setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    callbacks.onConfirmClick()
                }

                override fun onCancelClick() {
                    callbacks.onCancelClick()
                }
            })
        }.show(childFragmentManager, CancelContractFragment().javaClass.simpleName)
    }

    private fun showStatusDialog(desc: String, callbacks: DialogClickInterface.onClickListener) {
        RetirementStatusDialogFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.DIALOG_DESC, desc)
                putParcelableArrayList(
                    Constants.RETIREMENT_STATUS_REQUEST,
                    mViewModel.dataModel.requestStatesList
                )
            }
            setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    callbacks.onConfirmClick()
                }

                override fun onCancelClick() {
                    requireActivity().onBackPressed()
                }
            })
            isCancelable = false
        }
            .show(childFragmentManager, CancelContractFragment().javaClass.simpleName)
    }

    private fun showRequestInfo() {
        val requestId = mViewModel.dataModel.requestId
        if (mViewModel.dataModel.retirementRequestInfo.isEmpty() && requestId != null) {
            mViewModel.getRetirementRequestInfo(requestId)
        } else {
            viewDataBinding?.apply {
                layoutRequestInfo.visibility = View.VISIBLE
                grHistoryInfo.visibility = View.GONE
                identityRequestInfoAdapter.setItems(mViewModel.dataModel.retirementRequestInfo[0].getIdentityInfo())
                workshopRequestInfoAdapter.setItems(mViewModel.dataModel.retirementRequestInfo[0].getWorkshopAndHomeInfo())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewDataBinding?.stepperLayout?.currentStepIndex == 5) {
            (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(2) as? StepRetirementAuthenticationBinding)?.apply {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    mViewModel.timerStateFlow.collectLatest { timer ->
                        if (timer.displaySeconds.isNumericString() && timer.displaySeconds != timer.textWhenStopped) {
                            val minute = (timer.secondsRemaining ?: 0) / 60
                            val second = ((timer.secondsRemaining ?: 0)) % 60
                            if (minute == 0 && second == 0) {
                                btnReceiveCode.apply {
                                    text = getString(R.string.receive_code)
                                    isEnabled = true
                                }
                            } else {
                                btnReceiveCode.text =
                                    String.format("%02d:%02d ثانیه", minute, second)
                            }
                        } else {
                            btnReceiveCode.apply {
                                text = getString(R.string.receive_code)
                                isEnabled = true
                            }
                        }
                    }
                }
                authenticationStepper.nextStepEnable = true
            }
        }
    }
}