package com.tamin.taminhamrah.ui.home.services.contracts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.MEDICAL_STUDENT_CODE
import com.tamin.taminhamrah.Constants.RED_CRESCENT_CODE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.RedCrossStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateSalary
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractByGuardianRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumOptionsResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumRateResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FinalConfirmResponse
import com.tamin.taminhamrah.databinding.ContractStepBranchBinding
import com.tamin.taminhamrah.databinding.ContractStepCondistionBinding
import com.tamin.taminhamrah.databinding.ContractStepConditionTermsBinding
import com.tamin.taminhamrah.databinding.ContractStepFinalBinding
import com.tamin.taminhamrah.databinding.ContractStepMonthlyInsurancePremiumBinding
import com.tamin.taminhamrah.databinding.ContractStepRegistrationBinding
import com.tamin.taminhamrah.databinding.ContractStepSalaryBinding
import com.tamin.taminhamrah.databinding.ContractStepTreatmentSupportBinding
import com.tamin.taminhamrah.databinding.ContractStepUploadImageBinding
import com.tamin.taminhamrah.databinding.ContractStepUserInfoBinding
import com.tamin.taminhamrah.databinding.GuardianshipContractStepBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_AUTHORIZATION
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_CONTRACT_APPLICANT
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_CONTRACT_TERMS
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_INSURANCE_PREMIUM
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_REGISTRATION
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_SALARY
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_SELECT_BRANCH
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_SUBMIT_CONTRACT
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_TREATMENT_SUPPORT
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_UPLOAD_IMAGE
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep.STEP_USER_INFO
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.studentContract.adapter.PremiumRateAdapter
import com.tamin.taminhamrah.ui.home.services.studentContract.model.GuardianType
import com.tamin.taminhamrah.ui.home.services.studentContract.payment.insurance.InsurancePaymentFragment
import com.tamin.taminhamrah.utils.HelperDate
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import com.tamin.taminhamrah.widget.LocationSelectorWidget
import com.tamin.taminhamrah.widget.LocationSelectorWidget.ListType.TYPE_BRANCH
import com.tamin.taminhamrah.widget.LocationSelectorWidget.ListType.TYPE_CITY
import com.tamin.taminhamrah.widget.LocationSelectorWidget.ListType.TYPE_PROVINCE
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.annotations.NotNull
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.util.Date

