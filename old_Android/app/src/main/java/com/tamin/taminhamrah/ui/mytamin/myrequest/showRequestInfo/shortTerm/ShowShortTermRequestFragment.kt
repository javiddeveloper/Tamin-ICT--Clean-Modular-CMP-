package com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.shortTerm

import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.Article16RequestInfoModel
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.RequestStatusModel
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.RequestStatusResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.ShortTermRequestInfoModel
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.ShortTermRequestInfoResponse
import com.tamin.taminhamrah.databinding.ShortTermActionStepBinding
import com.tamin.taminhamrah.databinding.ShortTermDetailRequestStepBinding
import com.tamin.taminhamrah.databinding.ShortTermDocumentStepBinding
import com.tamin.taminhamrah.databinding.ShortTermUserInfoBinding
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.ShowRequestBaseFragment
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.shortTerm.adapter.ActionsBranchAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowShortTermRequestFragment :
    ShowRequestBaseFragment(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {
   
    //Variables
    private val requestInfo by lazy { ArrayList<ShortTermRequestInfoModel>() }
    private val requestInfoAdapterList by lazy { KeyValueAdapter() }
    private val userInfoAdapterList by lazy { KeyValueAdapter() }
    private val actionInfoAdapterList by lazy { ActionsBranchAdapter() }

    //Base Methode
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getData() {
        super.getData()
            if (referenceId.isNotBlank() && requestType != 0) {
                mViewModel.getRequestInfo(referenceId = referenceId,requestType)
        }
    }

    //Listeners
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.layoutStepper?.nextStep()
        if (stepIndex == 2 && documentFiles.isEmpty()) {
            if (requestInfo.isNotEmpty()) {
                val list = ArrayList<Article16RequestInfoModel.ObjectionPhoto>()
                requestInfo[0].getDocumentList(requestType).forEach { document ->
                    document?.documentFile?.let {
                      list.add(Article16RequestInfoModel.ObjectionPhoto(guid =document.documentFile, type = document.documentType?:""))
                    } ?: showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_recive_data)
                    )
                }
                mViewModel.downloadDocument(list)
            }
        }
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.layoutStepper?.previousStep()
    }
    
    //Utils
    private fun initStepper() {
        viewDataBinding?.layoutStepper?.apply {
            initial(
                listOf(
                    detailOfRequestStep(),
                    detailOfUserStep(),
                    uploadDocumentStep(),
                    actionsInfoStep()
                )
            )
            onNextStepClickListener = this@ShowShortTermRequestFragment
            onPreviousStepClickListener = this@ShowShortTermRequestFragment
        }
    }
    
    //step1
    private fun detailOfRequestStep() = ShortTermDetailRequestStepBinding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.layoutStepper, true
    ).apply {
        initialRecyclerView(rcvInfo, requestInfoAdapterList)
        if (requestInfo.isNotEmpty()) {
            requestInfoAdapterList.setItems(requestInfo[0].getRequestInfo(requestType = requestType))
        } else
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_recive_data),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
    }

    //step2
    private fun detailOfUserStep() =
        ShortTermUserInfoBinding.inflate(
            LayoutInflater.from(requireContext()),
            viewDataBinding?.layoutStepper,
            true
        ).apply {
            initialRecyclerView(rcvInfo, userInfoAdapterList)
            if (requestInfo.isNotEmpty())
                userInfoAdapterList.setItems(requestInfo[0].getUserInfo(requestType))
        }

    //step3
    private fun uploadDocumentStep() = ShortTermDocumentStepBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.layoutStepper, true
    ).apply {
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = documentAdapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }
    }

    //step4
    private fun actionsInfoStep() = ShortTermActionStepBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.layoutStepper, true
    ).apply {
        if (requestInfo.isNotEmpty()) {
            tvDescAction.descTxt.text = requestInfo.last().getDescAction(requestType)
        }
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = actionInfoAdapterList
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
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

    override fun setupObserver() {
        super.setupObserver()
        mViewModel.mldRequestShortTermStatus.observe(this, ::onRequestShortTermStatusResponse)
        mViewModel.mldRequestShortTermInfo.observe(this, ::onRequestInfoShortTermResponse)
    }

    //Listeners
    private fun onRequestShortTermStatusResponse(result: RequestStatusResponse) {
        if (result.isSuccess) {
            val list = ArrayList<RequestStatusModel>()
            list.addAll(result.data?.list ?: emptyList())
            if (list.isNotEmpty() && list.last().rejectReason?.isNotBlank() == true) {
                viewDataBinding?.appBar?.tvSubTitle?.text = getString(
                    R.string.reason_rejection_request,
                    list.last().rejectReason
                )
            }
                actionInfoAdapterList.setItems(list)

        }
    }

    private fun onRequestInfoShortTermResponse(result: ShortTermRequestInfoResponse) {
        if (result.isSuccess) {
            requestInfo.addAll(result.data?.list ?: emptyList())
            initStepper()
        }
    }

}