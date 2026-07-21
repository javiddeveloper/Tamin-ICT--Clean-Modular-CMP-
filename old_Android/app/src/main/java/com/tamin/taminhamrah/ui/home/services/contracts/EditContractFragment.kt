package com.tamin.taminhamrah.ui.home.services.contracts

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.AppBarLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.MEDICAL_STUDENT_CODE
import com.tamin.taminhamrah.Constants.RED_CRESCENT_CODE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.ProvinceResponse
import com.tamin.taminhamrah.data.remote.models.services.RedCrossStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateSalary
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumOptionsResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumRateResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.Personal
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.UpdateAddressInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianContract
import com.tamin.taminhamrah.databinding.ContractStepFinalBinding
import com.tamin.taminhamrah.databinding.ContractStepMonthlyInsurancePremiumBinding
import com.tamin.taminhamrah.databinding.ContractStepSalaryBinding
import com.tamin.taminhamrah.databinding.ContractStepTreatmentSupportBinding
import com.tamin.taminhamrah.databinding.ContractStepUploadImageBinding
import com.tamin.taminhamrah.databinding.ContractStepUserInfoBinding
import com.tamin.taminhamrah.databinding.FragmentShowContractBinding
import com.tamin.taminhamrah.databinding.GuardianshipContractStepBinding
import com.tamin.taminhamrah.databinding.ShowContractStepContractInfoBinding
import com.tamin.taminhamrah.databinding.UploadImageLayoutBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.contracts.ContractBaseFragment.Companion.REQUEST_CODE_IMAGE_DOC
import com.tamin.taminhamrah.ui.home.services.contracts.ContractBaseFragment.Companion.REQUEST_CODE_IMAGE_GUARDIANSHIP
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep.STEP_CONTRACT_INFO
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep.STEP_EDIT_CONTRACT
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep.STEP_GUARDIANSHIP
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep.STEP_INSURANCE_PREMIUM
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep.STEP_SALARY
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep.STEP_TREATMENT_SUPPORT
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep.STEP_UPLOAD_IMAGE
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditFreelanceStep.STEP_USER_INFO
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.studentContract.adapter.PremiumRateAdapter
import com.tamin.taminhamrah.ui.home.services.studentContract.model.GuardianType
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.HelperDate
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import org.jetbrains.annotations.NotNull
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import java.io.File
import java.util.Date
import kotlin.math.abs

