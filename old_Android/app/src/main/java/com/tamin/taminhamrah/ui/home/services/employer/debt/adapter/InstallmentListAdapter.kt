package com.tamin.taminhamrah.ui.home.services.employer.debt.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtModel
import com.tamin.taminhamrah.databinding.DebtItemBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class InstallmentListAdapter(private val onActionClickListener: AdapterInterface.OnItemClickListener<WorkShopDebtModel>? = null) :
    BasePagingAdapter<WorkShopDebtModel, DebtItemBinding>(DiffUtilCallBack) {

    override fun getLayoutResId() = R.layout.debt_item

    override fun bindItem(
        binding: DebtItemBinding,
        item: WorkShopDebtModel?,
        position: Int
    ) {
        with(binding) {
            this.item = item
            item?.apply {
                btnActions.setOnClickListener {
                    onActionClickListener?.onItemClick(item)
                }
            }

        }
    }

    override fun initViewHolder(binding: DebtItemBinding, itemView: View) {

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkShopDebtModel>() {
        override fun areItemsTheSame(
            oldItem: WorkShopDebtModel,
            newItem: WorkShopDebtModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkShopDebtModel,
            newItem: WorkShopDebtModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}