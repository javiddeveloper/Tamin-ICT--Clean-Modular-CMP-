package com.tamin.taminhamrah.ui.home.services.employer.protestStatus

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.SmsModel
import com.tamin.taminhamrah.databinding.ListItemSmsBinding
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class ObjectionSmsAdapter : BasePagingAdapter<SmsModel,ListItemSmsBinding>(DiffUtilCallBack) {



    object DiffUtilCallBack : DiffUtil.ItemCallback<SmsModel>() {
        override fun areItemsTheSame(
            oldItem: SmsModel,
            newItem: SmsModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SmsModel,
            newItem: SmsModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.list_item_sms

    }

    override fun bindItem(binding: ListItemSmsBinding, item: SmsModel?, position: Int) {
        binding.item=item
    }

    override fun initViewHolder(binding: ListItemSmsBinding, itemView: View) {
    }
}