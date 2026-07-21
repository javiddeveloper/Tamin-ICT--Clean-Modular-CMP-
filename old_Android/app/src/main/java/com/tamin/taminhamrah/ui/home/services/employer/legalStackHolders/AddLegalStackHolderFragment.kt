package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.legalStackHolder.LegalStackHolder
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.FragmentAddLegalAgentBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddLegalStackHolderFragment :
    BaseFragment<FragmentAddLegalAgentBinding, LegalStackHolderViewModel>() {
    companion object {
        const val ARG_IS_SPECIAL_ITEM = "ARG_IS_SPECIAL_ITEM"
        const val ARG_ITEM_STACK_HOLDER = "ARG_ITEM_STACK_HOLDER"
    }

    private val isSpecialItem: Boolean by lazy {
        arguments?.getBoolean(ARG_IS_SPECIAL_ITEM) ?: false
    }

    private val stackHolderItem :LegalStackHolder? by lazy{
        arguments?.getParcelable(AddLegalStackHolderListFragment.ARG_ITEM_STACK_HOLDER) as? LegalStackHolder
    }

    override val mViewModel: LegalStackHolderViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId() = R.layout.fragment_add_legal_agent

    override fun setupObserver() {
        mViewModel.mldTicketResult.observe(viewLifecycleOwner, ::onRequestTicket)
        mViewModel.mldVerifyTicketResult.observe(viewLifecycleOwner, ::showVerifyTicketResult)
        mViewModel.mldVerifyUser.observe(viewLifecycleOwner, ::onVerifyUser)
    }

    override fun getData() {}

    private fun onVerifyUser(result: GeneralRes?) {
         if (result?.isSuccess == true) {
            showAlertDialog(MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success),
                MessageOfRequestDialogFragment.DismissType.NORMAL)
         }
    }

    private fun onRequestTicket(result: GeneralRes?) {

    }

    private fun showVerifyTicketResult(result: GeneralRes?) {
        if (result?.isSuccess == true) {
            viewDataBinding?.btnSubmit?.isClickable = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onClick()
        setupObserver()
    }

    override fun initView() {
        viewDataBinding?.apply {

            setupToolbar(
                viewDataBinding?.appBar,
                viewDataBinding?.appbarBackgroundImage?.imageBackground
            )

            inputNationalCode.setTextWidget(stackHolderItem?.nationalId ?: "")
            cbSelectElecNotif.isChecked = stackHolderItem?.isElecNotif() ?: false
            cbSelect.isChecked = stackHolderItem?.isInternetList() ?: false
            cbSelectRegistration.isChecked = stackHolderItem?.isRegistration() ?: false

            if (!isSpecialItem) {
                inputSelectContract.gone()
            } else {
                inputSelectContract.visible()

                inputSelectContract.getIt().setOnClickListener {

                      val dialog = WorkshopContractListDialog.
                      newInstance( stackHolderItem?.workshopId,stackHolderItem?.branchCode, mViewModel.reqModel.contractRows).apply {
                          setListener(object :DialogResultInterface.OnResultListener<ArrayList<String?>>{
                              override fun onDialogResult(item: ArrayList<String?>) {
                                  mViewModel.reqModel.contractRows = item
                                  var result =""
                                      for (row in item){
                                      result = "$row,$result"
                                  }

                                  inputSelectContract.setValue(result)
                              }
                          })
                      }

                    dialog.show(childFragmentManager, WorkshopContractListDialog().javaClass.simpleName)
                }

            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onClick() {

        viewDataBinding?.apply {
               btnVerifyCode.setOnClickListener {
                   mViewModel.requestTicket(inputNationalCode.getValueNationalCode())
               }
            btnSubmit.setOnClickListener {
                if (inputNationalCode.getValueNationalCode().isNotEmpty() &&
                    inputVerificationCode.getValue().isNotEmpty()
                ) {
                    with(mViewModel.reqModel) {
                        accessCode =  stackHolderItem?.getAccessCode(
                            cbSelectElecNotif.isChecked,
                            cbSelect.isChecked,
                            cbSelectRegistration.isChecked
                        )
                        branchCode =  stackHolderItem?.branchCode
                        nationalCode = inputNationalCode.getValueNationalCode()
                        workshopId = stackHolderItem?.workshopId
                        ticket = inputVerificationCode.getValue()
                        special = isSpecialItem
                    }

                    mViewModel.submitNewLegalAgent(
                        inputVerificationCode.getValue(),
                    )
                }
            }
        }
    }



}