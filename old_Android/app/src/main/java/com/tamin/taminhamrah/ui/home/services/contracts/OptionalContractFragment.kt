package com.tamin.taminhamrah.ui.home.services.contracts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateSalary
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumRateResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FinalConfirmResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractByGuardian
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractRequest
import com.tamin.taminhamrah.databinding.ContractStepBranchBinding
import com.tamin.taminhamrah.databinding.ContractStepCondistionBinding
import com.tamin.taminhamrah.databinding.ContractStepConditionTermsBinding
import com.tamin.taminhamrah.databinding.ContractStepFinalBinding
import com.tamin.taminhamrah.databinding.ContractStepRegistrationBinding
import com.tamin.taminhamrah.databinding.ContractStepSalaryBinding
import com.tamin.taminhamrah.databinding.ContractStepUserInfoBinding
import com.tamin.taminhamrah.databinding.GuardianshipContractStepBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumOptionalContractStep
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
class OptionalContractFragment :
    ContractBaseFragment<EnumOptionalContractStep>() {

    override val mViewModel: ContractViewModel by viewModels()

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun setupObserver() {
        super.setupObserver()
        mViewModel.mldPremiumRateForContract.observe(this, ::onContractPremiumRateResponse)
        mViewModel.mldCheckAndCalculateSalaryForContract.observe(this, ::onCalculateSalaryResponse)
        mViewModel.mldFinalRequestMakeContract.observe(this, ::onMakeContractResponse)
    }

    override fun getStepsView() = EnumOptionalContractStep.values().map { inflateStepView(it) }

    override fun inflateStepView(step: EnumOptionalContractStep): ViewBinding {
        return when (step) {
            EnumOptionalContractStep.STEP_REGISTRATION -> {
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
            EnumOptionalContractStep.STEP_AUTHORIZATION -> {
                ContractStepCondistionBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {

                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    val eligibilityStatus = mViewModel.dataModel.eligibilityStatus

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
                                    UiUtils.createTextColorGreenAndBold(getString(R.string.label_optional_insurance))
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
                                        UiUtils.createTextColorGreenAndBold(getString(R.string.label_optional_insurance))
                                    )
                                }
                            } else {
                                message = getString(
                                    R.string.you_have_the_necessary_conditions_to_sign_an_insurance_contract,
                                    mViewModel.getEligibilityStatusList()[eligibilityStatus - 1],
                                    UiUtils.createTextColorGreenAndBold(getString(R.string.label_optional_insurance))
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
            EnumOptionalContractStep.STEP_CONTRACT_TERMS -> {
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
                            "rulesAndRegulationsHtmlFile/rules2.pdf"
                        )
                        handlePageDestination(R.id.action_contract_pdf, bundle)
                    }
                }
            }
            EnumOptionalContractStep.STEP_USER_INFO -> {
                ContractStepUserInfoBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {

                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

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
            EnumOptionalContractStep.STEP_CONTRACT_APPLICANT -> {
                GuardianshipContractStepBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    addGuardianshipDoc.tvUploadHint.text = getString(R.string.label_image_guid)

                    ////// Image Upload

                    addGuardianshipDoc.recycler.apply {
                        this.adapter = guardianshipImageListAdapter
                        if (itemDecorationCount == 0) {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }
                    }

                    if (mViewModel.dataModel.guardianshipImage != null)
                        guardianshipImageListAdapter.setItems(listOf(mViewModel.dataModel.guardianshipImage!!))
                    addGuardianshipDoc.tvTitle.text = getString(R.string.label_guardianship_doc)

                    val clickListener = View.OnClickListener {
                        if (mViewModel.dataModel.guardianshipImage != null) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.INFO,
                                getString(R.string.error_max_upload_1_item)
                            )
                        } else {

//                            mViewModel.dataModel.apply {
//                                guardianName = etName.getValue(false)
//                                guardianDate = guardianshipDate.inputDate.text.toString()
//                                guardianNationalId = etNationalCode.getValueNationalCode(false)
//                                guardianNumber = etGuardianShipNumber.getValue(false)
//                            }

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
            EnumOptionalContractStep.STEP_SELECT_BRANCH -> {
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
            EnumOptionalContractStep.STEP_SALARY -> {
                ContractStepSalaryBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
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

                    mViewModel.dataModel.selectedValueSeekbar.apply {
                        layoutPriceSeekBar.priceSeekBar.currentValue =
                            if (this < mViewModel.dataModel.minPremiumRate)
                                mViewModel.dataModel.minPremiumRate
                            else if (this > mViewModel.dataModel.maxPremiumRate)
                                mViewModel.dataModel.maxPremiumRate
                            else
                                this
                    }



                    layoutPriceSeekBar.priceSeekBar.setListener { _, currentValue ->
                        if (mViewModel.dataModel.selectedValueSeekbar != currentValue) {
                            stepperItem.nextStepEnable = false
                            groupValue.visibility = View.GONE
                            mViewModel.dataModel.selectedSalary = -1
                        }
                        mViewModel.dataModel.selectedValueSeekbar = currentValue.toDouble()
                    }

                    btnCalculate.setOnClickListener {
                        val currentDouble = layoutPriceSeekBar.priceSeekBar.currentValue.toDouble()

                        val doubleCurrent = if (currentDouble > mViewModel.dataModel.maxPremiumRate)
                            mViewModel.dataModel.maxPremiumRate
                        else
                            currentDouble

                        mViewModel.calculateSalaryForOptionalContract(
                            doubleCurrent.toLong().toString()
                        )

                    }
                    layoutPriceSeekBar.imgAdd.setOnClickListener {

                        val pureValue =
                            (layoutPriceSeekBar.priceSeekBar.currentValue / InsuranceContractFragment.PREMIUM_RATE_ADD_STEP).toLong() * InsuranceContractFragment.PREMIUM_RATE_ADD_STEP
                        layoutPriceSeekBar.priceSeekBar.currentValue = pureValue

                        if (pureValue + InsuranceContractFragment.PREMIUM_RATE_ADD_STEP <= mViewModel.dataModel.maxPremiumRate)
                            layoutPriceSeekBar.priceSeekBar.currentValue =
                                pureValue + InsuranceContractFragment.PREMIUM_RATE_ADD_STEP
                    }
                    layoutPriceSeekBar.imgMines.setOnClickListener {
                        val pureValue =
                            (layoutPriceSeekBar.priceSeekBar.currentValue / InsuranceContractFragment.PREMIUM_RATE_ADD_STEP).toLong() * InsuranceContractFragment.PREMIUM_RATE_ADD_STEP

                        layoutPriceSeekBar.priceSeekBar.currentValue = pureValue

                        if (pureValue - InsuranceContractFragment.PREMIUM_RATE_ADD_STEP >= mViewModel.dataModel.minPremiumRate)
                            layoutPriceSeekBar.priceSeekBar.currentValue =
                                pureValue - InsuranceContractFragment.PREMIUM_RATE_ADD_STEP

                    }
                }
            }
            EnumOptionalContractStep.STEP_SUBMIT_CONTRACT -> {
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
        when (stepIndex) {

            EnumOptionalContractStep.STEP_SELECT_BRANCH.stepIndex -> {
                (getStep(stepIndex) as? ContractStepBranchBinding)?.apply {

                    if (
                        locationSelector.getProvinceId().isNullOrEmpty()
                        || locationSelector.getCityId().isNullOrEmpty()
                        || locationSelector.getBranchId().isNullOrEmpty()
                    )
                        return


                    if (mViewModel.dataModel.needToCalculate) {
                        mViewModel.getOptionalContractPremiumRate()
                        (getStep(EnumOptionalContractStep.STEP_SALARY.stepIndex) as? ContractStepSalaryBinding)?.apply {
                            stepperItem.nextStepEnable = false
                            tvShowValueOfCal.visibility = View.GONE
                            img.visibility = View.GONE
                        }
                    } else
                        viewDataBinding?.stepper?.nextStep()
                }
            }
            EnumOptionalContractStep.STEP_SUBMIT_CONTRACT.stepIndex -> {

                if (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN)
                    mViewModel.makeOptionalContractByGuardian(
                        selectedSalary = mViewModel.dataModel.selectedValueSeekbar.toInt(),
                        OptionalContractByGuardian(mViewModel.dataModel)
                    )
                else
                    mViewModel.makeOptionalContract(
                        selectedSalary = mViewModel.dataModel.selectedValueSeekbar.toInt(),
                        OptionalContractRequest(mViewModel.dataModel)
                    )
            }
            else -> {
                viewDataBinding?.stepper?.nextStep()
            }
        }
    }

    private fun onCalculateSalaryResponse(result: CalculateSalary) {
        if (result.isSuccess) {
            (getStep(EnumOptionalContractStep.STEP_SALARY.stepIndex) as? ContractStepSalaryBinding)?.apply {
                stepperItem.nextStepEnable = true
                tvShowValueOfCal.visibility = View.VISIBLE
                img.visibility = View.VISIBLE
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
                mViewModel.dataModel.finalText =
                    if (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN)
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
                (getStep(EnumOptionalContractStep.STEP_SUBMIT_CONTRACT.stepIndex) as? ContractStepFinalBinding)?.descFinalConfirm?.text =
                    HtmlCompat.fromHtml(
                        mViewModel.dataModel.finalText,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
            }
        }

    }

    private fun onMakeContractResponse(result: FinalConfirmResponse) {
        if (result.isSuccess) {
            result.data?.apply {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.SUCCESS2ACTION, getString(
                        R.string.desc_success_contract,
                        contractNumber.toString(),
                        (PersianDateFormat.format(
                            PersianDate(contractDate),
                            "y/m/d"
                        )).toString()
                    ),
                    btnCancel = true
                )
                dialog.titleBtnOk = getString(R.string.label_paymennt_contract)
                dialog.titleBtnCancel = getString(R.string.understand)
                dialog.setDialogClickListener(object :
                    DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        requireActivity().onBackPressed()
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

    private fun onContractPremiumRateResponse(result: ContractPremiumRateResponse) {
        if (result.isSuccess) {
            result.data?.apply {
                mViewModel.dataModel.needToCalculate = false
                mViewModel.dataModel.minPremiumRate = lowPremium?.toDouble() ?: -1.0
                mViewModel.dataModel.maxPremiumRate = highPremium?.toDouble() ?: -1.0

                (getStep(EnumOptionalContractStep.STEP_SALARY.stepIndex) as? ContractStepSalaryBinding)?.apply {
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

                        mViewModel.dataModel.selectedValueSeekbar =
                            mViewModel.dataModel.minPremiumRate
                    }
                }
            }
            viewDataBinding?.stepper?.nextStep()

        }
    }

}