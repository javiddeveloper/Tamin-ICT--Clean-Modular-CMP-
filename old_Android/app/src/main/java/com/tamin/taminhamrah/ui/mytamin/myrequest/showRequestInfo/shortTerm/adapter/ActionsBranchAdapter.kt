package com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.shortTerm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.RequestStatusModel
import com.tamin.taminhamrah.databinding.ListItemActionsBranchBinding
import com.tamin.taminhamrah.utils.ConvertDate

class ActionsBranchAdapter : RecyclerView.Adapter<ActionsBranchAdapter.ItemViewHolder>() {
    private var mItems = emptyList<RequestStatusModel>()

    fun setItems(list: List<RequestStatusModel>) {
        val diffCallback = DiffCallback(mItems, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = list
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemActionsBranchBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.apply {
            tvValueProcessingDate.text =
                ConvertDate.convertTimestampToPersianDate(item.processingTimeStamp ?: 0)
            tvValueResult.text = item.processResult?.replace("</br>","") ?: ""
            if (item.rejectReason?.isNotBlank() == true)
                tvValueReasonReject.text = item.rejectReason
            else
                groupRejectReason.isVisible = false
        }
    }

    override fun getItemCount(): Int = mItems.size

    class ItemViewHolder(var binding: ListItemActionsBranchBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DiffCallback(
        private val oldList: List<RequestStatusModel>,
        private val newList: List<RequestStatusModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}