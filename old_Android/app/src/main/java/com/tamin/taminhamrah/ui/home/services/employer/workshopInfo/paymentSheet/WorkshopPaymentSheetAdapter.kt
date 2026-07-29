package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.paymentSheet

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopPaymentSheet
import com.tamin.taminhamrah.databinding.ListItemWorkshopPaymentSheetBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class WorkshopPaymentSheetAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopPaymentSheet>? = null) :
    BasePagingAdapter<WorkshopPaymentSheet, ListItemWorkshopPaymentSheetBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_payment_sheet
    }

    override fun bindItem(
        binding: ListItemWorkshopPaymentSheetBinding,
        item: WorkshopPaymentSheet?,
        position: Int

    ) {
        with(binding) {
            this.item = item ?: return


        }
    }

    override fun initViewHolder(binding: ListItemWorkshopPaymentSheetBinding, itemView: View) {
        binding.btnShowDetail.setOnClickListener {
            if (binding.groupVisit.visibility == View.GONE) {
                binding.groupVisit.visibility = View.VISIBLE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.hide_detail)

            } else {
                binding.groupVisit.visibility = View.GONE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.show_detail)
            }

            itemView.requestFocus()
        }
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkshopPaymentSheet>() {
        override fun areItemsTheSame(
            oldItem: WorkshopPaymentSheet,
            newItem: WorkshopPaymentSheet
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkshopPaymentSheet,
            newItem: WorkshopPaymentSheet
        ): Boolean {
            return oldItem == newItem
        }
    }
}
