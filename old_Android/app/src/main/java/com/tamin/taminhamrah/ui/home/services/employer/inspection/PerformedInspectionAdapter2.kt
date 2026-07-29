package com.tamin.taminhamrah.ui.home.services.employer.inspection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.performedInspections.InspectionResponse
import com.tamin.taminhamrah.databinding.ListItemPerfomredInspectionBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class PerformedInspectionAdapter2
    : PagingDataAdapter<InspectionResponse, PerformedInspectionAdapter2.ItemViewHolder>(POST_COMPARATOR) {
    var onItemClickListener: AdapterInterface.OnItemClickListener<InspectionResponse>? = null

   // private var mItems = emptyList<InspectionResponse>()

   /* fun setItems(
        items: List<InspectionResponse>,
        listener: AdapterInterface.OnItemClickListener<InspectionResponse>

    ) {
        onItemClickListener = listener
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
    }*/

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemPerfomredInspectionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            holder.updateScore(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
//        holder.binding.item = item

        holder.binding.recycler.apply {
            val itemAdapter = KeyValueAdapter()
            adapter = itemAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(this.context))
            }

//            item?.createKeyValue(item)?.let { itemAdapter.setItems(it) }
        }

        holder.binding.btnSeeDetail.setOnClickListener {
            item?.let { it1 -> onItemClickListener?.onItemClick(it1) }
        }

    }

    class ItemViewHolder(var binding: ListItemPerfomredInspectionBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun updateScore(item: InspectionResponse?) {
//            this.binding.item = item
        }
    }

    companion object {
        private val PAYLOAD_SCORE = Any()
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<InspectionResponse>() {
            override fun areContentsTheSame(oldItem: InspectionResponse, newItem: InspectionResponse): Boolean =
               true// oldItem == newItem

            override fun areItemsTheSame(oldItem: InspectionResponse, newItem: InspectionResponse): Boolean =
                oldItem.isSuccess == newItem.isSuccess

            override fun getChangePayload(oldItem: InspectionResponse, newItem: InspectionResponse): Any? {
                return if (sameExceptScore(oldItem, newItem)) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptScore(oldItem: InspectionResponse, newItem: InspectionResponse): Boolean {
            // DON'T do this copy in a real app, it is just convenient here for the demo :)
            // because reddit randomizes scores, we want to pass it as a payload to minimize
            // UI updates between refreshes
            return true//oldItem.copy(inspectionNo = newItem.inspectionNo) == newItem
        }
    }



   /* inner class DiffCallback(
        private val mOldList: List<InspectionResponse>,
        private val mNewList: List<InspectionResponse>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].toString() == mNewList[newItemPosition].toString()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition].toString()
            val newItem = mNewList[newItemPosition].toString()
            return (newItem == oldItem)
        }
    }*/
}
