package com.tamin.taminhamrah.ui.home.services.employer.debt.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt
import com.tamin.taminhamrah.databinding.ListItemDebtInfoBinding
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class DebtInfoAdapter(
    var checkedItems: MutableSet<WorkShopDebt> = mutableSetOf(),
    var workshopDebtListener: WorkshopDebtListener? = null
) : BasePagingAdapter<WorkShopDebt, ListItemDebtInfoBinding>(DiffUtilCall) {

    override fun bindItem(binding: ListItemDebtInfoBinding, item: WorkShopDebt?, position: Int) {
        with(binding) {
            this.item = item ?: return

            cbSelectDebt.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    checkedItems.add(item)
                else
                    checkedItems.remove(item)

                workshopDebtListener?.onCheckedChange(item, isChecked)
            }

            btnShowDetail.setOnClickListener {
                if (group.visibility == View.GONE) {
                    group.visibility = View.VISIBLE
                    btnShowDetail.text =
                        binding.btnShowDetail.context.getString(R.string.hide_detail)
                } else {
                    group.visibility = View.GONE
                    btnShowDetail.text =
                        binding.btnShowDetail.context.getString(R.string.show_detail)
                }
            }
        }
    }

    fun getSelectedItems() = checkedItems

    override fun initViewHolder(binding: ListItemDebtInfoBinding, itemView: View) {}

    object DiffUtilCall : DiffUtil.ItemCallback<WorkShopDebt>() {
        override fun areItemsTheSame(
            oldItem: WorkShopDebt,
            newItem: WorkShopDebt
        ): Boolean { return oldItem == newItem }

        override fun areContentsTheSame(
            oldItem: WorkShopDebt,
            newItem: WorkShopDebt
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun getLayoutResId() = R.layout.list_item_debt_info
    interface WorkshopDebtListener {
        fun onCheckedChange(workShopDebt: WorkShopDebt, checked: Boolean)
        fun onActionClick(workShopDebt: WorkShopDebt)
    }
}
