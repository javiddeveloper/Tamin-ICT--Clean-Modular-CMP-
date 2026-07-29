package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDemandDoc
import com.tamin.taminhamrah.databinding.ListItemWorkshopDemandDocBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class WorkshopDemandDocsAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkShopDemandDoc>? = null) :
    BasePagingAdapter<WorkShopDemandDoc, ListItemWorkshopDemandDocBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_demand_doc
    }

    override fun bindItem(
        binding: ListItemWorkshopDemandDocBinding,
        item: WorkShopDemandDoc?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            binding.recycler.apply {
                val itemAdapter = KeyValueAdapter()
                adapter = itemAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(this.context))
                }

                itemAdapter.setItems(item.createKeyValue(item))
            }

            binding.btnDebitReason.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = binding.btnDebitReason.text.toString())
            }

            binding.btnShowDetails.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = binding.btnShowDetails.text.toString())
            }
        }
    }

    override fun initViewHolder(binding: ListItemWorkshopDemandDocBinding, itemView: View) {

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

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkShopDemandDoc>() {
        override fun areItemsTheSame(
            oldItem: WorkShopDemandDoc,
            newItem: WorkShopDemandDoc
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkShopDemandDoc,
            newItem: WorkShopDemandDoc
        ): Boolean {
            return oldItem == newItem
        }
    }
}
