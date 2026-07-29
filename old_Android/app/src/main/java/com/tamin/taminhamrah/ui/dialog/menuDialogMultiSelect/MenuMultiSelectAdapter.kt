package com.tamin.taminhamrah.ui.dialog.menuDialogMultiSelect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.ListItemMenuMultiSelectServiceBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class MenuMultiSelectAdapter:
    RecyclerView.Adapter<MenuMultiSelectAdapter.ItemViewHolder>() {

    var onItemClickListener: AdapterInterface.OnItemClickListener<MenuModel>? = null

    private var mItems = emptyList<MenuModel>()
    fun setItems(
        items: List<MenuModel>,
        clickListener: AdapterInterface.OnItemClickListener<MenuModel>?
    ) {
        this.onItemClickListener = clickListener
        mItems = items
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            ListItemMenuMultiSelectServiceBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.item = item

        holder.binding.appCompatCheckBox.setOnClickListener({
            onItemClickListener?.onItemClick(item)
        })

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemMenuMultiSelectServiceBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<MenuModel>,
        private val mNewList: List<MenuModel>
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