@AndroidEntryPoint
class InsuranceContractFragment :
    ContractBaseFragment<EnumFreelanceStep>() {

    override val mViewModel: ContractViewModel by viewModels()

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun setupObserver() {
        super.setupObserver()
        mViewModel.mldPremiumRateForContract.observe(this, ::onContractPremiumRateResponse)
        mViewModel.mldCheckAndCalculateSalaryForContract.observe(this, ::onCalculateSalaryResponse)
        mViewModel.mldFinalRequestMakeContract.observe(this, ::onMakeContractResponse)
        mViewModel.mldPremiumOptions.observe(this, ::onPremiumOptionsResponse)
        mViewModel.mldCheckRedCrossStatus.observe(this, ::onCheckRedCrossStatusResponse)
    }


    override fun getStepsView() = EnumFreelanceStep.values().map { inflateStepView(it) }

    override fun inflateStepView(step: EnumFreelanceStep): ViewBinding {
        return when (step) {
            STEP_REGISTRATION -> {
                ContractStepRegistrationBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    itemDesc2.descTxt.text = HtmlCompat.fromHtml(
                        getString(
                            R.string.label_have_insurance_number_of_tamin,
                            UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.insuranceId)
                        ),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
            }
            STEP_AUTHORIZATION -> {
                ContractStepCondistionBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    val eligibilityStatus =
                        mViewModel.dataModel.eligibilityStatus

                    stepperItem.nextStepEnable =
                        if (eligibilityStatus <= 0 || eligibilityStatus >= 5) {
                            imgEligibilityStatus.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(), R.drawable.ic_close_outline
                                )
                            )
                            labelEligibilityStatus.text = HtmlCompat.fromHtml(
                                getString(
                                    R.string.you_dont_have_the_necessary_conditions_to_sign_an_insurance_contract,
                                    mViewModel.getEligibilityStatusList()[4],
                                    UiUtils.createTextColorGreenAndBold(getString(if (contractType == EnumInsuranceType.TYPE_STUDENT) R.string.label_student_insurance else R.string.label_freelancer_insurance))
                                ),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            false
                        } else {
                            var message = ""
                            if (eligibilityStatus == 3) {

                                mViewModel.mldCheckContractCondition.value?.data?.apply {
                                    val year = newAge?.substring(0, 2)
                                    val month = newAge?.substring(2, 4)
                                    val day = newAge?.substring(4, 6)

                                    val ageMessage = getString(
                                        R.string.title_contract_eligibility_step_3,
                                        history?.toString(),
                                        year,
                                        month,
                                        day
                                    )

                                    message = getString(
                                        R.string.you_have_the_necessary_conditions_to_sign_an_insurance_contract,
                                        ageMessage,
                                        UiUtils.createTextColorGreenAndBold(getString(R.string.label_freelancer_insurance))
                                    )
                                }
                            } else {
                                message = getString(
                                    R.string.you_have_the_necessary_conditions_to_sign_an_insurance_contract,
                                    mViewModel.getEligibilityStatusList()[eligibilityStatus - 1],
                                    UiUtils.createTextColorGreenAndBold(getString(R.string.label_freelancer_insurance))
                                )
                            }

                            labelEligibilityStatus.text = HtmlCompat.fromHtml(
                                message,
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )

                            imgEligibilityStatus.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(), R.drawable.ic_check_outline
                                )
                            )
                            true
                        }
                }
            }
            STEP_CONTRACT_TERMS -> {
                ContractStepConditionTermsBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {

                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    ItemDescRules.imageView4.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_color_orange
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )

                    labelDescCommitment.text =
                        HtmlCompat.fromHtml(
                            getString(
                                R.string.confirm_rules_desc,
                                if (mViewModel.dataModel.isMan()) getString(R.string.gentleman) else getString(
                                    R.string.lady
                                ),
                                UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.getFullName())
                            ),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )

                    cbConfirmRules.setOnCheckedChangeListener { _, checked ->
                        mViewModel.dataModel.isConfirmRules = checked
                        stepperItem.nextStepEnable = mViewModel.dataModel.isConfirmRules
                    }
                    stepperItem.nextStepEnable = mViewModel.dataModel.isConfirmRules
                    cbConfirmRules.isChecked = mViewModel.dataModel.isConfirmRules

                    btnShowRules.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.TOOLBAR_TITLE,
                            getString(R.string.label_rules_and_equlation)
                        )
                        bundle.putString(
                            PdfViewerActivity.ARG_PDF_FILE_ASSET,
                            "rulesAndRegulationsHtmlFile/rules.pdf"
                        )
                        handlePageDestination(R.id.action_contract_pdf, bundle)
                    }
                }
            }
            STEP_USER_INFO -> {
                ContractStepUserInfoBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {

                    stepperItem.apply {
                        index = step.stepIndex
                        title = step.title
                    }

                    itemDescAddress.descTxt.text = getString(R.string.label_desc_address)
                    selectCityName.setValue(mViewModel.dataModel.usersCity)
                    inputMobile.getInput().setText(mViewModel.dataModel.usersMobile)

                    if (!loadTempPersonalInfo) {
                        inputAddress.getInput().setText(mViewModel.dataModel.usersAddress)
                        inputPhoneNumber.getInput().setText(mViewModel.dataModel.usersPhoneNumber)
                        inputZipCode.getInput().setText(mViewModel.dataModel.usersZipCode)
                    } else {
                        loadTempPersonalInfo = false
                        inputAddress.getInput().setText(mViewModel.dataModel.tempAddress)
                        inputPhoneNumber.getInput().setText(mViewModel.dataModel.tempPhoneNumber)
                        inputZipCode.getInput().setText(mViewModel.dataModel.tempZipCode)
                    }

                    if (!mViewModel.dataModel.mobileExist)
                        inputMobile.gone()
                    else {
                        inputMobile.visible()
                        inputMobile.enableView(false)
                    }

                    selectCityName.getIt().setOnClickListener {
                        showDialog(
                            ContractDialogType.CITY_LIST_EMPTY_PROVINCE,
                            getString(R.string.label_select_city_name),
                            object : MenuInterface.OnResult {
                                override fun onResult(itemResult: MenuModel) {
                                    if (itemResult.title != null) {
                                        mViewModel.dataModel.usersCity = itemResult.title ?: ""
                                        selectCityName.setValue(mViewModel.dataModel.usersCity)
                                    }

                                }
                            })

                    }

                }
            }
            STEP_CONTRACT_APPLICANT -> {
                GuardianshipContractStepBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.apply {
                        index = step.stepIndex
                        title = step.title
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
                    addGuardianshipDoc.uploadErrorImg
                        .setColorFilter(
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
                        }
                        GuardianType.FOR_ITSELF -> {
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
                    etNationalCode.getInput().doAfterTextChanged {
                        mViewModel.dataModel.guardianNationalId =
                            etNationalCode.getInput().text.toString()
                    }
                    etName.getInput().doAfterTextChanged {
                        mViewModel.dataModel.guardianName = etName.getInput().text.toString()
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
            STEP_SELECT_BRANCH -> {
                ContractStepBranchBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title


                    locationSelector.setListener(object :
                        LocationSelectorWidget.OnLocationSelectListener {
                        override fun onLocationSelect(
                            type: LocationSelectorWidget.ListType,
                            title: String?,
                            code: String?
                        ) {
                            when (type) {
                                TYPE_PROVINCE -> {
                                    mViewModel.dataModel.provinceName = title ?: ""
                                    mViewModel.dataModel.provinceCode = code ?: ""
                                    mViewModel.dataModel.cityNameOfBranch = ""
                                    mViewModel.dataModel.cityCode = ""
                                    mViewModel.dataModel.branchName = ""
                                    mViewModel.dataModel.brchCodeNew = ""

                                }
                                TYPE_CITY -> {
                                    mViewModel.dataModel.cityNameOfBranch = title ?: ""
                                    mViewModel.dataModel.cityCode = code ?: ""
                                    mViewModel.dataModel.branchName = ""
                                    mViewModel.dataModel.brchCodeNew = ""
                                }
                                TYPE_BRANCH -> {
                                    mViewModel.dataModel.branchName = title ?: ""
                                    mViewModel.dataModel.brchCodeNew = code ?: ""

                                    if (!mViewModel.dataModel.provinceCode.isNullOrEmpty() &&
                                        !mViewModel.dataModel.cityCode.isNullOrEmpty() &&
                                        !mViewModel.dataModel.brchCodeNew.isNullOrEmpty()
                                    )
                                        stepperItem.nextStepEnable = true
                                }
                            }
                        }
                    })

                    locationSelector.setProvince(
                        mViewModel.dataModel.provinceName,
                        mViewModel.dataModel.provinceCode
                    )
                    locationSelector.setCity(
                        mViewModel.dataModel.cityNameOfBranch,
                        mViewModel.dataModel.cityCode
                    )
                    locationSelector.setBranch(
                        mViewModel.dataModel.branchName,
                        mViewModel.dataModel.brchCodeNew
                    )

                    val province = locationSelector.getProvinceId()
                    val city = locationSelector.getCityId()
                    val branch = locationSelector.getBranchId()

                    if (!province.isNullOrEmpty() && !city.isNullOrEmpty() && !branch.isNullOrEmpty())
                        stepperItem.nextStepEnable = true
                    ItemDesc3.descTxt.text = getString(R.string.desc_select_branch1)
                    ItemDesc4.descTxt.text = getString(R.string.desc_select_branch2)

                }
            }
            STEP_UPLOAD_IMAGE -> {
                ContractStepUploadImageBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.apply {
                        index = step.stepIndex
                        title = step.title
                    }

                    layoutUploadImage.recycler.apply {
                        this.adapter = contractImageListAdapter
                        if (itemDecorationCount == 0) {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }
                    }

                    if (mViewModel.dataModel.imageFile != null)
                        contractImageListAdapter.setItems(listOf(mViewModel.dataModel.imageFile!!))

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
                            } else {
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.INFO,
                                    getString(R.string.error_max_upload_1_item)
                                )
                            }
                        }
                    }
                }
            }
            STEP_TREATMENT_SUPPORT -> {
                ContractStepTreatmentSupportBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.apply {
                        index = step.stepIndex
                        title = step.title
                    }

                    //Todo:remove this if after that remove restart methode
                    if (mViewModel.dataModel.cntFreeJobCode ==  RED_CRESCENT_CODE) {
                        rbNotWantTreatmentSupport.isClickable = false
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
                            R.id.action_contract_to_DependentsFragment,
                            bundle = bundle
                        )

                    }
                    rgTreatmentSupport.setOnCheckedChangeListener(null)
                    if (mViewModel.dataModel.cntDrmn == "1") {
                        rbWantTreatmentSupport.isChecked = true
                        agreementLayout.visible()
                    } else if (mViewModel.dataModel.cntDrmn == "2") {
                        agreementLayout.gone()
                        rbNotWantTreatmentSupport.isChecked = true
                    }

                    rgTreatmentSupport.setOnCheckedChangeListener { _, _ ->
                        if (rbWantTreatmentSupport.isChecked) {
                            mViewModel.dataModel.cntDrmn = "1"
                            agreementLayout.visible()
                            stepperItem.nextStepEnable = mViewModel.dataModel.agreement

                        } else {
                            stepperItem.nextStepEnable = true
                            mViewModel.dataModel.cntDrmn = "2"
                            agreementLayout.gone()
                        }

                        mViewModel.dataModel.needToCalculate = true
                    }

                    checkboxAgreement.setOnCheckedChangeListener { p0, p1 ->
                        mViewModel.dataModel.agreement = checkboxAgreement.isChecked
                        stepperItem.nextStepEnable =
                            (mViewModel.dataModel.agreement && mViewModel.dataModel.cntDrmn == "1") || mViewModel.dataModel.cntDrmn == "2"

                    }
                    if (mViewModel.dataModel.agreement)
                        checkboxAgreement.isChecked = true

                    stepperItem.nextStepEnable =
                        (mViewModel.dataModel.agreement && mViewModel.dataModel.cntDrmn == "1") || mViewModel.dataModel.cntDrmn == "2"


                }
            }
            STEP_INSURANCE_PREMIUM -> {
                ContractStepMonthlyInsurancePremiumBinding.inflate(
                    LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true
                ).apply {
                    stepperItem.apply {
                        index = step.stepIndex
                        title = step.title
                    }
                    ItemDesc5.descTxt.text = getText(R.string.desc_monthly_premium1)
                    ItemDesc6.descTxt.text = getText(R.string.desc_monthly_premium2)
                    ItemDesc7.descTxt.text = getText(R.string.desc_monthly_premium3)
                    val adapter = PremiumRateAdapter(object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            mViewModel.dataModel.premiumRateCode = itemResult.id ?: "00"
                            mViewModel.dataModel.selectedPercentDesc = itemResult.description2 ?: ""
                            mViewModel.dataModel.needToCalculate = true

                            stepperItem.nextStepEnable =
                                !(contractType == EnumInsuranceType.TYPE_FREELANCE && mViewModel.dataModel.jobDesc == "")
                        }
                    })
                    recyclerPaymentRate.adapter = adapter
                    adapter.setItems(mViewModel.dataModel.premiumOptionList)
                    var premiumRateCode = false
                    var jobDesc = false
                    if (mViewModel.dataModel.premiumRateCode != "00") {
                        adapter.changeCheck(mViewModel.dataModel.premiumRateCode)
                        premiumRateCode = true
                    }
                    if (mViewModel.dataModel.jobDesc.isNotBlank()
                        && mViewModel.dataModel.cntFreeJobCode.isNotBlank()
                    ) {
                        selectInsuredJobTitle.getIt().error = null
                        selectInsuredJobTitle.setValue(mViewModel.dataModel.jobDesc)
                        jobDesc = true

                    }
                    stepperItem.nextStepEnable = jobDesc && premiumRateCode
                    if (contractType == EnumInsuranceType.TYPE_FREELANCE) {
                        selectInsuredJobTitle.visible()
                        selectInsuredJobTitle.setValue(mViewModel.dataModel.jobDesc)
                        selectInsuredJobTitle.getIt().setOnClickListener {
                            showDialog(
                                ContractDialogType.JOB_TITLES,
                                getString(R.string.label_select_job_name),
                                object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        tempCntCode = itemResult.id ?: ""
                                        tempCntTitle = itemResult.title ?: ""

                                        when(itemResult.id){
                                            MEDICAL_STUDENT_CODE ->{
                                                mViewModel.checkMedicalStudent()
                                            }
                                            RED_CRESCENT_CODE ->{
                                                val day = PersianDate.today().shDay
                                                if (day>20){
                                                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.WARNING,getString(R.string.error_redCrescentCode_select_job_title))
                                                    return
                                                }
                                                mViewModel.checkRedCrossStatus()
                                            }
                                            else->{
                                                (adapter as? PremiumRateAdapter?)?.enableSelectAllItem()
                                                setTreatmentSupport(false)
                                                mViewModel.dataModel.cntFreeJobCode = itemResult.id ?: ""
                                                mViewModel.dataModel.jobDesc = itemResult.title ?: ""
                                                selectInsuredJobTitle.getIt().error = null
                                                selectInsuredJobTitle.setValue(mViewModel.dataModel.jobDesc)
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
                    if (mViewModel.dataModel.premiumRateCode != "00" && contractType != EnumInsuranceType.TYPE_FREELANCE)
                        stepperItem.nextStepEnable = true
                }
            }
            STEP_SALARY -> {
                ContractStepSalaryBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {

                    //Todo:remove this if after that remove restart methode
                    if (mViewModel.dataModel.cntFreeJobCode == RED_CRESCENT_CODE) {
                        layoutPriceSeekBar.root.isVisible= false
                    }
                    layoutPriceSeekBar.root.isVisible = mViewModel.dataModel.maxPremiumRate != mViewModel.dataModel.minPremiumRate

                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    var rate = ""
                    for (item in mViewModel.dataModel.premiumOptionList) {
                        if (item.spcrateCode == mViewModel.dataModel.premiumRateCode)
                            rate = item.insurDpercent
                    }
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

                    if (mViewModel.dataModel.cntDrmn == "1") {
                        ItemDesc8.root.visible()
                        ItemDesc8.descTxt.text =
                            HtmlCompat.fromHtml(
                                getString(
                                    R.string.desc_monthly_premium4,
                                    UiUtils.createTextColorGreenAndBold(
                                        Utility.addSeparator(
                                            mViewModel.dataModel.paymentTabayi ?: 0
                                        )
                                    )
                                ),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                    }

                    if (mViewModel.dataModel.selectedSalary > 0) {

                        tvShowValueOfCal.text = HtmlCompat.fromHtml(
                            getString(
                                R.string.value_calcaluate_salary_for_contract,
                                UiUtils.createTextColorGreenAndBold(Utility.addSeparator((mViewModel.dataModel.selectedSalary)))
                            ),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )

                        if (mViewModel.dataModel.cntDrmn == "1") {
                            ItemDesc8.root.visible()
                            ItemDesc8.descTxt.text =
                                HtmlCompat.fromHtml(
                                    getString(
                                        R.string.desc_monthly_premium4,
                                        UiUtils.createTextColorGreenAndBold(
                                            Utility.addSeparator(
                                                mViewModel.dataModel.paymentTabayi ?: 0
                                            )
                                        )
                                    ),
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                        } else
                            ItemDesc8.root.gone()
                        stepperItem.nextStepEnable = true


                    } else {
                        stepperItem.nextStepEnable = false
                        groupValue.visibility = View.GONE
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


                    layoutPriceSeekBar.priceSeekBar.setListener { slidr, currentValue ->

                        if (mViewModel.dataModel.selectedValueSeekbar != currentValue) {
                            stepperItem.nextStepEnable = false
                            groupValue.visibility = View.GONE
                            ItemDesc8.root.gone()
                            mViewModel.dataModel.selectedSalary = -1
                        }
                        mViewModel.dataModel.selectedValueSeekbar = currentValue.toDouble()


                    }
                    btnCalculate.setOnClickListener {
                        val currentDouble = layoutPriceSeekBar.priceSeekBar.currentValue.toDouble()

                        val doubleCurrent = when{
                            mViewModel.dataModel.cntFreeJobCode == RED_CRESCENT_CODE ->{
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
                    layoutPriceSeekBar.imgAdd.setOnClickListener {

                        val pureValue =
                            (layoutPriceSeekBar.priceSeekBar.currentValue / PREMIUM_RATE_ADD_STEP).toLong() * PREMIUM_RATE_ADD_STEP
                        layoutPriceSeekBar.priceSeekBar.currentValue = pureValue

                        if (pureValue + PREMIUM_RATE_ADD_STEP <= mViewModel.dataModel.maxPremiumRate)
                            layoutPriceSeekBar.priceSeekBar.currentValue =
                                pureValue + PREMIUM_RATE_ADD_STEP
                    }
                    layoutPriceSeekBar.imgMines.setOnClickListener {
                        val pureValue =
                            (layoutPriceSeekBar.priceSeekBar.currentValue / PREMIUM_RATE_ADD_STEP).toLong() * PREMIUM_RATE_ADD_STEP

                        layoutPriceSeekBar.priceSeekBar.currentValue = pureValue

                        if (pureValue - PREMIUM_RATE_ADD_STEP >= mViewModel.dataModel.minPremiumRate)
                            layoutPriceSeekBar.priceSeekBar.currentValue =
                                pureValue - PREMIUM_RATE_ADD_STEP

                    }
                }
            }
            STEP_SUBMIT_CONTRACT -> {
                ContractStepFinalBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    if (mViewModel.dataModel.finalText.isNotEmpty())
                        descFinalConfirm.text =
                            HtmlCompat.fromHtml(
                                mViewModel.dataModel.finalText,
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )


                    cbDescFinalConfirm.setOnCheckedChangeListener { _, isChecked ->
                        stepperItem.nextStepEnable = isChecked
                    }
                }
            }
        }
    }

    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        super.onNextStepClickListener(stepIndex, step)
        if (handelNextClick)
            return

        viewDataBinding?.stepper?.apply {
            when (stepIndex) {
                STEP_SELECT_BRANCH.stepIndex -> {
                    (getStep(stepIndex) as? ContractStepBranchBinding)?.apply {

                        val province = locationSelector.getProvinceId()
                        val city = locationSelector.getCityId()
                        val branch = locationSelector.getBranchId()

                        if (province.isNullOrEmpty() || city.isNullOrEmpty() || branch.isNullOrEmpty())
                            return
                        nextStep()
                    }
                }
                STEP_TREATMENT_SUPPORT.stepIndex -> {
                    if (mViewModel.dataModel.premiumOptionList.isEmpty())
                        mViewModel.getPremiumOptions()
                    else
                        nextStep()
                }
                STEP_INSURANCE_PREMIUM.stepIndex -> {
                    if (mViewModel.dataModel.needToCalculate) {
                        mViewModel.getContractPremiumRate(
                            mViewModel.dataModel.premiumRateCode,
                            mViewModel.dataModel.cntFreeJobCode
                        )
                        (getStep(STEP_SALARY.stepIndex) as? ContractStepSalaryBinding)?.apply {
                            stepperItem.nextStepEnable = false
                            tvShowValueOfCal.visibility = View.GONE
                            img.visibility = View.GONE
                            ItemDesc8.root.gone()

                        }

                    } else
                        nextStep()

                }
                STEP_SUBMIT_CONTRACT.stepIndex -> {

                    if (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN) {
                        val requestModel = ContractByGuardianRequest(mViewModel.dataModel)
                        mViewModel.makeContractByGuardian(
                            selectedSalary = mViewModel.dataModel.selectedValueSeekbar.toInt(),
                            requestModel
                        )
                    } else
                        mViewModel.makeContract(
                            selectedSalary = mViewModel.dataModel.selectedValueSeekbar.toInt(),
                            ContractRequest(mViewModel.dataModel)
                        )
                }
                else -> {
                    nextStep()
                }
            }
        }
    }

    private fun onCalculateSalaryResponse(result: CalculateSalary) {
        if (result.isSuccess) {
            (getStep(STEP_SALARY.stepIndex) as? ContractStepSalaryBinding)?.apply {
                stepperItem.nextStepEnable = true
                tvShowValueOfCal.visibility = View.VISIBLE
                img.visibility = View.VISIBLE
                if (mViewModel.dataModel.cntDrmn == "1") {
                    ItemDesc8.root.visible()
                    ItemDesc8.descTxt.text =
                        HtmlCompat.fromHtml(
                            getString(
                                R.string.desc_monthly_premium4,
                                UiUtils.createTextColorGreenAndBold(
                                    Utility.addSeparator(
                                        mViewModel.dataModel.paymentTabayi ?: 0
                                    )
                                )
                            ),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                }

                mViewModel.dataModel.selectedSalary = result.data ?: -1

                val str2 = getString(
                    R.string.value_calcaluate_salary_for_contract,
                    UiUtils.createTextColorGreenAndBold(Utility.addSeparator((mViewModel.dataModel.selectedSalary)))
                )
                tvShowValueOfCal.text = HtmlCompat.fromHtml(
                    str2,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
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

                (getStep(STEP_SUBMIT_CONTRACT.stepIndex) as? ContractStepFinalBinding)?.descFinalConfirm?.text =
                    HtmlCompat.fromHtml(
                        str,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
            }
        }
    }

    private fun onContractPremiumRateResponse(result: ContractPremiumRateResponse) {
        if (result.isSuccess) {
            result.data?.apply {
                mViewModel.dataModel.needToCalculate = false
                viewDataBinding?.let { binding ->
                    mViewModel.dataModel.paymentTabayi = paymentTabayi
                    mViewModel.dataModel.minPremiumRate = lowPremium?.toDouble() ?: -1.0
                    mViewModel.dataModel.maxPremiumRate = highPremium?.toDouble() ?: -1.0

                    (getStep(STEP_SALARY.stepIndex) as? ContractStepSalaryBinding)?.apply {

                        if (mViewModel.dataModel.cntFreeJobCode == RED_CRESCENT_CODE) {
                            mViewModel.dataModel.maxPremiumRate = lowPremium?.toDouble() ?: -1.0
                            layoutPriceSeekBar.root.isVisible= false
                        }else {
                            layoutPriceSeekBar.root.isVisible = mViewModel.dataModel.maxPremiumRate != mViewModel.dataModel.minPremiumRate
                        }

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

                            layoutPriceSeekBar.priceSeekBar.currentValue =
                                if (selected < mViewModel.dataModel.minPremiumRate)
                                    mViewModel.dataModel.minPremiumRate
                                else if (selected > mViewModel.dataModel.maxPremiumRate)
                                    mViewModel.dataModel.maxPremiumRate
                                else
                                    selected

                        }
                    }
                }
            }
            viewDataBinding?.stepper?.nextStep()
        }
    }

    private fun onPremiumOptionsResponse(result: ContractPremiumOptionsResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.premiumOptionList.addAll(result.data?.list ?: emptyList())
            val adapter =
                (getStep(STEP_INSURANCE_PREMIUM.stepIndex) as? ContractStepMonthlyInsurancePremiumBinding?)?.recyclerPaymentRate?.adapter
            (adapter as? PremiumRateAdapter?)?.setItems(mViewModel.dataModel.premiumOptionList)

            viewDataBinding?.stepper?.nextStep()
        }
    }

    var tempCntCode = ""
    var tempCntTitle = ""
    private fun onCheckRedCrossStatusResponse(result: RedCrossStatusResponse) {
        if (result.isSuccess && result.data == true) {
            (getStep(STEP_INSURANCE_PREMIUM.stepIndex) as? ContractStepMonthlyInsurancePremiumBinding?)?.apply {
                mViewModel.dataModel.cntFreeJobCode = tempCntCode
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
                }else if(tempCntCode == MEDICAL_STUDENT_CODE){
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

    private fun setTreatmentSupport(isSupport: Boolean) {
        (getStep(STEP_TREATMENT_SUPPORT.stepIndex) as? ContractStepTreatmentSupportBinding)?.apply {
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

    private fun onMakeContractResponse(result: FinalConfirmResponse) {
        if (result.isSuccess) {
            result.data?.apply {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                 val disablePayment =  (tempCntCode == RED_CRESCENT_CODE || tempCntCode == MEDICAL_STUDENT_CODE)
                dialog.arguments = createBundle(MessageOfRequestDialogFragment.MessageType.SUCCESS2ACTION, getString(
                        R.string.desc_success_contract,
                        contractNumber.toString(),
                        (PersianDateFormat.format(
                            PersianDate(contractDate),
                            "y/m/d"
                        )).toString()
                    ),
                    btnCancel = !disablePayment
                )
                dialog.titleBtnOk = if (disablePayment) getString(R.string.understand) else getString(R.string.label_paymennt_contract)
                dialog.titleBtnCancel = getString(R.string.understand)
                dialog.setDialogClickListener(object :
                    DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        requireActivity().onBackPressed()
                        if (disablePayment) return
                        val bundle = Bundle()
                        bundle.putInt(
                            Constants.TOOLBAR_ICON_IMAGE,
                            R.drawable.ic_mobile_payment
                        )

                        bundle.putString(
                            Constants.TOOLBAR_TITLE,
                            "${getString(R.string.label_insurance_payment)} ${contractType.insuranceName}"
                        )

                        bundle.putSerializable(
                            InsurancePaymentFragment.INSURANCE_TYPE,
                            contractType
                        )

                        handlePageDestination(
                            R.id.action_contractList_to_insurancePayment,
                            bundle
                        )
                    }

                    override fun onCancelClick() {
                        requireActivity().onBackPressed()
                    }
                })
                dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
            }
        }
    }

    companion object {
        const val PREMIUM_RATE_ADD_STEP = 10000.0
    }
}

