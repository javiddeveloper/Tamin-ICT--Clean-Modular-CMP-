package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.databinding.ItemListOvalBubbleBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class HistoryEditedAdapter : RecyclerView.Adapter<HistoryEditedAdapter.ItemViewHolder>() {

    var deleteListener: AdapterInterface.OnDeleteClickListener<String>? = null

    private var mItems = emptyList<String>()
    fun setItems(
        items: List<String>
    ) {
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: String) {
        mItems = mItems.filterNot { it == item }
        notifyDataSetChanged()
    }

    fun setListener(listener: AdapterInterface.OnDeleteClickListener<String>? = null) {
        deleteListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemListOvalBubbleBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.apply {
            tvValue.text = item
            btnClose.setOnClickListener {
                deleteListener?.onDelete(item)
            }
        }
    }


    override fun getItemCount() = mItems.size

    class ItemViewHolder(var binding: ItemListOvalBubbleBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<String>,
        private val mNewList: List<String>
    ) : DiffUtil.Callback() {

        override fun getOldListSize()= mOldList.size

        override fun getNewListSize()= mNewList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)=
            mOldList[oldItemPosition] == mNewList[newItemPosition]


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]
            return (newItem == oldItem)
        }
    }


}
