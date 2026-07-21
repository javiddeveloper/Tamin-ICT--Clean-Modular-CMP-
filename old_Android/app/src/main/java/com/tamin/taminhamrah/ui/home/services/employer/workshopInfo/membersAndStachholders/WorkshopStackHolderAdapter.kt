package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.membersAndStachholders

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.WorkshopStackHolderModel
import com.tamin.taminhamrah.databinding.ListItemWorkshopStackholderBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class WorkshopStackHolderAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopStackHolderModel>? = null) :
    BasePagingAdapter<WorkshopStackHolderModel, ListItemWorkshopStackholderBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_stackholder
    }

    override fun bindItem(
        binding: ListItemWorkshopStackholderBinding,
        item: WorkshopStackHolderModel?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return
        }
    }

    override fun initViewHolder(binding: ListItemWorkshopStackholderBinding, itemView: View) {

       /* binding.btnShowDetail.setOnClickListener {
            if (binding.groupVisit.visibility == View.GONE) {
                binding.groupVisit.visibility = View.VISIBLE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.label_hide_detail)

            } else {
                binding.groupVisit.visibility = View.GONE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.label_show_detail)
            }

            itemView.requestFocus()
            //onItemClickListener?.onItemClick(item)
        }*/
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkshopStackHolderModel>() {
        override fun areItemsTheSame(
            oldItem: WorkshopStackHolderModel,
            newItem: WorkshopStackHolderModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkshopStackHolderModel,
            newItem: WorkshopStackHolderModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}



