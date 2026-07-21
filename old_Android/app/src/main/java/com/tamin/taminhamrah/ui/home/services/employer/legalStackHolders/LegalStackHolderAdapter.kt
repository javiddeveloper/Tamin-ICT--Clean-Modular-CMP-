package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.legalStackHolder.LegalStackHolder
import com.tamin.taminhamrah.databinding.ListItemStackholderBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class LegalStackHolderAdapter(
    var onItemClickListener: AdapterInterface.OnItemClickListener<LegalStackHolder>? = null,
    private val showMoreInfo: Boolean = false
) :
    BasePagingAdapter<LegalStackHolder, ListItemStackholderBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_stackholder
    }

    override fun bindItem(
        binding: ListItemStackholderBinding,
        item: LegalStackHolder?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            if (showMoreInfo){
                btnAction.gone()
                groupMoreInfo.visible()
            }

            btnEdit.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnEdit.text.toString())
            }
            btnDelete.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnDelete.text.toString())
            }

            btnAction.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnAction.text.toString())
            }
        }
    }

    override fun initViewHolder(binding: ListItemStackholderBinding, itemView: View) {

        /* binding.btnShowDetail.setOnClickListener {
             if (binding.groupDetails.visibility == View.GONE) {
                 binding.groupDetails.visibility = View.VISIBLE
                 binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.label_hide_detail)
             } else {
                 binding.groupDetails.visibility = View.GONE
                 binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.label_show_detail)
             }

             itemView.requestFocus()
         }*/
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<LegalStackHolder>() {
        override fun areItemsTheSame(
            oldItem: LegalStackHolder,
            newItem: LegalStackHolder
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: LegalStackHolder,
            newItem: LegalStackHolder
        ): Boolean {
            return oldItem == newItem
        }
    }
}
