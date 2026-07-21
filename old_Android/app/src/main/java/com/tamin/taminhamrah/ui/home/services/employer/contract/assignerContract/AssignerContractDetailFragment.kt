package com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract

import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfo
import com.tamin.taminhamrah.databinding.FragmentAssignerContractDetailBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.home.services.employer.contract.ContractInfoViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssignerContractDetailFragment :
    BaseFragment<FragmentAssignerContractDetailBinding, ContractInfoViewModel>() {

    companion object{
        const val ARG_SELECTED_ITEM="ARG_SELECTED_ITEM"
    }

    lateinit var contractDetailAdapter: KeyValueAdapter
    lateinit var assignerInfoAdapter: KeyValueAdapter
    lateinit var contractorInfoAdapter: KeyValueAdapter

    override val mViewModel: ContractInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_assigner_contract_detail
    }

    override fun setupObserver() {

    }

    override fun initView() {

        contractDetailAdapter = KeyValueAdapter()
        viewDataBinding?.recyclerContractDetail?.apply {
            this.adapter = contractDetailAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(requireContext()))
            }
        }
        getItem()?.createKeyValueContractInfo(getItem())?.let { contractDetailAdapter.setItems(it) }

        assignerInfoAdapter = KeyValueAdapter()
        viewDataBinding?.recyclerAssignerInfo?.apply {
            this.adapter = assignerInfoAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(requireContext()))
            }
        }
        getItem()?.createKeyValueAssignerDetail(getItem())?.let { assignerInfoAdapter.setItems(it) }

        contractorInfoAdapter = KeyValueAdapter()
        viewDataBinding?.recyclerContractorInfo?.apply {
            this.adapter = contractorInfoAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(requireContext()))
            }
        }
        getItem()?.createKeyValueContractor(getItem())?.let { contractorInfoAdapter.setItems(it) }

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {
    }


    override fun onClick() {
    }

    private fun getItem(): ContractInfo? {
        return arguments?.getParcelable(ARG_SELECTED_ITEM) as? ContractInfo
    }


}