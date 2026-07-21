package com.tamin.taminhamrah.ui.home.services.accountlist

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.BankAccount
import com.tamin.taminhamrah.databinding.ListItemBankAccountBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class BankAccountAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<BankAccount>? = null) :
    BasePagingAdapter<BankAccount, ListItemBankAccountBinding>(DiffUtilCallBack) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_bank_account
    }

    override fun bindItem(binding: ListItemBankAccountBinding, item: BankAccount?, position: Int) {
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

            recycler.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    override fun initViewHolder(binding: ListItemBankAccountBinding, itemView: View) {
        /* binding.btnShowDetail.setOnClickListener {
             if (binding.groupDetails.visibility == View.GONE) {
                 binding.groupDetails.visibility = View.VISIBLE
                 binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.label_hide_detail)
             } else {
                 binding.groupDetails.visibility = View.GONE
                 binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.label_show_detail)
             }

             itemView.requestFocus()
         }*/
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<BankAccount>() {
        override fun areItemsTheSame(
            oldItem: BankAccount,
            newItem: BankAccount
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: BankAccount,
            newItem: BankAccount
        ): Boolean {
            return oldItem.equals(newItem)
        }
    }
}