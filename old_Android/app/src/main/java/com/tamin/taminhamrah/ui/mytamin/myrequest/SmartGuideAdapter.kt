package com.tamin.taminhamrah.ui.mytamin.myrequest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.user.SmartGuideItem
import com.tamin.taminhamrah.databinding.ListItemSmartGuideBinding

class SmartGuideAdapter :
    RecyclerView.Adapter<SmartGuideAdapter.ItemViewHolder>() {

    /*  var onItemClickListener: AdapterInterface.OnItemClickListener<SmartGuideItem>? = null
  */
    private var mItems = emptyList<SmartGuideItem>()
    fun setItems(
        items: List<SmartGuideItem>/*,
        clickListener: AdapterInterface.OnItemClickListener<SmartGuideItem>?*/
    ) {
//        this.onItemClickListener = clickListener
        mItems = items
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            ListItemSmartGuideBinding.inflate(inflater, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.item = item

        /* holder.itemView.setOnClickListener {
             onItemClickListener?.onItemClick(item)
         }*/

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemSmartGuideBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<SmartGuideItem>,
        private val mNewList: List<SmartGuideItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].id == mNewList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }
}
