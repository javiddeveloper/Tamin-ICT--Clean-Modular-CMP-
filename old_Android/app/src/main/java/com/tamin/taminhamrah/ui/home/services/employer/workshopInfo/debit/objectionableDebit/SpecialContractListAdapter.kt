package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit.objectionableDebit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.WorkshopInfoModel
import com.tamin.taminhamrah.databinding.ListItemSpecialContractBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class SpecialContractListAdapter : RecyclerView.Adapter<SpecialContractListAdapter.ItemViewHolder>() {
    var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopInfoModel>? = null

    private var mItems = emptyList<WorkshopInfoModel>()

    fun setItems(
        items: List<WorkshopInfoModel>,
        listener: AdapterInterface.OnItemClickListener<WorkshopInfoModel>

    ) {
        onItemClickListener = listener
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemSpecialContractBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.item = item
        holder.binding.btnDebit.setOnClickListener {
            onItemClickListener?.onItemClick(item,tag = holder.binding.btnDebit.text.toString() )
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemSpecialContractBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DiffCallback(
        private val mOldList: List<WorkshopInfoModel>,
        private val mNewList: List<WorkshopInfoModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].toString() == mNewList[newItemPosition].toString()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition].toString()
            val newItem = mNewList[newItemPosition].toString()
            return (newItem == oldItem)
        }
    }
}
