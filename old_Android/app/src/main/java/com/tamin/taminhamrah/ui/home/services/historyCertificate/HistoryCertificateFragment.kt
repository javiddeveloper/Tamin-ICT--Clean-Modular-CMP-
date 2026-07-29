package com.tamin.taminhamrah.ui.home.services.historyCertificate

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.SendInsuranceHistoryToInstitutionResponse
import com.tamin.taminhamrah.databinding.FragmentHistoryCertificateBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.menuDialogMultiSelect.MenuDialogMultiSelectFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.extentions.createBundle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryCertificateFragment :
    BaseFragment<FragmentHistoryCertificateBinding, HistoryCertificateViewModel>() {
    override val mViewModel: HistoryCertificateViewModel by viewModels()

    private var allHistorySelected = false
    private var historyAndWageSelected = false
    private var combineHistorySelected = false

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_history_certificate
    }

    override fun setupObserver() {
        mViewModel.mldSendHistoryCertificate.observe(this, ::onSendCertificate)
        mViewModel.mldPensionCheck.observe(this, ::onPensionCheck)
    }

    private fun onSendCertificate(response: SendInsuranceHistoryToInstitutionResponse) {
        if (!response.isSuccess)
            return
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.SUCCESS,
            getString(R.string.send_insurance_history_to_institution)
        )
        dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
            override fun onConfirmClick() {
                requireActivity().onBackPressed()
            }

            override fun onCancelClick() {
            }
        })
        dialog.show(childFragmentManager, HistoryCertificateFragment().javaClass.simpleName)
    }


    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            moreViews = null
        )
        viewDataBinding?.apply {
            selectTypeHistory.getIt().setOnClickListener {
                selectTypeHistory.getLayout().isErrorEnabled = false
                resetSelected()
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    MenuDialogMultiSelectFragment.ARG_MENU_ITEMS,
                    mViewModel.getHistoryInsuranceTypeList()
                )
                val dialog = MenuDialogMultiSelectFragment()
                dialog.arguments = bundle
                dialog.setListener(object : MenuInterface.OnResultListItem {
                    override fun onResult(list: List<MenuModel>) {

                        if (list.isEmpty()) {
                            selectTypeHistory.setValue("")
                            return
                        }

                        var str: String? = null
                        list.forEach { item ->
                            str = if (str.isNullOrBlank())
                                item.title
                            else
                                "$str + ${item.title} "


                            when (item.id) {
                                "1" -> {
                                    allHistorySelected = true
                                }
                                "2" -> {
                                    historyAndWageSelected = true
                                }
                                "3" -> {
                                    combineHistorySelected = true
                                }
                            }

                        }
                        selectTypeHistory.setValue(str ?: "")
                    }
                })
                dialog.show(childFragmentManager, HistoryCertificateFragment().javaClass.simpleName)
            }
            btnCertificateSendToInbox.setOnClickListener {
                if (!allHistorySelected && !historyAndWageSelected && !combineHistorySelected) {
                    selectTypeHistory.getLayout().error = getString(R.string.error_select_history_insurance_type)
                } else {
                    selectTypeHistory.getLayout().isErrorEnabled = false
                    mViewModel.sendInsuranceHistoryToInstitution(
                        allHistorySelected,
                        historyAndWageSelected,
                        combineHistorySelected
                    )
                }
            }
        }
    }

    private fun resetSelected() {
        allHistorySelected = false
        historyAndWageSelected = false
        combineHistorySelected = false
    }

    override fun getData() {
        mViewModel.pensionCheck()
    }

    override fun onClick() {
    }

    private fun onPensionCheck(result: CheckInsuredInfoResponse) {
        if (!result.isSuccess) return
        when (result.data?.typeUser) {
            EnumTypeUser.ANONYMOUS.title -> {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
            }
            EnumTypeUser.PENSIONER.title -> {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    result.data?.list?.get(1) ?: getString(R.string.error_active_relation_user_is_pensioner)
                )
                dialog.setDialogClickListener(object :
                    DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        requireActivity().onBackPressed()
                    }

                    override fun onCancelClick() {
                    }

                })
                dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
            }
            EnumTypeUser.INSURED.title -> {
            }
        }
    }

}