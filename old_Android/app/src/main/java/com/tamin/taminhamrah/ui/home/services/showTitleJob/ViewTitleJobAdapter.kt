package com.tamin.taminhamrah.ui.home.services.showTitleJob

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.TitlesJobModel
import com.tamin.taminhamrah.databinding.ListItemTitlesJobBinding
import com.tamin.taminhamrah.ui.base.BasePagingAdapter


class ViewTitleJobAdapter : BasePagingAdapter<TitlesJobModel,ListItemTitlesJobBinding>(DiffCallback) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_titles_job
    }

    override fun bindItem(binding: ListItemTitlesJobBinding, item: TitlesJobModel?, position: Int) {
        binding.apply {
            itemInsuranceCode.tvInsuranceCodeValue.text = item?.risuid
            itemWorkshopCode.tvWorkShopCodeValue.text = item?.rwshId
            tvValueStartDate.text = item?.workStartDate()
            tvValueTitleJob.text = item?.jobDesc
            tvValueWorkShop.text = item?.rwshName
        }
    }

    override fun initViewHolder(binding: ListItemTitlesJobBinding, itemView: View) {
    }

    object DiffCallback : DiffUtil.ItemCallback<TitlesJobModel>() {
        override fun areItemsTheSame(
            oldItem: TitlesJobModel,
            newItem: TitlesJobModel
        ): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(
            oldItem: TitlesJobModel,
            newItem: TitlesJobModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}
