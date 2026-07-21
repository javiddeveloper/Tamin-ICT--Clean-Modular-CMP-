package com.tamin.taminhamrah.ui.home.services.funeralAllowance

import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.CorrectedAccountNumberResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.DeceasedInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.RequestAllowanceFuneralResponse
import com.tamin.taminhamrah.databinding.FragmentFuneralAllowanceBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.disableButton
import com.tamin.taminhamrah.utils.extentions.enableButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FuneralAllowanceFragment :
    BaseFragment<FragmentFuneralAllowanceBinding, FuneralAllowanceViewModel>() {

    //Base variables
    override val mViewModel: FuneralAllowanceViewModel by viewModels()
    val userInfoAdapter by lazy { ExpandableListAdapter(expandingIndex = 3) }
    val deceasedInfoAdapter by lazy { KeyValueAdapter() }
    val registeredRequestAdapter by lazy { KeyValueAdapter() }

    //Base methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_funeral_allowance
    override fun setupObserver() {
        mViewModel.mldFuneralInfoResponse.observe(this, ::onFuneralInfoResponse)
        mViewModel.mldInquiryDeceasedInfo.observe(this, ::onDeceasedInfoResponse)
        mViewModel.mldSubmitAllowanceFuneral.observe(this, ::onRequestFuneralResponse)
        mViewModel.mldCorrectedAccountNumber.observe(this, ::onCorrectedAccountNumberResponse)
    }

    override fun getData() {
        mViewModel.getInfoFuneral()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnShowDetail.setOnClickListener {
                userInfoAdapter.toggleMinifyMode()
                btnShowDetail.text = if (userInfoAdapter.isMinifyMode())
                    getString(R.string.show_detail) else getString(R.string.hide_detail)
            }

            btnInquiryEligibility.setOnClickListener {
                if (edNationalCode.getValueNationalCode()
                        .isBlank() || edNationalCode.getLayout().isErrorEnabled
                ) {
                    edNationalCode.getLayout().error =
                        getString(R.string.error_not_valid_national_id)
                } else {
                    mViewModel.inquiryDeceasedInfo(edNationalCode.getValueNationalCode())
                }
            }

            btnSubmitRequest.setOnClickListener {
                if (!edNationalCode.getLayout().isErrorEnabled)
                    mViewModel.submitRequestFuneralAllowance(mViewModel.requestModel)
            }

            btnCorrectedAccountNumber.setOnClickListener {
                mViewModel.correctedAccountNumber(mViewModel.requestId.toString())
            }
        }
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null
            )
            setupUserInfoRecyclerView()
            setupDeceasedInfoRecyclerView()
            setupRegisterRequestRecyclerView()

            btnSubmitRequest.disableButton()
            edNationalCode.getInput().doAfterTextChanged {
                edNationalCode.getLayout().isErrorEnabled = false
                mViewModel.requestModel.deadNationalId = it.toString()
                btnSubmitRequest.disableButton()
            }
        }
    }

    //Base Listeners
    private fun onCorrectedAccountNumberResponse(result: CorrectedAccountNumberResponse) {
        if (result.isSuccess) {
            if (result.data != null) {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.SUCCESS,
                    result.data,
                    MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
            } else {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.WARNING,
                    getString(R.string.error_recive_data))
            }
        }
    }

    private fun onRequestFuneralResponse(result: RequestAllowanceFuneralResponse) {
        if (result.isSuccess) {
            if (result.data != null) {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.SUCCESS,
                    result.data,
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
            } else {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.WARNING,
                    getString(R.string.error_recive_data))
            }
        }
    }

    private fun onDeceasedInfoResponse(result: DeceasedInfoResponse) {
        if (result.isSuccess) {
            val list = result.data
            /*if list[6] == 1 => Confirmed deceased information inquiry*/
            if (list.size >= 8) {
                if (list[6] == "1") {
                    viewDataBinding?.recyclerDeceasedInfo?.visibility = View.VISIBLE

                    viewDataBinding?.apply {
                        btnSubmitRequest.enableButton()
                        btnSubmitRequest.visibility = View.VISIBLE
                    }
                    deceasedInfoAdapter.setItems(result.getDeceasedInfo())
                } else {
                    viewDataBinding?.apply {
                        recyclerDeceasedInfo.visibility = View.GONE
                        btnSubmitRequest.visibility = View.GONE
                    }
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.INFO,
                        list[7].ifEmpty { getString(R.string.not_eligible_funeral_allowance) })
                }
            } else {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.INFO,
                    getString(R.string.error_recive_data))
            }
        }
    }



private fun onFuneralInfoResponse(result: FuneralAllowanceResponse) {
    if (result.isSuccess) {
        viewDataBinding?.apply {
            parentLayout.visibility = View.VISIBLE
            userInfoAdapter.setItems(result.data?.getUserInfo() ?: emptyList())
            mViewModel.requestModel.shorttermRequest = result.data?.getRequestInfo()
            if (result.data?.flag == true) {
                layoutDeceasedInfo.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
                mViewModel.requestId = result.data.request?.id ?: 0
                itemDesc.descTxt.text = getText(R.string.error_bank_account_number_funeral_desc)
                registeredRequestAdapter.setItems(result.data.getRegisterRequestInfo())
            }
        }
    }
}

//Utils
private fun setupUserInfoRecyclerView() {
    viewDataBinding?.recyclerUserInfo?.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = userInfoAdapter
        if (itemDecorationCount == 0)
            addItemDecoration(UiUtils.createDivider(context))
    }
}

private fun setupDeceasedInfoRecyclerView() {
    viewDataBinding?.recyclerDeceasedInfo?.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = deceasedInfoAdapter
        if (itemDecorationCount == 0)
            addItemDecoration(UiUtils.createDivider(context))
    }
}

private fun setupRegisterRequestRecyclerView() {
    viewDataBinding?.recyclerRegisteredRequest?.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = registeredRequestAdapter
        if (itemDecorationCount == 0)
            addItemDecoration(UiUtils.createDivider(context))
    }
}

}