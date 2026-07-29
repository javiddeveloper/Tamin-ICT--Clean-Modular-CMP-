package com.tamin.taminhamrah.ui.home.services.employer.contract

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfoNew
import com.tamin.taminhamrah.databinding.FragmentContractInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.contract.clause38.Clause38Fragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class ContractInfoFragment :
    BaseFragment<FragmentContractInfoBinding, ContractInfoViewModel>(),
    AdapterInterface.OnItemClickListener<ContractInfoNew>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    lateinit var listAdapter: ContractInfoAdapter

    override val mViewModel: ContractInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_contract_info
    }

    override fun setupObserver() {
     /*   mViewModel.mldWorkshopList.observe(this, {
            it.peekContent()?.let { it1 ->
                showResult(it1)
            }
        })
*/
    }

    override fun initView() {
      Timber.tag("FragmentInitTest").e("CALLED Init view")
        listAdapter = ContractInfoAdapter(this)
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

    private fun fetchData(workshopId: String?="", branchCode: String?="", peymanRow: String?="") {
        this@ContractInfoFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getContractListFlow(workshopId, branchCode,peymanRow)
                ?.collectLatest { pagingData ->
                    listAdapter.submitData(pagingData)
                }
        }
    }

    override fun onClick() {
        viewDataBinding?.let {
            it.appBar.toolbar.apply {
                imgAction.setOnClickListener {
                    val dialog = ContractSearchDialogFragment()
                    dialog.setListener(this@ContractInfoFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }
            }
        }
    }

    override fun onItemClick(item: ContractInfoNew, transitionView: View?, tag: String?) {

//        val bundle = Bundle()
//        bundle.putString(Constants.TOOLBAR_TITLE, tag)
//        handlePageDestination(R.id.action_contracts_to_computationalBase, setTitleInfo(item, tag))

        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

        val dialog = MenuDialogFragment.newInstance()
        dialog.setMenuListener(object : MenuInterface.OnFetchData {

            override fun onFetch() {
                this@ContractInfoFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getContractActionListFlow()
                        .collectLatest { pagingData ->
                            dialog.updateData(pagingData)
                        }
                }
            }

        }, object : MenuInterface.OnResult {

            override fun onResult(menu: MenuModel) {
                when (menu.id) {

                    "0" -> {
                        handlePageDestination(R.id.action_contracts_to_computationalBase, setTitleInfo(item, tag))
                    }

                    "1" -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.TOOLBAR_TITLE, menu.title)
                        bundle.putString(Constants.TOOLBAR_SUBTITLE, "کدگارگاه : ${item.workshop?.workshopId}")
                        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
                        bundle.putParcelable(Clause38Fragment.ARG_CONTRACT_ITEM, item)

                        handlePageDestination(R.id.action_contracts_to_clause38, bundle)
                    }
                }
            }
        })
        dialog.show(childFragmentManager, "p'opo'[p")
    }

    private fun setTitleInfo(item: ContractInfoNew, menuTitle: String?): Bundle {

        val bundle = Bundle()
        bundle.putString(
            Constants.TOOLBAR_TITLE, menuTitle
        )
        bundle.putString(
            Constants.TOOLBAR_SUBTITLE,
            "${getString(R.string.label_workshop_code)} : ${item.workshop?.workshopId} "
        )

        bundle.putString(
            Constants.TOOLBAR_SUB_SUBTITLE,
            "${getString(R.string.label_peyman_row)} : ${item.contractRow} "
        )

        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
        bundle.putString(mViewModel.ARG_WORKSHOP_ID, item.workshop?.workshopId)
        bundle.putString(mViewModel.ARG_BRANCH_CODE, item.workshop?.branchCode)
        bundle.putString(mViewModel.ARG_CONTRACT_ROW, item.contractRow)
        bundle.putString(mViewModel.ARG_CONTRACT_SEQUENCE, item.contractSequence)
        return bundle
    }

    override fun onDialogResult(item: Map<String, String>) {
       val workshopId = item[Constants.WORKSHOP_ID] ?: ""
        val branchCode = item[Constants.BRANCH_ID] ?: ""
        val peymanRow = item[Constants.PEYMAN_ROW] ?: ""
        fetchData(workshopId,branchCode,peymanRow )

    }
}