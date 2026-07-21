package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.WorkshopContract
import com.tamin.taminhamrah.databinding.ListItemWorkshopContractBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class WorkshopContractAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopContract>? = null) :
    BasePagingAdapter<WorkshopContract, ListItemWorkshopContractBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_contract
    }

    override fun bindItem(
        binding: ListItemWorkshopContractBinding,
        item: WorkshopContract?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

        }
    }

    override fun initViewHolder(binding: ListItemWorkshopContractBinding, itemView: View) {

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkshopContract>() {
        override fun areItemsTheSame(
            oldItem: WorkshopContract,
            newItem: WorkshopContract
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkshopContract,
            newItem: WorkshopContract
        ): Boolean {
            return oldItem == newItem
        }
    }
}