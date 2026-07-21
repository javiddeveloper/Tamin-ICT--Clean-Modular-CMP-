package com.tamin.taminhamrah.ui.treatment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.PrescriptionItemBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class PrescriptionAdapter : RecyclerView.Adapter<PrescriptionAdapter.ItemViewHolder>() {
    private var onChildItemClickListener: AdapterInterface.OnItemClickListener<MenuModel>? = null
    private var mItems: MutableList<MenuModel> = ArrayList()

    fun setItems(
        items: List<MenuModel>,
        childClickListener: AdapterInterface.OnItemClickListener<MenuModel>?
    ) {

        this.onChildItemClickListener = childClickListener
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ItemViewHolder(val binding: PrescriptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =

        ItemViewHolder(
            PrescriptionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.apply {
            container.setOnClickListener {
                onChildItemClickListener?.onItemClick(
                    item,
                    imgIcon
                )
            }
            imgIcon.setImageDrawable(imgIcon.context.getDrawable(item.iconRes))
            txtTitle.text = this.txtTitle.context.getString(item.titleStringResId)

        }
    }

    override fun getItemCount() = mItems.size

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