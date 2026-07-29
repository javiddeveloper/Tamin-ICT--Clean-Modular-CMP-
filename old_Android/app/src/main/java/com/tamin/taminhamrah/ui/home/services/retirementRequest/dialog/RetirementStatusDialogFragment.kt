package com.tamin.taminhamrah.ui.home.services.retirementRequest.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogRetirementStateOfRequestBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseDialogFragment
import com.tamin.taminhamrah.ui.home.services.retirementRequest.adapter.RequestStatusAdapter
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.RetirementRequestStateModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetirementStatusDialogFragment : BaseDialogFragment<DialogRetirementStateOfRequestBinding>(
    DialogRetirementStateOfRequestBinding::inflate
) {
    private var onClickListener: DialogClickInterface.onClickListener? = null

    fun setDialogClickListener(clickListener: DialogClickInterface.onClickListener) {
        onClickListener = clickListener
    }

    private val statusAdapter by lazy { RequestStatusAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        childFragmentManager.fragments.takeIf { it.isNotEmpty() }
            ?.map { (it as? DialogFragment)?.dismiss() }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onClick()
        initDialog()
    }

    private fun initView() {
        viewBinding.statusRecycler.apply {
            adapter = statusAdapter
            layoutManager = LinearLayoutManager(requireContext())

            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }
    }


    fun initDialog() {
        val data =
            arguments?.getParcelableArrayList<RetirementRequestStateModel>(Constants.RETIREMENT_STATUS_REQUEST)
        val desc = arguments?.getString(Constants.DIALOG_DESC)?:getString(R.string.retirement_status_check_of_branch)
        if (data == null) {
            viewBinding.tvDesc.text = getString(R.string.error_recive_data)
            viewBinding.tvDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_dialog_red))
            viewBinding.btnOk.isVisible = false
        } else {
            statusAdapter.setItems(data)
            viewBinding.tvDesc.text = desc
        }
    }

    private fun onClick() {
        viewBinding.apply {
            btnOk.setOnClickListener {
                dismiss()
                onClickListener?.onConfirmClick()
            }
            btnCancel.setOnClickListener {
                onClickListener?.onCancelClick()
                dismiss()
            }
        }
    }

}