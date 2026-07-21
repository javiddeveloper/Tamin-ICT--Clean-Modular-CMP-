package com.tamin.taminhamrah.ui.home.services.employer.contract

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfoNew
import com.tamin.taminhamrah.databinding.ListItemContractInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class ContractInfoAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<ContractInfoNew>? = null) :
    BasePagingAdapter<ContractInfoNew, ListItemContractInfoBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_contract_info
    }

    override fun bindItem(
        binding: ListItemContractInfoBinding,
        item: ContractInfoNew?,
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

            btnShowDetail.visibility = View.VISIBLE

            itemAdapter.setItems(item.createKeyValue(item))

            val itemAdapterMore = KeyValueAdapter()
            recyclerMoreInfo.apply{
                adapter = itemAdapterMore
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(this.context))
                }
            }
            itemAdapterMore.setItems(item.createKeyValueDetails(item))

            btnActions.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnActions.text.toString())
            }

        }
    }

    override fun initViewHolder(binding: ListItemContractInfoBinding, itemView: View) {

        binding.btnShowDetail.setOnClickListener {
            if (binding.groupShowMoreInfo.visibility == View.GONE) {
                binding.groupShowMoreInfo.visibility = View.VISIBLE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.hide_detail)
            } else {
                binding.groupShowMoreInfo.visibility = View.GONE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.show_detail)
            }
        }

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<ContractInfoNew>() {
        override fun areItemsTheSame(
            oldItem: ContractInfoNew,
            newItem: ContractInfoNew
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ContractInfoNew,
            newItem: ContractInfoNew
        ): Boolean {
            return oldItem.contractRow == newItem.contractRow
        }
    }
}
