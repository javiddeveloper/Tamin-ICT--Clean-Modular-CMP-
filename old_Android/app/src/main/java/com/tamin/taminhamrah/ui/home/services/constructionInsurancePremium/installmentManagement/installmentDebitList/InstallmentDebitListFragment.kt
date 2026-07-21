package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.installmentManagement.installmentDebitList

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentInstallmentDebitListBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapterPaging
import com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.ConstructionInsurancePremiumViewModel
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class InstallmentDebitListFragment :
    BaseFragment<FragmentInstallmentDebitListBinding, ConstructionInsurancePremiumViewModel>() {

    //region Variables
    override val mViewModel: ConstructionInsurancePremiumViewModel by viewModels()
    var branchId = ""
    var debitNumber = ""
    val listAdapter by lazy { KeyValueAdapterPaging() }
    //endregion

    //region Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_installment_debit_list

    override fun setupObserver() {
    }

    override fun initView() {
        viewDataBinding?.apply{
            setupRecycler(recyclerRequestInfo,listAdapter)
        }
    }

    override fun getData() {
        arguments?.apply {
            branchId = getString(Constants.BRANCH_ID) ?: ""
            debitNumber = getString(Constants.DEBIT_NUMBER) ?: ""
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mViewModel.getDetailDebitList(debitNumber = debitNumber, branchId = branchId)
                .collectLatest { pagingData ->
                    val result = pagingData.map {
                        it.getDetailInstallmentDebitList()
                    }
                    viewDataBinding?.apply {
                        if (parentLayout.visibility== View.GONE)
                            parentLayout.isVisible = true
                            listAdapter.submitData(result)
                    }

                }
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {
            imageBack.setOnClickListener { requireActivity().onBackPressed() }
        }
    }
    //endregion


}