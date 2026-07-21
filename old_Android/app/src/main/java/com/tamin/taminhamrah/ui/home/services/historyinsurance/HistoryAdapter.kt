package com.tamin.taminhamrah.ui.home.services.historyinsurance
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.ListItemYearHistoryInsuranceBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.ui.base.BasePagingAdapter


class HistoryAdapter :
    BasePagingAdapter<String, ListItemYearHistoryInsuranceBinding>(DiffCallback) {
     var onItemClickListener: OnItemClickListener<String>? = null

    object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

    }
    override fun getLayoutResId(): Int {
        return R.layout.list_item_year_history_insurance
    }

    override fun initViewHolder(binding: ListItemYearHistoryInsuranceBinding, itemView: View) {
    }

    override fun bindItem(
        binding: ListItemYearHistoryInsuranceBinding,
        item: String?,
        position: Int
    ) {
        item?.let {year->
            binding.year = year
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(year)
            }
        }
    }

}
