package com.tamin.taminhamrah.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.ListItemNavigatorBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import java.util.Collections.emptyList

class NavigatorAdapter : RecyclerView.Adapter<NavigatorAdapter.ItemViewHolder>() {

    var onItemClickListener: AdapterInterface.OnItemClickListener<MenuModel>? = null

    private var mItems = emptyList<MenuModel>()
    fun setItems(
        items: List<MenuModel>,
        clickListener: AdapterInterface.OnItemClickListener<MenuModel>?
    ) {
        this.onItemClickListener = clickListener
        mItems = items
        notifyItemRangeChanged(0, mItems.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            ListItemNavigatorBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
       // holder.binding.item = item
        val context = holder.binding.root.context
        holder.binding.tvTitle.text = item.title
        if (item.isNew) holder.binding.labelNew.visible()
        else holder.binding.labelNew.gone()

        if (item.iconRes == R.drawable.ic_exit_to_app) {
            holder.binding.icon.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.red
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        if (item.textColor.isNotEmpty())
            holder.binding.tvTitle.setTextColor(Color.parseColor(item.textColor))

        if (item.title.isNullOrBlank() && item.titleStringResId!=0){
          item.title = context.getString(item.titleStringResId)
        }
        if (item.description.isNullOrBlank() && item.descStringResId !=0){
            item.description = context.getString(item.descStringResId)
        }
        holder.binding.item = item

        ImageUtils.imageDrawable(holder.binding.icon, item.iconRes)

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemNavigatorBinding) :
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
