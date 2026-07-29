package com.tamin.taminhamrah.ui.home.services.wageandhistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.databinding.ListItemWageAndHistoryInsuranceByMonthBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class WageAndHistoryDetailAdapter :
    RecyclerView.Adapter<WageAndHistoryDetailAdapter.ItemViewHolder>() {
    var onShowMoreClickListener: AdapterInterface.OnShowMoreClickListener<Int>? = null
    private var mItems = emptyList<WageAndHistoryModel>()
    fun setItems(
        items: ArrayList<WageAndHistoryModel>,
        onShowMoreClickListener: AdapterInterface.OnShowMoreClickListener<Int>? = null
    ) {
        this.onShowMoreClickListener = onShowMoreClickListener
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
            ListItemWageAndHistoryInsuranceByMonthBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = mItems[position]
            holder.binding.item = currentItem
           holder.binding.apply {
               btnInfoWorkShopExpand.setOnClickListener {
                   groupDetails.apply {
                       visibility = if (visibility == View.VISIBLE) {
                           View.GONE
                       } else
                           View.VISIBLE
                       onShowMoreClickListener?.onShowMoreClick(position)
                   }
               }
            }

    }



    override fun getItemCount() = mItems.size

    class ItemViewHolder(var binding: ListItemWageAndHistoryInsuranceByMonthBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<WageAndHistoryModel>,
        private val mNewList: List<WageAndHistoryModel>
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
