package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.membersAndStachholders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogWorkshopMemberSearchBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopMemberSearchDialogFragment :
    BaseBottomSheetDialogFragment<DialogWorkshopMemberSearchBinding,WorkshopInfoViewModel>() {
    override val mViewModelDialog: WorkshopInfoViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_workshop_member_search
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
                map[mViewModelDialog.ARG_NATIONAL_CODE] = inputNationalCode.getNullableValue()
                map[mViewModelDialog.ARG_INSURANCE_NUMBER] = inputInsuranceNumber.getNullableValue()
                mListener?.onDialogResult(map)

                dismiss()
            }

            btnGetAllList.setOnClickListener {
                val map = HashMap<String, String>()
                map[mViewModelDialog.ARG_NATIONAL_CODE] = ""
                map[mViewModelDialog.ARG_INSURANCE_NUMBER] = ""
                mListener?.onDialogResult(map)

                dismiss()
            }

           /* inputWorkshopCode.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setSearchTitle(s, btnSearch)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            inputBranchCode.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setSearchTitle(s, btnSearch)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })*/
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