@AndroidEntryPoint
class EditContractFragment :
    BaseFragment<FragmentShowContractBinding, ContractViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {
    /////////////////////////////////////////////////Class Variables //////////////////////////////////////////////

    companion object {
        const val ARG_CONTRACT_TYPE = "ARG_CONTRACT_TYPE"
    }

    private val contractType by lazy {
        (arguments?.getSerializable(ARG_CONTRACT_TYPE) as? EnumInsuranceType?)
            ?: throw Exception("set contract type")
    }
    private var lastJobCode = ""
    override val mViewModel: ContractViewModel by viewModels()

    var tempCntCode = ""
    var tempCntTitle = ""
    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                provideImageForUpload(
                    Constants.REQUEST_DEFAULT_IMAGE_TYPE,
                    if (result.data != null) Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI)) else null,
                    result.resultCode
                )

            } catch (e: Exception) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.image_upload_error)
                )
            }
        }

    val contractImageListAdapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(onContractImageClickListener)
    }
    val guardianshipImageListAdapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(onGuardianshipImageClickListener)
    }
    val onContractImageClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?,
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        val bundle = Bundle()
                        bundle.putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                        bundle.putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                        handlePageDestination(
                            R.id.action_editContractFragment_to_ImageViewerActivity,
                            bundle
                        )
                    }
                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.imageFile = null
                        contractImageListAdapter.clearItems()
                    }
                }
            }

        }
    }
    val onGuardianshipImageClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?,
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        val bundle = Bundle()
                        bundle.putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                        bundle.putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                        handlePageDestination(
                            R.id.action_editContractFragment_to_ImageViewerActivity,
                            bundle
                        )
                    }
                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.guardianshipImage = null
                        guardianshipImageListAdapter.clearItems()
                    }
                }
            }
        }
    }
    private var restartedFragment = false


    /////////////////////////////////////////////////Parent Methods//////////////////////////////////////////////


    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId() = R.layout.fragment_show_contract

    override fun initView() {
        viewDataBinding?.appBar?.toolbar?.imageBack?.setOnClickListener { backButtonPress() }
        setupToolbarContract()
    }

    override fun getData() {
        mViewModel.getRegistrationInfo(isOptional = false)
    }

    override fun onClick() {
    }

    override fun onStart() {
        super.onStart()
        if (restartedFragment) {
            initStepper(viewDataBinding?.stepper?.currentStepIndex ?: 1)
            restartedFragment = false
        }
    }

    override fun onStop() {
        restartedFragment = true
        (getStep(STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.apply {
            mViewModel.dataModel.tempAddress = inputAddress.getValue(false)
            mViewModel.dataModel.tempZipCode = inputZipCode.getValue(false)
            mViewModel.dataModel.tempPhoneNumber = inputPhoneNumber.getValue(false)
            loadTempPersonalInfo = true
        }
        super.onStop()
    }

    override fun setupObserver() {
        mViewModel.mldCheckContractCondition.observe(this, ::onCheckContractConditionResponse)
        mViewModel.mldRegistrationInfo.observe(this, ::onRegistrationInfoResponse)
        mViewModel.mldSaveUsersAddressInfo.observe(this, ::onUpdateUserAddressResponse)
        mViewModel.mldUploadImageContractDoc.observe(this, ::onUploadImageResponse)
        mViewModel.mldDownloadImage.observe(this, ::onDownloadImageResponse)
        mViewModel.mldDownloadImageGuardian.observe(this, ::onDownloadImageGuardianResponse)
        mViewModel.mldPremiumRateForContract.observe(this, ::onContractPremiumRateResponse)
        mViewModel.mldCheckAndCalculateSalaryForContract.observe(this, ::onCalculateSalaryResponse)
        mViewModel.mldGetProvinceWhitOutPaging.observe(this, ::onProvinceInfoResponse)
        mViewModel.mldPremiumOptions.observe(this, ::onPremiumOptionsResponse)
        mViewModel.mldUpdateContract.observe(this, ::onUpdateContractResponse)
        mViewModel.mldPdf.observe(this, ::onDownloadPdfFileResponse)
        mViewModel.mldUploadImageGuardianshipDoc.observe(this, ::onUploadGuardianshipImageResponse)
        mViewModel.mldCheckRedCrossStatus.observe(this, ::onCheckRedCrossStatusResponse)
    }


    /////////////////////////////////////////////////STEPS//////////////////////////////////////////////

    private fun initStepper(initialStep: Int = 1) {
        val stepLayout = ArrayList<ViewBinding>()
        viewDataBinding?.apply {
            EnumEditFreelanceStep.values().forEach { stepLayout.add(inflateView(it)) }
            stepper.initial(stepLayout, initialStep)
            stepper.onNextStepClickListener = this@EditContractFragment
            stepper.onPreviousStepClickListener = this@EditContractFragment
        }
    }

    var loadTempPersonalInfo = false

    private fun inflateView(it: EnumEditFreelanceStep): ViewBinding {
        return when (it) {
            STEP_CONTRACT_INFO -> {
                ShowContractStepContractInfoBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.apply {
                        index = STEP_CONTRACT_INFO.step
                        title = STEP_CONTRACT_INFO.title
                    }
                    valueInsuranceNum.text = mViewModel.dataModel.insuranceId
                    valueBranchInfo.text =
                        "${mViewModel.dataModel.cityNameOfBranch} - ${mViewModel.dataModel.branchAddress}"
                }
            }
            STEP_USER_INFO -> {
                ContractStepUserInfoBinding
                    .inflate(LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true)
                    .apply {
                        stepperItem.apply {
                            index = STEP_USER_INFO.step
                            title = STEP_USER_INFO.title
                        }

                        itemDescAddress.descTxt.text = getString(R.string.label_desc_address)
                        mViewModel.dataModel.apply {
                            selectCityName.setValue(cityNameOfBranch)
                            inputMobile.getInput().setText(usersMobile)

                            if (!loadTempPersonalInfo) {
                                inputAddress.getInput().setText(usersAddress)
                                inputPhoneNumber.getInput().setText(usersPhoneNumber)
                                inputZipCode.getInput().setText(usersZipCode)

                            } else {
                                loadTempPersonalInfo = false
                                inputAddress.getInput().setText(usersAddress)
                                inputPhoneNumber.getInput().setText(usersPhoneNumber)
                                inputZipCode.getInput().setText(usersZipCode)

                            }
                            if (!mobileExist)
                                inputMobile.gone()
                            else {
                                inputMobile.visible()
                                inputMobile.enableView(false)
                            }
                        }

                        selectCityName.getIt().setOnClickListener {
                            showDialog(ContractDialogType.CITY_LIST_EMPTY_PROVINCE,
                                getString(R.string.label_select_city_name),
                                object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        mViewModel.dataModel.cityCode = itemResult.id ?: ""
                                        mViewModel.dataModel.cityNameOfBranch = itemResult.title ?: ""
                                        selectCityName.setValue(mViewModel.dataModel.cityNameOfBranch)
                                    }
                                })
                        }

                    }
            }
            STEP_UPLOAD_IMAGE -> {
                ContractStepUploadImageBinding
                    .inflate(LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true)
                    .apply {

                        stepperItem.apply {
                            index = STEP_UPLOAD_IMAGE.step
                            title = STEP_UPLOAD_IMAGE.title
                        }

                        layoutUploadImage.recycler.apply {
                            adapter = contractImageListAdapter
                            if (itemDecorationCount == 0) {
                                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                            }
                        }

                        val imageFile = mViewModel.dataModel.imageFile
                        contractImageListAdapter.setItems(
                            if (imageFile == null) emptyList() else listOf(
                                imageFile
                            )
                        )

                        layoutUploadImage.btnAddDocument.setOnClickListener {
                            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

                            if (layoutUploadImage.inputImageDesc.getInput().text.isNullOrBlank()) {
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.INFO,
                                    getString(R.string.label_necessary_input_image_desc)
                                )
                            } else {
                                if (mViewModel.dataModel.imageFile == null) {
                                    mViewModel.dataModel.tempImageName =
                                        layoutUploadImage.inputImageDesc.getInput().text.toString()
                                    chooseImage(REQUEST_CODE_IMAGE_DOC)
                                } else
                                    showAlertDialog(
                                        MessageOfRequestDialogFragment.MessageType.INFO,
                                        getString(R.string.error_max_upload_1_item)
                                    )

                            }
                        }
                    }
            }
            STEP_GUARDIANSHIP -> {
                GuardianshipContractStepBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.apply {
                        index = STEP_GUARDIANSHIP.step
                        title = STEP_GUARDIANSHIP.title
                    }

                    addGuardianshipDoc.tvUploadHint.text = getString(R.string.label_image_guid)
                    addGuardianshipDoc.tvTitle.text = getString(R.string.label_guardianship_doc)

                    ////// Image Upload
                    addGuardianshipDoc.recycler.apply {
                        this.adapter = guardianshipImageListAdapter
                        if (itemDecorationCount == 0) {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }
                    }

                    if (mViewModel.dataModel.guardianshipImage != null)
                        guardianshipImageListAdapter.setItems(listOf(mViewModel.dataModel.guardianshipImage!!))

                    val clickListener = View.OnClickListener {
                        if (mViewModel.dataModel.guardianshipImage != null) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.INFO,
                                getString(R.string.error_max_upload_1_item)
                            )

                        } else {

//

                            mViewModel.dataModel.guardianImageName =
                                getString(R.string.label_guardianship_doc)
                            chooseImage(REQUEST_CODE_IMAGE_GUARDIANSHIP)
                        }
                        addGuardianshipDoc.uploadErrorGroup.gone()
                    }
                    addGuardianshipDoc.tvTitle.setOnClickListener(null)
                    addGuardianshipDoc.layoutViewImage.setOnClickListener(null)
                    addGuardianshipDoc.tvTitle.setOnClickListener(clickListener)
                    addGuardianshipDoc.layoutViewImage.setOnClickListener(clickListener)
                    // Image Error
                    addGuardianshipDoc.uploadErrorImg.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_recycler_color_icon
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )

                    addGuardianshipDoc.tvUploadError.text = getString(R.string.label_error_image)
                    ///////////// Date Picker
                    guardianshipDate.hint = getString(R.string.label_guardianship_date)
                    guardianshipDate.inputDate.setOnClickListener {
                        val datePickerStart = getDatePicker()
                        datePickerStart?.setListener(object : MyPersianPickerListener {
                            @SuppressLint("SetTextI18n")
                            override fun onDateSelected(@NotNull myPersianPickerDate: MyPersianPickerDate) {
                                val persianMonth = if (myPersianPickerDate.persianMonth in 1..9)
                                    "0${myPersianPickerDate.persianMonth}"
                                else
                                    "${myPersianPickerDate.persianMonth}"
                                guardianshipDate.inputDate.setText("${myPersianPickerDate.persianYear}/$persianMonth/${myPersianPickerDate.persianDay}")
                                val date = Date(myPersianPickerDate.timestamp + 70200000)

                                mViewModel.dataModel.guardianDateFormatted =
                                    HelperDate.convertServerDateFormatToMobileDateFormat(date)

                                mViewModel.dataModel.guardianDate =
                                    guardianshipDate.inputDate.text.toString()

                            }

                            override fun onDismissed() {}
                        })
                        datePickerStart?.show()
                        guardianshipDate.tilDate.isErrorEnabled = false

                    }

                    ///// Show and Hide Guardianship Detail
                    ///// Radio Button
                    containerItSelf.setOnClickListener {
                        if (!rbItSelf.isChecked)
                            rbItSelf.isChecked = !rbItSelf.isChecked
                    }
                    containerGuardianShip.setOnClickListener {
                        if (!rbGuardianShip.isChecked)
                            rbGuardianShip.isChecked = !rbGuardianShip.isChecked
                    }

                    rbItSelf.setOnCheckedChangeListener(null)
                    rbGuardianShip.setOnCheckedChangeListener(null)

                    when (mViewModel.dataModel.guardianShip) {
                        GuardianType.FOR_GUARDIAN -> {
                            stepperItem.nextStepEnable = true
                            rbGuardianShip.isChecked = true
                            rbItSelf.isChecked = false
                            guardianshipLayout.visible()
                            rgGuardianShip.check(R.id.rbGuardianShip)
                            //set guardian image
                            if (mViewModel.dataModel.guardianGuid != "00" && mViewModel.dataModel.guardianshipImage == null) {
                                if (!mViewModel.imageDownloaded)
                                    mViewModel.downloadContractImage(
                                        mViewModel.dataModel.guardianGuid,
                                        1
                                    )
                            }
                        }
                        GuardianType.FOR_ITSELF -> {
                            rgGuardianShip.check(R.id.rbItSelf)
                            stepperItem.nextStepEnable = true
                            guardianshipLayout.gone()
                            rbItSelf.isChecked = true
                            rbGuardianShip.isChecked = false

                        }
                        null -> {
                            stepperItem.nextStepEnable = false
                        }
                    }
                    val checkListener = CompoundButton.OnCheckedChangeListener { p0, p1 ->
                        Timber.tag("rbGuardianShip.Checked").i("rbItSelf: p0= $p0 p1= $p1")
                        var checkChange = false
                        if (p0.id == rbItSelf.id && rbItSelf.isChecked) {
                            rbGuardianShip.isChecked = false
                            checkChange = true
                        }

                        if (p0.id == rbGuardianShip.id && rbGuardianShip.isChecked) {
                            rbItSelf.isChecked = false
                            checkChange = true
                        }
                        if (checkChange) {
                            if (rbItSelf.isChecked) {
                                mViewModel.dataModel.guardianShip = GuardianType.FOR_ITSELF
                                stepperItem.nextStepEnable = true
                                guardianshipLayout.gone()
                            } else {
                                mViewModel.dataModel.guardianShip = GuardianType.FOR_GUARDIAN
                                stepperItem.nextStepEnable = true
                                guardianshipLayout.visible()
                            }
                        }
                    }

                    rbItSelf.setOnCheckedChangeListener(checkListener)
                    rbGuardianShip.setOnCheckedChangeListener(checkListener)

                    ////// Set Default Value
                    etName.getInput().doAfterTextChanged {
                        mViewModel.dataModel.guardianName = etName.getInput().text.toString()
                    }
                    etNationalCode.getInput().doAfterTextChanged {
                        mViewModel.dataModel.guardianNationalId =
                            etNationalCode.getInput().text.toString()

                    }
                    etGuardianShipNumber.getInput().doAfterTextChanged {
                        mViewModel.dataModel.guardianNumber =
                            etGuardianShipNumber.getInput().text.toString()
                    }

                    etName.getInput().setText(mViewModel.dataModel.guardianName)
                    etNationalCode.getInput().setText(mViewModel.dataModel.guardianNationalId)
                    etGuardianShipNumber.getInput().setText(mViewModel.dataModel.guardianNumber)
                    guardianshipDate.input = mViewModel.dataModel.guardianDate

                }
            }
            STEP_TREATMENT_SUPPORT -> {
                ContractStepTreatmentSupportBinding
                    .inflate(LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true)
                    .apply {
                        stepperItem.apply {
                            index = STEP_TREATMENT_SUPPORT.step
                            title = STEP_TREATMENT_SUPPORT.title
                        }
                        btnShowSubordinatePeople.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString(
                                Constants.TOOLBAR_TITLE,
                                getString(R.string.title_subordinate_people)
                            )
                            bundle.putString(
                                Constants.TOOLBAR_ICON_IMAGE,
                                "https://eservices.tamin.ir/pwa/assets/icon-eservices/relationship.svg"
                            )
                            handlePageDestination(
                                R.id.action_editContractFragment_to_showAndRegisterDependentsFragment,
                                bundle = bundle
                            )
                        }
                        rgTreatmentSupport.setOnCheckedChangeListener(null)
                        mViewModel.dataModel.apply {
                            if (cntFreeJobCode == RED_CRESCENT_CODE) {
                                rbWantTreatmentSupport.isChecked = true
                                rbNotWantTreatmentSupport.isClickable = false
                                checkboxAgreement.isChecked = true
                                mViewModel.dataModel.cntDrmn = "1"
                                agreementLayout.visible()
                                stepperItem.nextStepEnable = mViewModel.dataModel.agreement
                            } else {
                                if (cntDrmn == "1") {
                                    rbWantTreatmentSupport.isChecked = true
                                    rbNotWantTreatmentSupport.isChecked = false
                                    agreementLayout.visible()
                                    checkboxAgreement.isChecked = agreement

                                } else {
                                    rbNotWantTreatmentSupport.isChecked = true
                                    rbWantTreatmentSupport.isChecked = false
                                    agreementLayout.gone()
                                    checkboxAgreement.isChecked = false
                                }
                            }
                        }
                        rgTreatmentSupport.setOnCheckedChangeListener { _, _ ->
                            mViewModel.dataModel.selectedSalary = -1
                            if (rbWantTreatmentSupport.isChecked) {
                                mViewModel.dataModel.cntDrmn = "1"
                                agreementLayout.visible()
                                stepperItem.nextStepEnable = mViewModel.dataModel.agreement

                            } else {
                                mViewModel.dataModel.cntDrmn = "2"
                                mViewModel.dataModel.agreement = false
                                checkboxAgreement.isChecked = false
                                stepperItem.nextStepEnable = true
                                agreementLayout.gone()
                            }
                        }
                        checkboxAgreement.setOnCheckedChangeListener { _, _ ->
                            mViewModel.dataModel.agreement = checkboxAgreement.isChecked
                            stepperItem.nextStepEnable =
                                (checkboxAgreement.isChecked &&
                                        rbWantTreatmentSupport.isChecked) || rbNotWantTreatmentSupport.isChecked
                        }
                        stepperItem.nextStepEnable =
                            (rbWantTreatmentSupport.isChecked && checkboxAgreement.isChecked) || rbNotWantTreatmentSupport.isChecked
                    }
            }
            STEP_INSURANCE_PREMIUM -> {
                ContractStepMonthlyInsurancePremiumBinding
                    .inflate(LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true)
                    .apply {

                        stepperItem.apply {
                            index = STEP_INSURANCE_PREMIUM.step
                            title = STEP_INSURANCE_PREMIUM.title
                        }
                        stepperItem.nextStepEnable = true

                        ItemDesc5.descTxt.text = getText(R.string.desc_monthly_premium1)
                        ItemDesc6.descTxt.text = getText(R.string.desc_monthly_premium2)
                        ItemDesc7.descTxt.text = getText(R.string.desc_monthly_premium3)

                        val adapter = PremiumRateAdapter(object : MenuInterface.OnResult {
                            override fun onResult(itemResult: MenuModel) {
                                mViewModel.dataModel.premiumRateCode = itemResult.id ?: "00"
                                mViewModel.dataModel.selectedPercentDesc = itemResult.description2 ?: ""
                                mViewModel.dataModel.selectedSalary = -1
                                stepperItem.nextStepEnable = true
                                mViewModel.dataModel.changePremiumRateCode = true

                            }
                        })

                        recyclerPaymentRate.adapter = adapter
                        adapter.setItems(mViewModel.dataModel.premiumOptionList)

                        if (contractType == EnumInsuranceType.TYPE_FREELANCE) {
                            selectInsuredJobTitle.visible()
                            selectInsuredJobTitle.setValue(mViewModel.dataModel.jobDesc)

                            selectInsuredJobTitle.getIt().setOnClickListener {
                                if (mViewModel.dataModel.cntFreeJobCode== RED_CRESCENT_CODE)
                                    return@setOnClickListener
                                showDialog(
                                    ContractDialogType.JOB_TITLES,
                                    getString(R.string.label_select_job_name),
                                    object : MenuInterface.OnResult {
                                        override fun onResult(itemResult: MenuModel) {
                                            tempCntCode = itemResult.id ?: ""
                                            tempCntTitle = itemResult.title ?: ""
                                            mViewModel.dataModel.changePremiumRateCode = true

                                            when (itemResult.id) {
                                                MEDICAL_STUDENT_CODE -> {
                                                    mViewModel.checkMedicalStudent()
                                                }
                                                RED_CRESCENT_CODE -> {
                                                    val day = PersianDate.today().shDay
                                                    if (day > 20) {
                                                        showAlertDialog(
                                                            MessageOfRequestDialogFragment.MessageType.WARNING,
                                                            getString(R.string.error_redCrescentCode_select_job_title)
                                                        )
                                                        return
                                                    }
                                                    mViewModel.checkRedCrossStatus()

                                                }
                                                else -> {
                                                    (adapter as? PremiumRateAdapter?)?.enableSelectAllItem()
                                                    setTreatmentSupport(false)

                                                    mViewModel.dataModel.jobDesc = tempCntTitle

                                                    selectInsuredJobTitle.setValue(itemResult.title?:"")
                                                    selectInsuredJobTitle.getIt().error = null
                                                    mViewModel.dataModel.needToCalculate = true
                                                    if (mViewModel.dataModel.premiumRateCode != "00")
                                                        stepperItem.nextStepEnable = true
                                                }
                                            }
                                        }
                                    })
                            }
                        } else {
                            selectInsuredJobTitle.gone()
                        }

                        if (mViewModel.dataModel.premiumRateCode != "00")
                            adapter.changeCheck(mViewModel.dataModel.premiumRateCode)
                        else
                            stepperItem.nextStepEnable = false
                    }
            }
            STEP_SALARY -> {
                ContractStepSalaryBinding
                    .inflate(LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true)
                    .apply {
                        stepperItem.apply {
                            index = STEP_SALARY.step
                            title = STEP_SALARY.title
                        }
                        var rate = ""
                        for (item in mViewModel.dataModel.premiumOptionList) {
                            if (item.spcrateCode == mViewModel.dataModel.premiumRateCode)
                                rate = item.insurDpercent
                        }
                        if (mViewModel.dataModel.cntFreeJobCode == RED_CRESCENT_CODE) {
                            layoutPriceSeekBar.root.isVisible = false
                            mViewModel.dataModel.maxPremiumRate = mViewModel.dataModel.minPremiumRate
                        }
                        layoutPriceSeekBar.root.isVisible =
                            mViewModel.dataModel.maxPremiumRate != mViewModel.dataModel.minPremiumRate

                        ItemDesc10.descTxt.text = getString(R.string.desc_monthly_premium7, rate)
                        ItemDesc11.descTxt.text = getText(R.string.desc_monthly_premium6)
                        ItemDesc9.descTxt.text = HtmlCompat.fromHtml(
                            getString(
                                R.string.desc_monthly_premium5,
                                UiUtils.createTextColorGreenAndBold(Utility.addSeparator(mViewModel.dataModel.minPremiumRate.toInt())),
                                UiUtils.createTextColorGreenAndBold(Utility.addSeparator(mViewModel.dataModel.maxPremiumRate.toInt()))
                            ),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )

                        layoutPriceSeekBar.priceSeekBar.apply {
                            setMin(mViewModel.dataModel.minPremiumRate)
                            max = mViewModel.dataModel.maxPremiumRate
                        }
                        tvShowValueOfCal.text = HtmlCompat.fromHtml(
                            getString(
                                R.string.value_calcaluate_salary_for_contract,
                                UiUtils.createTextColorGreenAndBold(Utility.addSeparator((mViewModel.dataModel.selectedSalary)))
                            ),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )

                        ItemDesc8.descTxt.text =
                            HtmlCompat.fromHtml(
                                getString(
                                    R.string.desc_monthly_premium4,
                                    UiUtils.createTextColorGreenAndBold(
                                        Utility.addSeparator(
                                            mViewModel.dataModel.paymentTabayi ?: 0
                                        )
                                    )
                                ), HtmlCompat.FROM_HTML_MODE_LEGACY
                            )


                        if (mViewModel.dataModel.selectedSalary > 0) {
                            stepperItem.nextStepEnable = true
                            groupValue.visible()

                            if (mViewModel.dataModel.cntDrmn == "1")
                                ItemDesc8.root.visible()
                            else
                                ItemDesc8.root.gone()


                        } else {
                            stepperItem.nextStepEnable = false
                            groupValue.gone()
                            ItemDesc8.root.gone()
                        }


                        val selected = mViewModel.dataModel.selectedValueSeekbar

                        layoutPriceSeekBar.priceSeekBar.currentValue =
                            if (selected < mViewModel.dataModel.minPremiumRate)
                                mViewModel.dataModel.minPremiumRate
                            else if (selected > mViewModel.dataModel.maxPremiumRate)
                                mViewModel.dataModel.maxPremiumRate
                            else
                                selected

                        layoutPriceSeekBar.priceSeekBar.setListener { _, _ ->
                            if (layoutPriceSeekBar.priceSeekBar.currentValue != mViewModel.dataModel.selectedValueSeekbar) {
                                mViewModel.dataModel.selectedSalary = -1
                                stepperItem.nextStepEnable = false
                                groupValue.visibility = View.GONE
                                ItemDesc8.root.gone()
                                mViewModel.dataModel.selectedValueSeekbar =
                                    layoutPriceSeekBar.priceSeekBar.currentValue.toDouble()
                            }
                        }
                        layoutPriceSeekBar.imgAdd.setOnClickListener {

                            val pureValue =
                                (layoutPriceSeekBar.priceSeekBar.currentValue / InsuranceContractFragment.PREMIUM_RATE_ADD_STEP).toLong() * InsuranceContractFragment.PREMIUM_RATE_ADD_STEP

                            layoutPriceSeekBar.priceSeekBar.currentValue =
                                if (pureValue + InsuranceContractFragment.PREMIUM_RATE_ADD_STEP <= mViewModel.dataModel.maxPremiumRate) pureValue + InsuranceContractFragment.PREMIUM_RATE_ADD_STEP else pureValue
                        }
                        layoutPriceSeekBar.imgMines.setOnClickListener {
                            val pureValue =
                                (layoutPriceSeekBar.priceSeekBar.currentValue / InsuranceContractFragment.PREMIUM_RATE_ADD_STEP).toLong() * InsuranceContractFragment.PREMIUM_RATE_ADD_STEP

                            layoutPriceSeekBar.priceSeekBar.currentValue =
                                if (pureValue - InsuranceContractFragment.PREMIUM_RATE_ADD_STEP >= mViewModel.dataModel.minPremiumRate) pureValue - InsuranceContractFragment.PREMIUM_RATE_ADD_STEP else pureValue
                        }
                        btnCalculate.setOnClickListener {
                            val currentDouble =
                                layoutPriceSeekBar.priceSeekBar.currentValue.toDouble()

                            val doubleCurrent = when{
                                tempCntCode == RED_CRESCENT_CODE ->{
                                    mViewModel.dataModel.minPremiumRate
                                }
                                currentDouble > mViewModel.dataModel.maxPremiumRate ->{
                                    mViewModel.dataModel.maxPremiumRate
                                }
                                else ->{
                                    currentDouble
                                }
                            }


                            mViewModel.checkAndCalculateSalaryForContract(
                                doubleCurrent.toLong().toString(),
                                mViewModel.dataModel.premiumRateCode
                            )
                        }

                    }
            }
            STEP_EDIT_CONTRACT -> {
                ContractStepFinalBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.apply {
                        index = STEP_EDIT_CONTRACT.step
                        title = STEP_EDIT_CONTRACT.title
                        nextButtonTitle = STEP_EDIT_CONTRACT.title
                    }
                    //  mViewModel.mldRegistrationInfo.value?.data?.apply {
                    val str = if (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN)
                        getString(
                            R.string.desc_final_confirm_guardian,
                            UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.guardianName),
                            UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.guardianNationalId),
                            getString(R.string.guardian),
                            UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.getFullName()),
                            getString(R.string.guardian_text_part2),
                            UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.nationalId),
                            UiUtils.createTextColorBlueAndBold(getString(R.string.insurance_type_heraf_and_optional)),
                            UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.selectedPercentDesc),
                            if (mViewModel.dataModel.cntDrmn == "1")
                                UiUtils.createTextColorGreenAndBold(getString(R.string.with_treatment_support))
                            else
                                UiUtils.createTextColorOrangeAndBold(getString(R.string.with_out_treatment_support)),
                            UiUtils.createTextColorGreenAndBold(
                                Utility.addSeparator(
                                    (mViewModel.dataModel.selectedSalary).toString().toInt()
                                )
                            ),
                            UiUtils.createTextColorBlueAndBold(PersianDate.today().shYear.toString() + "/" + PersianDate.today().shMonth + "/" + PersianDate.today().shDay),
                        ) else
                        getString(
                            R.string.desc_final_confirm,
                            UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.getFullName()),
                            UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.nationalId),
                            UiUtils.createTextColorBlueAndBold(getString(R.string.insurance_type_heraf_and_optional)),
                            UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.selectedPercentDesc),
                            if (mViewModel.dataModel.cntDrmn == "1")
                                UiUtils.createTextColorGreenAndBold(getString(R.string.with_treatment_support))
                            else
                                UiUtils.createTextColorOrangeAndBold(getString(R.string.with_out_treatment_support)),

                            UiUtils.createTextColorGreenAndBold(
                                Utility.addSeparator(
                                    (mViewModel.dataModel.selectedSalary).toString().toInt()
                                )
                            ),
                            UiUtils.createTextColorBlueAndBold(PersianDate.today().shYear.toString() + "/" + PersianDate.today().shMonth + "/" + PersianDate.today().shDay),
                        )


                    descFinalConfirm.text = HtmlCompat.fromHtml(
                        str,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )

                    if (mViewModel.mldCheckContractCondition.value?.data?.isPaid == true) {
                        stepperItem.nextStepEnable = false
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_can_not_edit_contact),
                            dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                        )
                    } else {
                        cbDescFinalConfirm.setOnCheckedChangeListener { _, isChecked ->
                            stepperItem.nextStepEnable = isChecked
                        }
                    }

                    cbDescFinalConfirm.isChecked = true

                }
            }
        }
    }

    private fun setFinalDesc() {
        mViewModel.dataModel.apply {
            val str = if (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN)
                getString(
                    R.string.desc_final_confirm_guardian,
                    UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.guardianName),
                    UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.guardianNationalId),
                    getString(R.string.guardian),
                    UiUtils.createTextColorGreenAndBold(getFullName()),
                    getString(R.string.guardian_text_part2),
                    UiUtils.createTextColorBlueAndBold(nationalId),
                    UiUtils.createTextColorBlueAndBold(getString(R.string.insurance_type_heraf_and_optional)),
                    UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.selectedPercentDesc),
                    if (mViewModel.dataModel.cntDrmn == "1")
                        UiUtils.createTextColorGreenAndBold(getString(R.string.with_treatment_support))
                    else
                        UiUtils.createTextColorOrangeAndBold(getString(R.string.with_out_treatment_support)),
                    UiUtils.createTextColorGreenAndBold(
                        Utility.addSeparator(
                            (mViewModel.dataModel.selectedSalary).toString().toInt()
                        )
                    ),
                    UiUtils.createTextColorBlueAndBold(PersianDate.today().shYear.toString() + "/" + PersianDate.today().shMonth + "/" + PersianDate.today().shDay),
                ) else
                getString(
                    R.string.desc_final_confirm,
                    UiUtils.createTextColorGreenAndBold(getFullName()),
                    UiUtils.createTextColorBlueAndBold(nationalId),
                    UiUtils.createTextColorBlueAndBold(getString(R.string.insurance_type_heraf_and_optional)),
                    UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.selectedPercentDesc),
                    if (mViewModel.dataModel.cntDrmn == "1")
                        UiUtils.createTextColorGreenAndBold(getString(R.string.with_treatment_support))
                    else
                        UiUtils.createTextColorOrangeAndBold(getString(R.string.with_out_treatment_support)),

                    UiUtils.createTextColorGreenAndBold(
                        Utility.addSeparator(
                            (mViewModel.dataModel.selectedSalary).toString().toInt()
                        )
                    ),
                    UiUtils.createTextColorBlueAndBold(PersianDate.today().shYear.toString() + "/" + PersianDate.today().shMonth + "/" + PersianDate.today().shDay),
                )
            mViewModel.dataModel.finalText = str
        }
        if (mViewModel.dataModel.finalText.isNotEmpty())
            (getStep(STEP_EDIT_CONTRACT.step) as? ContractStepFinalBinding)?.descFinalConfirm?.text =
                HtmlCompat.fromHtml(
                    mViewModel.dataModel.finalText,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
    }

    /////////////////////////////////////////////////Services Responses//////////////////////////////////////////////
    // initial responses
    private fun onCheckContractConditionResponse(result: CheckAgeAndHistoryResponse) {
        if (!result.isSuccess) return
        Timber.tag("dataContractCondition").v("${result.data}")
        Timber.tag("dataContractCondition")
            .v("${result.data?.provinceCode} ${result.data?.provinceName}")
        mViewModel.dataModel.loadAgeAndHistoryData(result.data)
        tempCntCode = mViewModel.dataModel.cntFreeJobCode
        initStepper()

    }

    private fun onRegistrationInfoResponse(result: ConcludingStudentInsuranceContractResponse) {
        if (!result.isSuccess) return
        result.data?.apply {
            mViewModel.dataModel.loadRegistrationInfo(this)
            viewDataBinding?.appBar?.apply {
                line2.visible()
                tvNationalCode.text = getString(
                    R.string.colon,
                    getString(R.string.label_national_code),
                    mViewModel.dataModel.nationalId
                )
                tvBirthDate.text = getString(
                    R.string.colon,
                    getString(R.string.birthdate),
                    getPersianDate(personalInfo?.dateOfBirth)

                )
                tvSubTitle.text = getString(
                    R.string.space,
                    mViewModel.dataModel.firstName,
                    mViewModel.dataModel.lastName
                )
            }
        }
    }

    private fun getStep(step: Int): ViewBinding? {
        return viewDataBinding?.stepper?.getStepLayoutBindingByStep(step)
    }

    private fun onUpdateUserAddressResponse(result: GeneralRes) {
        if (!result.isSuccess) return
        (getStep(STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.apply {
            mViewModel.dataModel.cityNameOfBranch = selectCityName.getValue(false)
            mViewModel.dataModel.usersAddress = inputAddress.getValue(false)
            mViewModel.dataModel.usersZipCode = inputZipCode.getValue(false)
            mViewModel.dataModel.usersMobile = inputMobile.getValue(false)
            mViewModel.dataModel.usersPhoneNumber = inputPhoneNumber.getValue(false)
        }

        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.SUCCESS,
            getString(R.string.message_success_verify_new_address_info)
        )
        dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
            override fun onConfirmClick() {
                (getStep(STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.root?.findViewById<View>(
                    R.id.btnNextStepRules
                )?.callOnClick()

            }

            override fun onCancelClick() {
            }
        })
        dialog.show(childFragmentManager, "FilterDialogFragment")
    }


    private fun onDownloadImageGuardianResponse(result: GeneralRes?) {
        if (result?.isSuccess == false) return

        mViewModel.dataModel.apply {
            mViewModel.imageDownloaded = true
            guardianshipImage =
                UploadedImageModel(
                    guid = guardianGuid,
                    imageUri = result?.data as? Uri?,
                    imageName = guardianImageName,
                    orgUri = tempImageUri
                )
            val imageFile = guardianshipImage
            guardianshipImageListAdapter.setItems(
                if (imageFile == null) emptyList() else listOf(
                    imageFile
                )
            )
            (getStep(STEP_UPLOAD_IMAGE.step) as? UploadImageLayoutBinding)?.root?.findViewById<View>(
                R.id.btnNextStepRules
            )?.callOnClick()
        }
    }


    private fun onDownloadImageResponse(result: GeneralRes) {
        if (!result.isSuccess) return
        mViewModel.dataModel.apply {
            imageFile =
                UploadedImageModel(
                    guid = guid,
                    imageUri = result.data as? Uri?,
                    imageName = tempImageName,
                    orgUri = tempImageUri
                )
            val imageFile = imageFile
            contractImageListAdapter.setItems(
                if (imageFile == null) emptyList() else listOf(
                    imageFile
                )
            )
            (getStep(STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.root?.findViewById<View>(
                R.id.btnNextStepRules
            )?.callOnClick()

        }

    }

    private fun onUploadImageResponse(result: UploadImageResponse) {
        if (!result.isSuccess)
            return
        mViewModel.dataModel.apply {
            guid = result.guid ?: ""
            imageFile =
                UploadedImageModel(
                    guid = result.guid,
                    imageUri = mViewModel.dataModel.tempImageUri,
                    imageName = mViewModel.dataModel.tempImageName,
                    orgUri = mViewModel.dataModel.tempImageUri
                )
        }
        val imageFile = mViewModel.dataModel.imageFile
        contractImageListAdapter.setItems(if (imageFile == null) emptyList() else listOf(imageFile))
    }

    private fun onPremiumOptionsResponse(result: ContractPremiumOptionsResponse) {
        if (!result.isSuccess) return

        mViewModel.dataModel.premiumOptionList.clear()
        mViewModel.dataModel.premiumOptionList.addAll(result.data?.list ?: emptyList())
        val adapter =
            (getStep(STEP_INSURANCE_PREMIUM.step)
                    as? ContractStepMonthlyInsurancePremiumBinding?)?.recyclerPaymentRate?.adapter as? PremiumRateAdapter
        adapter?.setItems(mViewModel.dataModel.premiumOptionList)
        adapter?.changeCheck(mViewModel.dataModel.premiumRateCode)

        if (tempCntCode== RED_CRESCENT_CODE) {
            mViewModel.dataModel.premiumRateCode = "03"
            mViewModel.dataModel.selectedPercentDesc = "18 درصد"
            adapter?.setEnabledItem("03")
        } else if (tempCntCode == MEDICAL_STUDENT_CODE) {
            mViewModel.dataModel.premiumRateCode = "02"
            mViewModel.dataModel.selectedPercentDesc = "14 درصد"
            adapter?.setEnabledItem("02")
        }
        (getStep(STEP_TREATMENT_SUPPORT.step) as? ContractStepTreatmentSupportBinding)?.root?.findViewById<View>(

            R.id.btnNextStepRules
        )?.callOnClick()


    }


    private fun onContractPremiumRateResponse(result: ContractPremiumRateResponse) {
        if (!result.isSuccess) return
        result.data?.apply {
            mViewModel.dataModel.paymentTabayi = paymentTabayi
            mViewModel.dataModel.minPremiumRate = lowPremium?.toDouble() ?: -1.0

            mViewModel.dataModel.maxPremiumRate =
                if (tempCntCode== RED_CRESCENT_CODE) {
                    lowPremium?.toDouble() ?: -1.0
                } else {
                    highPremium?.toDouble() ?: -1.0
                }
            mViewModel.dataModel.changePremiumRateCode = false

            (getStep(STEP_SALARY.step) as? ContractStepSalaryBinding)?.apply {

                layoutPriceSeekBar.root.isVisible =
                    tempCntCode!= RED_CRESCENT_CODE ||
                            mViewModel.dataModel.maxPremiumRate != mViewModel.dataModel.minPremiumRate

                ItemDesc9.descTxt.text = HtmlCompat.fromHtml(
                    getString(
                        R.string.desc_monthly_premium5,
                        UiUtils.createTextColorGreenAndBold(Utility.addSeparator(mViewModel.dataModel.minPremiumRate.toInt())),
                        UiUtils.createTextColorGreenAndBold(Utility.addSeparator(mViewModel.dataModel.maxPremiumRate.toInt()))
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                var rate = ""
                for (item in mViewModel.dataModel.premiumOptionList) {
                    if (item.spcrateCode == mViewModel.dataModel.premiumRateCode)
                        rate = item.insurDpercent
                }
                ItemDesc10.descTxt.text = getString(R.string.desc_monthly_premium7, rate)

                layoutPriceSeekBar.priceSeekBar.apply {
                    setMin(mViewModel.dataModel.minPremiumRate)
                    max = mViewModel.dataModel.maxPremiumRate
                    mViewModel.dataModel.selectedValueSeekbar =
                        mViewModel.dataModel.minPremiumRate.toDouble()

                    val selected = mViewModel.dataModel.selectedValueSeekbar

                    currentValue =
                        if (selected < mViewModel.dataModel.minPremiumRate)
                            mViewModel.dataModel.minPremiumRate
                        else if (selected > mViewModel.dataModel.maxPremiumRate)
                            mViewModel.dataModel.maxPremiumRate
                        else
                            selected

                    layoutPriceSeekBar.root.isVisible = mViewModel.dataModel.maxPremiumRate != mViewModel.dataModel.minPremiumRate
                }
            }

        }
        (getStep(STEP_INSURANCE_PREMIUM.step) as? ContractStepMonthlyInsurancePremiumBinding)?.root?.findViewById<View>(
            R.id.btnNextStepRules
        )?.callOnClick()
    }


    private fun onProvinceInfoResponse(result: ProvinceResponse) {
        val provinceList = result.data?.list
        if (result.isSuccess && !provinceList.isNullOrEmpty()) {
            mViewModel.dataModel.provinceName = provinceList[0].provinceName ?: ""
            mViewModel.dataModel.provinceCode = provinceList[0].provinceCode
        }
    }

    private fun onCalculateSalaryResponse(result: CalculateSalary) {
        if (!result.isSuccess) return
        (getStep(STEP_SALARY.step) as? ContractStepSalaryBinding)?.apply {
            stepperItem.nextStepEnable = true
            mViewModel.dataModel.selectedSalary = result.data ?: -1
            groupValue.visibility = View.VISIBLE

            tvShowValueOfCal.text = HtmlCompat.fromHtml(
                getString(
                    R.string.value_calcaluate_salary_for_contract,
                    UiUtils.createTextColorGreenAndBold(Utility.addSeparator(mViewModel.dataModel.selectedSalary))
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            if (mViewModel.dataModel.cntDrmn == "1")
                ItemDesc8.root.visible()
            else
                ItemDesc8.root.gone()
        }
    }


    // download pdf contract response
    private fun onDownloadPdfFileResponse(result: PdfDownloadResponse) {
        if (!result.isSuccess)
            return
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
        val bundle = Bundle()
        bundle.putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
        bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
        handlePageDestination(R.id.action_editContractFragment_to_Activity_pdf_view, bundle)
    }

    // update pdf contract response
    private fun onUpdateContractResponse(result: GeneralRes) {
        if (!result.isSuccess){
           mViewModel.dataModel.cntFreeJobCode = lastJobCode
            return
        }
        result.data?.apply {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.desc_success_update_contract)
            )
            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    requireActivity().onBackPressed()
                }

                override fun onCancelClick() {
                }
            })
            dialog.show(childFragmentManager, EditContractFragment().javaClass.simpleName)
        }
    }

    private fun onUploadGuardianshipImageResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.guardianshipImage =
                UploadedImageModel(
                    guid = result.guid,
                    imageUri = mViewModel.dataModel.guardianImageUri,
                    imageName = mViewModel.dataModel.guardianImageName,
                    orgUri = mViewModel.dataModel.guardianImageUri
                )
            guardianshipImageListAdapter.setItems(listOf(mViewModel.dataModel.guardianshipImage!!))
        }
    }

    private fun onCheckRedCrossStatusResponse(result: RedCrossStatusResponse) {
        if (result.isSuccess && result.data == true) {
            (getStep(STEP_INSURANCE_PREMIUM.step) as? ContractStepMonthlyInsurancePremiumBinding?)?.apply {
                mViewModel.dataModel.jobDesc = tempCntTitle
                selectInsuredJobTitle.getIt().error = null
                selectInsuredJobTitle.setValue(mViewModel.dataModel.jobDesc)
                mViewModel.dataModel.needToCalculate = true

                if (mViewModel.dataModel.premiumRateCode != "00")
                    stepperItem.nextStepEnable = true

                val adapter = recyclerPaymentRate.adapter
                if (tempCntCode == RED_CRESCENT_CODE) {
                    mViewModel.dataModel.premiumRateCode = "03"
                    mViewModel.dataModel.selectedPercentDesc = "18 درصد"
                    (adapter as? PremiumRateAdapter?)?.setEnabledItem("03")
                    setTreatmentSupport(true)
                } else if(tempCntCode == MEDICAL_STUDENT_CODE){
                    mViewModel.dataModel.premiumRateCode = "02"
                    mViewModel.dataModel.selectedPercentDesc = "14 درصد"
                    (adapter as? PremiumRateAdapter?)?.setEnabledItem("02")
                }
            }

        } else {
            val dialog =
                DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_select_job),
                btnCancel = false
            )
            dialog.titleBtnOk =
                getString(R.string.understand)
            dialog.show(childFragmentManager, "")
            return
        }
    }

    ///////////////////////////////////////////////// Call Backs //////////////////////////////////////////////
    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int,
    ) {
        mViewModel.uploadImage(body, requestCode)
        if (requestCode == REQUEST_CODE_IMAGE_DOC)
            mViewModel.dataModel.tempImageUri = imageUri
        else
            mViewModel.dataModel.guardianImageUri = imageUri
    }


    override fun onNextStepClickListener(
        stepIndex: Int,
        step: com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView,
    ) {
        viewDataBinding?.stepper?.apply {

            when (stepIndex) {
                STEP_USER_INFO.step ->
                    if (changedAddressInfo()) {
                        Timber.tag("debugUpdateUser").i("changedAddressInfo=true")
                        if (checkValidInput())
                            (getStep(STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.apply {
                                mViewModel.updateUserAddressInfo(
                                    updateAddressInfoRequest = UpdateAddressInfoRequest(
                                        cityId = mViewModel.dataModel.cityCode,
                                        address = inputAddress.getValue(false),
                                        zipCode = inputZipCode.getValue(false),
                                        mobile = inputMobile.getValue(false),
                                        phoneNumber = inputPhoneNumber.getValue(false),
                                        personal = Personal(ssn = mViewModel.dataModel.ssn)
                                    )
                                )

                            }

                    } else if (mViewModel.dataModel.guid != "00" && mViewModel.dataModel.imageFile == null)
                        mViewModel.downloadContractImage(mViewModel.dataModel.guid, 2)
                    else
                        nextStep()

                STEP_GUARDIANSHIP.step -> {
                    if (mViewModel.dataModel.guardianShip == GuardianType.FOR_ITSELF ||
                        (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN && checkGuardianshipInput())
                    ) {
                        nextStep()
                    }
                }
                STEP_TREATMENT_SUPPORT.step -> {
                    if (mViewModel.dataModel.premiumOptionList.isEmpty())
                        mViewModel.getPremiumOptions()
                    else
                        nextStep()
                }
                STEP_INSURANCE_PREMIUM.step -> {
                    if (mViewModel.dataModel.minPremiumRate < 0 || mViewModel.dataModel.maxPremiumRate < 0 || mViewModel.dataModel.changePremiumRateCode) {
                        mViewModel.getContractPremiumRate(
                            mViewModel.dataModel.premiumRateCode,
                            tempCntCode
                        )
                    } else {
                        (getStep(STEP_SALARY.step) as? ContractStepSalaryBinding)?.apply {
                            layoutPriceSeekBar.priceSeekBar.apply {
                                setMin(mViewModel.dataModel.minPremiumRate)
                                max = mViewModel.dataModel.maxPremiumRate
                                val selectedValue = mViewModel.dataModel.selectedValueSeekbar
                                if (selectedValue < mViewModel.dataModel.minPremiumRate || selectedValue > mViewModel.dataModel.maxPremiumRate)
                                    mViewModel.dataModel.selectedValueSeekbar =
                                        mViewModel.dataModel.minPremiumRate.toDouble()
                                currentValue = mViewModel.dataModel.selectedValueSeekbar
                            }
                            var rate = ""
                            for (item in mViewModel.dataModel.premiumOptionList) {
                                if (item.spcrateCode == mViewModel.dataModel.premiumRateCode)
                                    rate = item.insurDpercent
                            }
                            ItemDesc10.descTxt.text =
                                getString(R.string.desc_monthly_premium7, rate)

                            ItemDesc9.descTxt.text = HtmlCompat.fromHtml(
                                getString(
                                    R.string.desc_monthly_premium5,
                                    UiUtils.createTextColorGreenAndBold(
                                        Utility.addSeparator(
                                            mViewModel.dataModel.minPremiumRate.toInt()
                                        )
                                    ),
                                    UiUtils.createTextColorGreenAndBold(
                                        Utility.addSeparator(
                                            mViewModel.dataModel.maxPremiumRate.toInt()
                                        )
                                    )
                                ),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            tvShowValueOfCal.text = HtmlCompat.fromHtml(
                                getString(
                                    R.string.value_calcaluate_salary_for_contract,
                                    UiUtils.createTextColorGreenAndBold(Utility.addSeparator((mViewModel.dataModel.selectedSalary)))
                                ),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            ItemDesc8.descTxt.text =
                                HtmlCompat.fromHtml(
                                    getString(
                                        R.string.desc_monthly_premium4,
                                        UiUtils.createTextColorGreenAndBold(
                                            Utility.addSeparator(
                                                mViewModel.dataModel.paymentTabayi ?: 0
                                            )
                                        )
                                    ), HtmlCompat.FROM_HTML_MODE_LEGACY
                                )

                            val selected = mViewModel.dataModel.selectedValueSeekbar

                            layoutPriceSeekBar.priceSeekBar.currentValue =
                                if (selected < mViewModel.dataModel.minPremiumRate)
                                    mViewModel.dataModel.minPremiumRate
                                else if (selected > mViewModel.dataModel.maxPremiumRate)
                                    mViewModel.dataModel.maxPremiumRate
                                else
                                    selected

                            if (mViewModel.dataModel.selectedSalary > 0) {
                                stepperItem.nextStepEnable = true
                                groupValue.visible()

                                if (mViewModel.dataModel.cntDrmn == "1")
                                    ItemDesc8.root.visible()
                                else
                                    ItemDesc8.root.gone()

                            } else {
                                stepperItem.nextStepEnable = false
                                groupValue.gone()
                                ItemDesc8.root.gone()
                            }
                        }
                        nextStep()
                    }
                }
                STEP_SALARY.step -> {
                    setFinalDesc()
                    nextStep()
                }
                STEP_EDIT_CONTRACT.step -> {

                    if (mViewModel.mldCheckContractCondition.value?.data?.isPaid == true) {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_can_not_edit_contact)
                        )
                    } else {
                        lastJobCode = mViewModel.dataModel.cntFreeJobCode
                        mViewModel.dataModel.cntFreeJobCode = tempCntCode

                        if (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN) {
                            mViewModel.updateGuardianContract(
                                UpdateGuardianContract(mViewModel.dataModel),
                                mViewModel.dataModel.selectedValueSeekbar.toInt().toString()
                            )
                        } else
                            mViewModel.updateContract(
                                UpdateContractRequest(mViewModel.dataModel),
                                mViewModel.dataModel.selectedValueSeekbar.toInt().toString()
                            )
                    }


//                    mViewModel.dataModel.apply {
//                        mViewModel.updateContract(
//                            body = UpdateContractRequest(
//                                cntDrmn = cntDrmn,
//                                cntFreeJobCode = mViewModel.dataModel.cntFreeJobCode,
//                                guid = mViewModel.dataModel.guid,
//                                guidName = mViewModel.dataModel.tempImageName,
//                                premiumRateCode = mViewModel.dataModel.premiumRateCode
//                            ),
//                            Premium = mViewModel.dataModel.selectedValueSeekbar.toInt()
//                                .toString()
//                        )
//                    }


                }
                else -> {
                    nextStep()
                }
            }
        }

    }

    override fun onPreviousStepClickListener(
        stepIndex: Int,
        step: com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView,
    ) {
        viewDataBinding?.stepper?.previousStep()
    }
/////////////////////////////////////////////////Utils //////////////////////////////////////////////


    override fun chooseImage(requestCode: Int) {
        val intent = Intent(activity, MultiCustomGalleryUI::class.java)
        intent.putExtra(Constants.TEMPID, Constants.REQUEST_DEFAULT_IMAGE_TYPE)
        intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)
        resultImageLaunch.launch(intent)
    }

    private fun changedAddressInfo(): Boolean {

        (getStep(STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.apply {
            return !(mViewModel.dataModel.cityNameOfBranch == selectCityName.getValue(false) &&
                    mViewModel.dataModel.usersAddress.trim() == inputAddress.getValue(false)
                .trim() &&
                    mViewModel.dataModel.usersZipCode == inputZipCode.getValue(false) &&
                    mViewModel.dataModel.usersMobile == inputMobile.getValue(false) &&
                    mViewModel.dataModel.usersPhoneNumber == inputPhoneNumber.getValue(false))
        }
        return true
    }

    private fun checkValidInput(): Boolean {
        var message = ""
        (getStep(STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.apply {
            val validationPhoneResult = Utility.checkPhoneNumber(inputPhoneNumber.getValue())
            when {
                inputZipCode.getValue().length < 10 -> {
                    message = getString(R.string.error_input_length_zip_code)
                    inputZipCode.setError(message)
                }
                inputMobile.getValue().length < 11 -> {
                    message = getString(R.string.error_input_length_mobile)
                    inputMobile.setError(message)
                }
                validationPhoneResult != 0 -> {
                    message = getString(validationPhoneResult)
                    inputPhoneNumber.setError(message)
                }
            }
            return message == ""
        }
        return false
    }

    fun setupToolbarContract() {
        viewDataBinding?.let {
            it.appBar.apply {
                var toolbarTitle = ""
                arguments?.let { args ->
                    ImageUtils.loadImage(imgIcon, Utility.getToolbarIconImage(args))
                    toolbarTitle = Utility.getToolbarTitle(args)
                }
                toolbar.imgInfo.visibility = View.GONE
                tvTitle.text = toolbarTitle

                appBarView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                    val maxScroll = appBarLayout.totalScrollRange
                    val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

                    handleAlphaOnTitle(percentage, containerAppbarTitle)
                    handleToolbarTitleVisibility(
                        percentage,
                        toolbar.tvToolbarTitle,
                        toolbarTitle,
                        it.appbarBackgroundImage.imageBackground,
                        null
                    )
                })
            }
        }
    }

    fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }


    private fun showDialog(
        type: ContractDialogType,
        title: String,
        onResultCallBack: MenuInterface.OnResult,
    ) {
        val dialog = MenuDialogFragment.newInstance(true, title).apply {
            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@EditContractFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            ContractDialogType.JOB_TITLES ->
                                mViewModel.getFreelancerJobTitles(jobTitle = "")
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.jobCode,
                                                title = it.jobTitle
                                            )
                                        }
                                        updateData(result)
                                    }
                            ContractDialogType.CITY_LIST_EMPTY_PROVINCE -> {
                                mViewModel.getCitiesList(provinceCode = null)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.cityCode,
                                                title = it.cityName
                                            )
                                        }
                                        updateData(result)
                                    }
                            }
                            else -> {}
                        }
                    }
                }
            }, onResultCallBack, object : MenuInterface.OnSearch {
                override fun onSearch(str: String) {
                    this@EditContractFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            ContractDialogType.JOB_TITLES -> {
                                mViewModel.getFreelancerJobTitles(str).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.jobCode,
                                            title = it.jobTitle
                                        )
                                    }
                                    updateData(result)
                                }
                            }
                            ContractDialogType.CITY_LIST_EMPTY_PROVINCE -> {
                                mViewModel.getCitiesList(
                                    provinceCode = null,
                                    cityName = str
                                ).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.cityCode,
                                            title = it.cityName
                                        )
                                    }
                                    updateData(result)
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
            })
        }

        dialog.show(childFragmentManager, EditContractFragment::javaClass.name)
    }

    private fun checkGuardianshipInput(): Boolean {

        var resultOk = false
        (getStep(STEP_GUARDIANSHIP.step) as? GuardianshipContractStepBinding)?.apply {

            when {
                etNationalCode.getValueNationalCode().isBlank() ||
                        etName.getValue().isBlank() ||
                        etGuardianShipNumber.getValue().isBlank() -> {
                    //do nothing--- error shown in getValue method..
                }
                guardianshipDate.inputDate.text.isNullOrBlank() -> {
                    guardianshipDate.tilDate.error =
                        getString(R.string.error_select_guardianship_date)
                }
                etGuardianShipNumber.getValue(false).length < 10 -> {
                    etGuardianShipNumber.getLayout().error =
                        getString(R.string.error_guardianship_number)
                }
                guardianshipDate.inputDate.text?.length!! < 7 -> {
                    guardianshipDate.tilDate.error =
                        getString(R.string.error_select_guardianship_date)
                }
                else -> resultOk = true
            }

            if (mViewModel.dataModel.guardianshipImage == null) {
                resultOk = false
                addGuardianshipDoc.uploadErrorGroup.visibility = View.VISIBLE
            } else {
                addGuardianshipDoc.uploadErrorGroup.visibility = View.GONE
            }

            if (resultOk) {
                mViewModel.dataModel.guardianName = etName.getValue(false)
                mViewModel.dataModel.guardianDate = guardianshipDate.inputDate.text.toString()
                mViewModel.dataModel.guardianNationalId = etNationalCode.getValueNationalCode(false)
                mViewModel.dataModel.guardianNumber = etGuardianShipNumber.getValue(false)
            }

        }
        return resultOk

    }

    override fun onDestroy() {
        val folder =
            File("${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tempImage/")
        folder.deleteRecursively()
        super.onDestroy()
    }

    private fun setTreatmentSupport(isSupport: Boolean) {
        (getStep(STEP_TREATMENT_SUPPORT.step) as? ContractStepTreatmentSupportBinding)?.apply {
            if (isSupport) {
                rbWantTreatmentSupport.isChecked = true
                rbNotWantTreatmentSupport.isClickable = false
                checkboxAgreement.isChecked = true
                mViewModel.dataModel.cntDrmn = "1"
                agreementLayout.visible()
                stepperItem.nextStepEnable = mViewModel.dataModel.agreement

            } else {
                rbNotWantTreatmentSupport.isClickable = true
            }
        }
    }
}

