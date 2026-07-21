package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.LetterInfo
import com.tamin.taminhamrah.databinding.ListItemLetterInfoBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class DistantCorrespondenceAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<LetterInfo>? = null) :
    BasePagingAdapter<LetterInfo, ListItemLetterInfoBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_letter_info
    }

    override fun bindItem(
        binding: ListItemLetterInfoBinding,
        item: LetterInfo?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            binding.btnActions.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    override fun initViewHolder(binding: ListItemLetterInfoBinding, itemView: View) {

        /*  binding.btnShowDetail.setOnClickListener {
              if (binding.groupVisit.visibility == View.GONE) {
                  binding.groupVisit.visibility = View.VISIBLE
                  binding.btnShowDetail.text =
                      binding.btnShowDetail.context.getString(R.string.label_hide_detail)

              } else {
                  binding.groupVisit.visibility = View.GONE
                  binding.btnShowDetail.text =
                      binding.btnShowDetail.context.getString(R.string.label_show_detail)
              }

              itemView.requestFocus()
              //onItemClickListener?.onItemClick(item)
          }*/
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<LetterInfo>() {
        override fun areItemsTheSame(
            oldItem: LetterInfo,
            newItem: LetterInfo
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: LetterInfo,
            newItem: LetterInfo
        ): Boolean {
            return oldItem == newItem
        }
    }
}