package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit

import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtInquiryResponse
import com.tamin.taminhamrah.databinding.FragmentWorkshopDepitInquiryBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopDebtInquiryFragment :
    BaseFragment<FragmentWorkshopDepitInquiryBinding, WorkshopInfoViewModel>() {

    lateinit var listAdapter: KeyValueAdapter

    override val mViewModel: WorkshopInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_depit_inquiry
    }

    override fun setupObserver() {
        mViewModel.mldWorkshopDepInquiry.observe(this, ::showResult)
    }

    private fun getWorkshopId(): String? {
        return arguments?.getString(Constants.WORKSHOP_ID)
    }

    private fun getBranchCode(): String? {
        return arguments?.getString(Constants.BRANCH_ID)
    }

    override fun initView() {
        listAdapter = KeyValueAdapter()
        viewDataBinding?.recyclerDebit?.getRecycler()?.adapter = listAdapter
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
        getWorkshopId()?.let { workshopId ->
            getBranchCode()?.let { branchCode ->
                mViewModel.getWorkshopDebtInquiry(workshopId, branchCode)
            }
        }
    }

    override fun onClick() {

    }

    private fun showResult(result: WorkShopDebtInquiryResponse) {
        if (result.isSuccess) {
            result.data?.let {
                listAdapter.setItems(it.createKeyValue())
            }
        }
    }
}