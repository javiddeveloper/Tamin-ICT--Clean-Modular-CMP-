package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.membersAndStachholders

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.WorkshopMemberModel
import com.tamin.taminhamrah.databinding.ListItemWorkshopMemberBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class WorkshopMemberAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopMemberModel>? = null) :
    BasePagingAdapter<WorkshopMemberModel, ListItemWorkshopMemberBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_member
    }

    override fun bindItem(
        binding: ListItemWorkshopMemberBinding,
        item: WorkshopMemberModel?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return
        }
    }

    override fun initViewHolder(binding: ListItemWorkshopMemberBinding, itemView: View) {

        binding.btnShowDetail.setOnClickListener {
            if (binding.groupVisit.visibility == View.GONE) {
                binding.groupVisit.visibility = View.VISIBLE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.hide_detail)

            } else {
                binding.groupVisit.visibility = View.GONE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.show_detail)
            }

            itemView.requestFocus()
            //onItemClickListener?.onItemClick(item)
        }
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkshopMemberModel>() {
        override fun areItemsTheSame(
            oldItem: WorkshopMemberModel,
            newItem: WorkshopMemberModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkshopMemberModel,
            newItem: WorkshopMemberModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}