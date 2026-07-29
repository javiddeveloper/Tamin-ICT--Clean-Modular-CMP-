package com.tamin.taminhamrah.ui.home.services.employer.debt.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.WorkshopSearchBottomSheetBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel.InstallmentDebtViewModel
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebtSearchBottomSheet(var mListener: WorkshopSearchListener? = null) :
    BaseBottomSheetDialogFragment<WorkshopSearchBottomSheetBinding, InstallmentDebtViewModel>() {

    companion object {
        const val DIALOG_TYPE = "DIALOG_TYPE"
    }

    override val mViewModelDialog: InstallmentDebtViewModel by viewModels()
    override fun getLayoutId() = R.layout.workshop_search_bottom_sheet
    val dialogType by lazy {
        (arguments?.getSerializable("DIALOG_TYPE") as? SearchType?) ?: SearchType.AGREEMENT_ROW
    }

    fun setListener(listener: WorkshopSearchListener?) {
        mListener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        viewBinding?.apply {
            if (dialogType == SearchType.LETTER_DATE) {
                letterDate.visible()
                inputAgreementRow.gone()
            } else {
                letterDate.gone()
                inputAgreementRow.visible()
            }
        }


        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
    }

    private fun onClick() {

        viewBinding?.apply {
            btnSearch.setOnClickListener {
                if (inputWorkshopCode.getNullableValue()
                        .isNotEmpty() || (dialogType == SearchType.AGREEMENT_ROW && inputAgreementRow.getNullableValue()
                        .isNotEmpty() || (dialogType == SearchType.LETTER_DATE && letterDate.getDateString()
                        .isNotEmpty()))
                ) {
                    mListener?.onWorkshopSearchListener(
                        inputWorkshopCode.getNullableValue(),
                        if (dialogType == SearchType.AGREEMENT_ROW) inputAgreementRow.getNullableValue() else letterDate.getDateString()
                    )
                    dismiss()
                }
            }

            btnGetAllList.setOnClickListener {
                mListener?.onWorkshopSearchListener("", "")
                dismiss()
            }
        }
    }

    interface WorkshopSearchListener {
        fun onWorkshopSearchListener(workshopId: String, agreementRow: String)
    }

    enum class SearchType : java.io.Serializable {
        AGREEMENT_ROW, LETTER_DATE
    }
}