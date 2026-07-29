package com.tamin.taminhamrah.ui.home.services.employer.debt.bottomSheet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfo
import com.tamin.taminhamrah.databinding.WorkshopActionBottomsheetBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.debt.InstallmentDebtFragment.Companion.WORK_SHOP_TAG
import com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel.InstallmentDebtViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopActionBottomSheet private constructor(var mListener: WorkshopActionListener?) :
    BaseBottomSheetDialogFragment<WorkshopActionBottomsheetBinding, InstallmentDebtViewModel>() {
    companion object {

        fun getInstance(
            mListener: WorkshopActionListener? = null,
            bundle: Bundle?
        ): WorkshopActionBottomSheet {
            val dialog = WorkshopActionBottomSheet(mListener)
            dialog.arguments = bundle
            return dialog
        }
    }

    override val mViewModelDialog: InstallmentDebtViewModel by viewModels()

    override fun getLayoutId() = R.layout.workshop_action_bottomsheet

    fun setListener(listener: WorkshopActionListener) {
        mListener = listener
    }

    private val workshopItem by lazy {
        arguments?.getSerializable(WORK_SHOP_TAG) as WorkshopInfo
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
    }

    private fun onClick() {
        viewBinding?.apply {
            tvShowDebt.setOnClickListener(onClickListener)
            //tvFollowRequest.setOnClickListener(onClickListener)
        }
    }

    val onClickListener = View.OnClickListener {
        viewBinding?.apply {
            if (it.id == tvShowDebt.id)
                mListener?.onShowDebtClick(workshopItem)
            else
                mListener?.onFollowRequestClick(workshopItem)
            dismiss()
        }
    }

    interface WorkshopActionListener {
        fun onShowDebtClick(item: WorkshopInfo)
        fun onFollowRequestClick(item: WorkshopInfo)
    }

}