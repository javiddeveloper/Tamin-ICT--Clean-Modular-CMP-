package com.tamin.taminhamrah.ui.home.services

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.databinding.ListItemPagingKeyValueBinding
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils

class KeyValueAdapterPaging : BasePagingAdapter<List<KeyValueModel>,ListItemPagingKeyValueBinding>(DiffUtilCallBack) {

    override fun getLayoutResId() = R.layout.list_item_paging_key_value

    override fun initViewHolder(binding: ListItemPagingKeyValueBinding, itemView: View) {
    }

    override fun bindItem(binding: ListItemPagingKeyValueBinding, items: List<KeyValueModel>?, position: Int) {
        val context = binding.root.context
        val adapter = KeyValueAdapter()
        binding.recycler.apply {
            this.adapter = adapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(context))
        }

        items?.let { adapter.setItems(it) }
    }


    object DiffUtilCallBack : DiffUtil.ItemCallback<List<KeyValueModel>>() {
        override fun areItemsTheSame(
            oldItem: List<KeyValueModel>,
            newItem: List<KeyValueModel>
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: List<KeyValueModel>,
            newItem: List<KeyValueModel>
        ): Boolean {
            return oldItem == newItem
        }
    }


}
