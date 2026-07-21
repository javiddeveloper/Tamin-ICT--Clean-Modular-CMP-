package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentWorkshopContractListBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WorkshopContractListFragment :
    BaseFragment<FragmentWorkshopContractListBinding, EmployerAgreementInfoViewModel>() {

    lateinit var listAdapter: WorkshopContractAdapter

    override val mViewModel: EmployerAgreementInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_contract_list
    }

    override fun setupObserver() {
    }

    override fun initView() {
        listAdapter = WorkshopContractAdapter()
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
        val workshopCode = arguments?.getString(mViewModel.ARG_WORKSHOP_ID)
        val branchCode = arguments?.getString(mViewModel.ARG_BRANCH_CODE)
        if (!workshopCode.isNullOrBlank() && !branchCode.isNullOrBlank()) {

            this@WorkshopContractListFragment.lifecycleScope.launchWhenCreated {
                mViewModel.getWorkshopContactList(workshopCode, branchCode)
                    .collectLatest { pagingData -> listAdapter.submitData(pagingData) }
            }
        }

    }

    override fun onClick() {

    }
}