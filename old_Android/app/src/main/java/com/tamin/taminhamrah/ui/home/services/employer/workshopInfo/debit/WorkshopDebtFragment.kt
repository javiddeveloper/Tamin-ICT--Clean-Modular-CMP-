package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.PaymentModel
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt
import com.tamin.taminhamrah.databinding.FragmentWorkshopInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopSearchDialogFragment
import com.tamin.taminhamrah.utils.EventObserver
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WorkshopDebtFragment :
    BaseFragment<FragmentWorkshopInfoBinding, WorkshopInfoViewModel>(),
    AdapterInterface.OnItemClickListener<WorkShopDebt>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    lateinit var listAdapter: WorkshopDeptAdapter

    @Inject
    lateinit var webLauncher: CustomTabsLauncher

    override val mViewModel: WorkshopInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_info
    }

    override fun setupObserver() {
        mViewModel.mldPaymentPreCheck.observe(this,EventObserver {showPaymentPreCheckResult(it)})
        mViewModel.mldPayment.observe(this, EventObserver {showPaymentResult(it)})
        mViewModel.mldPaymentPreview.observe(this, EventObserver {showPaymentPreviewResult(it)})
    }

    override fun initView() {
        webLauncher.registerLifecycle(viewLifecycleOwner)
        listAdapter = WorkshopDeptAdapter(this)
        setupRecycler(viewDataBinding?.recycler, listAdapter)

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {

        if (mViewModel.workshopDebtPager == null)
            this@WorkshopDebtFragment.lifecycleScope.launchWhenCreated {
                getWorkshopId()?.let { workshopId ->
                    getBranchCode()?.let { branchCode ->
                        mViewModel.getWorkshopDebtListFlow(
                            workshopId,
                            branchCode
                        )?.collectLatest { pagingData ->
                            listAdapter.submitData(pagingData)
                        }
                    }
                }
            }
    }

    override fun onClick() {
        viewDataBinding?.let {
            it.appBar.toolbar.apply {
                imgAction.setOnClickListener {
                    val dialog = WorkshopSearchDialogFragment()
                    dialog.setListener(this@WorkshopDebtFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
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

    private fun showPaymentPreCheckResult(result: PaymentModel) {
        result.apply {
            mViewModel.normalDebitPayment(
                branchCode,
                workshopId,
                debitNumber,
                peymanSequence,
                seporde
            )
        }
    }

    private fun showPaymentResult(result: PaymentResponse) {

        if (result.isSuccess) {
            if (result.data?.succeed == true)
                mViewModel.normalDebitPaymentPreview()
            else {
                if (!result.data?.responseMessage.isNullOrBlank()) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        result.data?.responseMessage ?: ""
                    )
                }
            }
        }
    }


    private fun showPaymentPreviewResult(result: PaymentInfoResponse) {
        if (result.isSuccess) {
            mViewModel.saveSystemType(Constants.PAYMENT_TYPE_WS_DEPT)
            webLauncher.launchUrl(Constants.TFH_PAYMENT_VIEW_PAGE+result.data?.ticket, launchInBrowser = true,) { intent ->
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
            //todo : check it before new version,,it is not work!!!

          /*  val bundle = Bundle()
            bundle.putParcelable(PaymentFragment.ARG_PAYMENT_INFO, result.data)
            handlePageDestination(R.id.action_workshopInfo_debt_to_Payment, bundle)*/
        }
    }

    override fun onItemClick(item: WorkShopDebt, transitionView: View?, tag: String?) {

        when (tag) {
            getString(R.string.label_show_demand_documents) -> {
                val bundle = Bundle()
                bundle.putString(
                    Constants.TOOLBAR_TITLE,
                    getString(R.string.label_show_demand_documents)
                )
                bundle.putString(Constants.TOOLBAR_SUBTITLE, Utility.getToolbarSubTitle(arguments))
                bundle.putString(Constants.TOOLBAR_SUB_SUBTITLE, "شماره بدهی : ${item.debitNumber}")
                bundle.putString(
                    Constants.TOOLBAR_ICON_IMAGE,
                    Utility.getToolbarIconImage(arguments)
                )
                bundle.putString(mViewModel.ARG_DEBT_NUMBER, item.debitNumber)
                bundle.putString(
                    Constants.BRANCH_ID,
                    arguments?.getString(Constants.BRANCH_ID)
                )

                handlePageDestination(R.id.action_workshopInfo_debt_to_documents, bundle)

            }

            getString(R.string.label_debit_payment) -> {
                item.debitNumber?.let { debitNumber ->
                    getBranchCode()?.let { branchCode ->
                        mViewModel.checkPaymentStatus(
                            branchCode = branchCode,
                            workshopId = getWorkshopId() ?: "0",
                            debitNumber = debitNumber,
                            peymanSequence = item.peymanSequence ?: "",
                            seporde = false,
                            debitRemain = item.debitRemain ?: 0,
                            debitCreateReason = /*item.debitcrtreasondesc ?:*/ "0"
                        )
                    }
                }
            }
        }
    }

    override fun onDialogResult(item: Map<String, String>) {
        val workshopId = item[Constants.WORKSHOP_ID] ?: ""
        val branchCode = item[Constants.BRANCH_ID] ?: ""
//        mViewModel.getWorkshopList(workshopId = workshopId, branchCode = branchCode)
    }


}