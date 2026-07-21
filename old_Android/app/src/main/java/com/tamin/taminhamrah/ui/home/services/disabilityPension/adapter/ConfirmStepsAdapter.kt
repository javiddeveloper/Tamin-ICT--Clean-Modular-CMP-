package com.tamin.taminhamrah.ui.home.services.disabilityPension.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.ItemConfirmStepBinding

class ConfirmStepsAdapter : RecyclerView.Adapter<ConfirmStepsAdapter.ItemViewHolder>(){
    private lateinit var binding : ItemConfirmStepBinding
    inner class ItemViewHolder : RecyclerView.ViewHolder(binding.root)
    private val mItems = ArrayList<MenuModel>()
    lateinit var mContext : Context
    fun setItems(items: List<MenuModel>){
        val diffResult = DiffUtil.calculateDiff(DiffCallBack(mItems, items), true)
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        mContext = parent.context
        binding = ItemConfirmStepBinding.inflate(LayoutInflater.from(mContext),
        parent,
        false)
        return ItemViewHolder()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        binding.apply {
            val colorRes = if (item.isSelected) R.color.green else R.color.gray
            imgCheck.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext,colorRes))
            if (item.title.isNullOrBlank() && item.titleStringResId>0)
            item.title = mContext.getString(item.titleStringResId)
            tvTitle.text = item.title
        }
    }

    override fun getItemCount() = mItems.size

    class DiffCallBack(
        private val mOldList: List<MenuModel>,
        private val mNewList: List<MenuModel>,
    ):DiffUtil.Callback(){
        override fun getOldListSize()= mOldList.size

        override fun getNewListSize()= mNewList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            mOldList[oldItemPosition].title == mNewList[newItemPosition].title

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            mOldList[oldItemPosition] == mNewList[newItemPosition]
    }
}