package com.tamin.taminhamrah.ui.home.services.construction.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPaymentInfo
import com.tamin.taminhamrah.databinding.ListItemWorkersPaymentInfoUiBinding
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.invisible
import com.tamin.taminhamrah.utils.extentions.isVisible
import com.tamin.taminhamrah.utils.extentions.visible
import timber.log.Timber


typealias PaymentAction = (info: WorkersPaymentInfo) -> Unit

class WorkersPaymentInfoAdapter(private var onPaymentAction: PaymentAction) :
    BasePagingAdapter<WorkersPaymentInfo, ListItemWorkersPaymentInfoUiBinding>(DiffCallback) {


    override fun getLayoutResId(): Int {
        return R.layout.list_item_workers_payment_info_ui
    }

    override fun bindItem(
        binding: ListItemWorkersPaymentInfoUiBinding,
        item: WorkersPaymentInfo?,
        position: Int
    ) {
        val adapter = KeyValueAdapter()
        binding.apply {
            payInfo = item
            Timber.e(
                "WorkersPaymentInfo :%s : %s",
                item?.professionalTitle,
                Thread.currentThread().name
            )
            recycler.adapter = adapter
            btnShowDetail.setOnClickListener {
                if (recycler.isVisible()) {
                    recycler.gone()
                    btnShowDetail.setText(R.string.show_detail)
                } else {
                    recycler.visible()
                    btnShowDetail.setText(R.string.hide_detail)
                }
            }

            if(item?.payable==true)btnPayIt.visible() else btnPayIt.invisible()
            btnPayIt.setOnClickListener {
                item?.let { it1 -> onPaymentAction.invoke(it1) }
            }

        }

        item?.let { adapter.setItems(it.exportKayValue()) }
    }

    override fun initViewHolder(binding: ListItemWorkersPaymentInfoUiBinding, itemView: View) {}


}

object DiffCallback : DiffUtil.ItemCallback<WorkersPaymentInfo>() {
    override fun areItemsTheSame(
        oldItem: WorkersPaymentInfo,
        newItem: WorkersPaymentInfo
    ): Boolean {
        // TODO : this need to discuss
        return oldItem.payDay == newItem.payDay
    }

    override fun areContentsTheSame(
        oldItem: WorkersPaymentInfo,
        newItem: WorkersPaymentInfo
    ): Boolean {
        return oldItem == newItem
    }
}