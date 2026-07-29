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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.AppBarLayout
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.ProvinceResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateSalary
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumRateResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.Personal
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.UpdateAddressInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianOptionalContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateOptionalContract
import com.tamin.taminhamrah.databinding.ContractStepFinalBinding
import com.tamin.taminhamrah.databinding.ContractStepSalaryBinding
import com.tamin.taminhamrah.databinding.ContractStepUserInfoBinding
import com.tamin.taminhamrah.databinding.FragmentShowContractBinding
import com.tamin.taminhamrah.databinding.GuardianshipContractStepBinding
import com.tamin.taminhamrah.databinding.ShowContractStepContractInfoBinding
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
import com.tamin.taminhamrah.ui.home.services.contracts.ContractBaseFragment.Companion.REQUEST_CODE_IMAGE_GUARDIANSHIP
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditOptionalContract
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditOptionalContract.STEP_CONTRACT_INFO
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditOptionalContract.STEP_EDIT_CONTRACT
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditOptionalContract.STEP_GUARDIANSHIP
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditOptionalContract.STEP_SALARY
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumEditOptionalContract.STEP_USER_INFO
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
class EditOptionalContractFragment : BaseFragment<FragmentShowContractBinding, ContractViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    override val mViewModel: ContractViewModel by viewModels()

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

    val guardianshipImageListAdapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(onGuardianshipImageClickListener)
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
        return Pair(com.tamin.taminhamrah.BR.viewModel, mViewModel)
    }

    override fun getLayoutId() = R.layout.fragment_show_contract

    override fun initView() {
        viewDataBinding?.appBar?.toolbar?.imageBack?.setOnClickListener { backButtonPress() }
        setupToolbarContract()
    }

    override fun getData() {
        mViewModel.getRegistrationInfo(isOptional = true)
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
        mViewModel.mldDownloadImageGuardian.observe(this, ::onDownloadImageGuardianResponse)
        mViewModel.mldPremiumRateForContract.observe(this, ::onContractPremiumRateResponse)
        mViewModel.mldCheckAndCalculateSalaryForContract.observe(this, ::onCalculateSalaryResponse)
        mViewModel.mldGetProvinceWhitOutPaging.observe(this, ::onProvinceInfoResponse)
        mViewModel.mldUpdateContract.observe(this, ::onUpdateContractResponse)
        mViewModel.mldPdf.observe(this, ::onDownloadPdfFileResponse)
        mViewModel.mldUploadImageGuardianshipDoc.observe(this, ::onUploadGuardianshipImageResponse)
    }


    /////////////////////////////////////////////////STEPS//////////////////////////////////////////////
    fun getStepsView() = EnumEditOptionalContract.values().map { inflateStepView(it) }

    private fun initStepper(initialStep: Int = 1) {
        viewDataBinding?.apply {
            stepper.initial(getStepsView(), initialStep)
            stepper.onNextStepClickListener = this@EditOptionalContractFragment
            stepper.onPreviousStepClickListener = this@EditOptionalContractFragment
        }
    }

    var loadTempPersonalInfo = false


    fun inflateStepView(step: EnumEditOptionalContract): ViewBinding {
        return when (step) {
            STEP_CONTRACT_INFO -> {
                ShowContractStepContractInfoBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = STEP_CONTRACT_INFO.step
                    stepperItem.title = STEP_CONTRACT_INFO.title
                    valueInsuranceNum.text = mViewModel.dataModel.insuranceId
                    valueBranchInfo.text =
                        "${mViewModel.dataModel.cityNameOfBranch} - ${mViewModel.dataModel.branchAddress}"
                }
            }
            STEP_USER_INFO -> {
                ContractStepUserInfoBinding
                    .inflate(LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true)
                    .apply {
                        stepperItem.index = STEP_USER_INFO.step
                        stepperItem.title = STEP_USER_INFO.title

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
            STEP_GUARDIANSHIP -> {
                GuardianshipContractStepBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = STEP_GUARDIANSHIP.step
                    stepperItem.title = STEP_GUARDIANSHIP.title

                    addGuardianshipDoc.tvUploadHint.text = getString(R.string.label_image_guid)
                    addGuardianshipDoc.tvTitle.text = getString(R.string.label_guardianship_doc)

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
                    // Im-age Error
                    addGuardianshipDoc.uploadErrorImg.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_recycler_color_icon
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    addGuardianshipDoc.tvUploadError.text = getString(R.string.label_error_image)
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
            STEP_SALARY -> {
                ContractStepSalaryBinding
                    .inflate(LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true)
                    .apply {
                        stepperItem.index = step.step
                        stepperItem.title = step.title
                        ItemDesc10.descTxt.text = getText(R.string.desc_monthly_premium7_optional)
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
                        ItemDesc8.root.gone()

                        if (mViewModel.dataModel.selectedSalary > 0) {

                            tvShowValueOfCal.text = HtmlCompat.fromHtml(
                                getString(
                                    R.string.value_calcaluate_salary_for_contract,
                                    UiUtils.createTextColorGreenAndBold(Utility.addSeparator((mViewModel.dataModel.selectedSalary)))
                                ),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            stepperItem.nextStepEnable = true
                        } else {
                            stepperItem.nextStepEnable = false
                            groupValue.visibility = View.GONE
                        }
                        val selected = mViewModel.dataModel.selectedValueSeekbar

                        layoutPriceSeekBar.priceSeekBar.currentValue =
                            if (selected < mViewModel.dataModel.minPremiumRate) mViewModel.dataModel.minPremiumRate else selected

                        layoutPriceSeekBar.priceSeekBar.setListener { _, currentValue ->
                            if (mViewModel.dataModel.selectedValueSeekbar != currentValue) {
                                stepperItem.nextStepEnable = false
                                groupValue.visibility = View.GONE
                                mViewModel.dataModel.selectedSalary = -1
                            }
                            mViewModel.dataModel.selectedValueSeekbar = currentValue.toDouble()
                        }
                        btnCalculate.setOnClickListener {
                            mViewModel.calculateSalaryForOptionalContract(
                                layoutPriceSeekBar.priceSeekBar.currentValue.toInt().toString()
                            )
                        }

                        val min = mViewModel.dataModel.minPremiumRate
                        val max = mViewModel.dataModel.maxPremiumRate
                        if (min > 0 && max > 0 && min == max) {
                            btnCalculate.gone()
                            stepperItem.nextStepEnable = true
                            layoutPriceSeekBar.root.gone()
                            ItemDesc9.root.gone()
                            tvSelectedValue.visible()
                            imgSelectedValue.visible()
                            tvSelectedValue.text = HtmlCompat.fromHtml(
                                getString(
                                    R.string.value_for_contract,
                                    UiUtils.createTextColorGreenAndBold(Utility.addSeparator(min.toInt()))
                                ),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
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
                    stepperItem.index = STEP_EDIT_CONTRACT.step
                    stepperItem.title = STEP_EDIT_CONTRACT.title
                    stepperItem.nextButtonTitle = STEP_EDIT_CONTRACT.title

                    setFinalDesc()

                    cbDescFinalConfirm.setOnCheckedChangeListener { _, isChecked ->
                        stepperItem.nextStepEnable = isChecked
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
                    R.string.optional_desc_final_confirm_guardian,
                    UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.guardianName),
                    UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.guardianNationalId),
                    getString(R.string.guardian),
                    UiUtils.createTextColorGreenAndBold(getFullName()),
                    getString(R.string.guardian_text_part2),
                    UiUtils.createTextColorBlueAndBold(nationalId),
                    UiUtils.createTextColorBlueAndBold(getString(R.string.optional_percent)),
                    UiUtils.createTextColorGreenAndBold(
                        Utility.addSeparator(
                            (mViewModel.dataModel.selectedSalary).toString().toInt()
                        )
                    ),
                    UiUtils.createTextColorBlueAndBold(PersianDate.today().shYear.toString() + "/" + PersianDate.today().shMonth + "/" + PersianDate.today().shDay),
                ) else
                getString(
                    R.string.optional_desc_final_confirm,
                    UiUtils.createTextColorGreenAndBold(getFullName()),
                    UiUtils.createTextColorBlueAndBold(nationalId),
                    UiUtils.createTextColorBlueAndBold(getString(R.string.optional_percent)),
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

//        if (result.data?.isPaid == true)
//            showAlertDialog(
//                MessageOfRequestDialogFragment.MessageType.ERROR,
//                getString(R.string.error_can_not_edit_contact),
//                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
//            ) else {
        mViewModel.dataModel.loadAgeAndHistoryData(result.data, true)
        initStepper()
        //    }
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
                if (imageFile == null) emptyList() else listOf(imageFile)
            )
//            (getStep(STEP_USER_INFO.step) as? UploadImageLayoutBinding)?.root?.findViewById<View>(
//                R.id.btnNextStepRules
//            )?.callOnClick()
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


    private fun onContractPremiumRateResponse(result: ContractPremiumRateResponse) {
        if (!result.isSuccess) return
        result.data?.apply {
            mViewModel.dataModel.minPremiumRate = lowPremium?.toDouble() ?: -1.0
            mViewModel.dataModel.maxPremiumRate = highPremium?.toDouble() ?: -1.0

            (getStep(STEP_SALARY.step) as? ContractStepSalaryBinding)?.apply {
                ItemDesc9.descTxt.text = HtmlCompat.fromHtml(
                    getString(
                        R.string.desc_monthly_premium5,
                        UiUtils.createTextColorGreenAndBold(Utility.addSeparator(mViewModel.dataModel.minPremiumRate.toInt())),
                        UiUtils.createTextColorGreenAndBold(Utility.addSeparator(mViewModel.dataModel.maxPremiumRate.toInt()))
                    ), HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                layoutPriceSeekBar.priceSeekBar.apply {
                    setMin(mViewModel.dataModel.minPremiumRate)
                    max = mViewModel.dataModel.maxPremiumRate
                    mViewModel.dataModel.selectedValueSeekbar =
                        mViewModel.dataModel.minPremiumRate
                }


            }
        }

        (getStep(STEP_GUARDIANSHIP.step) as? GuardianshipContractStepBinding)?.root?.findViewById<View>(
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
        (getStep(EnumEditOptionalContract.STEP_SALARY.step) as? ContractStepSalaryBinding)?.apply {
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
        if (!result.isSuccess) return
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


    ///////////////////////////////////////////////// Call Backs //////////////////////////////////////////////
    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int,
    ) {
        mViewModel.uploadImage(body, requestCode)
        if (requestCode == ContractBaseFragment.REQUEST_CODE_IMAGE_DOC)
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
                STEP_USER_INFO.step -> if (changedAddressInfo()) {
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
                } else
                    nextStep()

                STEP_GUARDIANSHIP.step -> {
                    if (mViewModel.dataModel.guardianShip == GuardianType.FOR_ITSELF ||
                        (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN && checkGuardianshipInput())
                    ) {
                        if (mViewModel.dataModel.minPremiumRate < 0 || mViewModel.dataModel.maxPremiumRate < 0 ) {
                            mViewModel.getOptionalContractPremiumRate()
                        } else {

                            (getStep(STEP_SALARY.step) as? ContractStepSalaryBinding)?.apply {
                                layoutPriceSeekBar.priceSeekBar.apply {
                                    setMin(mViewModel.dataModel.minPremiumRate)
                                    max = mViewModel.dataModel.maxPremiumRate
                                    val selectedValue = mViewModel.dataModel.selectedValueSeekbar
                                    if (selectedValue < mViewModel.dataModel.minPremiumRate || selectedValue > mViewModel.dataModel.maxPremiumRate)
                                        mViewModel.dataModel.selectedValueSeekbar =
                                            mViewModel.dataModel.minPremiumRate

                                    Timber.tag("ChangeSelectedVal")
                                        .w("selectedValueSeekbar=${mViewModel.dataModel.selectedValueSeekbar}\ncurrentValue=$currentValue")
                                    currentValue =
                                        mViewModel.dataModel.selectedValueSeekbar
                                }
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
                                if (mViewModel.dataModel.selectedSalary > 0) {
                                    stepperItem.nextStepEnable = true
                                    groupValue.visible()
                                } else {
                                    stepperItem.nextStepEnable = false
                                    groupValue.gone()
                                }
                                ItemDesc8.root.gone()
                                val min = mViewModel.dataModel.minPremiumRate
                                val max = mViewModel.dataModel.maxPremiumRate
                                if (min > 0 && max > 0 && min == max) {
                                    btnCalculate.gone()
                                    stepperItem.nextStepEnable = true
                                    layoutPriceSeekBar.root.gone()
                                    ItemDesc9.root.gone()
                                    tvSelectedValue.visible()
                                    imgSelectedValue.visible()
                                    tvSelectedValue.text = HtmlCompat.fromHtml(
                                        getString(
                                            R.string.value_for_contract,
                                            UiUtils.createTextColorGreenAndBold(
                                                Utility.addSeparator(
                                                    min.toInt()
                                                )
                                            )
                                        ),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY
                                    )
                                }

                            }
                            nextStep()
                        }
                    }
                }
                STEP_SALARY.step -> {
                    setFinalDesc()
                    nextStep()
                }
                STEP_EDIT_CONTRACT.step -> {


                    if (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN) {
                        mViewModel.updateGuardianOptionalContract(
                            UpdateGuardianOptionalContract(mViewModel.dataModel),
                            mViewModel.dataModel.selectedValueSeekbar.toInt().toString()
                        )
                    } else
                        mViewModel.updateOptionalContract(
                            UpdateOptionalContract(),
                            mViewModel.dataModel.selectedValueSeekbar.toInt().toString()
                        )

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
        (getStep(EnumEditOptionalContract.STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.apply {
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
        (getStep(EnumEditOptionalContract.STEP_USER_INFO.step) as? ContractStepUserInfoBinding)?.apply {
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
                validationPhoneResult!=0 -> {
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
                    this@EditOptionalContractFragment.lifecycleScope.launchWhenCreated {
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
                    this@EditOptionalContractFragment.lifecycleScope.launchWhenCreated {
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
                addGuardianshipDoc.uploadErrorGroup.visible()
            }

            if (resultOk) {
                addGuardianshipDoc.uploadErrorGroup.gone()
                etGuardianShipNumber.getLayout().isErrorEnabled = false
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

}