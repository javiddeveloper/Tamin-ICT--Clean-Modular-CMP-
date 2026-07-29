package com.tamin.taminhamrah.ui.home.services.wageandhistory

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.databinding.ListItemYearWithDateBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class WageAndHistoryAdapter :
    BasePagingAdapter<WageAndHistoryModel, ListItemYearWithDateBinding>(DiffUtillCallBack) {

    var onItemClickListener: OnItemClickListener<String>? = null

    override fun getLayoutResId(): Int {
        return R.layout.list_item_year_with_date
    }


    override fun bindItem(
        binding: ListItemYearWithDateBinding,
        item: WageAndHistoryModel?,
        position: Int
    )
    {
        var sum = 0
        item?.let {
            sum = sumDays(it)
        }
        binding.apply {
            year = item?.hisyear
            isSmaller365 = sum.compareTo(365) == -1
            day = sum.toString()
            binding.root.setOnClickListener {
                year?.let {
                    onItemClickListener?.onItemClick(it)
                }
            }
        }

    }


    override fun initViewHolder(
        binding: ListItemYearWithDateBinding,
        itemView: View) {

    }

    object DiffUtillCallBack : DiffUtil.ItemCallback<WageAndHistoryModel>() {
        override fun areItemsTheSame(
            oldItem: WageAndHistoryModel,
            newItem: WageAndHistoryModel
        ): Boolean {
            return oldItem.hisyear == newItem.hisyear
        }

        override fun areContentsTheSame(
            oldItem: WageAndHistoryModel,
            newItem: WageAndHistoryModel
        ): Boolean {
            return oldItem.hisyear == newItem.hisyear
        }

    }

    private fun sumDays(currentItem: WageAndHistoryModel): Int {
        var sum = 0
        currentItem.apply {

            hismon1?.let { sum += it.toInt() }
            hismon2?.let { sum += it.toInt() }
            hismon3?.let { sum += it.toInt() }
            hismon4?.let { sum += it.toInt() }
            hismon5?.let { sum += it.toInt() }
            hismon6?.let { sum += it.toInt() }
            hismon7?.let { sum += it.toInt() }
            hismon8?.let { sum += it.toInt() }
            hismon9?.let { sum += it.toInt() }
            hismon10?.let { sum += it.toInt() }
            hismon11?.let { sum += it.toInt() }
            hismon12?.let { sum += it.toInt() }
            return sum
        }
    }
}
