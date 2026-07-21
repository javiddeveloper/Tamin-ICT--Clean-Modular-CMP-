package com.tamin.taminhamrah.ui.home.services.studentContract.payment.insurance

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.AppBarLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateFreelanceDebitResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckContractStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FreelanceLastPaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalInsuranceLastPaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentResponse
import com.tamin.taminhamrah.databinding.FragmentInsurancePaymentBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.studentContract.payment.PaymentCalculationDetailFragment
import com.tamin.taminhamrah.utils.EventObserver
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import java.lang.Integer.parseInt
import kotlin.math.abs

@AndroidEntryPoint
class InsurancePaymentFragment :
    BaseFragment<FragmentInsurancePaymentBinding, InsurancePaymentViewModel>() {

    companion object {
        const val INSURANCE_TYPE = "INSURANCE_TYPE"
    }

    lateinit var debitListAdapter: KeyValueAdapter

    @Inject
    lateinit var webLauncher: CustomTabsLauncher

    override val mViewModel: InsurancePaymentViewModel by viewModels()

    private val insuranceType by lazy {
        arguments?.getSerializable(INSURANCE_TYPE) as? EnumInsuranceType
    }

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_insurance_payment
    }

    override fun setupObserver() {
        mViewModel.mldUserInfo.observe(this, ::showResultInfoUser)
        mViewModel.mldCheckAgeAndHistory.observe(this, ::showResultCheckAgeAndHistory)
        mViewModel.mldCheckContractStatus.observe(this, ::showResultContractStatus)
        mViewModel.mldCheckSuccessPayment.observe(this, ::showResultCheckSuccessPayment)
        mViewModel.mldLastPayment.observe(this, ::showResultLastPayment)
        mViewModel.mldLastPaymentOptional.observe(this, ::showResultLastPaymentOptional)
        mViewModel.mldCalculateDebit.observe(this, EventObserver { showResultCalculateDebit(it) })
        mViewModel.mldPayment.observe(this, EventObserver { showPaymentResult(it) })

    }

    private fun showResultCheckSuccessPayment(result: GeneralRes) {
        //do nothing!!!!
    }

    private fun showResultContractStatus(result: CheckContractStatusResponse) {
        if (result.isSuccess) {
            if ((result.data as? String) == "ok14") {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_can_not_pay_indivisual_insurance)
                )
            }
        }
    }

    private fun showResultCheckAgeAndHistory(result: CheckAgeAndHistoryResponse) {
        if (result.isSuccess) {
            /*result.data?.contract?.cntFreeJobCode?.let {
                if (it != "099785" || it != "099796") {//099785 houseKeeper , 099796 student
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_can_not_pay_from_this_menu)
                    )

                    dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {

                            requireActivity().onBackPressed()
                        }

                        override fun onCancelClick() {
                            requireActivity().onBackPressed()
                        }
                    })

                    dialog.show(childFragmentManager, "CheckAgeAndHistoryResponse")
                } else {
                    if (it == "110977") {//
                        mViewModel.checkContractStatus()
                    }
                }

            }*/

            if (result.data?.contract?.cntFreeJobCode == "110977") {// دانشجویان علوم پزشکی
                mViewModel.checkContractStatus()
            }

        }
    }

    private fun showResultCalculateDebit(result: CalculateFreelanceDebitResponse) {
        if (result.isSuccess) {
            if (!result.data?.messageInformation.isNullOrEmpty()) {

                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    result.data?.messageInformation!!
                )
            }
            result.data?.createKeyValue()?.let { debitListAdapter.setItems(it) }

            viewDataBinding?.paymentGroup?.visibility = View.VISIBLE

        }
    }

    private fun showResultLastPayment(result: FreelanceLastPaymentResponse) {
        if (result.isSuccess) {

            if (!result.data?.getLocalDate().equals("")) {
                viewDataBinding?.descSuccessPaymentHistory?.visibility = View.VISIBLE
                viewDataBinding?.imageView1?.visibility = View.VISIBLE
                viewDataBinding?.descSuccessPaymentHistory?.text =
                    getString(R.string.label_last_payment, result.data?.getLocalDate())
            }

            result.data?.chekReloLap.let {
                if (it != "1" && it != "") {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        result.data?.chekReloLap ?: ""
                    )
                    dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {


                        }

                        override fun onCancelClick() {

                        }
                    }
                    )
                    dialog.show(childFragmentManager, "chekReloLap")
                }
            }
        }
    }

    private fun showResultLastPaymentOptional(result: OptionalInsuranceLastPaymentResponse) {
        if (result.isSuccess) {
            if (result.getLocalDate() != "") {
                viewDataBinding?.descSuccessPaymentHistory?.visibility = View.VISIBLE
                viewDataBinding?.imageView1?.visibility = View.VISIBLE
                viewDataBinding?.descSuccessPaymentHistory?.text =
                    getString(R.string.label_last_payment, result.getLocalDate())
            }
        }
    }

    private fun showResultInfoUser(result: ConcludingStudentInsuranceContractResponse) {
        if (result.isSuccess) {
            mViewModel.checkAgeAndHistory(insuranceType ?: EnumInsuranceType.TYPE_FREELANCE)
            result.data?.apply {
                //viewDataBinding?.appBar?.userInfo = result.data

                viewDataBinding?.appBar?.line2?.visible()
                viewDataBinding?.appBar?.tvNationalCode?.text =
                    "${getString(R.string.label_national_code)} : ${personalInfo?.nationalId}"
                viewDataBinding?.appBar?.tvBirthDate?.text =
                    "${getString(R.string.birthdate)} : ${getPersianDate(personalInfo?.dateOfBirth)}"
                viewDataBinding?.appBar?.tvSubTitle?.text =
                    "${personalInfo?.firstName ?: ""} ${personalInfo?.lastName ?: ""}"
            }
        }
    }

    private fun showPaymentResult(result: PaymentResponse) {
        if (result.isSuccess) {
            if (result.data == null) {

                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_failed_request)
                )

            } else {
                if (result.data?.succeed == true) {

                    viewDataBinding?.paymentGroup?.visibility = View.GONE
                    month = 0
                    setMonthCount()
                    mViewModel.saveSystemType(insuranceType?.systemType)
                    webLauncher.launchUrl(result.data?.paymentURL, launchInBrowser = true) { intent ->
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                    }
                    requireActivity().onBackPressedDispatcher.onBackPressed()

                } else {
                    if (!result.data?.responseMessage.isNullOrBlank()) {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            result.data?.responseMessage ?: ""
                        )
                    }
                }
            }
        }
    }

    override fun initView() {
        webLauncher.registerLifecycle(viewLifecycleOwner)
        setupToolbar()
        // handlePageDestination(R.id.action_insurancePaymentFragment_to_Payment, Bundle())
        debitListAdapter = KeyValueAdapter()
        viewDataBinding?.apply {
            when (insuranceType) {
                EnumInsuranceType.TYPE_STUDENT, EnumInsuranceType.TYPE_FREELANCE -> {
                    //self_employed
                    // student
                    labelDesc1.descTxt.text = getText(R.string.label_insurance_payment_desc_1)
                    labelDesc2.descTxt.text = getText(R.string.label_insurance_payment_desc_2)
                    labelDesc3.descTxt.text = getText(R.string.label_insurance_payment_desc_3)
                    labelDesc4.descTxt.text = getText(R.string.label_insurance_payment_desc_4)
                }

                EnumInsuranceType.TYPE_OPTIONAL -> {
                    //optional
                    labelDesc1.descTxt.text =
                        getText(R.string.label_optional_insurance_payment_desc_1)
                    labelDesc2.descTxt.text =
                        getText(R.string.label_optional_insurance_payment_desc_2)
                    labelDesc3.descTxt.text =
                        getText(R.string.label_optional_insurance_payment_desc_3)
                    labelDesc4.descTxt.text =
                        getText(R.string.label_optional_insurance_payment_desc_4)
                }


                else -> {}
            }

            setMonthCount()
            recyclerPaymentInfo.apply {
                adapter = debitListAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(requireContext()))
                }
            }
        }

    }

    override fun getData() {
        when (insuranceType) {
            EnumInsuranceType.TYPE_OPTIONAL -> {
                mViewModel.getInitDataOptional()
            }

            EnumInsuranceType.TYPE_FRACTION -> mViewModel.getFractionData()

            else -> {
                mViewModel.getInitData()
            }
        }
        insuranceType?.systemType?.let { mViewModel.updatePaymentStatus(it) }
    }

    private var month = 0
    private fun setMonthCount() {
        viewDataBinding?.tvMonthCount?.text =
            getString(R.string.label_insurance_month_count, month.toString())
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnCalculatePayment.setOnClickListener {
                appBar.appBarView.setExpanded(false, true)
                nestedScrollView.scrollTo(0, btnCalculatePayment.bottom)
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                if (month == 0) {

                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.select_month_fo_caculate)
                    )

                } else {
                    if (!mViewModel.mldLastPayment.value?.data?.medicalRsltResend.isNullOrEmpty()) {
                        val medicalRsltResendMonth = parseInt(
                            mViewModel.mldLastPayment.value?.data?.medicalRsltResend.toString()
                                .substring(2, 4)
                        )
                        if (medicalRsltResendMonth < month) {

                            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                            dialog.arguments = createBundle(
                                MessageOfRequestDialogFragment.MessageType.WARNING,
                                getString(R.string.message_medical_result_resend_month)
                            )
                            dialog.setDialogClickListener(object :
                                DialogClickInterface.onClickListener {
                                override fun onConfirmClick() {
                                    mViewModel.calculateDebitByMonth(month, insuranceType)

                                }

                                override fun onCancelClick() {

                                }
                            }
                            )
                            dialog.show(childFragmentManager, "medicalRsltResendMonth")
                        }
                    } else {
                        mViewModel.calculateDebitByMonth(month, insuranceType)
                    }
                }
            }

            btnMonthCountInc.setOnClickListener {
                if (month in 0..11) {
                    month += 1
                    btnMonthCountInc.isClickable = true
                    btnMonthCountDec.isClickable = true

                    viewDataBinding?.isEnabledButton = true

                } else {
                    if (month == 12) {
                        btnMonthCountInc.isClickable = false
                        btnMonthCountDec.isClickable = true

                        // viewDataBinding?.isEnabledButton = false
                    }
                }
                viewDataBinding?.paymentGroup?.visibility = View.GONE
                setMonthCount()
            }

            btnMonthCountDec.setOnClickListener {
                if (month in 2..12) {
                    month -= 1
                    btnMonthCountInc.isClickable = true
                    btnMonthCountDec.isClickable = true
                    viewDataBinding?.isEnabledButton = true

                } else {
                    if (month == 1) {
                        month = 0
                        btnMonthCountInc.isClickable = true
                        btnMonthCountDec.isClickable = false

                        viewDataBinding?.isEnabledButton = false
                    }
                }

                viewDataBinding?.paymentGroup?.visibility = View.GONE
                setMonthCount()
            }

            btnPayment.setOnClickListener {

                mViewModel.mldCalculateDebit.value?.peekContent()?.data?.apply {
                    mViewModel.insurancePayment(
                        startDate,
                        endDate,
                        total,
                        month,
                        insuranceType?.systemType,
                        mViewModel.getRedirectUrl(insuranceType)
                    )
                }
            }

            appBar.toolbar.imageBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnCheckPaymentDetails.setOnClickListener {
                val bundleService = Bundle()
                bundleService.putString(
                    Constants.TOOLBAR_TITLE,
                    btnCheckPaymentDetails.text.toString()
                )

                bundleService.putInt(
                    Constants.TOOLBAR_ICON_IMAGE,
                    Utility.getToolbarIconDrawable(arguments)
                )

                bundleService.putLong(
                    PaymentCalculationDetailFragment.ARG_START_DATE,
                    mViewModel.mldCalculateDebit.value?.peekContent()?.data?.startDate ?: 0L
                )
                bundleService.putLong(
                    PaymentCalculationDetailFragment.ARG_END_DATE,
                    mViewModel.mldCalculateDebit.value?.peekContent()?.data?.endDate ?: 0L
                )

                bundleService.putSerializable(
                    PaymentCalculationDetailFragment.INSURANCE_TYPE,
                    insuranceType
                )

                handlePageDestination(
                    R.id.action_payment_to_details,
                    bundle = bundleService
                )
            }
        }
    }

    fun setupToolbar() {
        viewDataBinding?.let {
            it.appBar.apply {
                var toolbarTitle = ""
                arguments?.let { args ->
                    ImageUtils.imageDrawable(imgIcon, Utility.getToolbarIconDrawable(args))
                    toolbarTitle = Utility.getToolbarTitle(args)
                }
                //    tvTitle.text=toolbarTitle
                toolbar.imgInfo.visibility = View.GONE
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
                tvTitle.text = toolbarTitle

            }
        }
    }

}