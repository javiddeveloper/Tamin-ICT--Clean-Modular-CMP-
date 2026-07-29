package com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.InspectionPerformedModel
import com.tamin.taminhamrah.databinding.ListItemInspectionPreformedBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter


class InspectionsPerformedAdapter(var onDownloadDetailClickListener: AdapterInterface.OnDownloadClickListener<String>) :
    BasePagingAdapter<InspectionPerformedModel, ListItemInspectionPreformedBinding>(DiffCallback) {

    override fun getLayoutResId() = R.layout.list_item_inspection_preformed

    override fun bindItem(
        binding: ListItemInspectionPreformedBinding,
        item: InspectionPerformedModel?,
        position: Int
    ) {
        binding.apply {
            tvValueNumInspection.text = item?.inspectionNo ?: "_"
            tvValueDateInspections.text = item?.getInspectionDatePersian() ?: "_"
            tvValueWorkshopName.text = item?.workshopName ?: "_"
            valueBranchName.text = item?.branchdesc ?: "_"
            tvDocSpecification.text = item?.activityDesc?.trim() ?: "_"

            item?.inspectionNo?.let { inspectionNo ->
                btnDownloadDetail.setOnClickListener {
                    onDownloadDetailClickListener.onDownload(inspectionNo)
                }
                itemInsuranceCode.tvInsuranceCodeValue.text = item.insuranceNo ?: "0"
                itemWorkshopCode.tvWorkShopCodeValue.text = item.workshopNo ?: "0"
            }

        }
    }

    override fun initViewHolder(binding: ListItemInspectionPreformedBinding, itemView: View) {
        itemView.requestFocus()
    }

    object DiffCallback : DiffUtil.ItemCallback<InspectionPerformedModel>() {
        override fun areItemsTheSame(
            oldItem: InspectionPerformedModel,
            newItem: InspectionPerformedModel
        ): Boolean {
            return oldItem.inspectionNo == newItem.inspectionNo
        }

        override fun areContentsTheSame(
            oldItem: InspectionPerformedModel,
            newItem: InspectionPerformedModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}
