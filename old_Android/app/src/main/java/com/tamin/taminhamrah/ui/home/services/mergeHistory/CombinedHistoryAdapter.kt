package com.tamin.taminhamrah.ui.home.services.mergeHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordModel
import com.tamin.taminhamrah.databinding.ListItemYearWithDateBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener


class CombinedHistoryAdapter(private val onItemClickListener: OnItemClickListener<String>? = null):
   RecyclerView.Adapter<CombinedHistoryAdapter.ItemViewHolder>() {


    private var mItems = emptyList<CombinedRecordModel>()

    fun setItems (items : List<CombinedRecordModel>){
        mItems = items.toMutableList()
        notifyItemRangeChanged(0,mItems.size)
    }

    private fun sumDays(currentItem: CombinedRecordModel): Int {
        var sum = 0
        currentItem.apply {
            hisMonth1?.let { sum += it.toInt() }
            hisMonth2?.let { sum += it.toInt() }
            hisMonth3?.let { sum += it.toInt() }
            hisMonth4?.let { sum += it.toInt() }
            hisMonth5?.let { sum += it.toInt() }
            hisMonth6?.let { sum += it.toInt() }
            hisMonth7?.let { sum += it.toInt() }
            hisMonth8?.let { sum += it.toInt() }
            hisMonth9?.let { sum += it.toInt() }
            hisMonth10?.let { sum += it.toInt() }
            hisMonth11?.let { sum += it.toInt() }
            hisMonth12?.let { sum += it.toInt() }
        }
        return sum
    }

    class ItemViewHolder(var binding: ListItemYearWithDateBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemYearWithDateBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
       val item = mItems[position]
       val sum = sumDays(item)
        holder.binding.apply {
            year = item.hisYear
            isSmaller365 = sum.compareTo(365) == -1
            day = sum.toString()
            root.setOnClickListener {
                year?.let {
                    onItemClickListener?.onItemClick(it)
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
