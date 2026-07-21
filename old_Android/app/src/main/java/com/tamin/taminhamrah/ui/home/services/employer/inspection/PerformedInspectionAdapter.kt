package com.tamin.taminhamrah.ui.home.services.employer.inspection

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.InspectionPerformedModel
import com.tamin.taminhamrah.databinding.ListItemPerfomredInspectionBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils


class PerformedInspectionAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<InspectionPerformedModel?>) :
    BasePagingAdapter<InspectionPerformedModel, ListItemPerfomredInspectionBinding>(DiffCallback) {


    override fun getLayoutResId() = R.layout.list_item_perfomred_inspection

    override fun bindItem(
        binding: ListItemPerfomredInspectionBinding,
        item: InspectionPerformedModel?,
        position: Int
    ) {
        binding.apply {
            recycler.apply {
                val itemAdapter = KeyValueAdapter()
                adapter = itemAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(this.context))
                }
                itemAdapter.setItems(item!!.createKeyValue(item))
            }

            btnSeeDetail.setOnClickListener {
                onItemClickListener.onItemClick(item, tag = btnSeeDetail.text.toString())
            }
            if (item?.objectable == "1") {
                btnObjection.visibility = View.VISIBLE
                btnObjection.setOnClickListener {
                    onItemClickListener.onItemClick(item, tag = btnObjection.text.toString())
                }
            }

        }
    }

    override fun initViewHolder(binding: ListItemPerfomredInspectionBinding, itemView: View) {
        itemView.requestFocus()
    }

    object DiffCallback : DiffUtil.ItemCallback<InspectionPerformedModel>() {
        override fun areItemsTheSame(
            oldItem: InspectionPerformedModel,
            newItem: InspectionPerformedModel
        ): Boolean {
            return oldItem.inspectionNo == newItem.inspectionNo
        }

        override fun areContentsTheSame(
            oldItem: InspectionPerformedModel,
            newItem: InspectionPerformedModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}


