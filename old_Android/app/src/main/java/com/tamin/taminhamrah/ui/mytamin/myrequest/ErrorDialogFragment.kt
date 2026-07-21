package com.tamin.taminhamrah.ui.mytamin.myrequest

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.user.RequestError
import com.tamin.taminhamrah.databinding.DialogErrorListBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ErrorDialogFragment :
    BaseBottomSheetDialogFragment<DialogErrorListBinding, BaseViewModel>() {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId() = R.layout.dialog_error_list

    companion object {
        const val ARG_ERROR_ITEMS = "ARG_ERROR_ITEMS"

        fun newInstance(errorList: ArrayList<RequestError>?): ErrorDialogFragment{
            val args = Bundle()
            args.putParcelableArrayList(ARG_ERROR_ITEMS, errorList)
            val fragment = ErrorDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var listAdapter: ErrorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
    }

    private fun getExtraValue(): ArrayList<RequestError>? {
        return arguments?.getParcelableArrayList(ARG_ERROR_ITEMS)
    }

    private fun init() {

        val defaultList = getExtraValue()
        listAdapter = ErrorAdapter()

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