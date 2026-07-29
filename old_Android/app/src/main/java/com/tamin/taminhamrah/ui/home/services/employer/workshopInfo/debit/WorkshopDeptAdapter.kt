package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt
import com.tamin.taminhamrah.databinding.ListItemWorkshopDebtBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class WorkshopDeptAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkShopDebt>? = null) :
    BasePagingAdapter<WorkShopDebt, ListItemWorkshopDebtBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_debt
    }

    override fun bindItem(
        binding: ListItemWorkshopDebtBinding,
        item: WorkShopDebt?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            binding.recyclerMain.apply {
                val itemAdapter = KeyValueAdapter()
                adapter = itemAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(this.context))
                }

                itemAdapter.setItems(item.createKeyValueMain(item))
            }

            binding.recyclerChild.apply {
                val itemAdapter = KeyValueAdapter()
                adapter = itemAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(this.context))
                }

                itemAdapter.setItems(item.createKeyValueChild(item))
            }

            binding.btnShowDetail.setOnClickListener {
                if (binding.groupChild.visibility == View.GONE) {
                    binding.groupChild.visibility = View.VISIBLE
                    binding.btnShowDetail.text =
                        binding.btnShowDetail.context.getString(R.string.hide_detail)
                } else {
                    binding.groupChild.visibility = View.GONE
                    binding.btnShowDetail.text =
                        binding.btnShowDetail.context.getString(R.string.show_detail)
                }
            }

            binding.btnAction.apply {
                text = context.getString(R.string.label_show_demand_documents)
                setOnClickListener {
                    onItemClickListener?.onItemClick(item, tag = text.toString())
                }
            }

            binding.btnPayDebit.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = binding.btnPayDebit.text.toString())
            }
        }
    }

    override fun initViewHolder(binding: ListItemWorkshopDebtBinding, itemView: View) {

        /* binding.btnShowDetail.setOnClickListener {
             if (binding.groupDetails.visibility == View.GONE) {
                 binding.groupDetails.visibility = View.VISIBLE
                 binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.label_hide_detail)
             } else {
                 binding.groupDetails.visibility = View.GONE
                 binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.label_show_detail)
             }

             itemView.requestFocus()
         }*/
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkShopDebt>() {
        override fun areItemsTheSame(
            oldItem: WorkShopDebt,
            newItem: WorkShopDebt
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkShopDebt,
            newItem: WorkShopDebt
        ): Boolean {
            return oldItem == newItem
        }
    }
}
