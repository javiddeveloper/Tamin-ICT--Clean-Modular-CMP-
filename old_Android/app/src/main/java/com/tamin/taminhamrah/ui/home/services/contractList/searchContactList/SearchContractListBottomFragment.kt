package com.tamin.taminhamrah.ui.home.services.contractList.searchContactList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.RadioButtonModel
import com.tamin.taminhamrah.databinding.DialogContractListSearchBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.home.services.contractList.ContractListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchContractListBottomFragment :
    BaseBottomSheetDialogFragment<DialogContractListSearchBinding, ContractListViewModel>() {
    var mListener: OnResultListener<Map<String, String>>? = null
    var item1 = RadioButtonModel()
    var item2 = RadioButtonModel()
    var item3 = RadioButtonModel()
    var selectedItem = "-1"

    override val mViewModelDialog: ContractListViewModel by viewModels()
    override fun getLayoutId() = R.layout.dialog_contract_list_search
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onClick()
    }

    private fun initView() {
        item1 = RadioButtonModel(
            desc = getString(R.string.label_insurance_freelance_job),
            isSelected = false
        )
        item2 = RadioButtonModel(
            desc = getString(R.string.label_insurance_optional),
            isSelected = false
        )
        item3 = RadioButtonModel(
            desc = getString(R.string.label_insurance_complete_fractional_history_of_the_month),
            isSelected = false
        )
        setItems()
    }

    private fun setItems() {
        viewBinding?.apply {
            itemFreelanceJob = item1
            itemOptional = item2
            itemCompleteFractional = item3
        }
    }

    private fun onClick() {
        viewBinding?.apply {
            layoutRbFreelanceJob.layoutRadios.setOnClickListener {
                item1.isSelected = !item1.isSelected
                selectedItem = if (item1.isSelected) "01" else "-1"
                item2.isSelected = false
                item3.isSelected = false
                setItems()
            }

            layoutRbOptional.layoutRadios.setOnClickListener {
                item1.isSelected = false
                item2.isSelected = !item2.isSelected
                selectedItem = if (item2.isSelected) "02" else "-1"
                item3.isSelected = false
                setItems()
            }

            layoutCompleteFractional.layoutRadios.setOnClickListener {
                item1.isSelected = false
                item2.isSelected = false
                item3.isSelected = !item3.isSelected
                selectedItem = if (item3.isSelected) "38" else "-1"
                setItems()
            }

            btnSearch.setOnClickListener {
                val map = HashMap<String, String>()
                if (inputContractNumber.getValue().isNotBlank())
                    map[mViewModelDialog.ARG_CONTRACT_NUMBER] = inputContractNumber.getValue()
                if (selectedItem != "-1")
                    map[mViewModelDialog.ARG_INSURANCE_TYPE] = selectedItem
                mListener?.onDialogResult(map)
                dismiss()
            }
        }

    }
}