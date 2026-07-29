package com.tamin.taminhamrah.ui.home.services.employer.contract.computationalBase

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.WorkshopInfoModel
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBase
import com.tamin.taminhamrah.databinding.FragmentContractInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.employer.contract.ContractInfoViewModel
import com.tamin.taminhamrah.ui.home.services.employer.contract.ContractSearchDialogFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ComputationalBaseFragment :
    BaseFragment<FragmentContractInfoBinding, ContractInfoViewModel>(),
    AdapterInterface.OnItemClickListener<ComputationalBase>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    lateinit var listAdapter: ComputationalBaseInfoAdapter

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
        listAdapter = ComputationalBaseInfoAdapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    override fun getData() {
        fetchData(arguments?.getString(mViewModel.ARG_WORKSHOP_ID),
            arguments?.getString(mViewModel.ARG_BRANCH_CODE),
            arguments?.getString(mViewModel.ARG_CONTRACT_ROW),
            arguments?.getString(mViewModel.ARG_CONTRACT_SEQUENCE))
    }

    private fun fetchData(
        workshopId: String? = "",
        branchCode: String? = "",
        contractRow: String? = "",
        contractSequence: String? = ""
    ) {
        this@ComputationalBaseFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getComputationalBaseListFlow(workshopId, branchCode, contractRow, contractSequence)
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
                    dialog.setListener(this@ComputationalBaseFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }
            }
        }
    }


    override fun onItemClick(item: ComputationalBase, transitionView: View?, tag: String?) {

        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, tag)

        bundle.putString(
            Constants.TOOLBAR_SUBTITLE,
            "${getString(R.string.label_workshop_code)} : ${item.contract?.workshop?.workshopId} "
        )

        bundle.putString(
            Constants.TOOLBAR_SUB_SUBTITLE,
            "${getString(R.string.label_peyman_row)} : ${item.contract?.contractRow} "
        )

        bundle.putParcelable(ComputationalBaseDetailFragment.ARG_SELECTED_ITEM, item)
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
        handlePageDestination(R.id.action_computational_base_to_detail, bundle)

        /*    view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
         //   val itemList = mViewModel.getActionsList()

            val dialog = MenuDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(MenuDialogFragment.ARG_MENU_ITEMS, itemList)
            bundle.putBoolean(MenuDialogFragment.SEARCH_VIEW_VISIBILITY, false)
            dialog.arguments = bundle
            dialog.setListener(object : MenuInterface.OnResult {
                override fun onResult(it: MenuModel) {
                    when (it.title) {
                        itemList[0].title -> {
                            handlePageDestination(
                                R.id.action_workshopInfo_to_debt_inquiry,
                                setTitleInfo(item, it.title)
                            )
                        }
                        itemList[1].title -> {
                            handlePageDestination(
                                R.id.action_workshopInfo_to_contract_list,
                                setTitleInfo(item, it.title)
                            )
                        }
                        itemList[2].title -> {
                            handlePageDestination(
                                R.id.action_workshopInfo_to_objectionable_debit,
                                setTitleInfo(item, it.title)
                            )
                        }

                        itemList[3].title -> {
                            handlePageDestination(
                                R.id.action_workshopInfo_to_details,
                                setTitleInfo(item, it.title)
                            )
                        }
                        itemList[4].title -> {
                            handlePageDestination(
                                R.id.action_workshopInfo_to_payment_sheet,
                                setTitleInfo(item, it.title)
                            )
                        }

                    }
                }
            })
            dialog.setStopListenr(object : AdapterInterface.OnStopDialogListener {
                override fun onStop() {

                }
            })

            dialog.show(childFragmentManager, "هعغهعغه")
    */
    }

    private fun setTitleInfo(item: WorkshopInfoModel, menuTitle: String?): Bundle {

        val bundle = Bundle()
        bundle.putString(
            Constants.TOOLBAR_TITLE, menuTitle
        )
        bundle.putString(
            Constants.TOOLBAR_SUBTITLE,
            "${getString(R.string.label_workshop_name)} : ${item.workshopName} "
        )
        bundle.putString(
            Constants.TOOLBAR_SUB_SUBTITLE,
            "${getString(R.string.label_workshop_code)} : ${item.workshopId} "
        )
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))
        //   bundle.putString(mViewModel.ARG_WORKSHOP_ID, item.workshopId)
        //   bundle.putString(mViewModel.ARG_BRANCH_CODE, item.branchCode)

        return bundle
    }

    override fun onDialogResult(item: Map<String, String>) {
        val workshopId = item[mViewModel.ARG_WORKSHOP_ID] ?: ""
        val branchCode = item[mViewModel.ARG_BRANCH_CODE] ?: ""
        val peymanRow = item[mViewModel.ARG_PEYMAN_ROW] ?: ""
        fetchData(workshopId, branchCode, peymanRow)

    }
}