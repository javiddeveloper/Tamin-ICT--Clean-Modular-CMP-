package com.tamin.taminhamrah.ui.home.services.combinedRecord

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordModel
import com.tamin.taminhamrah.databinding.ListItemYearWithDateBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.ui.base.BasePagingAdapter


class CombinedRecordAdapter :
    BasePagingAdapter<CombinedRecordModel, ListItemYearWithDateBinding>(DiffUtillCallBack) {

    var onItemClickListener: OnItemClickListener<String>? = null

    override fun getLayoutResId(): Int {
        return R.layout.list_item_year_with_date
    }


    override fun bindItem(
        binding: ListItemYearWithDateBinding,
        item: CombinedRecordModel?,
        position: Int
    )
    {
        var sum = 0
        item?.let {
            sum = sumDays(it)
        }
        binding.apply {
            year = item?.hisYear
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
        itemView: View
    ) {

    }

    object DiffUtillCallBack : DiffUtil.ItemCallback<CombinedRecordModel>() {
        override fun areItemsTheSame(
            oldItem: CombinedRecordModel,
            newItem: CombinedRecordModel
        ): Boolean {
            return oldItem.hisYear == newItem.hisYear
        }

        override fun areContentsTheSame(
            oldItem: CombinedRecordModel,
            newItem: CombinedRecordModel
        ): Boolean {
            return oldItem.hisYear == newItem.hisYear
        }

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
}
