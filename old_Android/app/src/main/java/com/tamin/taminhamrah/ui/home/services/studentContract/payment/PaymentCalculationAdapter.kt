package com.tamin.taminhamrah.ui.home.services.studentContract.payment

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.CalculationModel
import com.tamin.taminhamrah.databinding.ListItemPaymentCalculationBinding
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class PaymentCalculationAdapter :
    BasePagingAdapter<CalculationModel, ListItemPaymentCalculationBinding
            >(DiffUtilCallBack) {


    object DiffUtilCallBack : DiffUtil.ItemCallback<CalculationModel>() {
        override fun areItemsTheSame(
            oldItem: CalculationModel,
            newItem: CalculationModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CalculationModel,
            newItem: CalculationModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.list_item_payment_calculation

    }

    override fun bindItem(
        binding: ListItemPaymentCalculationBinding,
        item: CalculationModel?,
        position: Int
    ) {
        binding.item = item
    }

    override fun initViewHolder(binding: ListItemPaymentCalculationBinding, itemView: View) {
    }
}