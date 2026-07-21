package com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.deferredInstallmentCertificate

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.DeferredInstallmentInfoResponse
import com.tamin.taminhamrah.databinding.DeferredInstallmentBorrowerInfoStepBinding
import com.tamin.taminhamrah.databinding.DeferredInstallmentLoanDetailsInfoStepBinding
import com.tamin.taminhamrah.databinding.DeferredInstallmentPensionerInfoStepBinding
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.ShowRequestBaseFragment
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.ShowRequestInfoViewModel
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeferredInstallmentRequestInfoFragment : ShowRequestBaseFragment(),StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    //Variables
    private var requestInfo: DeferredInstallmentInfoResponse?=null
    private val pensionerInfoAdapterList by lazy { KeyValueAdapter() }
    private val borrowerInfoAdapterList by lazy { KeyValueAdapter() }
    private val loanInfoAdapterList by lazy { KeyValueAdapter()}
    override val mViewModel: ShowRequestInfoViewModel by viewModels()

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel,mViewModel)


    override fun setupObserver() {
        super.setupObserver()
        mViewModel.mldDeferredInstallmentInfo.observe(this,::onDeferredInstallmentInfoResponse)
    }

    override fun getData() {
        super.getData()
        if (referenceId.isNotBlank()) {
            mViewModel.getDeferredInstallmentInfo(referenceId)
        } else {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_recive_data)
            )
        }
    }
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.layoutStepper?.nextStep()
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.layoutStepper?.previousStep()
    }

    //Listeners
    private fun onDeferredInstallmentInfoResponse(response: DeferredInstallmentInfoResponse) {
        if (response.isSuccess){
            requestInfo = response
            initStepper()
        }
    }

    //Utils
    private fun initStepper() {
        viewDataBinding?.layoutStepper?.apply {
            initial(listOf(pensionerInfoStepper(),borrowerInfoStepper(),loanInfoStepper()))
            onNextStepClickListener = this@DeferredInstallmentRequestInfoFragment
            onPreviousStepClickListener = this@DeferredInstallmentRequestInfoFragment
        }
    }


    //step1
    private fun pensionerInfoStepper() = DeferredInstallmentPensionerInfoStepBinding.inflate(
        LayoutInflater.from(requireContext()),viewDataBinding?.layoutStepper,true).apply {
        initialRecyclerView(rcvInfo, pensionerInfoAdapterList)
        requestInfo?.data?.getPensionerInfo()?.let {
            pensionerInfoAdapterList.setItems(it)
        }
    }

    //step2
    private fun borrowerInfoStepper()= DeferredInstallmentBorrowerInfoStepBinding.inflate(
        LayoutInflater.from(requireContext()),viewDataBinding?.layoutStepper,true
    ).apply {
        initialRecyclerView(rcvInfo, borrowerInfoAdapterList)
        requestInfo?.data?.getBorrowerInfo()?.let {
            borrowerInfoAdapterList.setItems(it)
        }
    }

    //step3
    private fun loanInfoStepper() =  DeferredInstallmentLoanDetailsInfoStepBinding.inflate(
        LayoutInflater.from(requireContext()),viewDataBinding?.layoutStepper,true).apply {
            initialRecyclerView(rcvInfo,loanInfoAdapterList)
        requestInfo?.data?.getLoanInfo()?.let {
            loanInfoAdapterList.setItems(it)
        }
    }

    private fun initialRecyclerView(recycler: RecyclerView, adapterList: KeyValueAdapter) {
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterList
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }
    }

}