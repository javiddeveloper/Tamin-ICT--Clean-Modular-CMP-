package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.paymentSheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.asDomainModel
import com.tamin.taminhamrah.databinding.DialogSearchWorkshopPaymentSheetBinding
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.annotations.NotNull

@AndroidEntryPoint
class WorkshopPaymentSheetSearchDialogFragment :
    BaseBottomSheetDialogFragment<DialogSearchWorkshopPaymentSheetBinding,WorkshopInfoViewModel>() {
    override val mViewModelDialog: WorkshopInfoViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_search_workshop_payment_sheet
    private var selectedDateFrom = "0"

    private var selectedDateTo = "0"
    private var selectedPaymentSheetStatus = "0"
    private var selectedDebitReason = "0"


    interface OnResultListener {
        fun onDialogResult(
            debitCause: String? = "",
            paymentType: String? = "",
            payNumberFrom: String? = "",
            payNumberTo: String? = "",
            dateFrom: String? = "",
            dateTo: String? = ""
        )
    }

    var mListener: OnResultListener? = null
    fun setListener(listener: OnResultListener) {
        mListener = listener
    }

    fun setupObserver() {

    }

    /* private var onListener: MenuInterface.OnResult? = null

     fun setListener(listener: MenuInterface.OnResult) {
         onListener = listener
     }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        setupObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun onClick() {

        viewBinding?.apply {

            inputPaymentType.getIt().setOnClickListener {
                openMenuDialog(getString(R.string.label_payment_sheet_type))

            }

            inputDebitCause.getIt().setOnClickListener {
                openMenuDialog(getString(R.string.label_debit_cause))
            }

            widgetDatePickerStart.inputDate.setOnClickListener {
                val datePickerStart = getDatePicker()
                datePickerStart?.setListener(object : MyPersianPickerListener {
                    override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                        val persianMonth = if (MyPersianPickerDate.persianMonth in 1..9) {
                            "0${MyPersianPickerDate.persianMonth}"
                        } else {
                            "${MyPersianPickerDate.persianMonth}"
                        }
                        selectedDateFrom = MyPersianPickerDate.timestamp.toString()
                        widgetDatePickerStart.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")
                    }

                    override fun onDismissed() {}
                })
                datePickerStart?.show()
            }

            widgetDatePickerEnd.inputDate.setOnClickListener {
                val datePickerStart = getDatePicker()
                datePickerStart?.setListener(object : MyPersianPickerListener {
                    override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                        val persianMonth = if (MyPersianPickerDate.persianMonth in 1..9) {
                            "0${MyPersianPickerDate.persianMonth}"
                        } else {
                            "${MyPersianPickerDate.persianMonth}"
                        }
                        selectedDateTo = MyPersianPickerDate.timestamp.toString()
                        widgetDatePickerEnd.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")
                    }

                    override fun onDismissed() {}
                })
                datePickerStart?.show()
            }

            btnSearch.setOnClickListener {
                mListener?.onDialogResult(
                    inputDebitCause.getValue(),
                    inputPaymentType.getValue(),
                    inputPayNumberFrom.getValue(),
                    inputPayNumberTo.getValue(),
                    selectedDateFrom,
                    selectedDateTo
                )
                dismiss()
            }
        }
    }

    private fun openMenuDialog(tag: String) {
        val dialog = MenuDialogFragment.newInstance(true,  tag)
        dialog.setMenuListener(object : MenuInterface.OnFetchData {

            override fun onFetch() {
                this@WorkshopPaymentSheetSearchDialogFragment.lifecycleScope.launchWhenCreated {

                    when (tag) {
                        getString(R.string.label_debit_cause) -> {

                            mViewModelDialog.getWorkshopDebitCauseFlow().collectLatest { pagingData ->
                                val result = pagingData.map { it.asDomainModel() }
                                dialog.updateData(result)
                            }

                        }
                        getString(R.string.label_payment_sheet_type) -> {
                            mViewModelDialog.getPaymentType().collectLatest { pagingData ->
                                dialog.updateData(pagingData)
                            }
                        }
                    }
                }
            }

        }, object : MenuInterface.OnResult {

            override fun onResult(itemResult: MenuModel) {
                when (tag) {
                    getString(R.string.label_debit_cause) -> {

                        itemResult.title?.let { viewBinding?.inputDebitCause?.setValue(it) }
                        selectedDebitReason = itemResult.id.toString()

                    }
                    getString(R.string.label_payment_sheet_type) -> {
                        itemResult.title?.let { viewBinding?.inputPaymentType?.setValue(it) }
                        selectedPaymentSheetStatus = itemResult.id.toString()
                    }
                }
            }
        })


        dialog.show(childFragmentManager, tag)
    }

}