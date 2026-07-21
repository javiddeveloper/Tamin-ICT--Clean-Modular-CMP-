package com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.submitInspectionRequest

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.SubmitInspectionRequestResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.databinding.FragmentSubmitInspectionRequestNewBinding
import com.tamin.taminhamrah.databinding.StepDescriptionInspectionBinding
import com.tamin.taminhamrah.databinding.StepUserInfoInspectionBinding
import com.tamin.taminhamrah.databinding.StepWorkshopInfoInspectionBinding
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.Utility.checkInputIsValidEMail
import com.tamin.taminhamrah.utils.Utility.checkMobileNumber
import com.tamin.taminhamrah.utils.Utility.checkPhoneNumber
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import com.tamin.taminhamrah.widget.DatePickerWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Date


@AndroidEntryPoint
class SubmitInspectionRequestFragment :
    BaseFragment<FragmentSubmitInspectionRequestNewBinding, SubmitInspectionRequestViewModel>(),
    StepperLayout.NextStepClickListener, StepperLayout.PreviousStepClickListener {

    //Class variables
    override val mViewModel: SubmitInspectionRequestViewModel by viewModels()
    private var selectedJob: String? = null

    enum class InspectionDialogType {
        BRANCH_LIST, JOB_TITLE_LIST
    }

    //Base methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_submit_inspection_request_new

    override fun setupObserver() {
        mViewModel.mldUserInfo.observe(this, ::onUserInfoResponse)
        mViewModel.mldSendInspection.observe(this, ::onSendSubmitResponse)
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null
            )
        }

    }

    override fun getData() {
        mViewModel.getProfileInfo()

    }

    override fun onClick() {
    }

    //Listeners
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        when (stepIndex) {
            1 ->{
                if (checkValidInputIdentityInfoStep())
                    viewDataBinding?.stepperLayout?.nextStep()
            }
            2 -> {
                if (checkValidInputWorkShopInfoStep())
                    viewDataBinding?.stepperLayout?.nextStep()
            }
            3 -> {
                mViewModel.sendInspectionRequest(mViewModel.dataModel.getRequestSubmitInspection())
            }
            else -> {
                viewDataBinding?.stepperLayout?.nextStep()
            }
        }
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepperLayout?.previousStep()
    }

    private fun onSendSubmitResponse(result: SubmitInspectionRequestResponse) {
        if (result.isSuccess) {
            showAlertDialog(type = MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(
                    R.string.label_request_with_tracking_number,
                    result.data?.request?.id.toString()),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
        }
    }

    private fun onUserInfoResponse(result: CurrentUserResponse) {
        if (result.isSuccess) {
            result.data?.let { mViewModel.dataModel.initUserInfo(it) }
            initialStepper()
        }
    }

    //Utils
    private fun initialStepper() {
        viewDataBinding?.stepperLayout?.apply {
            initial(arrayListOf(initialUserInfoStep(),
                initialWorkshopInfoStep(),
                initialFinalStep()))
            onNextStepClickListener = this@SubmitInspectionRequestFragment
            onPreviousStepClickListener = this@SubmitInspectionRequestFragment
        }
    }

    private fun checkValidInputWorkShopInfoStep(): Boolean {
        (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(2) as? StepWorkshopInfoInspectionBinding)?.apply {
            val checkValidPhone =   checkPhoneNumber(inputWorkShopPhoneNumber.getValue(false))
            when {
                inputWorkShopName.getValue(false).isEmpty() -> {
                    showError(
                        inputWorkShopName.getLayout(),
                        strError = getString(R.string.error_enter_workshop_name)
                    )
                }
                inputEmployerName.getValue(false).isBlank() -> showError(
                    inputEmployerName.getLayout(),
                    strError = getString(R.string.error_enter_employer_name)
                )
                inputBranchName.getValue(false).isBlank() -> {
                    showError(
                        inputBranchName.getLayout(),
                        strError = getString(R.string.error_input_branch_name), false
                    )
                }
                inputWorkShopPhoneNumber.getValue(false).isNotBlank() && checkValidPhone!=0 ->{
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(checkValidPhone))

                }
                selectInsuredJobTitle.getValue(false).isBlank() -> {
                    showError(
                        selectInsuredJobTitle.getLayout(),
                        strError = getString(R.string.error_enter_title_job), false
                    )
                }
                mViewModel.dataModel.startDate == 0L -> showError(
                    widgetDatePickerStartDate.getLayout(),
                    strError = getString(R.string.error_select_start_date), false
                )
                mViewModel.dataModel.endDate == 0L -> showError(
                    widgetDatePickerEndDate.getLayout(),
                    strError = getString(R.string.error_select_end_date), false
                )

                mViewModel.dataModel.endDate < mViewModel.dataModel.startDate -> showError(
                    widgetDatePickerEndDate.getLayout(),
                    strError = getString(R.string.error_diffrent_end_date_and_start_date_selected),
                    false
                )
                inputAddress.getValue(false).isBlank() -> showError(
                    inputAddress.getLayout(),
                    strError = getString(R.string.error_enter_workshop_address), false
                )
                else -> {
                    mViewModel.dataModel.apply {
                        workShopName = inputWorkShopName.getValue()
                        employerName = inputEmployerName.getValue()
                        jobTitle = selectedJob.toString()
                        workshopAddress = inputAddress.getValue()
                        workshopId = inputWorkShopCode.getValue(false)
                        workshopTell = inputWorkShopPhoneNumber.getValue(false)
                    }
                    return true
                }
            }
        }
        return false
    }


    private fun checkValidInputIdentityInfoStep(): Boolean {
        (viewDataBinding?.stepperLayout?.getStepLayoutBindingByStep(1) as? StepUserInfoInspectionBinding)?.apply {
            val mobile = inputMobile.getValue(false)
            val tell = inputPhoneNumber.getValue(false)
            val email = inputEmail.getValue(false)
            val checkValidTell = checkPhoneNumber(tell)
            val checkValidMobile = checkMobileNumber(mobile)
            val checkValidEmail = checkInputIsValidEMail(email)
            when{
                mobile.isNotBlank() && checkValidMobile!=0->{
                        showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(checkValidMobile))
                }
                tell.isNotBlank() && checkValidTell!=0 ->{
                        showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(checkValidTell))
                }
                email.isNotBlank() && !checkValidEmail ->{
                        showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(R.string.error_input_is_email_not_valid))
                }
                else->{
                    return true
                }
            }
        }
        return false
    }

    private fun showError(
        textLayout: TextInputLayout? = null,
        strError: String,
        scroll: Boolean = true,
    ) {
        textLayout?.error = strError
        if (scroll)
            viewDataBinding?.apply {
                appBar.appBarView.setExpanded(false, true)
                textLayout?.requestFocus()
                nestedScrollView.scrollTo(
                    0,
                    textLayout?.top ?: textLayout?.top ?: 0
                )
            }
    }

    //Step1
    private fun initialUserInfoStep() =
        StepUserInfoInspectionBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true).apply {
            mViewModel.dataModel.apply {
                if (email.isNotBlank()) {
                    inputEmail.setValueOfText(email)
                    inputEmail.enableView(false)
                }
                if (mobile.isNotBlank()) {
                    inputMobile.setValueOfText(mobile)
                    inputMobile.enableView(false)
                }

                tvValueFullName.text = fullName
                tvValueNationalCode.text = nationalId
            }
        }

    //Step2
    private fun initialWorkshopInfoStep() =
        StepWorkshopInfoInspectionBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true).apply {

            inputBranchName.getIt().setOnClickListener {
                inputBranchName.getLayout().isErrorEnabled = false
                showDialog(InspectionDialogType.BRANCH_LIST,
                    getString(R.string.label_select_branch_name),
                    resultCallback = object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            mViewModel.dataModel.branchCode = itemResult.id ?: ""
                            itemResult.title?.let { it1 -> inputBranchName.setValue(it1) }
                        }
                    }
                )
            }

            selectInsuredJobTitle.getIt().setOnClickListener {
                selectInsuredJobTitle.getIt().error = null
                showDialog(InspectionDialogType.JOB_TITLE_LIST,
                    getString(R.string.label_select_job_name),
                    resultCallback = object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectedJob = itemResult.id ?: ""
                            itemResult.title?.let { it1 -> selectInsuredJobTitle.setValue(it1) }
                        }
                    })
            }

            widgetDatePickerStartDate.setListener(object :
                DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String,
                ) {
                    mViewModel.dataModel.startDate = timeStamp
                }
            })

            widgetDatePickerEndDate.setListener(object :
                DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String,
                ) {
                    mViewModel.dataModel.endDate = timeStamp
                }
            })

        }

    //Step3
    private fun initialFinalStep() =
        StepDescriptionInspectionBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepperLayout,
            true).apply {
            inputDescription.getInput().addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (!text.isNullOrBlank() && text.length > 10) {
                        stepperDesc.nextStepEnable = true
                        mViewModel.dataModel.descInspection = text.toString()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
        }


    private fun showDialog(
        type: InspectionDialogType,
        title: String,
        resultCallback: MenuInterface.OnResult,
    ) {
        val dialog = MenuDialogFragment.newInstance(true, title)
        dialog.setMenuListener(object : MenuInterface.OnFetchData {
            override fun onFetch() {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    when (type) {
                        InspectionDialogType.BRANCH_LIST -> {
                            mViewModel.getBranchRequests().collectLatest { pagingData ->
                                val result = pagingData.map {
                                    MenuModel(
                                        id = it.code,
                                        title = it.name
                                    )
                                }
                                dialog.updateData(result)
                            }
                        }
                        InspectionDialogType.JOB_TITLE_LIST -> {
                            mViewModel.getJob().collectLatest { pagingData ->
                                val result = pagingData.map {
                                    MenuModel(
                                        id = it.jobCode,
                                        title = it.jobDescription
                                    )
                                }
                                dialog.updateData(result)
                            }
                        }
                    }
                }
            }
        }, resultCallback, object : MenuInterface.OnSearch {
            override fun onSearch(str: String) {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    when (type) {
                        InspectionDialogType.BRANCH_LIST -> {
                            mViewModel.getBranchRequests(str).collectLatest { pagingData ->
                                val result = pagingData.map {
                                    MenuModel(
                                        id = it.code,
                                        title = it.name
                                    )
                                }
                                dialog.updateData(result)
                            }
                        }
                        InspectionDialogType.JOB_TITLE_LIST -> {
                            if (str.isNotBlank()) {
                                if (!str[0].isDigit()) {
                                    dialog.viewBinding?.labelWarning?.gone()
                                    mViewModel.getJob(str).collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.jobCode,
                                                title = it.jobDescription
                                            )
                                        }
                                        dialog.updateData(result)
                                    }
                                } else {
                                    dialog.viewBinding?.labelWarning?.apply {
                                        visible()
                                        text = getString(R.string.error_char_search)
                                        setTextColor(ContextCompat.getColor(context,
                                            R.color.text_color_dialog_red))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
        dialog.show(childFragmentManager, "SubmitInspectionRequestFragment")
    }
}
