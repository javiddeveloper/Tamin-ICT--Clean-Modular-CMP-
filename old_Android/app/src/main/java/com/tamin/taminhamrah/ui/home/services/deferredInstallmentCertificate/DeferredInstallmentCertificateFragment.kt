package com.tamin.taminhamrah.ui.home.services.deferredInstallmentCertificate

import android.annotation.SuppressLint
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.Bank
import com.tamin.taminhamrah.data.remote.models.services.DeferredInstallmentCertificateResponse
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.remote.models.services.asDomainModel
import com.tamin.taminhamrah.databinding.FragmentDeferredInstallmentCertificateBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.viewEdictPensioner.EdictPensionerFragment
import com.tamin.taminhamrah.utils.HelperDate
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.Date

@AndroidEntryPoint
class DeferredInstallmentCertificateFragment :
    BaseFragment<FragmentDeferredInstallmentCertificateBinding, DeferredInstallmentCertificateViewModel>(),
    DialogClickInterface.onClickListener {

    override val mViewModel: DeferredInstallmentCertificateViewModel by viewModels()
    private var currentAmount: String = ""
    private var numberInstallments: String = ""
    private var currentGuaranteeAmount: String = ""
    private var trueGuaranteeAmount: Long = 0
    var selectedBankCode: String = ""
    var guaranteeStatus: String = ""
    var birthDate: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var nationalId: String = ""
    private var sendGuaranteeAmount: Long = 0
    private var sendAmountPer: String = ""

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_deferred_installment_certificate
    }

    override fun setupObserver() {
        mViewModel.mldDeferredInstallmentCertificate.observe(this, ::showRequestResult)
        mViewModel.mldPensionerIdList.observe(this, ::showResultPensionerId)
    }

    private fun showResultPensionerId(result: PensionerIdResponse) {
        if (result.isSuccess) {
            mViewModel.pensionIdModelList.apply {
                clear()
                addAll(result.data?.list ?: emptyList())
                if (isNotEmpty()) {
                    viewDataBinding?.selectPensionId?.setValue(get(0).pensionerId ?: "")
                }
            }
        }
    }

    private fun showRequestResult(result: DeferredInstallmentCertificateResponse) {
        Timber.tag("showRequestResult: ").i(result.toString())
        if (result.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                String.format(
                    (String.format(
                        requireActivity().getString(R.string.message_number_tracking_deferred_installment_certificate),
                        result.data?.request?.refCode
                    ))
                )
            )
            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    requireActivity().onBackPressed()
                }

                override fun onCancelClick() {
                }

            })
            dialog.show(childFragmentManager, "MenuDialogFragment")
        }
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupToolbar(
                appBar,
                viewDataBinding?.appbarBackgroundImage?.imageBackground)
            widgetNationalCode.getInput().doAfterTextChanged {
                if (widgetNationalCode.getValueNationalCode(false).length == 10)
                    widgetNationalCode.getLayout().isErrorEnabled = false
            }

            layoutNumberInstallments.getInput().doAfterTextChanged {
                layoutNumberInstallments.getLayout().isErrorEnabled = false
            }
        }
        setRealTimeValueInTextView()


    }

    override fun getData() {
        mViewModel.getPensionerIdList()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            widgetDatePicker.inputDate.setOnClickListener {
                widgetDatePicker.tilDate.isErrorEnabled = false
                val datePicker = getDatePicker()
                datePicker?.setListener(object : MyPersianPickerListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSelected( selectedDate: MyPersianPickerDate) {

                        val date = Date(selectedDate.timestamp + 70200000)
                        birthDate = HelperDate.convertServerDateFormatToMobileDateFormat(date)



                        widgetDatePicker.inputDate.setText("${selectedDate.persianYear}/${selectedDate.persianMonth}/${selectedDate.persianDay}")
                    }

                    override fun onDismissed() {}
                })
                datePicker?.show()
            }

            selectPensionId.getIt().setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                val dialog = MenuDialogFragment.newInstance(true , getString(R.string.pension_number))

                dialog. setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        this@DeferredInstallmentCertificateFragment.lifecycleScope.launchWhenCreated {
                            Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = {
                                    LocalPagingSource(mViewModel.pensionIdModelList)
                                }
                            ).flow.cachedIn(lifecycleScope).collectLatest {paginData->
                                dialog.updateData(paginData.map {
                                    MenuModel(id = it.pensionerId, title = it.pensionerId)
                                })
                            }
                        }
                    }
                }, object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        selectPensionId.getLayout().isErrorEnabled = false
                        selectPensionId.setValue(itemResult.id ?: "")
                    }
                })

                dialog.  show(childFragmentManager, EdictPensionerFragment().javaClass.simpleName)

            }

            selectGuaranteeStatus.getIt().setOnClickListener {
                selectGuaranteeStatus.getLayout().isErrorEnabled = false
                val dialog =
                    MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_status_guarantee))
                dialog.setMenuListener(object : MenuInterface.OnFetchData {

                    override fun onFetch() {
                        this@DeferredInstallmentCertificateFragment.lifecycleScope.launchWhenCreated {
                            mViewModel.guaranteeListFlow.collectLatest { pagingData ->
                                dialog.updateData(pagingData)
                            }
                        }
                    }

                }, object : MenuInterface.OnResult {

                    override fun onResult(itemResult: MenuModel) {
                        itemResult.id?.let {
                            guaranteeStatus = it
                            itemResult.title?.let { it1 ->
                                selectGuaranteeStatus.setValue(it1)
                                if (itemResult.id.equals("1")) {
                                    groupVisit.visibility =
                                        View.VISIBLE
                                } else
                                    groupVisit.visibility =
                                        View.GONE
                            }
                        }
                    }
                })
                dialog.show(childFragmentManager, "r;lokioijm")

            }



            selectBankName.getIt().setOnClickListener {
                selectBankName.getLayout().isErrorEnabled = false
                val dialog = MenuDialogFragment.newInstance(true, getString(R.string.label_bank))
                dialog.setMenuListener(object : MenuInterface.OnFetchData {

                    override fun onFetch() {
                        this@DeferredInstallmentCertificateFragment.lifecycleScope.launchWhenCreated {
                            mViewModel.getBeneficiaryListFlow().collectLatest { pagingData ->
                                val result = pagingData.map { it.asDomainModel() }
                                dialog.updateData(result)
                            }
                        }
                    }

                }, object : MenuInterface.OnResult {

                    override fun onResult(itemResult: MenuModel) {
                        itemResult.title?.let { it1 -> selectBankName.setValue(it1) }
                        selectedBankCode = itemResult.id.toString()
                    }
                }, object : MenuInterface.OnSearch {
                    override fun onSearch(str: String) {
                        this@DeferredInstallmentCertificateFragment.lifecycleScope.launchWhenCreated {
                            mViewModel.getBeneficiaryListFlow(str).collectLatest { pagingData ->
                                val result = pagingData.map { it.asDomainModel() }
                                dialog.updateData(result)
                            }
                        }
                    }
                })
                dialog.show(childFragmentManager, "MenuDialogFragment")

            }

            btnFinalApproval.setOnClickListener {
                if (checkValidInput()) {
                    var borrowerName = "به نام خودم به عنوان وام گیرنده"
                    if (guaranteeStatus == "1") {
                        borrowerName = " ضمانت آقا/خانم $firstName$lastName"
                    }
                    val bankName = selectBankName.getValue()
                    val serviceName = getString(R.string.deferred_installment)
                    val desc =
                        getString(R.string.message_confirm_deferred_installment_certificate,
                            UiUtils.createTextColorGreenAndBold(viewDataBinding?.selectPensionId?.getValue(false)),
                            UiUtils.createTextColorBlueAndBold(bankName),
                            UiUtils.createTextColorOrangeAndBold(borrowerName),
                            bankName,
                            UiUtils.createTextColorOrangeAndBold(serviceName), bankName)

                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.CONFIRM, desc)
                }
            }
        }
    }

    private fun setRealTimeValueInTextView() {
        viewDataBinding?.apply {
            layoutRefundAmount.getInput().doAfterTextChanged {
                layoutRefundAmount.getLayout().isErrorEnabled = false
            }
            layoutAmountPerInstallment.getInput().doAfterTextChanged { amount ->
                var cleanString = ""
                amount?.let { _amount ->
                    layoutAmountPerInstallment.getInput().let {
                        if (_amount.toString() != currentAmount && _amount.toString() != "") {
                            cleanString = _amount.replace("""[$,.]""".toRegex(), "")
                            if (cleanString.toLong() < 500000) {
                                layoutAmountPerInstallment.getLayout().error =
                                    getString(R.string.error_minimum_500000_rials)
                            } else {
                                layoutAmountPerInstallment.getLayout().isErrorEnabled =
                                    false
                            }
                            val formatted = Utility.getNumberWithSeparator(cleanString.toLong())
                            currentAmount = formatted
                            sendAmountPer = cleanString
                            it.setText(formatted)
                            it.setSelection(formatted.length)
                        }
                    }

                    layoutNumberInstallments.getInput().text?.let {
                        if (it.isNotBlank() && cleanString != "") {
                            val a = cleanString
                            val b = it.toString()
                            val mul = a.toLong() * b.toLong()
                            val total = mul + (mul * 0.20)
                            trueGuaranteeAmount = total.toLong()
                            sendGuaranteeAmount = total.toLong()
                            layoutRefundAmount.getInput().setText(
                                Utility.getNumberWithSeparator(
                                    mul
                                )
                            )
                            inputGuaranteeAmount.getInput().setText(
                                Utility.getNumberWithSeparator(
                                    total.toLong()
                                )
                            )
                        }
                    }
                }
            }

            layoutNumberInstallments.getInput().doAfterTextChanged { number ->
                number?.let { num ->
                    var b = num.replace("""[$,.]""".toRegex(), "")
                    if (b.trim() != "") {
                        if (b.toInt() > 240) {
                            layoutNumberInstallments.getInput().setText("240")
                            b = "240"
                        }
                        layoutAmountPerInstallment.getInput().text?.let { a ->
                            if (a.toString() != "") {
                                val inputStrAmountPer = a.toString()
                                val inputStr =
                                    inputStrAmountPer.replace("""[$,.]""".toRegex(), "")
                                val mul = inputStr.toLong() * b.toLong()
                                val total = mul + (mul * 0.20)
                                trueGuaranteeAmount = total.toLong()
                                sendGuaranteeAmount = total.toLong()
                                numberInstallments = b

                                layoutRefundAmount.getInput().setText(
                                    Utility.getNumberWithSeparator(
                                        mul
                                    )
                                )
                                inputGuaranteeAmount.getInput().setText(
                                    Utility.getNumberWithSeparator(
                                        total.toLong()
                                    )
                                )
                            }
                        }
                    } else {
                        layoutRefundAmount.getInput().setText("")
                        inputGuaranteeAmount.getInput().setText("")
                    }
                }
            }

            inputGuaranteeAmount.getInput().doAfterTextChanged { amount ->
                inputGuaranteeAmount.getLayout().isErrorEnabled = false
                var cleanString: String
                amount?.let { _amount ->
                    inputGuaranteeAmount.getInput().let {
                        if (_amount.toString() != currentGuaranteeAmount && _amount.toString() != ""
                        ) {
                            cleanString = _amount.replace("""[$,.]""".toRegex(), "")
                            val formatted = Utility.getNumberWithSeparator(cleanString.toLong())
                            currentGuaranteeAmount = formatted
                            sendGuaranteeAmount = cleanString.toLong()
                            it.setText(formatted)
                            it.setSelection(formatted.length)
                        }
                    }
                }
            }

        }
    }

    private fun checkValidInput(): Boolean {
        viewDataBinding?.apply {

            when {

                guaranteeStatus.isBlank() -> {
                    showError(
                        textLayout = selectGuaranteeStatus.getLayout(),
                        strError = getString(R.string.error_select_guarantee_Status))
                    return false
                }
                else -> {
                    if (guaranteeStatus == "1") {
                        when {
                            layoutNameOthers.getInput().text.isNullOrBlank() -> {
                                showError(
                                    textLayout = layoutNameOthers.getLayout(),
                                    strError = getString(R.string.error_input_name))
                                return false
                            }
                            layoutLastNameOthers.getInput().text.isNullOrBlank() -> {
                                showError(
                                    textLayout = layoutLastNameOthers.getLayout(),
                                    strError = getString(R.string.error_input_last_name))
                                return false
                            }
                            widgetNationalCode.getValueNationalCode(false).isEmpty() -> {
                                showError(
                                    textLayout = widgetNationalCode.getLayout(),
                                    strError = getString(R.string.please_enter_valid_national_code))
                                return false
                            }
                            birthDate.isBlank() -> {
                                showError(
                                    textLayout = widgetDatePicker.tilDate,
                                    strError = getString(R.string.error_select_date_birthday))
                                return false
                            }
                        }
                    }

                    firstName = layoutNameOthers.getInput().text.toString()
                    lastName = layoutLastNameOthers.getInput().text.toString()
                    nationalId = widgetNationalCode.getValueNationalCode()

                    ///////////
                    when {
                        selectBankName.getIt().text.isNullOrBlank() -> {
                            showError(
                                textLayout = selectBankName.getLayout(),
                                strError = getString(R.string.error_select_bank),
                                scroll = false)
                        }
                        layoutBranchName.getInput().text.isNullOrBlank() -> {
                            showError(
                                textLayout = layoutBranchName.getLayout(),
                                strError = getString(R.string.error_input_branch_name),
                                scroll = false)
                        }
                        layoutAmountPerInstallment.getInput().text.isNullOrBlank() -> {
                            showError(
                                textLayout = layoutAmountPerInstallment.getLayout(),
                                strError = getString(R.string.error_input_amount_per_installment),
                                scroll = false)
                        }
                        layoutNumberInstallments.getInput().text.isNullOrBlank() -> {
                            showError(
                                textLayout = layoutNumberInstallments.getLayout(),
                                strError = getString(R.string.error_input_number_installment),
                                scroll = false)
                        }
                        layoutRefundAmount.getInput().text.isNullOrBlank() -> {
                            showError(
                                textLayout = layoutRefundAmount.getLayout(),
                                strError = getString(R.string.error_input_refund_amount),
                                scroll = false)
                        }
                        inputGuaranteeAmount.getInput().text.isNullOrBlank() -> {
                            showError(
                                textLayout = inputGuaranteeAmount.getLayout(),
                                strError = getString(R.string.error_input_guarantee_amount),
                                scroll = false)
                        }
                        else -> {
                            if (Utility.removeNumberSeparator(currentGuaranteeAmount)
                                    .toLong() < trueGuaranteeAmount
                            ) {
                                return false
                            }/* else if (!Utility.checkInputIsValidPersionName(firstName) || !Utility.checkInputIsValidBranch(lastName)
                            ) {
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.error_input_first_name_not_valid)
                                )
                                return false
                            } else if (!Utility.checkInputIsValidBranch(viewDataBinding?.layoutBranchName?.getInput()?.text.toString())) {
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.error_input_is_branch_name_not_valid)
                                )
                                return false
                            }*/ else if (selectBankName.getIt().editableText.isBlank() || sendAmountPer == ""
                                || numberInstallments == "" || sendGuaranteeAmount.equals("")
                                || sendAmountPer.toLong() < 500000 || numberInstallments.toLong() > 240 ||
                                numberInstallments.toLong() < 12
                            ) {
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.minimum_amount_is_not_observed)
                                )
                                return false
                            } else {
                                return true
                            }
                        }
                    }
                    ///////////
                }
            }
        }
        return false
    }

    override fun onConfirmClick() {
        when (guaranteeStatus) {
            "0" -> {
                mViewModel.sendRequestDeferredInstallmentCertificateForMe(
                    Bank(selectedBankCode),
                    viewDataBinding?.layoutBranchName?.getInput()?.text.toString(),
                    guaranteeStatus,
                    sendGuaranteeAmount,
                    sendGuaranteeAmount.toString(),
                    numberInstallments,
                    sendAmountPer.toLong(),
                    viewDataBinding?.selectPensionId?.getValue(false)?:""
                )
            }
            "1" -> {
                mViewModel.sendRequestDeferredInstallmentCertificateForOthers(
                    Bank(selectedBankCode),
                    viewDataBinding?.layoutBranchName?.getInput()?.text.toString(),
                    guaranteeStatus,
                    sendGuaranteeAmount,
                    sendGuaranteeAmount.toString(),
                    numberInstallments,
                    sendAmountPer.toLong(),
                    viewDataBinding?.selectPensionId?.getValue(false)?:"",
                    birthDate,
                    firstName,
                    lastName,
                    nationalId
                )
            }
        }

    }

    override fun onCancelClick() {
    }

    private fun showError(
        textLayout: TextInputLayout? = null,
        strError: String,
        scroll: Boolean = true,
    ) {
        textLayout?.error = strError
        if (scroll)
            viewDataBinding?.apply {
                appBar.appBarView.setExpanded(false, true)
                textLayout?.requestFocus()
                nestedScrollView.scrollTo(0, textLayout?.top ?: 0)
            }
    }

}