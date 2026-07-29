package com.tamin.taminhamrah.ui.aiAgent.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tamin.taminhamrah.databinding.HistoryCategoryEditBottomSheetBinding
import com.tamin.taminhamrah.ui.aiAgent.ui.CATEGORY_ITEM_EDIT
import com.tamin.taminhamrah.utils.dismissWithResult


class HistoryCategoryEditBottomSheet(
) : BottomSheetDialogFragment() {

    val args: HistoryCategoryEditBottomSheetArgs by navArgs()
    private lateinit var binding: HistoryCategoryEditBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HistoryCategoryEditBottomSheetBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onClick()

    }

    private fun initView() {
        binding.apply {
            edtMessage.setValueOfText(args.categoryItem.title)
        }
    }

    private fun onClick() {
        binding.btnEdit.setOnClickListener {
            val newTitle = binding.edtMessage.getValue(true)
            val editedItem = args.categoryItem.copy(title = newTitle)
            dismissWithResult(editedItem, CATEGORY_ITEM_EDIT)
        }
    }
}
