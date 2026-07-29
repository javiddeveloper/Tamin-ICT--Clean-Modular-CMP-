package com.tamin.taminhamrah.ui.home.services

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.databinding.ListItemKeyValueBinding

class KeyValueAdapter :
    RecyclerView.Adapter<KeyValueAdapter.ItemViewHolder>() {

    private var mItems = emptyList<KeyValueModel>()

    fun setItems(items: List<KeyValueModel>) {

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
        return ItemViewHolder(
            ListItemKeyValueBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        val context = holder.binding.root.context
        if (item._keyStringResId!=0)
            item._key = context.getString(item._keyStringResId)
        if (item._valueStringResId!=0)
            item._value = context.getString(item._valueStringResId)
        holder.binding.item = item

        if(item._hasBackground){
            holder.itemView.background = AppCompatResources.getDrawable(holder.itemView.context, R.drawable.bg_round_top_corners)
        }

        if (item._isKeyBold) {
            holder.binding.tvKey.setTypeface(holder.binding.tvKey.typeface, Typeface.BOLD)
        }
        if (item._isValueBold) {
            holder.binding.tvValue.setTypeface(holder.binding.tvValue.typeface, Typeface.BOLD)
        }

        if (item._textColor==EnumTextColor.NORMAL){
                ColorStateList.valueOf(
                MaterialColors.getColor(
                    holder.binding.tvValue,
                    androidx.appcompat.R.attr.colorPrimary
            ))
        }else{
            holder.binding.tvValue.setTextColor(ContextCompat.getColor(context, item._textColor.colorRes))
        }

        if (item._key.isBlank() && item._keyStringResId != 0)
            item._key = context.getString(item._keyStringResId)

        holder.itemView.setOnClickListener {
//            onItemClickListener?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemKeyValueBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<KeyValueModel>,
        private val mNewList: List<KeyValueModel>
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
