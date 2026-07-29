package com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfo
import com.tamin.taminhamrah.databinding.ListItemAssignerContractInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class AssignerContractInfoAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<ContractInfo>? = null) :
    BasePagingAdapter<ContractInfo, ListItemAssignerContractInfoBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_assigner_contract_info
    }

    override fun bindItem(
        binding: ListItemAssignerContractInfoBinding,
        item: ContractInfo?,
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

            itemAdapter.setItems(item.createKeyValueAssigner(item))

            btnActions.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }


        }
    }

    override fun initViewHolder(binding: ListItemAssignerContractInfoBinding, itemView: View) {

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

    object DiffUtilCallBack : DiffUtil.ItemCallback<ContractInfo>() {
        override fun areItemsTheSame(
            oldItem: ContractInfo,
            newItem: ContractInfo
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ContractInfo,
            newItem: ContractInfo
        ): Boolean {
            return oldItem.contractRow == newItem.contractRow
        }
    }
}
