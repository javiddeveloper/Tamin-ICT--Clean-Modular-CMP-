package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.WorkshopInfoWithoutContract
import com.tamin.taminhamrah.databinding.ListItemWorkshopWithoutContractBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class WorkshopWithoutContractAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopInfoWithoutContract>? = null) :
    BasePagingAdapter<WorkshopInfoWithoutContract, ListItemWorkshopWithoutContractBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_without_contract
    }

    override fun bindItem(
        binding: ListItemWorkshopWithoutContractBinding,
        item: WorkshopInfoWithoutContract?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            /* binding.btnShowDetail.setOnClickListener {
                 if (binding.groupChild.visibility == View.GONE) {
                     binding.groupChild.visibility = View.VISIBLE
                     binding.btnShowDetail.text =
                         binding.btnShowDetail.context.getString(R.string.label_hide_detail)
                 } else {
                     binding.groupChild.visibility = View.GONE
                     binding.btnShowDetail.text =
                         binding.btnShowDetail.context.getString(R.string.label_show_detail)
                 }
             }*/

            binding.btnAction.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    override fun initViewHolder(binding: ListItemWorkshopWithoutContractBinding, itemView: View) {

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

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkshopInfoWithoutContract>() {
        override fun areItemsTheSame(
            oldItem: WorkshopInfoWithoutContract,
            newItem: WorkshopInfoWithoutContract
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkshopInfoWithoutContract,
            newItem: WorkshopInfoWithoutContract
        ): Boolean {
            return oldItem == newItem
        }
    }
}
