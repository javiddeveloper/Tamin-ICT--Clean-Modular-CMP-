package com.tamin.taminhamrah.ui.home.services.disabilityPension

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityDependentResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityPersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveInfoResponse
import com.tamin.taminhamrah.databinding.FragmentDisabilityPensionBinding
import com.tamin.taminhamrah.databinding.StepDisabilityBranchInfoBinding
import com.tamin.taminhamrah.databinding.StepDisabilityDependentsInfoBinding
import com.tamin.taminhamrah.databinding.StepDisabilityFinalRegistrationBinding
import com.tamin.taminhamrah.databinding.StepDisabilityHistoryBinding
import com.tamin.taminhamrah.databinding.StepDisabilityIdentityInfoBinding
import com.tamin.taminhamrah.databinding.StepDisabilityMedicalCommissionBinding
import com.tamin.taminhamrah.databinding.StepDisabilityRulesBinding
import com.tamin.taminhamrah.databinding.StepDisabilityUploadDocBinding
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
import com.tamin.taminhamrah.ui.home.services.disabilityPension.adapter.ConfirmStepsAdapter
import com.tamin.taminhamrah.ui.home.services.disabilityPension.adapter.DependentInfoAdapter
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.isNumericString
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import androidx.core.net.toUri

/*In eservices site backend, this service is implemented in such a way that when saving the request,
an api call is first made as a post, the result of which is the creation of an entity in the backend database,
 then another api is called to save the required documents. which edits the previously created entity and changes
 the status value in this table from 3 to 4 and puts a list of GUIDs and information of uploaded documents in this
 entity, this api is called as PUT and finally In the last step, for final confirmation, another api is called
 as PUT to edit the previous table again, this time setting the status value to 0. At first, it was tried
 that instead of calling these three APIs at different stages of information completion, at the end of the service,
 an API would be called in the form of POST and include the complete information of the request, but in the end,
 we realized that as a result of each API, an operation is performed in the backend and database.
 It is possible that it is hidden from our view and we had to behave in the same way as eservices site */

