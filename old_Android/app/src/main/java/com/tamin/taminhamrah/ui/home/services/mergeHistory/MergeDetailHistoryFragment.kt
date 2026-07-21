package com.tamin.taminhamrah.ui.home.services.mergeHistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordModel
import com.tamin.taminhamrah.databinding.FragmentCombinedRecordDetailsBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MergeDetailHistoryFragment :
    BaseBottomSheetDialogFragment<FragmentCombinedRecordDetailsBinding,MergeHistoryViewModel>(){

    companion object {
        const val ARG_SELECTED_ITEM = "ARG_SELECTED_ITEM"
        const val ARG_TOOLBAR_IMAGE = "ARG_SELECTED_ITEM"
    }

    override val mViewModelDialog: MergeHistoryViewModel by activityViewModels()
    override fun getLayoutId()= R.layout.fragment_combined_record_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun getData(): CombinedRecordModel? {
        return arguments?.getParcelable(ARG_SELECTED_ITEM) as? CombinedRecordModel
    }

    private fun initView() {
        viewBinding?.item = getData()
    }

}