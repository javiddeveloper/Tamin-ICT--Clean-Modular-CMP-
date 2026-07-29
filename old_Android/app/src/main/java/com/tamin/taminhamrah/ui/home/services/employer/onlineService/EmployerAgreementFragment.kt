package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerCommitmentResponse
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.WorkshopInfoWithoutContract
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.databinding.EmployerAgreementStep1Binding
import com.tamin.taminhamrah.databinding.EmployerAgreementStep2Binding
import com.tamin.taminhamrah.databinding.EmployerAgreementStep3Binding
import com.tamin.taminhamrah.databinding.FragmentStepperBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.navigateUp
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class EmployerAgreementFragment :
    BaseFragment<FragmentStepperBinding, EmployerAgreementInfoViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener{

////////////////////////////////////////////////////////////////////////////////// Class Variables ////////////////////////////////////

    override val mViewModel: EmployerAgreementInfoViewModel by viewModels()

    lateinit var listAdapter: WorkshopWithoutContractAdapter

    private var restartedFragment = false

    private var timer: CountDownTimer? = null
    private var fragmentIsInBackground = false
    private var timerIsFinished = false

////////////////////////////////////////////////////////////////////////////////// Class  Variables////////////////////////////////////

    override fun getBindingVariable(): Pair<Int, Any?> = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_stepper

    override fun setupObserver() {
        mViewModel.mldRequestOfVerificationCode.observe(this, ::onResult)
        mViewModel.mldResultOfVerificationCode.observe(this, ::onCommitmentResponse)
        mViewModel.mldAgreementResult.observe(this, ::onAgreementResult)
        mViewModel.mldCurrentUser.observe(this, ::onUserResult)
    }

    private fun onUserResult(result: CurrentUserResponse?) {
        if (result?.isSuccess==true){
            (viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? EmployerAgreementStep1Binding)?.apply {

                if(!result.data?.mobile.isNullOrBlank()) {
                    inputMobile.enableView(false)
                }

                inputMobile.getInput().setText(result.data?.mobile?:"")
                inputEmail.getInput().setText(result.data?.email?:"")
            }
        }
    }

    override fun initView() {

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
        initStepper()

    }

    override fun getData() {

        mViewModel.getCurrentUserInfo()

    }

    override fun onClick() {

    }


    override fun onStop() {
        fragmentIsInBackground = true
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        if (restartedFragment) {
            initStepper(viewDataBinding?.stepper?.currentStepIndex ?: 1)
            restartedFragment = false
        }
        fragmentIsInBackground = false
        if (timerIsFinished) showTimerFinishedAlert()
    }

    private fun onResult(result: GeneralRes?, showAlertMessage: Boolean = true) {
        if (result?.isSuccess == true) {

            if (showAlertMessage)
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.CONFIRM,
                    getString(R.string.message_succes_verification_code)
                )

            (viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? EmployerAgreementStep1Binding)?.apply {

                mViewModel.dataModel.emailNew = inputEmail.getValue()
                mViewModel.dataModel.mobileNew = inputMobile.getValue()

                groupRegister.visibility = View.GONE
                groupVerifyCode.visibility = View.VISIBLE
                timer?.start()

                stepperVerifyCode.nextButtonTitle =
                    getString(R.string.label_submit_and_view_employer_info)

            }

        } else {
            (viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? EmployerAgreementStep1Binding)?.apply {

                groupRegister.visibility = View.VISIBLE
                groupVerifyCode.visibility = View.GONE
            }
        }
    }

    private fun onCommitmentResponse(result: EmployerCommitmentResponse?) {
        if (result?.isSuccess == true) {

            mViewModel.dataModel.apply {
                mobilePrev = result.data?.mobile
                emailPrev = result.data?.email
                fullName = "${result.data?.firstName} ${result.data?.lastName}"
                nationalCode = result.data?.nationalCode
            }

            viewDataBinding?.stepper?.nextStep()

            (viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? EmployerAgreementStep1Binding)?.apply {
                groupRegister.visibility = View.VISIBLE
                groupVerifyCode.visibility = View.GONE

                stepperVerifyCode.nextButtonTitle = getString(R.string.send_validation_request)

                mViewModel.dataModel.verificationCode = inputVerificationCode.getValue()

                timer?.cancel()
                tvCountDown.text = "00:00"
            }
            (viewDataBinding?.stepper?.getStepLayoutBindingByStep(2) as? EmployerAgreementStep2Binding)?.apply {

                mViewModel.dataModel.apply {
                    tvCurrentMob.text = mobilePrev
                    tvNewMob.text = mobileNew
                    tvCurrentEmail.text = emailPrev
                    tvNewEmail.text = emailNew
                    tvFullName.text = fullName
                    tvNationalCode.text = nationalCode
                }

                this@EmployerAgreementFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getWorkshopsInfoWithoutContract()
                        .collectLatest { pagingData -> listAdapter.submitData(pagingData) }
                }

            }

            (viewDataBinding?.stepper?.getStepLayoutBindingByStep(3) as? EmployerAgreementStep3Binding)?.apply {

                tv1.text = HtmlCompat.fromHtml(
                    getString(
                        R.string.label_employer_online_services_agreement_1,
                        mViewModel.dataModel.fullName,
                        mViewModel.dataModel.nationalCode
                    ), HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
    }

    private fun onAgreementResult(result: GeneralRes?) {
        if (result?.isSuccess == true) {
            navigateUp()
        }
    }

    private fun initialOTP() = EmployerAgreementStep1Binding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper,
        true
    ).apply {

        timer = object : CountDownTimer(5 * 60 * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                timerIsFinished = false
                var seconds = ((millisUntilFinished / 1000).toInt() % 60).toString()
                var minutes = (millisUntilFinished / (1000 * 60) % 60).toString()

                if (minutes.length == 1) minutes = "0$minutes"
                if (seconds.length == 1) seconds = "0$seconds"
                tvCountDown.text = "$minutes : $seconds"
            }

            override fun onFinish() {
                timerIsFinished = true
                if (!fragmentIsInBackground) showTimerFinishedAlert()
            }
        }

        stepperVerifyCode.nextButtonTitle = getString(R.string.send_validation_request)

        mViewModel.dataModel.apply {
            if(!mobileNew.isNullOrBlank()) {
                inputMobile.enableView(false)
                inputMobile.getInput().setText(mobileNew)
            }
            inputEmail.getInput().setText(emailNew)
            inputVerificationCode.getInput().setText(verificationCode)
        }

        btnEditInfo.setOnClickListener {
            timer?.cancel()
            resetOptStep(this)
        }

    }

    private fun showTimerFinishedAlert() {
        val itemViewBinding =
            viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? EmployerAgreementStep1Binding
        itemViewBinding?.apply {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.message_send_verification_code_finished)
            )
            dialog.setDialogClickListener(object :
                DialogClickInterface.onClickListener {
                override fun onConfirmClick() {

                    viewDataBinding?.apply {
                        resetOptStep(itemViewBinding)
                    }
                }

                override fun onCancelClick() {
                }
            }
            )
            dialog.show(childFragmentManager, "returnToPrevPage")
        }

    }

    private fun resetOptStep(itemViewBinding: EmployerAgreementStep1Binding) {
        itemViewBinding.apply {
            groupRegister.visibility = View.VISIBLE
            groupVerifyCode.visibility = View.GONE
            stepperVerifyCode.nextButtonTitle = getString(R.string.send_validation_request)
        }

    }

    private fun initialStepEmployerInfo() = EmployerAgreementStep2Binding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper,
        true
    ).apply {
        stepperEmployerInfo.nextButtonTitle =
            getString(R.string.label_submit_and_see_online_services_agreement)

        listAdapter = WorkshopWithoutContractAdapter(object : OnItemClickListener<WorkshopInfoWithoutContract>{
            override fun onItemClick(
                item: WorkshopInfoWithoutContract,
                transitionView: View?,
                tag: String?
            ) {
                val bundle = Bundle()
                bundle.putString(
                    Constants.TOOLBAR_TITLE,
                    getString(R.string.label_workshop_contract_row_list)
                )
                bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
                bundle.putString(mViewModel.ARG_WORKSHOP_ID, item.workshopId)
                bundle.putString(mViewModel.ARG_BRANCH_CODE, item.branchCode)

                handlePageDestination(
                    R.id.action_agreement_to_contact_list,
                    bundle
                )
            }
        })
        setupRecycler(recycler, listAdapter)

        mViewModel.dataModel.apply {
            tvCurrentMob.text = mobilePrev
            tvNewMob.text = mobileNew
            tvCurrentEmail.text = emailPrev
            tvNewEmail.text = emailNew
            tvFullName.text = fullName
            tvNationalCode.text = nationalCode

            this@EmployerAgreementFragment.lifecycleScope.launchWhenCreated {
                mViewModel.getWorkshopsInfoWithoutContract()
                    .collectLatest { pagingData -> listAdapter.submitData(pagingData) }
            }
        }
    }

    private fun initialStepCommitment() = EmployerAgreementStep3Binding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper,
        true
    ).apply {
        stepperRules.nextButtonTitle = getString(R.string.label_confirm_agreement_rules)
        cbConfirmRules.setOnCheckedChangeListener { _, checked ->
            stepperRules.nextStepEnable = checked
        }
    }

    private fun initStepper(initialStep: Int = 1) {
        Timber.tag("debugReturnPage").i("initStepper: ")
        viewDataBinding?.apply {
            val stepLayouts = listOf(
                initialOTP(),
                initialStepEmployerInfo(),
                initialStepCommitment()
            )
            // mViewModel.mldAddedUser.value?.needToCalculate = true
            stepper.initial(stepLayouts, initialStep)
            stepper.onNextStepClickListener = this@EmployerAgreementFragment
            stepper.onPreviousStepClickListener = this@EmployerAgreementFragment
        }
    }

    //////////////////////////////////////////////////////////////////////////////////Call Backs ////////////////////////////////////
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.apply {
            when (stepIndex) {
                1 -> {
                    (getStepLayoutBindingByStep(1) as? EmployerAgreementStep1Binding)?.apply {

                        when (stepperVerifyCode.nextButtonTitle) {
                            getString(R.string.send_validation_request) -> {
                                if (inputMobile.getValue().isNotBlank() && inputEmail.getValue().isNotBlank())

                                    if (inputMobile.getValue() == mViewModel.dataModel.mobileNew &&
                                        inputEmail.getValue() == mViewModel.dataModel.emailNew
                                    )
                                        onResult(
                                            mViewModel.mldRequestOfVerificationCode.value, false
                                        )
                                    else {
                                        mViewModel.sendCommitmentRequest(inputMobile.getValue(), inputEmail.getValue())
                                    }
                            }
                            getString(R.string.label_submit_and_view_employer_info) -> {
                                if (inputVerificationCode.getValue().isNotBlank()) {
                                    if (inputVerificationCode.getValue() == mViewModel.dataModel.verificationCode)
                                        onCommitmentResponse(mViewModel.mldResultOfVerificationCode.value)
                                    else
                                        mViewModel.getUserInfoWithVerificationCode(
                                            inputVerificationCode.getValue()
                                        )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    nextStep()
                }
                3 -> {
                    mViewModel.postEmployerAgreement()
                }
                else -> {
                    nextStep()
                }
            }
        }
    }

    override fun onPreviousStepClickListener(
        stepIndex: Int,
        step: VerticalStepperItemView
    ) {
        viewDataBinding?.stepper?.previousStep()
    }
}
