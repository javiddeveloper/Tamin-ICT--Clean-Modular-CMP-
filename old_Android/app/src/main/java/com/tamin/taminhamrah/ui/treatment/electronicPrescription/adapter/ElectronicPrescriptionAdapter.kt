package com.tamin.taminhamrah.ui.treatment.electronicPrescription.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescription
import com.tamin.taminhamrah.databinding.ListItemElectronicPrescriptionBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class ElectronicPrescriptionAdapter(
    var onItemClickListener: AdapterInterface.OnItemClickListener<ElectronicPrescription>? = null,
    var onDownloadClickListener: AdapterInterface.OnDownloadClickListener<ElectronicPrescription>? = null
) : BasePagingAdapter<ElectronicPrescription, ListItemElectronicPrescriptionBinding>(
    DiffUtilCallBack
) {
    override fun getLayoutResId() = R.layout.list_item_electronic_prescription

    override fun bindItem(
        binding: ListItemElectronicPrescriptionBinding,
        item: ElectronicPrescription?,
        position: Int
    ) {
        binding.apply {
            item?.let {
                prescription = item
                btnDownload.isVisible = item.flagSata != "1"
                tvTrackingCode.text = item.trackingCode?.toString() ?: "-"

                if (item.flagSata == "2")
                    btnDownload.text =
                        btnDownload.context.getString(R.string.test_report_prescription)
                else
                    btnDownload.text = btnDownload.context.getString(
                        R.string.download_prescription)


                btnDownload.setOnClickListener {
                    onDownloadClickListener?.onDownload(item)
                }

                btnShowDetail.setOnClickListener { onItemClickListener?.onItemClick(item) }
            }


        }
    }

    override fun initViewHolder(binding: ListItemElectronicPrescriptionBinding, itemView: View) {}

    object DiffUtilCallBack : DiffUtil.ItemCallback<ElectronicPrescription>() {
        override fun areItemsTheSame(
            oldItem: ElectronicPrescription,
            newItem: ElectronicPrescription
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ElectronicPrescription,
            newItem: ElectronicPrescription
        ) = oldItem == newItem
    }
}
