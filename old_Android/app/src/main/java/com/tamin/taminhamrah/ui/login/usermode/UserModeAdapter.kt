package com.tamin.taminhamrah.ui.login.usermode

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.ListItemUserModeBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class UserModeAdapter(private val mItems: List<MenuModel>, private val mListener: AdapterInterface.OnItemClickListener<MenuModel>?=null) :
    RecyclerView.Adapter<UserModeAdapter.ItemViewHolder>() {
    private var userAvatarUrl = ""

    /*fun setItems() {
        mItems = items
        notifyItemRangeChanged(0,mItems.size)
    }*/

    fun setAvatar(userAvatarUrl: String) {

        this.userAvatarUrl = userAvatarUrl
        notifyItemRangeChanged(0, mItems.size)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            ListItemUserModeBinding.inflate(inflater, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.item = item

       // ImageUtils.loadUserAvatar(holder.binding.imgProfile, userAvatarUrl)
        holder.itemView.setOnClickListener {
            for (i in mItems) {
                if (i.isSelected) i.isSelected = false
            }
            item.isSelected = true

            mListener?.onItemClick(item)

            notifyItemRangeChanged(0, mItems.size)

        }

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun hasSelectedItem(): Boolean {
        for (item in mItems) {
            if (item.isSelected) return true
        }
        return false
    }

    fun getSelectedItem(): MenuModel? {
        for (item in mItems)
            if (item.isSelected) return item
        return null
    }

    class ItemViewHolder(var binding: ListItemUserModeBinding) :
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
            return mOldList[oldItemPosition].title == mNewList[newItemPosition].title
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }
}
