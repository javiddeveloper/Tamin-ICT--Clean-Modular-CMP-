package com.tamin.taminhamrah.ui.home.services.contracts

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FinalConfirmResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.Personal
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.UpdateAddressInfoRequest
import com.tamin.taminhamrah.databinding.ContractStepCondistionBinding
import com.tamin.taminhamrah.databinding.ContractStepConditionTermsBinding
import com.tamin.taminhamrah.databinding.ContractStepFinalBinding
import com.tamin.taminhamrah.databinding.ContractStepUserInfoBinding
import com.tamin.taminhamrah.databinding.FragmentInsuranceContractBinding
import com.tamin.taminhamrah.databinding.ShowContractStepContractInfoBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.contracts.model.FractionStepEnum
import com.tamin.taminhamrah.ui.home.services.studentContract.payment.insurance.InsurancePaymentFragment
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import timber.log.Timber
import kotlin.math.abs


@AndroidEntryPoint
class

FractionContractFragment() :
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener,
    BaseFragment<FragmentInsuranceContractBinding, ContractViewModel>() {

    override fun getLayoutId() = R.layout.fragment_insurance_contract

    override val mViewModel: ContractViewModel by viewModels()
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun setupObserver() {
        mViewModel.mldCheckContractCondition.observe(this, ::onCheckContractCondition)
        mViewModel.mldRegistrationInfo.observe(this, ::onRegistrationInfoResult)
        mViewModel.mldSaveUsersAddressInfo.observe(this, ::onUpdateUserAddress)
        mViewModel.mldFinalRequestMakeContract.observe(this, ::onMakeContractResponse)
    }


    override fun initView() {
        viewDataBinding?.appBar?.toolbar?.imageBack?.setOnClickListener { backButtonPress() }
        setupToolbarContract()

    }

    override fun getData() {
        mViewModel.getRegistrationInfo(isFraction = true)
    }

    override fun onClick() {
    }

    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {


        when (stepIndex) {
            FractionStepEnum.STEP_USER_INFO.stepIndex -> {
                if (changedAddressInfo()) {
                    if (checkValidInput()) {
                        (getStep(stepIndex) as? ContractStepUserInfoBinding)?.apply {
                            mViewModel.updateUserAddressInfo(
                                updateAddressInfoRequest = UpdateAddressInfoRequest(
                                    cityId = selectCityName.tag?.toString(),
                                    address = inputAddress.getValue(false),
                                    zipCode = inputZipCode.getValue(false),
                                    mobile = inputMobile.getValue(false),
                                    phoneNumber = inputPhoneNumber.getValue(false),
                                    personal = Personal(ssn = mViewModel.mldRegistrationInfo.value?.data?.personalInfo?.ssn)
                                )
                            )

                        }

                    }
                } else {
                    viewDataBinding?.stepper?.nextStep()
                }
            }

            FractionStepEnum.STEP_SUBMIT_CONTRACT.stepIndex -> {
                mViewModel.makeFractionContract()
            }

            else -> {
                viewDataBinding?.stepper?.nextStep()
            }
        }
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.previousStep()

    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    private var restartedFragment = false
    var loadTempPersonalInfo = false

    /*  override fun onStart() {
          super.onStart()
          if (restartedFragment) {
              initStepper(viewDataBinding?.stepper?.currentStepIndex ?: 1)
              restartedFragment = false
          }

      }

      override fun onStop() {
          restartedFragment = true
          (getStep(FractionStepEnum.STEP_USER_INFO.stepIndex) as? ContractStepUserInfoBinding)?.apply {
              mViewModel.dataModel.tempAddress = inputAddress.getValue(false)
              mViewModel.dataModel.tempZipCode = inputZipCode.getValue(false)
              mViewModel.dataModel.tempPhoneNumber = inputPhoneNumber.getValue(false)
              loadTempPersonalInfo = true
          }

          super.onStop()

      }*/

    fun initStepper(startIndex: Int = 1) {
        viewDataBinding?.apply {
            stepper.initial(getStepsView(), startIndex)
            stepper.onNextStepClickListener = this@FractionContractFragment
            stepper.onPreviousStepClickListener = this@FractionContractFragment
        }
    }

    fun getStepsView() = FractionStepEnum.entries.map { inflateStepView(it) }
    fun getStep(step: Int): ViewBinding? {
        return viewDataBinding?.stepper?.getStepLayoutBindingByStep(step)
    }

    fun inflateStepView(step: FractionStepEnum): ViewBinding {
        return when (step) {
            FractionStepEnum.STEP_INSURANCE_INFO -> {
                ShowContractStepContractInfoBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    labelBranchTitle.text = getString(R.string.label_branch_of_tamin)
                    valueBranchInfo.text = mViewModel.dataModel.branchAddress
                    valueInsuranceNum.text = mViewModel.dataModel.insuranceId

                }
            }

            FractionStepEnum.STEP_AUTHORIZATION -> {
                ContractStepCondistionBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    val eligibilityStatus = mViewModel.dataModel.eligibilityStatus

                    stepperItem.nextStepEnable =
                        if (eligibilityStatus !in 1..<5) {
                            imgEligibilityStatus.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(), R.drawable.ic_close_outline
                                )
                            )
                            labelEligibilityStatus.text = HtmlCompat.fromHtml(
                                getString(
                                    R.string.you_dont_have_the_necessary_conditions_to_sign_an_insurance_contract,
                                    mViewModel.getEligibilityStatusList()[4],
                                    UiUtils.createTextColorGreenAndBold(getString(R.string.label_fraction_contract))
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
                                        UiUtils.createTextColorGreenAndBold(getString(R.string.label_fraction_contract))
                                    )
                                }
                            } else {
                                message = getString(
                                    R.string.you_have_the_necessary_conditions_to_sign_an_insurance_contract,
                                    mViewModel.getEligibilityStatusList()[eligibilityStatus - 1],
                                    UiUtils.createTextColorGreenAndBold(getString(R.string.label_fraction_contract))
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

            FractionStepEnum.STEP_CONTRACT_TERMS -> {
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
                        ), PorterDuff.Mode.SRC_IN
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
                            "rulesAndRegulationsHtmlFile/rules_fraction_contract.pdf"
                        )
                        handlePageDestination(R.id.action_contract_pdf, bundle)
                    }
                }
            }

            FractionStepEnum.STEP_USER_INFO -> {
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

            FractionStepEnum.STEP_SUBMIT_CONTRACT -> {
                ContractStepFinalBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItem.index = step.stepIndex
                    stepperItem.title = step.title

                    mViewModel.dataModel.finalText = getString(
                        R.string.desc_final_confirm_fraction,
                        UiUtils.createTextColorGreenAndBold(mViewModel.dataModel.getFullName()),
                        UiUtils.createTextColorBlueAndBold(mViewModel.dataModel.nationalId),
                        UiUtils.createTextColorGreenAndBold("27%"),
                        UiUtils.createTextColorBlueAndBold(PersianDate.today().shYear.toString() + "/" + PersianDate.today().shMonth + "/" + PersianDate.today().shDay)
                    )
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

    fun showDialog(
        type: ContractDialogType,
        title: String,
        resultCallback: MenuInterface.OnResult
    ) {
        val dialog = MenuDialogFragment.newInstance(true, title).apply {

            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@FractionContractFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            ContractDialogType.BRANCH_LIST ->
                                mViewModel.getBranchesInfoList(cityCode = mViewModel.dataModel.cityCode)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.code,
                                                title = it.name + "-" + if (it.branchAddress.isNullOrBlank()) "" else it.branchAddress,
                                                description = it.branchAddress
                                            )
                                        }
                                        updateData(result)
                                    }

                            ContractDialogType.CITY_LIST_EMPTY_PROVINCE -> {
                                mViewModel.getCityListFlow()
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

                            ContractDialogType.CITY_LIST ->
                                mViewModel.getCitiesList(provinceCode = if (type == ContractDialogType.CITY_LIST) mViewModel.dataModel.provinceCode else null)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.cityCode,
                                                title = it.cityName
                                            )
                                        }
                                        updateData(result)
                                    }

                            ContractDialogType.PROVINCES_LIST ->
                                mViewModel.getProvincesList().collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.provinceCode,
                                            title = it.provinceName
                                        )
                                    }
                                    updateData(result)
                                }

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
                        }

                    }
                }
            }, resultCallback, object : MenuInterface.OnSearch {
                override fun onSearch(str: String) {
                    this@FractionContractFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            ContractDialogType.BRANCH_LIST ->
                                mViewModel.getBranchesInfoList(
                                    cityCode = mViewModel.dataModel.cityCode,
                                    branchName = str
                                ).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.code,
                                            title = it.name + "-" + it.branchAddress,
                                        )
                                    }
                                    updateData(result)
                                }

                            ContractDialogType.CITY_LIST_EMPTY_PROVINCE -> {
                                mViewModel.getCityListFlow(str)
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

                            ContractDialogType.CITY_LIST ->
                                mViewModel.getCitiesList(
                                    provinceCode = if (type == ContractDialogType.CITY_LIST) mViewModel.dataModel.provinceCode else null,
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

                            ContractDialogType.PROVINCES_LIST ->
                                mViewModel.getProvincesList(str).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.provinceCode,
                                            title = it.provinceName
                                        )
                                    }
                                    updateData(result)
                                }

                            ContractDialogType.JOB_TITLES ->
                                mViewModel.getFreelancerJobTitles(
                                    jobTitle = str,
                                ).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.jobCode,
                                            title = it.jobTitle,
                                        )
                                    }
                                    updateData(result)
                                }
                        }
                    }
                }
            })
        }
        dialog.show(childFragmentManager, (MenuDialogFragment::class.java).simpleName)
    }

    fun setupToolbarContract() {
        viewDataBinding?.let {
            it.appBar.apply {
                var toolbarTitle = ""
                arguments?.let { args ->
                    ImageUtils.loadImage(imgIcon, Utility.getToolbarIconImage(args))
                    toolbarTitle = Utility.getToolbarTitle(args)
                    Timber.tag("WomenContractTitle").e("Women Called : title=$toolbarTitle")

                }
                toolbar.imgInfo.visibility = View.GONE
                tvTitle.text = toolbarTitle
                appBarView.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
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
                }
            }
        }
    }

    fun checkValidInput(): Boolean {
        var message: String

        (getStep(FractionStepEnum.STEP_USER_INFO.stepIndex) as? ContractStepUserInfoBinding)?.apply {
            if (inputZipCode.getValue().length < 10) {
                message = getString(R.string.error_input_length_zip_code)
                inputZipCode.setError(message)
                return false
            }

            val validationPhoneResult = Utility.checkPhoneNumber(inputPhoneNumber.getValue())
            if (validationPhoneResult != 0) {
                inputPhoneNumber.setError(getString(validationPhoneResult))
                return false
            }
            return true

        }
        return false
    }

    fun changedAddressInfo(): Boolean {

        (getStep(FractionStepEnum.STEP_USER_INFO.stepIndex) as? ContractStepUserInfoBinding)?.apply {
            return !(mViewModel.dataModel.usersCity == selectCityName.getValue(false) &&
                    mViewModel.dataModel.usersAddress.trim() == inputAddress.getValue(false)
                .trim() &&
                    mViewModel.dataModel.usersZipCode == inputZipCode.getValue(false) &&
//                    mViewModel.dataModel.usersMobile == inputMobile.getValue(false) &&
                    mViewModel.dataModel.usersPhoneNumber == inputPhoneNumber.getValue(false))
        }
        return true
    }

    fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }

    fun onCheckContractCondition(result: CheckAgeAndHistoryResponse) {

        if (result.isSuccess) {
            viewDataBinding?.let { binding ->
                if (result.data == null) {
                    binding.rootLayoutErrorRegister.visibility =
                        View.VISIBLE
                } else {
                    result.data.apply {
                        mViewModel.loadBranchInfo(result.data)
                        mViewModel.dataModel.eligibilityStatus = result.data.eligibilityStatus ?: -1

                        if (!isInsurance) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                getString(R.string.insurance_error),
                                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                            )
                        } else if ((if (newAge == null || newAge.length < 3) 0 else newAge.take(2).toInt()) < 18
                        ) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                getString(R.string.you_are_under_18_years_old_its_not_possible_for_provide_offline_services),
                                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                            )
                        } else if (checkFractionMonthStatus != "1") {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                checkFractionMonthStatus ?: "",
                                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                            )
                        } else if (contract?.premiumType?.insuranceTypeCode == "38") {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                getString(R.string.insurance_fraction_error),
                                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                            )
                        } else {
                            initStepper()
                        }
                    }
                }
            }
        }
    }

    fun onRegistrationInfoResult(result: ConcludingStudentInsuranceContractResponse) {
        if (result.isSuccess) {
            result.data?.apply {
                viewDataBinding?.userInfo = this
                viewDataBinding?.appBar?.line2?.visible()
                "${getString(R.string.label_national_code)} : ${personalInfo?.nationalId}".also { viewDataBinding?.appBar?.tvNationalCode?.text = it }
                "${getString(R.string.birthdate)} : ${getPersianDate(personalInfo?.dateOfBirth)}".also { viewDataBinding?.appBar?.tvBirthDate?.text = it }
                "${personalInfo?.firstName ?: ""} ${personalInfo?.lastName ?: ""}".also { viewDataBinding?.appBar?.tvSubTitle?.text = it }

                mViewModel.dataModel.genderCode =
                    result.data?.personalInfo?.gender?.genderCode ?: ""
                mViewModel.dataModel.genderDesc =
                    result.data?.personalInfo?.gender?.genderDesc ?: ""

                mViewModel.dataModel.insuranceId = result.data?.insuranceId ?: ""
                mViewModel.dataModel.firstName = personalInfo?.firstName ?: ""
                mViewModel.dataModel.lastName = personalInfo?.lastName ?: ""
                mViewModel.dataModel.ssn = personalInfo?.ssn ?: ""
                mViewModel.dataModel.nationalId = personalInfo?.nationalId ?: ""
                lastContact?.apply {
                    mViewModel.dataModel.usersAddress = address ?: ""
                    mViewModel.dataModel.usersZipCode = zipCode ?: ""
                    mViewModel.dataModel.usersMobile = mobile ?: ""
                    mViewModel.dataModel.mobileExist = mViewModel.dataModel.usersMobile.isNotBlank()
                    mViewModel.dataModel.usersPhoneNumber = phoneNumber ?: ""
                }
            }
        }
    }

    fun onUpdateUserAddress(result: GeneralRes) {
        if (result.isSuccess) {
            (getStep(FractionStepEnum.STEP_USER_INFO.stepIndex) as? ContractStepUserInfoBinding)?.apply {
                mViewModel.dataModel.usersCity = selectCityName.getValue(false)
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
                    val root =
                        (getStep(FractionStepEnum.STEP_USER_INFO.stepIndex) as? ContractStepUserInfoBinding)?.root
                    val btn = root?.findViewById<AppCompatButton>(R.id.btnNextStepRules)
                    btn?.callOnClick()
                }

                override fun onCancelClick() {
                }

            })

            dialog.show(childFragmentManager, "FilterDialogFragment")
        }
    }

    fun onMakeContractResponse(result: FinalConfirmResponse) {
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
                            "${getString(R.string.label_insurance_payment)} ${EnumInsuranceType.TYPE_FRACTION.insuranceName}"
                        )

                        bundle.putSerializable(
                            InsurancePaymentFragment.INSURANCE_TYPE,
                            EnumInsuranceType.TYPE_FRACTION
                        )

                        handlePageDestination(
                            R.id.action_servicesFragment_to_insurancePayment,
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
}


