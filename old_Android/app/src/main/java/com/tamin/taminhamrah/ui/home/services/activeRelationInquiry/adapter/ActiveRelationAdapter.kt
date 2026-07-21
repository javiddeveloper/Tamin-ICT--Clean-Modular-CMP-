package com.tamin.taminhamrah.ui.home.services.activeRelationInquiry.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelation
import com.tamin.taminhamrah.databinding.ListItemActiveRelationBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class ActiveRelationAdapter (private var onItemClickListener: AdapterInterface.OnItemClickListener<ActiveRelation>? = null) :
    BasePagingAdapter<ActiveRelation, ListItemActiveRelationBinding>(DiffUtilCallBack) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_active_relation
    }

    override fun bindItem(
        binding: ListItemActiveRelationBinding,
        item: ActiveRelation?,
        position: Int
    ) {

        with(binding) {
            this.item = item
            layoutAnimation.rootLayout
            if (item?.hasRelation() == true) {
                btnCertificate.setOnClickListener {
                    onItemClickListener?.onItemClick(item)
                }
            }
        }
    }

    override fun initViewHolder(binding: ListItemActiveRelationBinding, itemView: View) {
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<ActiveRelation>() {
        override fun areItemsTheSame(
            oldItem: ActiveRelation,
            newItem: ActiveRelation
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ActiveRelation,
            newItem: ActiveRelation
        ): Boolean {
            return oldItem == newItem
        }
    }


}