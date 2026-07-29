package com.tamin.taminhamrah.ui.home.services.viewShortTermSupport

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.ViewShortTermRequestModel
import com.tamin.taminhamrah.databinding.ListItemShorttermRequestBinding
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class ViewShortTermAdapter :
    BasePagingAdapter<ViewShortTermRequestModel, ListItemShorttermRequestBinding>(DiffCallback) {

    class ItemViewHolder(var binding: ListItemShorttermRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getLayoutResId(): Int {
        return R.layout.list_item_shortterm_request
    }

    override fun bindItem(
        binding: ListItemShorttermRequestBinding,
        iteminput: ViewShortTermRequestModel?,
        position: Int
    ) {
        binding.apply {
            item = iteminput
            btnShowDetail.setOnClickListener {
                if (!it.tag.equals("disable"))
                    if (layoutRootDisplay.visibility == View.GONE) {
                        layoutRootDisplay.visibility = View.VISIBLE
                        btnShowDetail.text =
                            btnShowDetail.context.getString(R.string.hide_detail)
                    } else {
                        layoutRootDisplay.visibility = View.GONE
                        btnShowDetail.text =
                            btnShowDetail.context.getString(R.string.show_detail)
                    }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ViewShortTermRequestModel>() {
        override fun areItemsTheSame(
            oldItem: ViewShortTermRequestModel,
            newItem: ViewShortTermRequestModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ViewShortTermRequestModel,
            newItem: ViewShortTermRequestModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun initViewHolder(binding: ListItemShorttermRequestBinding, itemView: View) {
    }
}
