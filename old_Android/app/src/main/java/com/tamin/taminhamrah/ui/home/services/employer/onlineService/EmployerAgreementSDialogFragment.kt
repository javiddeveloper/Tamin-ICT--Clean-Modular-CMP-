package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogEmployerAgreementSearchBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployerAgreementSDialogFragment :
    BaseBottomSheetDialogFragment<DialogEmployerAgreementSearchBinding,EmployerAgreementInfoViewModel >() {

    override val mViewModelDialog : EmployerAgreementInfoViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_employer_agreement_search
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
        onClick()
        initView()
        setupObserver()
    }

    var selectedStatusId: String? = null
    private fun initView() {

    }

    @SuppressLint("SetTextI18n")
    private fun onClick() {
        viewBinding?.apply {
            btnSearch.setOnClickListener {
                val map = HashMap<String, String>()
                map[Constants.WORKSHOP_ID] = inputWorkshopCode.getNullableValue()
                map[Constants.BRANCH_ID] = inputBranchCode.getNullableValue()
                mListener?.onDialogResult(map)
                dismiss()
            }
        }
    }
}