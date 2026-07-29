package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit.objectionableDebit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.WorkshopInfoModel
import com.tamin.taminhamrah.databinding.FragmentWorkshopInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SpecialContactListFragment :
    BaseFragment<FragmentWorkshopInfoBinding, WorkshopInfoViewModel>(),
    AdapterInterface.OnItemClickListener<WorkshopInfoModel>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    lateinit var listAdapter: SpecialContractListAdapter

    override val mViewModel: WorkshopInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_info
    }

    override fun setupObserver() {

    }

    override fun initView() {
        listAdapter = SpecialContractListAdapter()
//        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    override fun getData() {
        getWorkshopId()?.let { workshopId ->
            this@SpecialContactListFragment.lifecycleScope.launchWhenCreated {
                mViewModel.getSpecialContactListFlow(workshopId).collectLatest { pagingData ->
//                    listAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun getWorkshopId(): String? {
        return arguments?.getString(Constants.WORKSHOP_ID)
    }

    override fun onClick() {
        viewDataBinding?.let {
            it.appBar.toolbar.apply {
                /*imgAction.setOnClickListener {
                    val dialog = WorkshopSearchDialogFragment()
                    dialog.setListener(this@SpecialContactListFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }*/
            }
        }
    }

    override fun onItemClick(item: WorkshopInfoModel, transitionView: View?, tag: String?) {

        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
        handlePageDestination(R.id.action_contracts_to_debit, setTitleInfo(item, tag))

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
        bundle.putString(Constants.WORKSHOP_ID, item.workshopId)
        bundle.putString(Constants.BRANCH_ID, item.branchCode)

        return bundle
    }

    override fun onDialogResult(item: Map<String, String>) {
        val workshopId = item[Constants.WORKSHOP_ID] ?: ""
        val branchCode = item[Constants.BRANCH_ID] ?: ""
//        mViewModel.getWorkshopList(workshopId = workshopId, branchCode = branchCode)
    }
}