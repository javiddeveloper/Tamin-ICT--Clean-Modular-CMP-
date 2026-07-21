package com.tamin.taminhamrah.ui.home.services.employer.contract.computationalBase

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBase
import com.tamin.taminhamrah.databinding.ListItemComputationalBaseBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class ComputationalBaseInfoAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<ComputationalBase>? = null) :
    BasePagingAdapter<ComputationalBase, ListItemComputationalBaseBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_computational_base
    }

    override fun bindItem(
        binding: ListItemComputationalBaseBinding,
        item: ComputationalBase?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            val itemAdapter = KeyValueAdapter()
            recycler.apply {
                adapter = itemAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(this.context))
                }
            }

            itemAdapter.setItems(item.createKeyValue(item))

            btnShowDetail.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnShowDetail.text.toString())
            }

        }
    }

    override fun initViewHolder(binding: ListItemComputationalBaseBinding, itemView: View) {

       /* binding.btnShowDetail.setOnClickListener {
            if (binding.groupShowMoreInfo.visibility == View.GONE) {
                binding.groupShowMoreInfo.visibility = View.VISIBLE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.label_hide_detail)
            } else {
                binding.groupShowMoreInfo.visibility = View.GONE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.label_show_detail)
            }
        }*/

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<ComputationalBase>() {
        override fun areItemsTheSame(
            oldItem: ComputationalBase,
            newItem: ComputationalBase
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ComputationalBase,
            newItem: ComputationalBase
        ): Boolean {
            return oldItem.equals(newItem)
        }
    }
}
