package com.tamin.taminhamrah.ui.mytamin.myrequest

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.user.SmartGuideItem
import com.tamin.taminhamrah.data.remote.models.user.SmartGuideResponse
import com.tamin.taminhamrah.databinding.DialogSmartGuideBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmartGuideDialogFragment :
    BaseBottomSheetDialogFragment<DialogSmartGuideBinding, BaseViewModel>() {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_smart_guide

    companion object {
        const val ARG_ERROR_ITEMS = "ARG_ERROR_ITEMS"
    }

    private lateinit var listAdapter: SmartGuideAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
    }

    private fun getExtraValue(): List<SmartGuideItem>? {
        val result = arguments?.getString(ARG_ERROR_ITEMS)
        val res=  Gson().fromJson(result, SmartGuideResponse::class.java)
        return res?.data?.list
    }

    private fun init() {

        val defaultList = getExtraValue()
        listAdapter = SmartGuideAdapter()

        viewBinding?.recycler?.apply {
            this.layoutManager = layoutManager
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    UiUtils.BackgroundItemDecoration(
                        ContextCompat.getColor(requireContext(), R.color.lineColor),
                        ContextCompat.getColor(requireContext(), android.R.color.white)
                    )
                )
            }
        }

        defaultList?.let { listAdapter.setItems(it) }

    }

    private fun onClick() {
        viewBinding?.btnReturn?.setOnClickListener { dismiss() }
    }
}