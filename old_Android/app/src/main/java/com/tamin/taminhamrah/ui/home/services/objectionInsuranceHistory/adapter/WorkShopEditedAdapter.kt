package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.databinding.ItemListOvalBubbleBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class WorkShopEditedAdapter : RecyclerView.Adapter<WorkShopEditedAdapter.ItemViewHolder>() {

    var onWorkShaopEditedListener: AdapterInterface.OnDeleteClickListener<String>? = null

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
    fun deleteItem(item : String) {
        mItems = mItems.filterNot { it == item }
        notifyDataSetChanged()
    }

    fun setListenr(onWorkShaopEditedListener: AdapterInterface.OnDeleteClickListener<String>? = null){
        this.onWorkShaopEditedListener = onWorkShaopEditedListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemListOvalBubbleBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = mItems[position]
        currentItem.let {
            holder.binding.tvValue.text = currentItem
            holder.binding.apply {
                btnClose.setOnClickListener {
                    onWorkShaopEditedListener?.onDelete(currentItem)
                }
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

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition] == mNewList[newItemPosition]
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]
            return (newItem == oldItem)
        }
    }


}