@AndroidEntryPoint
class DisabilityPensionFragment :
    BaseFragment<FragmentDisabilityPensionBinding, DisabilityPensionViewModel>(),
    StepperLayout.NextStepClickListener, StepperLayout.PreviousStepClickListener {

    //Class variables
    override val mViewModel: DisabilityPensionViewModel by viewModels()
    private val dependentInfoAdapter by lazy { DependentInfoAdapter() }
    private val identityAdapterInfo by lazy { ExpandableListAdapter(expandingIndex = 3) }
    private val branchInfoAdapter by lazy { KeyValueAdapter() }
    private val confirmStepsAdapter by lazy { ConfirmStepsAdapter() }
    val documentListAdapter: ImagePreviewAdapter by lazy { ImagePreviewAdapter(onItemDocumentClick) }
    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.REQUEST_LUNCHER) {
                val imageUri = if (result.data != null)
                    result.data?.extras?.getString(Constants.IMAGE_URI)?.toUri()
                else
                    null
                //capture image from camera imageUri is null
                if (imageUri == null || !isDuplicateImage(
                        imageUri,
                        mViewModel.dataModel.documentList
                    )
                )
                    provideImageForUpload(mViewModel.dataModel.tempImageType, imageUri)
                else
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.error_select_repeat_pic)
                    )
            }
        }
    private val onItemDocumentClick by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?,
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        handlePageDestination(
                            R.id.action_disability_to_image_activity,
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

    //Base methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_disability_pension

    override fun setupObserver() {
        mViewModel.apply {
            mldRegistrationInfo.observe(this@DisabilityPensionFragment, ::onUserInfoResponse)
            mldDependentInfo.observe(this@DisabilityPensionFragment, ::onDependentInfoResponse)
            mldRefreshDependent.observe(
                this@DisabilityPensionFragment,
                ::onRefreshDependentResponse
            )
            mldPersonalInfo.observe(this@DisabilityPensionFragment, ::onPersonalInfoResponse)
            mldCombinedList.observe(this@DisabilityPensionFragment, ::onCombinedHistoryInfo)
            mldMedicalCommissionPdf.observe(
                this@DisabilityPensionFragment,
                ::onMedicalCommissionPdfResponse
            )
            mldUploadImage.observe(this@DisabilityPensionFragment, ::onUploadImageResponse)
            mldSaveDisabilityUserInfo.observe(
                this@DisabilityPensionFragment,
                ::onDisabilitySaveInfoResponse
            )
            mldFinalConfirm.observe(this@DisabilityPensionFragment, ::onFinalConfirmResponse)
        }
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupToolbar(
                appBar = appbar,
                appbarBackgroundImage = appbarBackgroundImage.imageBackground, moreViews = null
            )
        }
    }

    override fun getData() {
        mViewModel.getRegistrationUserInfo()
    }

    override fun onClick() {
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

    //listeners
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepperLayout?.apply {
            when (stepIndex) {
                1 -> {
                    if (mViewModel.dataModel.dependentsList.isEmpty())
                        mViewModel.getDisabilityPensionDependentInfo()
                    else
                        nextStep()
                }

                2 -> {
                    if (mViewModel.dataModel.identityInfo.isEmpty())
                        mViewModel.getDisabilityPersonalInfo()
                    else
                        nextStep()
                }

                4 -> {
                    if (mViewModel.dataModel.historyDay.isBlank() && mViewModel.dataModel.historyMonth.isBlank())
                        mViewModel.getCombinedRecordList()
                    else
                        nextStep()
                }
                7 -> {
                    showConfirmDialog()
                }

                8 -> {
                    mViewModel.saveAndConfirmRequest()
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

    private fun onUserInfoResponse(result: IdentityInfoResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.apply {
                userNationalId = result.data?.nationalId ?: ""
                genderDesc =
                    if (result.data?.gender == "01") getString(R.string.gentleman) else getString(R.string.lady)
                userName = " ${result.data?.firstName} ${result.data?.lastName} "
            }
            initialStepper()
        }
    }

    private fun onRefreshDependentResponse(result: GeneralRes) {
        if (result.isSuccess)
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_refresh_dependent)
            )
    }

    private fun onDependentInfoResponse(result: DisabilityDependentResponse) {
        if (result.isSuccess) {
            result.data?.list?.forEach { user ->
                val res = Utility.getTendencyResId(
                    tendencyCode = user.relationWithTamin.tendencyInfo?.baseTendency?.tendencyCode
                        ?: "0",
                    genderCode = user.relationWithTamin.personal.gender.genderCode
                )
                user.relationWithTamin.personal.relation = if (res != 0) getString(res) else "_"
            }
            mViewModel.dataModel.dependentsList.apply {
                clear()
                addAll(result.data?.list ?: emptyList())
                dependentInfoAdapter.setItems(this)
            }
            viewDataBinding?.stepperLayout?.nextStep()
        }
    }

    private fun onMedicalCommissionPdfResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val file = Utility.writeByteStreamToDisk(
                Utility.getToolbarTitle(arguments),
                requireContext(),
                result.pdf
            )
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }

            handlePageDestination(R.id.action_disability_to_Activity_pdf_view, Bundle().apply {
                putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
                putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            })
        }
    }

    private fun onCombinedHistoryInfo(result: CombinedRecordResponse) {
        if (result.isSuccess) {
            val list = result.data?.list ?: emptyList()
            if (list.isNotEmpty()) {
                val normalizedDuration = Utility.normalizeHistoryDuration(
                    list[0].historyYears,
                    list[0].historyMonths,
                    list[0].historyDays
                )
                mViewModel.dataModel.apply {
                    historyDay = normalizedDuration.days.toString()
                    historyMonth = normalizedDuration.months.toString()
                    historyYear = normalizedDuration.years.toString()
                    totalHistory = list[0].sumHistoryYears ?: "_"

                    (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(5) as? StepDisabilityHistoryBinding)?.apply {
                        tvDays.text = getString(R.string.day_by_value_two_line, historyDay)
                        tvMonths.text = getString(R.string.month_by_value_two_line, historyMonth)
                        tvYears.text = getString(R.string.year_by_value_two_line, historyYear)
                        val strWithSeparator =
                            Utility.addSeparator(if (totalHistory.isNumericString()) totalHistory.toInt() else 0)
                        val str = getString(
                            R.string.day_by_value,
                            UiUtils.createTextColorBlueAndBold(strWithSeparator)
                        )
                        tvTotalDays.text =
                            HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                    viewDataBinding?.stepperLayout?.nextStep()
                }
            } else {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
            }
        }
    }

    private fun onPersonalInfoResponse(result: DisabilityPersonalInfoResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.identityInfo.apply {
                clear()
                addAll(result.data?.getIdentityInfo() ?: emptyList())
                identityAdapterInfo.setItems(this)
            }
            mViewModel.dataModel.branchInfo.apply {
                clear()
                addAll(result.data?.getBranchInfo() ?: emptyList())
                branchInfoAdapter.setItems(this)
            }

            if (result.data != null)
                mViewModel.dataModel.loadRequestInfo(result.data)

            viewDataBinding?.stepperLayout?.nextStep()
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

    private fun onDisabilitySaveInfoResponse(result: DisabilitySaveInfoResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.requestId = result.data.request?.id ?: 0
            viewDataBinding?.stepperLayout?.nextStep()
            confirmStepsAdapter.setItems(mViewModel.getConfirmSteps())
        }
    }

    private fun onFinalConfirmResponse(result: DisabilitySaveInfoResponse) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                desc = if (result.data.request?.refCode.isNullOrBlank())
                    getString(R.string.message_success)
                else
                    getString(
                        R.string.message_success_final_disability_registration,
                        result.data.request.refCode
                    ),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
        }
    }

    //utils
    private fun initialStepper() {
        viewDataBinding?.stepperLayout?.apply {
            initial(
                arrayListOf<ViewBinding>(
                    initialRulesStep(),
                    initialDependentInfoStep(),
                    initialIdentityInfoStep(),
                    initialBranchInfoStep(),
                    initialHistoryStep(),
                    initialMedicalCommissionStep(),
                    initialUploadDocumentStep(),
                    initialFinalRegistrationStep()
                )
            )
            onNextStepClickListener = this@DisabilityPensionFragment
            onPreviousStepClickListener = this@DisabilityPensionFragment
        }
    }

    //step 1
    private fun initialRulesStep() =
        StepDisabilityRulesBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true
        ).apply {
            tvDescRules.descTxt.text = getText(R.string.desc_view_rules_disability_pension)
            val str =
                this@DisabilityPensionFragment.getString(
                    R.string.desc_rules_disability_pension,
                    mViewModel.dataModel.genderDesc,
                    UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.userName)
                )

            tvDescCommitment.text = HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
            cbConfirmRulesStep.setOnCheckedChangeListener { _, isChecked ->
                rulesStepper.nextStepEnable = isChecked
            }
            btnShowRules.setOnClickListener {
                handlePageDestination(R.id.action_disability_to_pdf, Bundle().apply {
                    putString(
                        Constants.TOOLBAR_TITLE,
                        this@DisabilityPensionFragment.getString(R.string.rules_title)
                    )
                    putString(
                        PdfViewerActivity.ARG_PDF_FILE_ASSET,
                        "rulesAndRegulationsHtmlFile/disability_pension_rules.pdf"
                    )
                })
            }
        }

    //step 2
    private fun initialDependentInfoStep() = StepDisabilityDependentsInfoBinding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepperLayout, true
    ).apply {
        recyclerInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dependentInfoAdapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }

        tvDescRules.descTxt.text = getText(R.string.hint_dependent_info_disability_pension)

        cbConfirmDependentsStep.setOnCheckedChangeListener { _, isChecked ->
            dependentStepper.nextStepEnable = isChecked
        }

        btnRefreshDependentInfo.setOnClickListener {
            DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
                arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.CONFIRM,
                    this@DisabilityPensionFragment.getString(R.string.message_confirm_refresh_dependent),
                    true
                )
                setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        mViewModel.refreshDependent()
                    }

                    override fun onCancelClick() {}
                })
            }.show(childFragmentManager, "ShowAndRegisterDependentsFragment")
        }

        btnAddDependent.setOnClickListener {
            handlePageDestination(R.id.action_disability_to_add_dependent,
                Bundle().apply {
                    putString(Constants.TOOLBAR_TITLE, getString(R.string.add_new_dependent))
                    putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_relation)
                })
        }
    }

    //step 3
    private fun initialIdentityInfoStep() = StepDisabilityIdentityInfoBinding.inflate(
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
            if (edLandlinePhone.getValue(false).length == 11)
                edLandlinePhone.getLayout().isErrorEnabled = false
        }
        edAddress.getInput().doAfterTextChanged {
            cbConfirmIdentityStep.isChecked = false
        }
        cbConfirmIdentityStep.setOnCheckedChangeListener { _, isChecked ->
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
            if (checkValidEnterStepIdentityInfo(this))
                identityStepper.nextStepEnable = isChecked
            else
                cbConfirmIdentityStep.isChecked = false
        }
    }

    //step 4
    private fun initialBranchInfoStep() =
        StepDisabilityBranchInfoBinding.inflate(
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
            }
            edWorkshopAddress.getInput().doAfterTextChanged {
                cbConfirm.isChecked = false
            }
            cbConfirm.setOnCheckedChangeListener { _, isChecked ->
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                if (checkValidEnterStepBranchInfo(this)) {
                    branchInfoStepper.nextStepEnable = isChecked
                    mViewModel.dataModel.saveInfoRequestModel.apply {
                        workshopName = edWorkshopName.getValue(false)
                        managerName = edEmployerName.getValue(false)
                        activityType = edWorkShopActivityDesk.getValue(false)
                        workshopAddress = edWorkshopAddress.getValue(false)
                    }
                } else
                    cbConfirm.isChecked = false
            }
        }

    //step 5
    private fun initialHistoryStep() =
        StepDisabilityHistoryBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true
        ).apply {
            cbConfirmHistoryStep.setOnCheckedChangeListener { _, isChecked ->
                historyStepper.nextStepEnable = isChecked
            }
            btnObjection.setOnClickListener {
                handlePageDestination(R.id.action_to_objectionInsuranceHistoryFragment,
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
    private fun initialMedicalCommissionStep() = StepDisabilityMedicalCommissionBinding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepperLayout, true
    ).apply {
        rgObjection.setOnCheckedChangeListener { _, _ ->
            medicalCommissionStepper.nextStepEnable = true
            if (rbNoObjection.isChecked) {
                medicalCommissionStepper.nextStepEnable = true
                tvDescObjectMedical.gone()
            }else {
                medicalCommissionStepper.nextStepEnable = false
                tvDescObjectMedical.visible()

            }
        }
        btnShowPdf.setOnClickListener {
            val workShop = mViewModel.dataModel.saveInfoRequestModel.workshopName ?: ""
            mViewModel.getMedicalCommissionPDF(workShop)
        }

        btnShowRegisterRequests.setOnClickListener {
            handlePageDestination(R.id.action_disability_to_registeredRequestFragment,
                Bundle().apply {
                    putString(
                        Constants.TOOLBAR_TITLE,
                        getString(R.string.registered_request_medical_commission)
                    )
                    putString(Constants.TOOLBAR_ICON_IMAGE, mViewModel.getImageUrl())
                })
        }
    }

    //step7
    private fun initialUploadDocumentStep() =
        StepDisabilityUploadDocBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true
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
                chooseImage()
            }
        }

    //step8
    private fun initialFinalRegistrationStep() = StepDisabilityFinalRegistrationBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepperLayout,
        true
    ).apply {
        recyclerFinal.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = confirmStepsAdapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }

        cbDescFinalConfirm.setOnCheckedChangeListener { _, isChecked ->
            stepperFinalRegistration.nextStepEnable = isChecked
        }
    }

    private fun checkValidEnterStepIdentityInfo(binding: StepDisabilityIdentityInfoBinding): Boolean {
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
                    mViewModel.dataModel.saveInfoRequestModel.apply {
                        phoneNumber = phone
                        address = strAddress
                    }
                    return true
                }
            }
            return false
        }
    }

    private fun checkValidEnterStepBranchInfo(binding: StepDisabilityBranchInfoBinding): Boolean {
        binding.apply {
            when {
                edWorkshopName.getValue(false).isBlank() ->
                    edWorkshopName.getLayout().error = getString(R.string.error_enter_workshop_name)

                edWorkshopAddress.getValue(false).isBlank() ->
                    edWorkshopAddress.getLayout().error =
                        getString(R.string.error_enter_workshop_address)

                else -> {
                    return true
                }
            }
            return false
        }
    }

    override fun chooseImage(requestCode: Int) {

        MenuDialogFragment().apply {
            arguments = Bundle().apply {
                putString(
                    MenuDialogFragment.ARG_MENU_TITLE,
                    this@DisabilityPensionFragment.getString(R.string.select_document_type)
                )
            }
            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@DisabilityPensionFragment.lifecycleScope.launchWhenCreated {
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
                    intent.putExtra(Constants.TEMPID, itemResult.id)
                    resultImageLaunch.launch(intent)
                }
            })

        }.show(childFragmentManager, "PensionSurvivorFragment")
    }

    private fun checkFileUploadedSize() {
        mViewModel.dataModel.apply {
            (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(7) as? StepDisabilityUploadDocBinding)?.apply {
                val isGone = mViewModel.dataModel.documentList.size == 5
                itemAddDoc.root.isVisible = !isGone
            }
        }
    }

    private fun showConfirmDialog() {
        DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
            arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.CONFIRM,
                this@DisabilityPensionFragment.getString(R.string.disability_confirm_uploaded_document),
                btnCancel = true
            )
            setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    viewDataBinding?.stepperLayout?.nextStep()
                    confirmStepsAdapter.setItems(mViewModel.getConfirmSteps())
                }

                override fun onCancelClick() {
                    dismiss()
                }

            })
        }.show(childFragmentManager, "DisabilityPensionFragment")
    }

}