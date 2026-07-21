package com.tamin.taminhamrah.ui.home.services.viewEdictPensioner.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.databinding.ListItemEdictInfoBinding

class EdictRowAdapter : RecyclerView.Adapter<EdictRowAdapter.ItemViewHolder>() {

    private lateinit var binding: ListItemEdictInfoBinding

    private var mItems = ArrayList<KeyValueModel>()
    fun setItems(items : List<KeyValueModel>){
        val diffResult = DiffUtil.calculateDiff(DiffCallback(mItems, items), true)
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EdictRowAdapter.ItemViewHolder {
        binding =
            ListItemEdictInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder()
    }

    override fun onBindViewHolder(holder: EdictRowAdapter.ItemViewHolder, position: Int) {
        val item = mItems[position]
        val context = holder.itemView.context

        if (item._keyStringResId!=0)
            item._key = context.getString(item._keyStringResId)
        if (item._valueStringResId!=0)
            item._value = context.getString(item._valueStringResId)
        binding.apply {
            this.item = item
            if (item._keyStringResId == R.string.org_history || item._keyStringResId == R.string.additional_history) {
                tvValue.text =
                    HtmlCompat.fromHtml(item._value, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                tvValue.text = item._value
            }

            if (item._isKeyBold) {
                tvKey.setTypeface(tvKey.typeface, Typeface.BOLD)
            }
            if (item._isValueBold) {
                tvValue.setTypeface(tvValue.typeface, Typeface.BOLD)
            }
        }
    }

    override fun getItemCount() = mItems.size

    inner class ItemViewHolder : RecyclerView.ViewHolder(binding.root)

    class DiffCallback(
        private val mOldList: List<KeyValueModel>,
        private val mNewList: List<KeyValueModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize()= mOldList.size

        override fun getNewListSize()= mNewList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)=
            mOldList[oldItemPosition]._key == mNewList[newItemPosition]._key


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }

}