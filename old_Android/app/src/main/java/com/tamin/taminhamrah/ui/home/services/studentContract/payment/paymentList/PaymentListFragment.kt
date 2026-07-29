package com.tamin.taminhamrah.ui.home.services.studentContract.payment.paymentList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.PaymentListModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.PaymentListResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.detailPayment.DetailPaymentListResponse
import com.tamin.taminhamrah.databinding.FragmentPaymentListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.studentContract.payment.paymentList.adapter.PaymentListAdapter
import com.tamin.taminhamrah.ui.home.services.studentContract.payment.paymentList.detailPayment.DetailPaymentBottomSheet
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PaymentListFragment : BaseFragment<FragmentPaymentListBinding, PaymentListViewModel>() {
    override val mViewModel: PaymentListViewModel by viewModels()
    var contractNumber = "0"
    lateinit var listAdapter: PaymentListAdapter

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_payment_list
    }

    override fun setupObserver() {
        mViewModel.mldPaymentList.observe(this, ::showResultPaymentList)
        mViewModel.mldDetailPaymentList.observe(this, ::showResultDetailPayment)
        mViewModel.mldCheckSuccessPayment.observe(this, ::showResultCheckSuccessPayment)
    }

    private fun showResultCheckSuccessPayment(result: GeneralRes) {

    }

    private fun showResultDetailPayment(result: DetailPaymentListResponse) {
        if (!result.isSuccess)
            return
        val dialog = DetailPaymentBottomSheet()
        val bundle = Bundle()
        dialog.arguments = bundle
        dialog.arguments
        dialog.setItem(result.data?.list)
        dialog.show(childFragmentManager, "trtyutyt")
    }

    private fun showResultPaymentList(result: PaymentListResponse) {
        if (!result.isSuccess)
            return
        viewDataBinding?.recycler?.showMessage()
        result.data?.list?.let { list ->
            if (list.isNotEmpty())
                viewDataBinding?.recycler?.hideMessage()

            listAdapter.setItems(
                list,
                object : AdapterInterface.OnItemClickListener<PaymentListModel> {
                    override fun onItemClick(
                        item: PaymentListModel,
                        transitionView: View?,
                        tag: String?
                    ) {
                        Timber.tag("onItemClick: ").i(item.toString())
                        item.debtNumber?.let { debtNumber ->
                            mViewModel.getDetailPaymentList(contractNumber, debtNumber)
                        }
                    }

                })
        }
    }


    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )

        arguments?.getString(Constants.CONTRACT_NUMBER_KEY)?.let { contractNumber = it }
        listAdapter = PaymentListAdapter()
        viewDataBinding?.recycler?.getRecycler()?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
            }
        }

    }

    override fun getData() {
        if (contractNumber != "0")
            mViewModel.getContractsPaymentsListFreelance(contractNumber)

        arguments?.getString(Constants.CONTRACT_SYSTEM_TYPE)
            ?.let { mViewModel.checkSuccessPayment(it) }
    }

    override fun onClick() {
    }


}