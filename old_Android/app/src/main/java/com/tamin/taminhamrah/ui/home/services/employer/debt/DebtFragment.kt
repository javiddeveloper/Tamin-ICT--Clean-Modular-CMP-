package com.tamin.taminhamrah.ui.home.services.employer.debt

import android.os.Bundle
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.DebtInstallmentResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfo
import com.tamin.taminhamrah.databinding.FragmentDebtBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.employer.debt.InstallmentDebtFragment.Companion.WORK_SHOP_TAG
import com.tamin.taminhamrah.ui.home.services.employer.debt.adapter.DebtInfoAdapter
import com.tamin.taminhamrah.ui.home.services.employer.debt.bottomSheet.InstallmentBottomSheet
import com.tamin.taminhamrah.ui.home.services.employer.debt.bottomSheet.InstallmentBottomSheet.Companion.DEBT_AMOUNT_TAG
import com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel.InstallmentDebtViewModel
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.getSerializable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.text.DecimalFormat


@AndroidEntryPoint
class DebtFragment : BaseFragment<FragmentDebtBinding, InstallmentDebtViewModel>() {
    override val mViewModel: InstallmentDebtViewModel by viewModels()
    val selectedItems: MutableSet<DebtDetailModel> = HashSet()

    //val workshopInfo by lazy { (arguments?.getSerializable(WORK_SHOP_TAG) as? WorkshopInfo?) }
    val workshopInfo by lazy {getSerializable(arguments, WORK_SHOP_TAG, WorkshopInfo::class.java) }

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_debt

    override fun setupObserver() {
        mViewModel.mldDebtDiscount.observe(viewLifecycleOwner, ::onGetDiscountResponse)
        mViewModel.mldDebtInstallment.observe(viewLifecycleOwner, ::onInstallmentDebtResponse)
    }

    private fun onInstallmentDebtResponse(response: DebtInstallmentResponse) {
        if (response.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.installment_success_message),
                btnCancel = true
            )
            dialog.setDialogClickListener(object :
                DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    requireActivity().onBackPressed()
                }

                override fun onCancelClick() {
                    requireActivity().onBackPressed()
                }
            })
            dialog.show(childFragmentManager, "")
        }
    }

    private fun onGetDiscountResponse(debtDetailModel: DebtDetailModel) {
        selectedItems.add(debtDetailModel)
        refreshDebtLayout()
    }

    private val listAdapter by lazy { DebtInfoAdapter(workshopDebtListener = debtClickList) }
    private val debtClickList: DebtInfoAdapter.WorkshopDebtListener =
        object : DebtInfoAdapter.WorkshopDebtListener {
            override fun onCheckedChange(workShopDebt: WorkShopDebt, checked: Boolean) {
                Timber.tag("getDebtDetailsTag")
                    .i("onCheckedChange : debitNumber=:${workShopDebt.debitNumber}  ")
                if (checked) {
                    var strErrorMessage = ""
                    workShopDebt.apply {
                        when {
                            stepCat == "3" && debitStatCode != "03" -> {
                                strErrorMessage = getString(R.string.error_msg_select_debit_1)
                            }

                            stepCat == "3" && docDateEblaghEjra.isNullOrEmpty() -> {
                                strErrorMessage = getString(R.string.error_msg_select_debit_2)
                            }

                            debitStepCode == "20" && !debitNumberInstallment.isNullOrEmpty() -> {
                                strErrorMessage = getString(
                                    R.string.error_msg_select_debit_3,
                                    debitNumberInstallment
                                )
                            }
                        }
                        if (selectedItems.size >= 1) {
                            selectedItems.forEach { oldItem ->
                                if ((oldItem.debt.stepCat == "3" && stepCat != "3") || (oldItem.debt.stepCat != "3" && oldItem.debt.stepCat == "3")) {
                                    strErrorMessage = getString(R.string.error_msg_select_debit_4)
                                }
                            }
                        }
                        if (strErrorMessage.isNotBlank()) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                strErrorMessage
                            )
                        }
                    }
                    mViewModel.getDebDiscount(workshopInfo?.branchCode ?: "", workShopDebt)
                } else {
                    val list = ArrayList<DebtDetailModel>()
                    for (debtModel in selectedItems) {
                        if (debtModel.debt.debitNumber == workShopDebt.debitNumber) {
                            list.add(debtModel)
                        }
                    }
                    selectedItems.removeAll(list.toSet())
                    refreshDebtLayout()
                }
            }

            override fun onActionClick(workShopDebt: WorkShopDebt) {
                Timber.tag("getDebtDetailsTag")
                    .i("onActionClick : debitNumber=:${workShopDebt.debitNumber}  ")
            }

        }

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
        )
        setupRecycler(viewDataBinding?.debtRecycler, listAdapter)

        viewDataBinding?.apply {
            layoutDesc1.descTxt.text = getText(R.string.desc_debt_1)
            layoutDesc2.descTxt.text = getText(R.string.desc_debt_2)
        }
        workshopInfo?.apply {
            lifecycleScope.launchWhenCreated {
                mViewModel.getDebtDetails(workshopId ?: "", branchCode ?: "", contractRow ?: "")
                    .collectLatest { data ->
                        listAdapter.submitData(data)
                    }
            }
        } ?: run {
            Toast.makeText(requireContext(), "خطا", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
        }
    }

    var sumRemainDebt = 0L
    fun refreshDebtLayout() {
        var sumDiscount = 0L
        sumRemainDebt = 0
        for (debt in selectedItems) {
            sumRemainDebt += debt.debt.debitRemain ?: 0
            sumDiscount += debt.discount
        }
        viewDataBinding?.apply {
            if (selectedItems.isEmpty() || sumRemainDebt < 100000000)
                cardDebtDetail.gone()
            else {
                tvDebtAmount.text = HtmlCompat.fromHtml(
                    getString(
                        R.string.price,
                        UiUtils.createTextColorOrangeAndBold(
                            DecimalFormat("#,###").format(
                                sumRemainDebt
                            )
                        )
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                tvDiscountAmount.text = HtmlCompat.fromHtml(
                    getString(
                        R.string.price,
                        UiUtils.createTextColorOrangeAndBold(
                            DecimalFormat("#,###").format(
                                sumDiscount
                            )
                        )
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                cardDebtDetail.visible()
            }
        }
    }

    override fun getData() {}

    private val onInstallmentClick by lazy {
        object : InstallmentBottomSheet.InstallmentClickListener {
            override fun onInstallmentClick(installmentNumber: String) {
                if (installmentNumber.isDigitsOnly()) {
                    val requestModel = InstallmentRequestModel(
                        HashSet<Debt>().apply {
                            for (item in selectedItems)
                                item.debt.debitNumber?.apply {
                                    add(Debt(this))
                                }
                        },
                        firstInstallmentPer = 25,
                        installmentNumber = installmentNumber.toInt() ,
                        workshopId = workshopInfo?.workshopId ?: "",
                        branchCode = workshopInfo?.branchCode ?: "",
                        peymanSequence = workshopInfo?.contractRow ?: ""
                    )
                    Timber.tag("TestJsonInstallmentRequest")
                        .i("requestJson=${Gson().toJson(requestModel)}")
                    mViewModel.installmentDebt(requestModel)
                }else{
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(R.string.label_error_enter_correct_data))
                }
            }
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnActions.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(DEBT_AMOUNT_TAG, sumRemainDebt)
                InstallmentBottomSheet.getInstance(onInstallmentClick, bundle)
                    .show(childFragmentManager, "")
            }
        }
    }
}