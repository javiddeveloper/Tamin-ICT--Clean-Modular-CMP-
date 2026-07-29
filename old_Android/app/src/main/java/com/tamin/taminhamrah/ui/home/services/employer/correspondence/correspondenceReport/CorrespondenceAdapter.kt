package com.tamin.taminhamrah.ui.home.services.employer.correspondence.correspondenceReport

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.user.InboxItem
import com.tamin.taminhamrah.databinding.ListItemInboxBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class CorrespondenceAdapter :
    BasePagingAdapter<InboxItem, ListItemInboxBinding>(DiffUtilCallBack) {

        var onDeleteListener: AdapterInterface.OnDeleteClickListener<InboxItem>? = null
        var onItemClickListener: AdapterInterface.OnItemClickListener<InboxItem>? = null

        override fun getLayoutResId(): Int {
            return R.layout.list_item_inbox
        }

        override fun bindItem(binding: ListItemInboxBinding, item: InboxItem?, position: Int) {
            with(binding) {
                this.item = item ?: return
                this.btnShowPdf.setOnClickListener { onItemClickListener?.onItemClick(item,tag=btnShowPdf.text.toString()) }
                this.btnInquiryLicense.setOnClickListener { onItemClickListener?.onItemClick(item, tag = btnInquiryLicense.text.toString()) }
                this.btnDelete.setOnClickListener { onDeleteListener?.onDelete(item) }
            }
        }


        override fun initViewHolder(binding: ListItemInboxBinding, itemView: View) {
            binding.btnShowDetail.setOnClickListener {
                if (binding.groupDetails.visibility == View.GONE) {
                    binding.groupDetails.visibility = View.VISIBLE
                    binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.hide_detail)
                } else {
                    binding.groupDetails.visibility = View.GONE
                    binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.show_detail)
                }

                itemView.requestFocus()
            }
        }

        fun updateVisibilityStatus(id: Int?) {
            if (id==null)
                return
            refresh()

        }

        object DiffUtilCallBack : DiffUtil.ItemCallback<InboxItem>() {
            override fun areItemsTheSame(
                oldItem: InboxItem,
                newItem: InboxItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: InboxItem,
                newItem: InboxItem
            ): Boolean {
                return oldItem .equals(newItem)
            }
        }

    }