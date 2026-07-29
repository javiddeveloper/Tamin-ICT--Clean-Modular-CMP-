package com.tamin.taminhamrah.ui.home.services.construction

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPayDebitResponse
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPaymentInfo
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.databinding.ConstructionWorkersInfoUiBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.construction.adapter.WorkersPaymentInfoAdapter
import com.tamin.taminhamrah.utils.EventObserver
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class WorkersPaymentInfoFragment :
    BaseFragment<ConstructionWorkersInfoUiBinding, WorkersPaymentInfoViewModel>() {

    override val mViewModel: WorkersPaymentInfoViewModel by viewModels()
    @Inject
    lateinit var webLauncher: CustomTabsLauncher
    private val listAdapter by lazy { WorkersPaymentInfoAdapter { doPaymentAction(it) } }

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.construction_workers_info_ui

    override fun setupObserver() {
        mViewModel.mldPayment.observe(this, EventObserver { showPaymentResult(it) })
        mViewModel.mldPaymentPreview.observe(this, EventObserver { showPaymentPreviewResult(it) })
    }

    override fun initView() {
        webLauncher.registerLifecycle(viewLifecycleOwner)
        viewDataBinding?.apply {
            setupRecycler(recycler, listAdapter)
            appBar.toolbar.imgInfo.setOnClickListener {
                mViewModel.saveBoolean(Constants.TapTargetHistoryFragment, false)
            }
            setupToolbar(
                viewDataBinding?.appBar,
                viewDataBinding?.appbarBackgroundImage?.imageBackground,
                moreViews = null
            )
        }
    }

    override fun getData() {
        lifecycleScope.launchWhenCreated {
            mViewModel.mldConstructionWorkersInfo.collectLatest {
                listAdapter.submitData(it)
            }
        }
    }

    override fun onClick() {}

    private fun doPaymentAction(info: WorkersPaymentInfo) = mViewModel.doPayment(info)
    private fun showPaymentPreviewResult(result: PaymentInfoResponse) {
        if (result.isSuccess) {
            mViewModel.saveSystemType(mViewModel.getPayType())
            webLauncher.launchUrl(Constants.TFH_PAYMENT_VIEW_PAGE+result.data?.ticket, launchInBrowser = true) { intent ->
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
//            requireActivity().onBackPressedDispatcher.onBackPressed()


//            val bundle = Bundle()
//            bundle.putParcelable(PaymentFragment.ARG_PAYMENT_INFO, result.data)
//            bundle.putString(PaymentFragment.SYSTEM_TYPE, mViewModel.getPayType())
//            handlePageDestination(R.id.action_workersPaymentInfo_to_Payment, bundle)
        }
    }
    private fun showPaymentResult(result: WorkersPayDebitResponse) {
        if (result.isSuccess) {
            if (result.data == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_failed_request)
                )
            } else {
                Timber.e("Worker PayDeBit Result: %s", result.data)
                mViewModel.normalPaymentPreview()
            }
        }
    }
}