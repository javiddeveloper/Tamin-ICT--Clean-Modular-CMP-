package com.tamin.taminhamrah.ui.home.services.studentContract.payment.paymentList.detailPayment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.detailPayment.DetailPaymentListModel
import com.tamin.taminhamrah.databinding.DialogDetailPaymentListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class
DetailPaymentBottomSheet :
    BaseBottomSheetDialogFragment<DialogDetailPaymentListBinding, BaseViewModel>() {

    override val mViewModelDialog :BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_detail_payment_list

            companion object {
        const val ARG_MENU_ITEMS = "ARG_MENU_ITEMS"
    }

    private var onStopDialogListener: AdapterInterface.OnStopDialogListener? = null
    private var defaultList = arrayListOf<DetailPaymentListModel>()


    private lateinit var listAdapter: DetailPaymentBottomSheetAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun setItem(list: List<DetailPaymentListModel>?) {
        list?.let { defaultList.addAll(it) }
    }

    private fun init() {
        viewBinding?.tvTitle?.apply {
            visibility = View.VISIBLE
            text = getString(R.string.label_history_detail_payment)
        }
        viewBinding?.view?.hasFocusable()
        listAdapter = DetailPaymentBottomSheetAdapter()
        viewBinding?.recycler?.getRecycler()?.apply {
            layoutManager = layoutManager
            adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    UiUtils.BackgroundItemDecoration(
                        ContextCompat.getColor(requireContext(), R.color.lineColor),
                        ContextCompat.getColor(requireContext(), android.R.color.white)
                    )
                )
            }
            listAdapter.setItems(defaultList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onStopDialogListener?.onStop()
    }


}