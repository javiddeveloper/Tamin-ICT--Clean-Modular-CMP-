package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.paymentSheet

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentWorkshopInfoBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WorkshopPaymentSheetsFragment :
    BaseFragment<FragmentWorkshopInfoBinding, WorkshopInfoViewModel>() {

    lateinit var listAdapter: WorkshopPaymentSheetAdapter
    override val mViewModel: WorkshopInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_info
    }

    override fun setupObserver() {
        // mViewModel.mldPaymentSheetList.observe(this, ::showResult)

    }

    override fun initView() {
        listAdapter = WorkshopPaymentSheetAdapter()
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    override fun onClick() {
        viewDataBinding?.appBar?.toolbar?.apply {
            imgAction.setOnClickListener {

                val dialog = WorkshopPaymentSheetSearchDialogFragment()
                val bundle = Bundle().apply {
                    putString(Constants.WORKSHOP_ID, getWorkshopId())
                    putString(Constants.BRANCH_ID, getBranchCode())
                }
                dialog.arguments = bundle
                dialog.setListener(object :
                    WorkshopPaymentSheetSearchDialogFragment.OnResultListener {
                    override fun onDialogResult(
                        debitCause: String?,
                        paymentType: String?,
                        payNumberFrom: String?,
                        payNumberTo: String?,
                        dateFrom: String?,
                        dateTo: String?
                    ) {
                        collectData(
                            debitCause,
                            paymentType,
                            payNumberFrom,
                            payNumberTo,
                            dateFrom,
                            dateTo
                        )
                    }
                })

                dialog.show(childFragmentManager, "uyt5uytuy")

            }
        }
    }

    override fun getData() {

        if (mViewModel.paymentSheetPager == null)
            collectData()
    }

    private fun collectData(
        debitCause: String? = "",
        paymentType: String? = "",
        payNumberFrom: String? = "",
        payNumberTo: String? = "",
        dateFrom: String? = "",
        dateTo: String? = ""
    ) {

        getWorkshopId()?.let { workshopId ->
            getBranchCode()?.let { branchCode ->
                this@WorkshopPaymentSheetsFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getWorkshopPaymentSheets(
                        workshopId,
                        branchCode,
                        debitCause,
                        paymentType,
                        payNumberFrom,
                        payNumberTo,
                        dateFrom,
                        dateTo
                    )
                        ?.collectLatest { pagingData ->
                            listAdapter.submitData(pagingData)
                        }
                }
            }
        }
    }

    private fun getWorkshopId(): String? {
        return arguments?.getString(Constants.WORKSHOP_ID)
    }

    private fun getBranchCode(): String? {
        return arguments?.getString(Constants.BRANCH_ID)
    }
}