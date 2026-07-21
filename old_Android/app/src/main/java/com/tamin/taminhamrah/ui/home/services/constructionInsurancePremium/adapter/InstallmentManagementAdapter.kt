package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.InstallmentConstructionListModel
import com.tamin.taminhamrah.databinding.ListItemInstallmentManagmentBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility

class InstallmentManagementAdapter : BasePagingAdapter<InstallmentConstructionListModel, ListItemInstallmentManagmentBinding>(DiffCallback) {
    override fun getLayoutResId() = R.layout.list_item_installment_managment

    var onItemClickListener: AdapterInterface.OnItemClickListener<InstallmentConstructionListModel>? = null
    var mContext : Context? = null


    override fun bindItem(
        binding: ListItemInstallmentManagmentBinding,
        item: InstallmentConstructionListModel?,
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
            tvValueDueDate.text = Utility.getDateSeparator(item?.dtnExpireDate)
            tvValueAmountPayable.text = Utility.getRialWithSeparator(item?.dtnAmount)
            detailAdapter.setItems(item?.getDetailInstallment() ?: emptyList())


            btnShowDetail.setOnClickListener {
                detailAdapter.toggleMinifyMode()
                btnShowDetail.text = if (detailAdapter.isMinifyMode())
                    mContext?.getString(R.string.show_detail) else mContext?.getString(R.string.hide_detail)
            }

        }
    }

    override fun initViewHolder(binding: ListItemInstallmentManagmentBinding, itemView: View) {
    }

    object DiffCallback : DiffUtil.ItemCallback<InstallmentConstructionListModel>() {
        override fun areItemsTheSame(
            oldItem: InstallmentConstructionListModel,
            newItem: InstallmentConstructionListModel
        ) =  oldItem.debitNumber == newItem.debitNumber

        override fun areContentsTheSame(
            oldItem: InstallmentConstructionListModel,
            newItem: InstallmentConstructionListModel
        ) = oldItem == newItem


    }
}