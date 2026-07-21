package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogWorkshopSearchBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopSearchDialogFragment :
    BaseBottomSheetDialogFragment<DialogWorkshopSearchBinding, WorkshopInfoViewModel>(

    ) {
    override val mViewModelDialog: WorkshopInfoViewModel by viewModels()
    override fun getLayoutId() = R.layout.dialog_workshop_search
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
                if (inputWorkshopCode.getNullableValue().isNotEmpty() ||
                    inputBranchCode.getNullableValue().isNotEmpty()
                ) {
                    val map = HashMap<String, String>()
                    map[Constants.WORKSHOP_ID] = inputWorkshopCode.getNullableValue()
                    map[Constants.BRANCH_ID] = inputBranchCode.getNullableValue()
                    mListener?.onDialogResult(map)
                    dismiss()
                }
            }

            btnGetAllList.setOnClickListener {
                val map = HashMap<String, String>()
                map[Constants.WORKSHOP_ID] = ""
                map[Constants.BRANCH_ID] = ""
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