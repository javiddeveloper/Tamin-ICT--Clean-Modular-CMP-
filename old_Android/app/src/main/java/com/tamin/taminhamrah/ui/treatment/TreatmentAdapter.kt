package com.tamin.taminhamrah.ui.treatment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.ServiceItem
import com.tamin.taminhamrah.databinding.DashboardItemBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.utils.ImageUtils
import timber.log.Timber

class TreatmentAdapter : RecyclerView.Adapter<TreatmentAdapter.ItemViewHolder>() {
    var onChildItemClickListener: AdapterInterface.OnItemClickListener<ServiceItem>? = null
    private var mItems: MutableList<ServiceItem> = ArrayList()

    fun setItems(
        items: List<ServiceItem>,
        childClickListener: AdapterInterface.OnItemClickListener<ServiceItem>?
    ) {
        Timber.tag("MainServiceAdapter").i("setItems:called")

        this.onChildItemClickListener = childClickListener
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
        notifyItemChanged(0, mItems.size)
    }

    inner class ItemViewHolder(val binding: DashboardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =

        ItemViewHolder(
            DashboardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.apply {
            this.item = item
            this.isEmployer = isEmployer
            holder.itemView.alpha = if (item.active) 1f else 0.5f
            container.setOnClickListener {
                onChildItemClickListener?.onItemClick(
                    item,
                    imgIcon
                )
            }
            ImageUtils.loadImage(imgIcon, item.getImageUrl())

        }
    }

    override fun getItemCount() = mItems.size

    inner class DiffCallback(
        private val mOldList: List<ServiceItem>,
        private val mNewList: List<ServiceItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].name == mNewList[newItemPosition].name ||
                    mOldList[oldItemPosition].id == mNewList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }

}