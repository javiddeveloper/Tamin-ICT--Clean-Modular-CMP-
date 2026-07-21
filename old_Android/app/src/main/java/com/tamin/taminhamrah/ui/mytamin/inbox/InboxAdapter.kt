package com.tamin.taminhamrah.ui.mytamin.inbox

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.user.InboxItem
import com.tamin.taminhamrah.databinding.ListItemInboxBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class InboxAdapter :
    BasePagingAdapter<InboxItem, ListItemInboxBinding>(DiffUtilCallBack) {

    var onDeleteListener: AdapterInterface.OnDeleteClickListener<InboxItem>? = null
    var onItemClickListener: AdapterInterface.OnItemClickListener<InboxItem>? = null
    var onToggleExpandListener: ((InboxItem) -> Unit)? = null

    override fun getLayoutResId(): Int {
        return R.layout.list_item_inbox
    }

    override fun bindItem(binding: ListItemInboxBinding, item: InboxItem?, position: Int) {
        with(binding) {
            this.item = item ?: return
            manageDetailsVisibility(this, item.expanded)

            this.btnShowPdf.setOnClickListener {
                onItemClickListener?.onItemClick(
                    item,
                    tag = btnShowPdf.text.toString()
                )
            }
            this.btnInquiryLicense.setOnClickListener {
                onItemClickListener?.onItemClick(
                    item,
                    tag = btnInquiryLicense.text.toString()
                )
            }
            this.btnDelete.setOnClickListener { onDeleteListener?.onDelete(item) }

            // Delegate the state change to the ViewModel
            binding.btnShowDetail.setOnClickListener {
                onToggleExpandListener?.invoke(item)
            }
        }
    }

    private fun manageDetailsVisibility(
        binding: ListItemInboxBinding,
        isExpanded: Boolean
    ) {
        if (isExpanded) {
            showDetails(binding)
        } else {
            hideDetails(binding)
        }
    }

    private fun showDetails(binding: ListItemInboxBinding) {
        binding.groupDetails.visible()
        binding.line5.visible()
        binding.btnShowDetail.text =
            binding.btnShowDetail.context.getString(R.string.hide_detail)
    }

    private fun hideDetails(binding: ListItemInboxBinding) {
        binding.groupDetails.gone()
        binding.line5.gone()
        binding.btnShowDetail.text =
            binding.btnShowDetail.context.getString(R.string.show_detail)
    }


    override fun initViewHolder(binding: ListItemInboxBinding, itemView: View) {
    }

    fun updateVisibilityStatus(id: Int?) {
        if (id == null)
            return

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<InboxItem>() {
        override fun areItemsTheSame(
            oldItem: InboxItem,
            newItem: InboxItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        // The expanded state is now part of the content comparison
        override fun areContentsTheSame(
            oldItem: InboxItem,
            newItem: InboxItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
