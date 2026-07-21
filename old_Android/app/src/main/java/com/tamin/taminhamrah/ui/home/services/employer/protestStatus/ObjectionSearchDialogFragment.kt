package com.tamin.taminhamrah.ui.home.services.employer.protestStatus

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogObjectionSearchBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ObjectionSearchDialogFragment : BaseBottomSheetDialogFragment<DialogObjectionSearchBinding,FollowObjectionsStatusViewModel>(

) {
    override val mViewModelDialog: FollowObjectionsStatusViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_objection_search
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
        setupObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun onClick() {

        viewBinding?.apply {
            btnSearch.setOnClickListener {
                val map = HashMap<String, String>()
                map[mViewModelDialog.ARG_WORKSHOP_CODE] = inputWorkshopCode.getNullableValue()
                map[mViewModelDialog.ARG_OBJECTION_NUMBER] = inputObjectionNumber.getNullableValue()
                map[mViewModelDialog.ARG_DEBIT_NUMBER] = inputDebitNumber.getNullableValue()
                mListener?.onDialogResult(map)
                dismiss()
            }
        }
    }
}