package com.tamin.taminhamrah.ui.home.services.employer.debt.followUpAndPayment.payment

import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants.BRANCH_CODE
import com.tamin.taminhamrah.Constants.DEBIT_SERIAL_NUMBER
import com.tamin.taminhamrah.Constants.DEBT_NUMBER
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.debit.InstallmentPaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentResponse
import com.tamin.taminhamrah.databinding.FragmentPaymentDebtBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.employer.debt.adapter.PaymentDebtListAdapter
import com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel.InstallmentDebtViewModel
import com.tamin.taminhamrah.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class PaymentDebtFragment : BaseFragment<FragmentPaymentDebtBinding, InstallmentDebtViewModel>() {

    override val mViewModel: InstallmentDebtViewModel by viewModels()

    @Inject
    lateinit var webLauncher: CustomTabsLauncher
    var debitSerialNumber = ""
    val itemAdapter by lazy {
        PaymentDebtListAdapter()
    }
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_payment_debt

    override fun setupObserver() {
        mViewModel.mldInstallmentPayment.observe(this,::onPaymentListResponse)
        mViewModel.mldPaymentDebitToken.observe(this, EventObserver{onPaymentDebtToken(it)})
        mViewModel.mldPaymentPreview.observe(this, EventObserver { onPaymentResult(it) })
    }

    override fun initView() {
        webLauncher.registerLifecycle(viewLifecycleOwner)
        viewDataBinding?.apply {
            recycler.apply {
                adapter = itemAdapter
            }
            setupToolbar(appBar, appbarBackgroundImage.imageBackground)
        }
    }

    override fun getData() {
        val debtNumber = arguments?.getString(DEBT_NUMBER)
        val branchCode = arguments?.getString(BRANCH_CODE)
        debitSerialNumber = arguments?.getString(DEBIT_SERIAL_NUMBER)?:""
        mViewModel.checkPaymentDebt(debitSerialNumber)
        if (debtNumber!=null && branchCode != null) {
            mViewModel.getPaymentDebitList(debtNumber, branchCode)
        }
    }

    override fun onClick() {
        viewDataBinding?.btnPayment?.setOnClickListener {
            mViewModel.getPaymentDebitToken(debitNumber = debitSerialNumber)
        }
    }

    //region Listener
    private fun onPaymentListResponse(response: InstallmentPaymentResponse) {
        if (response.isSuccess){
            itemAdapter.setItems(response.data?.list?: emptyList())
        }
    }


    private fun onPaymentDebtToken(result: PaymentResponse) {
        if (result.isSuccess) {

            mViewModel.saveSystemType( EnumInsuranceType.TYPE_DEBT.systemType)
            webLauncher.launchUrl(result.data?.paymentURL, launchInBrowser = true)  { intent ->
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            }

           /* if (result.data == null) {

                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_failed_request)
                )

            } else {
                if (result.data?.succeed == true) {
                    mViewModel.normalDebitPaymentPreview()

                } else {
                    if (!result.data?.responseMessage.isNullOrBlank()) {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            result.data?.responseMessage ?: ""
                        )
                    }
                }
            }*/
        }
    }

    private fun onPaymentResult(result: PaymentInfoResponse) {
        /*if (result.isSuccess) {
            handlePageDestination(R.id.action_paymentDebtFragment_to_Payment, Bundle().apply {
                putParcelable(PaymentFragment.ARG_PAYMENT_INFO, result.data)
                putString(PaymentFragment.SYSTEM_TYPE, EnumInsuranceType.TYPE_DEBT.systemType)
                putString(DEBIT_SERIAL_NUMBER,debitSerialNumber)
            })
        }*/
    }
    //endregion
}