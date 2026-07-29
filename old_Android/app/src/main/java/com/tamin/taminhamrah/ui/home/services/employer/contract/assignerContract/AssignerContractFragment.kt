package com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfo
import com.tamin.taminhamrah.databinding.FragmentContractInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.contract.ContractInfoViewModel
import com.tamin.taminhamrah.ui.home.services.employer.contract.ContractSearchDialogFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AssignerContractFragment :
    BaseFragment<FragmentContractInfoBinding, ContractInfoViewModel>(),
    AdapterInterface.OnItemClickListener<ContractInfo>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    lateinit var listAdapter: AssignerContractInfoAdapter

    override val mViewModel: ContractInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_contract_info
    }

    override fun setupObserver() {

    }

    override fun initView() {
        listAdapter = AssignerContractInfoAdapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    override fun getData() {
        fetchData()
    }

    private fun fetchData(
        workshopId: String? = "",
        branchCode: String? = "",
        peymanRow: String? = ""
    ) {
        this@AssignerContractFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getAssignerContractListFlow(workshopId, branchCode, peymanRow)
                .collectLatest { pagingData ->
                    listAdapter.submitData(pagingData)
                }
        }
    }

    override fun onClick() {
        viewDataBinding?.let {
            it.appBar.toolbar.apply {
                imgAction.setOnClickListener {
                    val dialog = ContractSearchDialogFragment(showPaymanRow = true)
                    dialog.setListener(this@AssignerContractFragment)
                    dialog.show(childFragmentManager, "ContractSearchDialogFragment")
                }
            }
        }
    }

    override fun onItemClick(item: ContractInfo, transitionView: View?, tag: String?) {

        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

        val dialog = MenuDialogFragment.newInstance()
        dialog.setMenuListener(object : MenuInterface.OnFetchData {

            override fun onFetch() {
                this@AssignerContractFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getAssignerActionListFlow()
                        .collectLatest { pagingData ->
                            dialog.updateData(pagingData)
                        }
                }
            }

        }, object : MenuInterface.OnResult {

            override fun onResult(menu: MenuModel) {
                when (menu.id) {
                     "0"-> {
                        val bundle = Bundle()
                        bundle.putString(Constants.TOOLBAR_TITLE, menu.title)
                        bundle.putParcelable(AssignerContractDetailFragment.ARG_SELECTED_ITEM, item)
                        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
                        handlePageDestination(R.id.action_assigner_contracts_to_detail, bundle)
                    }
                    "1"-> {
                        val bundle = Bundle()
                        bundle.putString(Constants.TOOLBAR_TITLE, menu.title)
                        bundle.putParcelable(AssignerContractDetailFragment.ARG_SELECTED_ITEM, item)

                        bundle.putString(mViewModel.ARG_WORKSHOP_ID,item.employer?.workshopId)
                        bundle.putString(mViewModel.ARG_BRANCH_CODE,item.employer?.branch?.code)
                        bundle.putString(mViewModel.ARG_CONTRACT_ROW,item.contractRow)
                        bundle.putString(mViewModel.ARG_CONTRACT_SEQUENCE,item.contractSequence)

                        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
                        handlePageDestination(R.id.action_contracts_to_computationalBase, bundle)



                    }
                    "2" -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.TOOLBAR_TITLE, menu.title)
                        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
                        bundle.putParcelable(mViewModel.ARG_CONTRACT, item)

                        handlePageDestination(R.id.action_assigner_to_mafasaHesabRegisterFragment, bundle)
                    }
                }
            }
        })
        dialog.show(childFragmentManager, "p'opo'[p")

    }

    private fun setTitleInfo(item: ContractInfo, menuTitle: String?): Bundle {

        val bundle = Bundle()
        bundle.putString(
            Constants.TOOLBAR_TITLE, menuTitle
        )
        bundle.putString(
            Constants.TOOLBAR_SUBTITLE,
            "${getString(R.string.label_workshop_name)} : ${item.employer?.workshopName}"
        )

        bundle.putString(
            Constants.TOOLBAR_SUB_SUBTITLE, "${getString(R.string.label_workshop_code)} : ${item.employer?.workshopId} | ${getString(R.string.label_peyman_row)} : ${item.contractRow} "
        )

        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
        bundle.putString(mViewModel.ARG_WORKSHOP_ID, item.employer?.workshopId)
        bundle.putString(mViewModel.ARG_BRANCH_CODE, item.employer?.branch?.code)
        bundle.putString(mViewModel.ARG_CONTRACT_ROW, item.contractRow)
        bundle.putString(mViewModel.ARG_CONTRACT_SEQUENCE, item.contractSequence)

        return bundle
    }

    override fun onDialogResult(item: Map<String, String>) {
        val workshopId = item[mViewModel.ARG_WORKSHOP_ID] ?: ""
        val branchCode = item[mViewModel.ARG_BRANCH_CODE] ?: ""
        val peymanRow = item[mViewModel.ARG_PEYMAN_ROW] ?: ""
        fetchData(workshopId, branchCode, peymanRow)

    }
}