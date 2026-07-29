package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerWorkshop
import com.tamin.taminhamrah.databinding.ListItemEmployerWorkshopBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class EmployerWorkshopAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<EmployerWorkshop>? = null) :
    BasePagingAdapter<EmployerWorkshop, ListItemEmployerWorkshopBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_employer_workshop
    }

    override fun bindItem(
        binding: ListItemEmployerWorkshopBinding,
        item: EmployerWorkshop?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

        }
    }

    override fun initViewHolder(binding: ListItemEmployerWorkshopBinding, itemView: View) {

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<EmployerWorkshop>() {
        override fun areItemsTheSame(
            oldItem: EmployerWorkshop,
            newItem: EmployerWorkshop
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: EmployerWorkshop,
            newItem: EmployerWorkshop
        ): Boolean {
            return oldItem == newItem
        }
    }
}