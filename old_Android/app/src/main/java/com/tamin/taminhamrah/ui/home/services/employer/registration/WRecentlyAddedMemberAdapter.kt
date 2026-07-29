package com.tamin.taminhamrah.ui.home.services.employer.registration

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopNewMember
import com.tamin.taminhamrah.databinding.ListItemWorkshopNewMemberBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class WRecentlyAddedMemberAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopNewMember>? = null) :
    BasePagingAdapter<WorkshopNewMember, ListItemWorkshopNewMemberBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_new_member
    }

    override fun bindItem(
        binding: ListItemWorkshopNewMemberBinding,
        item: WorkshopNewMember?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            if (item.personal?.request?.status == null) {
                binding.btnConfirm.visibility = View.VISIBLE
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnFollow.visibility = View.GONE
            } else {
                binding.btnConfirm.visibility = View.GONE
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
                binding.btnFollow.visibility = View.VISIBLE
            }

            binding.btnConfirm.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = binding.btnConfirm.text.toString())
            }
            binding.btnEdit.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = binding.btnEdit.text.toString())
            }
            binding.btnDelete.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = binding.btnDelete.text.toString())
            }
            binding.btnFollow.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = binding.btnFollow.text.toString())
            }
        }
    }

    override fun initViewHolder(binding: ListItemWorkshopNewMemberBinding, itemView: View) {

        /*  binding.btnShowDetail.setOnClickListener {
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

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkshopNewMember>() {
        override fun areItemsTheSame(
            oldItem: WorkshopNewMember,
            newItem: WorkshopNewMember
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkshopNewMember,
            newItem: WorkshopNewMember
        ): Boolean {
            return oldItem == newItem
        }
    }
}