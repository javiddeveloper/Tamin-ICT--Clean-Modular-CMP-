package com.tamin.taminhamrah.ui.home.services.studentContract.cancelContract

import android.content.res.ColorStateList
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.google.android.material.color.MaterialColors
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.STATUS_CANCEL_CONTRACT
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract.CancelContractRequest
import com.tamin.taminhamrah.databinding.FragmentCancelContractBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.contracts.ContractViewModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.extentions.createBundle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CancelContractFragment :
    BaseFragment<FragmentCancelContractBinding, ContractViewModel>() {

    override val mViewModel: ContractViewModel by viewModels()

    private var selectedReasonCancelContract = ""

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_cancel_contract
    }

    override fun setupObserver() {
        mViewModel.mldCheckContractCondition.observe(
            this,
            ::showResultCheckAgeAndHistory
        )
        mViewModel.mldRequestCancelContract.observe(this, ::showResultRequestCancelContract)
        mViewModel.checkContractCondition(contractType == "02")
    }

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            moreViews = null
        )
    }

    val contractType by lazy {
        arguments?.getString(Constants.CONTRACT_SYSTEM_TYPE) ?: "01"
    }

    override fun getData() {
        mViewModel.checkContractCondition(contractType == "02")
    }

    override fun onClick() {
        viewDataBinding?.apply {
            selectReasonCancelContract.getIt().setOnClickListener {
                selectReasonCancelContract.getLayout().isErrorEnabled = false
                showMenuDialog(
                    getString(R.string.select_reason_cancel_contract),
                    object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            isCancelContractActive = true
                            btnCancelContract.backgroundTintList = ColorStateList.valueOf(
                                MaterialColors.getColor(
                                    btnCancelContract,
                                    androidx.appcompat.R.attr.colorPrimary
                                )
                            )

                            selectedReasonCancelContract = itemResult.id ?: "0"
                            selectReasonCancelContract.setValue(itemResult.title ?: "")
                        }
                    })
            }


            btnCancelContract.setOnClickListener {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    getString(R.string.are_you_sure_cancel_contract), true
                )
                dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        mViewModel.requestCancelContract(
                            id = selectedReasonCancelContract,
                            body = CancelContractRequest(
                                inputDescription.getValue(false),
                                STATUS_CANCEL_CONTRACT
                            ),
                            isOptional = contractType == "02"
                        )
                    }

                    override fun onCancelClick() {
                    }
                })
                dialog.show(childFragmentManager, CancelContractFragment().javaClass.simpleName)
            }
        }
    }

    private fun showMenuDialog(
        title: String,
        onResult: MenuInterface.OnResult
    ) {
        val dialog = MenuDialogFragment.newInstance(true, title)
        dialog.setMenuListener(object : MenuInterface.OnFetchData {
            override fun onFetch() {
                this@CancelContractFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getSelfContractStateList()
                        .collectLatest { pagingData ->
                            val result = pagingData.map {
                                MenuModel(
                                    id = it.selfIsuContStatCode.toString(),
                                    title = it.selfIsuContStatDesc,
                                )
                            }
                            dialog.updateData(result)
                        }
                }
            }

        }, object : MenuInterface.OnResult {
            override fun onResult(itemResult: MenuModel) {
                onResult.onResult(itemResult)
            }
        })
        dialog.show(childFragmentManager, CancelContractFragment().javaClass.simpleName)
    }

    private fun showResultRequestCancelContract(result: GeneralRes) {
        if (result.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.your_contract_is_canceled)
            )
            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    requireActivity().onBackPressed()
                }

                override fun onCancelClick() {
                }
            })
            dialog.show(childFragmentManager, CancelContractFragment().javaClass.simpleName)
        }
    }

    private fun showResultCheckAgeAndHistory(result: CheckAgeAndHistoryResponse) {
        if (result.isSuccess) {
            viewDataBinding?.apply {
                item = result.data?.contract
                valueEndDateContract.text =
                    ConvertDate.convertTimestampToPersianDate(
                        result.data?.contract?.startDate ?: 0
                    )
            }
        }
    }
}