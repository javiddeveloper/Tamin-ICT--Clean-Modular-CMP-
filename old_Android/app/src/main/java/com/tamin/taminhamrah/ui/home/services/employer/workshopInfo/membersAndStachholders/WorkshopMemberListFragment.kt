package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.membersAndStachholders

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.asDomainModel
import com.tamin.taminhamrah.databinding.FragmentWorkshopInfoBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WorkshopMemberListFragment :
    BaseFragment<FragmentWorkshopInfoBinding, WorkshopInfoViewModel>(),
    DialogResultInterface.OnResultListener<Map<String, String>> {

    lateinit var listAdapter: WorkshopMemberAdapter

    override val mViewModel: WorkshopInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_info
    }

    override fun setupObserver() {
    }

    private fun getWorkshopId(): String? {
        return arguments?.getString(Constants.WORKSHOP_ID)
    }

    private fun getBranchCode(): String? {
        return arguments?.getString(Constants.BRANCH_ID)
    }

    override fun initView() {
        listAdapter = WorkshopMemberAdapter()
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )

    }

    override fun getData() {
        collectData()
    }

    private fun collectData(nationalCode: String? = "", insuranceNumber: String? = "") {

        this@WorkshopMemberListFragment.lifecycleScope.launchWhenCreated {

            getWorkshopId()?.let { workshopId ->
                getBranchCode()?.let { branchCode ->
                    mViewModel.getWorkshopMemberFlow(
                        workshopId,
                        branchCode,
                        insuranceNumber,
                        nationalCode
                        ).collectLatest { pagingData ->
                        val result = pagingData.map { it.asDomainModel() }
                        listAdapter.submitData(result)
                    }
                }
            }
        }
    }

    override fun onClick() {

        viewDataBinding?.let {
            it.appBar.toolbar.apply {
                imgAction.setOnClickListener {

                    val dialog = WorkshopMemberSearchDialogFragment()
                    dialog.setListener(this@WorkshopMemberListFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }
            }
        }
    }

    override fun onDialogResult(item: Map<String, String>) {
        val nationalCode = item[mViewModel.ARG_NATIONAL_CODE] ?: ""
        val insuranceNumber = item[mViewModel.ARG_INSURANCE_NUMBER] ?: ""

        collectData(nationalCode, insuranceNumber)
    }


}