package com.tamin.taminhamrah.ui.home.services.account

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.FragmentBankAccountDeclarationBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.ServiceGuideDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.widget.DatePickerWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Date


@AndroidEntryPoint
class BankAccountDeclarationFragment :
    BaseFragment<FragmentBankAccountDeclarationBinding, BankAccountDeclarationViewModel>() {

    override val mViewModel: BankAccountDeclarationViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_bank_account_declaration
    }

    override fun setupObserver() {
        mViewModel.mldAccountResult.observe(this, ::showResult)
    }

    override fun initView() {

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
    }

    override fun getData() {

    }

    @SuppressLint("SetTextI18n")
    override fun onClick() {
        viewDataBinding?.apply {
            var bankId = ""
            var accountTypeCode = ""
            var startDate = ""

            widgetDatePickerAccount.setListener(object : DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String
                ) {
                    startDate = serverFormattedDateWithDayOffset
                }
            })

            inputBankName.getIt().doOnTextChanged { text, start, before, count ->
                if (text.isNullOrBlank())
                    inputBankName.setError(getString(R.string.error_fill_fields))
                else
                inputBankName.disableError()
            }
            inputBankName.getIt().setOnClickListener {
                val dialog =
                    MenuDialogFragment.newInstance(menuTitle = getString(R.string.bank_name))
                dialog.setMenuListener(object : MenuInterface.OnFetchData {

                    override fun onFetch() {
                        this@BankAccountDeclarationFragment.lifecycleScope.launchWhenCreated {
                            mViewModel.mldBanKList.collectLatest { pagingData ->
                                dialog.updateData(pagingData)
                            }
                        }
                    }

                }, object : MenuInterface.OnResult {

                    override fun onResult(itemResult: MenuModel) {
                        itemResult.let {
                            bankId = itemResult.id!!
                            itemResult.title?.let { it1 -> inputBankName.getIt().setText(it1) }
                        }
                    }
                })
                dialog.show(childFragmentManager, "trtyutyt")

            }

            inputAccountType.getIt().doOnTextChanged { text, start, before, count ->
                if (text.isNullOrBlank())
                    inputAccountType.setError(getString(R.string.error_fill_fields))
                else
                    inputAccountType.disableError()
            }
            inputAccountType.getIt().setOnClickListener {
                val dialog =
                    MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_bank_account_type))
                dialog.setMenuListener(object : MenuInterface.OnFetchData {

                    override fun onFetch() {
                        this@BankAccountDeclarationFragment.lifecycleScope.launchWhenCreated {
                            mViewModel.mldAccountTypeList.collectLatest { pagingData ->
                                dialog.updateData(pagingData)
                            }
                        }
                    }

                }, object : MenuInterface.OnResult {

                    override fun onResult(itemResult: MenuModel) {
                        itemResult.let {
                            accountTypeCode = itemResult.id!!
                            itemResult.title?.let { it1 -> inputAccountType.getIt().setText(it1) }
                        }
                    }
                })
                dialog.show(childFragmentManager, "xzcdagfd")

            }

            inputAccountNumber.getInput().doOnTextChanged { text, start, before, count ->
                if (text.isNullOrBlank())
                    inputAccountNumber.setError(getString(R.string.error_fill_fields))
                else
                    inputAccountNumber.disableError()
            }

            appBar.toolbar.imgInfo.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(
                    ServiceGuideDialogFragment.ARG_GUIDE_TITLE,
                    getString(R.string.label_declare_bank_account)
                )
                bundle.putString(
                    ServiceGuideDialogFragment.ARG_GUIDE_RULES,
                    getString(R.string.label_declare_bank_account_rules)
                )
                handlePageDestination(R.id.action_bank_account_to_guide_dialog, bundle)
            }

            btnSubmitAccountNumber.setOnClickListener {
                if (startDate.isBlank())
                    widgetDatePickerAccount.setError(getString(R.string.error_fill_fields))
                else if (bankId.isBlank())
                    inputBankName.setError(getString(R.string.error_fill_fields))
                else if (accountTypeCode.isBlank())
                    inputAccountType.setError(getString(R.string.error_fill_fields))
                else if (inputAccountNumber.getValue(false).isBlank())
                    inputAccountNumber.setError(getString(R.string.error_fill_fields))
                else {
                    inputAccountNumber.getInput().text?.let {
                        mViewModel.sendBankAccountInfo(
                            it.toString(),
                            accountTypeCode,
                            bankId,
                            startDate
                        )
                    }

                }
            }
        }
    }

    fun showResult(result: GeneralRes) {
        if (result.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_declare_bank_account)
            )
            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    //   handlePageDestination(R.id.action_objectionInsuranceHistoryFragment_to_servicesFragment)
                    requireActivity().onBackPressed()
                }

                override fun onCancelClick() {
                }
            }
            )
            dialog.show(childFragmentManager, "showCertificateResult")
        }
    }

}