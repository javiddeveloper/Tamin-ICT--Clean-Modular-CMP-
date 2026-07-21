package com.tamin.taminhamrah.ui.home.services.employer.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.databinding.FragmentPaymentResultBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.payment.viewmodel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentResultDialogFragment :
    BaseBottomSheetDialogFragment<FragmentPaymentResultBinding, PaymentViewModel>() {
    companion object {
        const val ARG_PAYMENT_TICKET = "ARG_PAYMENT_TICKET"
        const val ARG_INITIALIZER_SERVICE = "ARG_INITIALIZER_SERVICE"
    }

    override val mViewModelDialog: PaymentViewModel by viewModels()
    override fun getLayoutId() = R.layout.fragment_payment_result
    var mListener: OnResultListener<Map<String, String>>? = null

    fun setListener(listener: OnResultListener<Map<String, String>>) {
        mListener = listener
    }


    fun setupObserver() {
        mViewModelDialog.mldPaymentResult.observe(this, ::getPaymentResult)
        mViewModelDialog.mldCheckSuccessPayment.observe(this, ::onUpdatePaymentStatus)
    }

    private fun onUpdatePaymentStatus(generalRes: GeneralRes?) {
        //do nothing
    }


    private var onListener: MenuInterface.OnResult? = null

    fun setListener(listener: MenuInterface.OnResult) {
        onListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        onClick()
        setupObserver()
    }

    private fun getData() {
        mViewModelDialog.getPaymentResult(getTicketFromArgs())

        when (arguments?.getString(ARG_INITIALIZER_SERVICE)) {
            Constants.REDIRECT_HOST_FREELANCE,
            Constants.REDIRECT_HOST_OPTIONAL,
//            Constants.REDIRECT_TFH,
            Constants.REDIRECT_HOST_FRACTION -> {
                mViewModelDialog.updatePaymentStatus()
            }
            Constants.REDIRECT_WORKERS_PAYMENT -> {
                mViewModelDialog.updateWorkersPaymentStatus()
            }
            Constants.REDIRECT_HOST_EMPLOYER_DEBT -> {
                mViewModelDialog.updatePaymentStatus()
            }
        }
    }

    private fun getTicketFromArgs(): String {
        return arguments?.getString(ARG_PAYMENT_TICKET) ?: ""
    }


    private fun getPaymentResult(result: PaymentInfoResponse?) {
        if (result?.isSuccess == true) {
            viewBinding?.apply {
                item = result.data
                Log.i("getPaymentResult: ", item.toString())
                when (result.data?.paymentStatus) {
                    "NOT_PAYED" -> {

                        setPaymentStatus(
                            getString(R.string.payment_not_payed),
                            ContextCompat.getColor(requireContext(), R.color.red)
                        )
                    }

                    "VERIFYING", "SUCCESSFUL" -> {
                        setPaymentStatus(
                            getString(R.string.payment_success),
                            ContextCompat.getColor(requireContext(), R.color.green)
                        )
                    }

                    "EXPIRED" -> {
                        setPaymentStatus(
                            getString(R.string.payment_expired),
                            ContextCompat.getColor(requireContext(), R.color.amber)
                        )
                    }

                    "UNKNOWN", "FAILED" -> {
                        setPaymentStatus(
                            getString(R.string.payment_failed),
                            ContextCompat.getColor(requireContext(), R.color.red)
                        )
                    }

                }
            }

        }
    }

    private fun setPaymentStatus(result: String, color: Int) {
        viewBinding?.apply {
            tvPaymentStatus.apply {
                text = result
                setTextColor(color)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onClick() {

        viewBinding?.apply {
            btnOk.setOnClickListener {
                dismiss()
            }
        }
    }

}