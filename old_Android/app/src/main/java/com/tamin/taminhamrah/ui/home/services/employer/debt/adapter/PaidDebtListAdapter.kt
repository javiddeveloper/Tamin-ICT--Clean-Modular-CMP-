package com.tamin.taminhamrah.ui.home.services.employer.debt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.employer.debit.DebtPaidListModel
import com.tamin.taminhamrah.databinding.ListItemDebtPaidBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class PaidDebtListAdapter():RecyclerView.Adapter<PaidDebtListAdapter.ItemViewHolder>() {

    var onItemClickListener: AdapterInterface.OnItemClickListener<DebtPaidListModel>? = null
    private var mItems = emptyList<DebtPaidListModel>()

    fun setItems(
        items: List<DebtPaidListModel>?,
        clickListener: AdapterInterface.OnItemClickListener<DebtPaidListModel>
    ) {
        this.onItemClickListener = clickListener
        mItems = items?: emptyList()
        notifyItemRangeChanged(0, mItems.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            ListItemDebtPaidBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.apply {
            btnPaymentReceipt.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
            val adapter = KeyValueAdapter()
            recycler.apply {
                this.adapter = adapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(context))
            }
          adapter.setItems(item.getKeyValueModel())
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class DiffCallback(
        private val mOldList: List<DebtPaidListModel>,
        private val mNewList: List<DebtPaidListModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].debitSubCode == mNewList[newItemPosition].debitSubCode
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }
    }


    class ItemViewHolder(var binding: ListItemDebtPaidBinding) :
        RecyclerView.ViewHolder(binding.root)
}