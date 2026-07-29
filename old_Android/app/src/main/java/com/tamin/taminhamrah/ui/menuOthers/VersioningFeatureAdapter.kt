package com.tamin.taminhamrah.ui.menuOthers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.databinding.ListItemVersioningFeatureBinding

class VersioningFeatureAdapter :
    RecyclerView.Adapter<VersioningFeatureAdapter.ItemViewHolder>() {

    private val mItems :MutableList<String> by lazy { ArrayList() }
    fun setItems(
        items: List<String>
    ) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            ListItemVersioningFeatureBinding.inflate(inflater, parent, false)
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.tvTitle.text = item
        holder.binding.tvIndex.text = ".${position + 1} "


    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemVersioningFeatureBinding) :
        RecyclerView.ViewHolder(binding.root)


//    inner class DiffCallback(
//        private val mOldList: List<String>,
//        private val mNewList: List<String>
//    ) : DiffUtil.Callback() {
//
//        override fun getOldListSize(): Int {
//            return mOldList.size
//        }
//
//        override fun getNewListSize(): Int {
//            return mNewList.size
//        }
//
//        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            return mOldList[oldItemPosition].title == mNewList[newItemPosition].title
//        }
//
//        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            val oldItem = mOldList[oldItemPosition]
//            val newItem = mNewList[newItemPosition]
//
//            return (newItem == oldItem)
//        }
//
//    }
}
