package com.tamin.taminhamrah.ui.home.services.employer.contract

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogContractSearchBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContractSearchDialogFragment(private val showPaymanRow: Boolean = false) :
    BaseBottomSheetDialogFragment<DialogContractSearchBinding,ContractInfoViewModel>() {

    override val mViewModelDialog : ContractInfoViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_contract_search

    var mListener: OnResultListener<Map<String, String>>? = null

    fun setListener(listener: OnResultListener<Map<String, String>>) {
        mListener = listener
    }

    fun setupObserver() {
    }

    private var onListener: MenuInterface.OnResult? = null

    fun setListener(listener: MenuInterface.OnResult) {
        onListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (showPaymanRow)
            viewBinding?.inputPeymanRow?.visibility = View.VISIBLE


        onClick()
        setupObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun onClick() {

        viewBinding?.apply {
            btnSearch.setOnClickListener {
                val map = HashMap<String, String>()
                map[Constants.WORKSHOP_ID] = inputWorkshopCode.getNullableValue()
                map[Constants.BRANCH_ID] = inputBranchCode.getNullableValue()
                map[Constants.PEYMAN_ROW] = inputPeymanRow.getNullableValue()
                mListener?.onDialogResult(map)
                dismiss()
            }
        }
    }

    private fun setSearchTitle(s: CharSequence?, btnSearch: AppCompatButton) {
        if (s.isNullOrBlank()) {
            btnSearch.text = getString(R.string.label_show_all_workshops)
        } else {
            btnSearch.text = getString(R.string.label_search_workshop)
        }
    }
}