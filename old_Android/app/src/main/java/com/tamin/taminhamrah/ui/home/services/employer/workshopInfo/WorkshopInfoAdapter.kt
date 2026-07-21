package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfo
import com.tamin.taminhamrah.databinding.ListItemWorkshopInfoDebtBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class WorkshopInfoAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopInfo>? = null) :
    BasePagingAdapter<WorkshopInfo, ListItemWorkshopInfoDebtBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId() = R.layout.list_item_workshop_info_debt


    override fun bindItem(
        binding: ListItemWorkshopInfoDebtBinding,
        item: WorkshopInfo?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return
            binding.btnRegisterDebt.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    override fun initViewHolder(binding: ListItemWorkshopInfoDebtBinding, itemView: View) {

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkshopInfo>() {
        override fun areItemsTheSame(
            oldItem: WorkshopInfo,
            newItem: WorkshopInfo
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkshopInfo,
            newItem: WorkshopInfo
        ): Boolean {
            return oldItem == newItem
        }
    }


}
