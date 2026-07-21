package com.tamin.taminhamrah.ui.home.services.studentContract.payment

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentPaymentListBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.studentContract.payment.insurance.InsurancePaymentViewModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PaymentCalculationDetailFragment :
    BaseFragment<FragmentPaymentListBinding, InsurancePaymentViewModel>() {
    override val mViewModel: InsurancePaymentViewModel by viewModels()
    lateinit var listAdapter: PaymentCalculationAdapter

    companion object {
        const val ARG_START_DATE = "ARG_START_DATE"
        const val ARG_END_DATE = "ARG_END_DATE"
        const val INSURANCE_TYPE = "INSURANCE_TYPE"
    }

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_payment_list
    }

    override fun setupObserver() {

    }

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )

        listAdapter = PaymentCalculationAdapter()

        setupRecycler(viewDataBinding?.recycler, listAdapter)

        viewDataBinding?.apply {
            labelTitle.text = getString(
                R.string.lable_payment_calculate,
                ConvertDate.convertTimestampToPersianDate(getStartDate()),
                ConvertDate.convertTimestampToPersianDate(getEndDate())
            )
            /* tvToolbarTitle.text = getString(R.string.sms)
             imageBack.setOnClickListener {
                 requireActivity().onBackPressed()
             }*/
        }

    }

    fun getStartDate(): Long {
        return arguments?.getLong(ARG_START_DATE) ?: 0L
    }

    fun getEndDate(): Long {
        return arguments?.getLong(ARG_END_DATE) ?: 0L
    }

    override fun getData() {

        this@PaymentCalculationDetailFragment.lifecycleScope.launchWhenCreated {
            if (arguments?.getSerializable(INSURANCE_TYPE) == EnumInsuranceType.TYPE_OPTIONAL) {
                mViewModel.getOptionalInsurancePaymentCalculationDetailList(getStartDate(), getEndDate())
                    ?.collectLatest { pagingData ->
                        listAdapter.submitData(pagingData)
                    }
            } else {
                mViewModel.getPaymentCalculationDetailList(getStartDate(), getEndDate())
                    ?.collectLatest { pagingData ->
                        listAdapter.submitData(pagingData)
                    }
            }

        }
    }

    override fun onClick() {
    }


}