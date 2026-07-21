package com.tamin.taminhamrah.ui.home.services.employer.payment

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.databinding.FragmentPaymentResultBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.employer.payment.viewmodel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentResultFragment :
    BaseFragment<FragmentPaymentResultBinding, PaymentViewModel>() {

    companion object {
        const val ARG_PAYMENT_TICKEt = "ARG_PAYMENT_TICKEt"
    }

    override val mViewModel: PaymentViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_payment_result
    }

    override fun setupObserver() {
        mViewModel.mldPaymentResult.observe(this, ::getPaymentResult)
    }

    private fun getPaymentResult(result: PaymentInfoResponse?) {
        if (result?.isSuccess == true) {
            viewDataBinding?.item = result.data
        }
    }

    override fun initView() {

        viewDataBinding?.apply {
            /* val preview = arguments?.getParcelable(ARG_PAYMENT_INFO) as? PaymentPreview
             item = preview
             layHeader.imbBack.apply {
                 visibility = View.VISIBLE
                 setOnClickListener {
                     requireActivity().onBackPressed()
                 }
             }*/
        }
    }

    override fun getData() {
        mViewModel.getPaymentResult(getTicketFromArgs())
        mViewModel.updatePaymentStatus()

    }

    private fun getTicketFromArgs(): String {
        return arguments?.getString(ARG_PAYMENT_TICKEt)?:""
    }


    override fun onClick() {

        viewDataBinding?.apply {


        }
    }

    fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }

}