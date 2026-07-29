package com.tamin.taminhamrah.ui.home.services.employer.contract.clause38

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.contract.Clause38Info
import com.tamin.taminhamrah.databinding.ListItemClause38Binding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class Clause38Adapter(var onItemClickListener: AdapterInterface.OnItemClickListener<Clause38Info>? = null) :
    BasePagingAdapter<Clause38Info, ListItemClause38Binding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_clause_38
    }



    override fun bindItem(
        binding: ListItemClause38Binding,
        item: Clause38Info?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            btnShowDetail.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnShowDetail.text.toString())
            }

        }
    }

    override fun initViewHolder(binding: ListItemClause38Binding, itemView: View) {

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<Clause38Info>() {
        override fun areItemsTheSame(
            oldItem: Clause38Info,
            newItem: Clause38Info
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Clause38Info,
            newItem: Clause38Info
        ): Boolean {
            return oldItem.contractRow == newItem.contractRow && oldItem.workshopId==newItem.workshopId
        }
    }
}
