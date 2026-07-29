package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordModel
import com.tamin.taminhamrah.databinding.ListItemYearHistoryInsuranceBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class YearHistoryAdapter (private val onItemClickListener: AdapterInterface.OnItemClickListener<String>? = null):
    RecyclerView.Adapter<YearHistoryAdapter.ItemViewHolder>() {

    private var mItems = emptyList<String>()

    fun setItems (items : List<String>){
        mItems = items.toMutableList()
        notifyItemRangeChanged(0,mItems.size)
    }

    class ItemViewHolder(var binding: ListItemYearHistoryInsuranceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemYearHistoryInsuranceBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.apply {
            year = item
            root.setOnClickListener {
                year?.let {
                    onItemClickListener?.onItemClick(item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class DiffCallback(
        private val mOldList: List<CombinedRecordModel>,
        private val mNewList: List<CombinedRecordModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = mOldList[oldItemPosition].hisYear == mNewList[newItemPosition].hisYear

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)= (mOldList[oldItemPosition] == mNewList[newItemPosition])
    }
}
