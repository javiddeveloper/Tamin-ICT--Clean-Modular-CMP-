package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.ConstructionFileModel
import com.tamin.taminhamrah.databinding.ListItemConstructionFilesBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.disableButton

class ConstructionFilesAdapter : BasePagingAdapter<ConstructionFileModel, ListItemConstructionFilesBinding>(DiffCallback) {
    override fun getLayoutResId() = R.layout.list_item_construction_files

    var onItemClickListener: AdapterInterface.OnItemClickListener<ConstructionFileModel>? = null
    var mContext : Context? = null


    override fun bindItem(
        binding: ListItemConstructionFilesBinding,
        item: ConstructionFileModel?,
        position: Int,
    ) {
        binding.apply {
            val detailAdapter = ExpandableListAdapter(expandingIndex = 2)
            binding.recycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = detailAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(context))
            }

            mContext = binding.root.context
            tvValueTotalFees.text = Utility.getRialWithSeparator(item?.sumOfComplications)
            tvValueCalculatedAmount.text = Utility.getRialWithSeparator(item?.totalPayment)
            detailAdapter.setItems(item?.getDetailConstructionFile() ?: emptyList())


            btnShowDetail.setOnClickListener {
                detailAdapter.toggleMinifyMode()
                btnShowDetail.text = if (detailAdapter.isMinifyMode())
                    mContext?.getString(R.string.show_detail) else mContext?.getString(R.string.hide_detail)
            }

            btnAction.setOnClickListener {
                if (item!=null)
                    onItemClickListener?.onItemClick(item=item)
                else
                    btnAction.disableButton()
            }
        }
    }

    override fun initViewHolder(binding: ListItemConstructionFilesBinding, itemView: View) {
    }

    object DiffCallback : DiffUtil.ItemCallback<ConstructionFileModel>() {
        override fun areItemsTheSame(
            oldItem: ConstructionFileModel,
            newItem: ConstructionFileModel
        ) =  oldItem.workshopInfo?.workshopId == newItem.workshopInfo?.workshopId

        override fun areContentsTheSame(
            oldItem: ConstructionFileModel,
            newItem: ConstructionFileModel
        ) = oldItem == newItem


    }
}