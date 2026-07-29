package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.InstallmentLetterListModel
import com.tamin.taminhamrah.databinding.ListItemInstallmentConstructionBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.disableButton

class InstallmentConstructionAdapter : BasePagingAdapter<InstallmentLetterListModel, ListItemInstallmentConstructionBinding>(DiffCallback) {
    override fun getLayoutResId() = R.layout.list_item_installment_construction

    var onItemClickListener: AdapterInterface.OnItemClickListener<InstallmentLetterListModel>? = null
    var mContext : Context? = null


    override fun bindItem(
        binding: ListItemInstallmentConstructionBinding,
        item: InstallmentLetterListModel?,
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
            tvValueStatus.text = item?.debitStatusDescription
            tvValueDebitAmountResidue.text = Utility.getRialWithSeparator(item?.remainingAmount)
            detailAdapter.setItems(item?.getDetailConstructionInstallment() ?: emptyList())


            btnShowDetail.setOnClickListener {
                detailAdapter.toggleMinifyMode()
                btnShowDetail.text = if (detailAdapter.isMinifyMode())
                    mContext?.getString(R.string.show_detail) else mContext?.getString(R.string.hide_detail)
            }

            btnAction.visibility = item?.debitNumberOld?.let {
                   View.VISIBLE
            }?:  View.INVISIBLE

            btnAction.setOnClickListener {
                if (item!=null)
                    onItemClickListener?.onItemClick(item=item)
                else
                    btnAction.disableButton()
            }
        }
    }

    override fun initViewHolder(binding: ListItemInstallmentConstructionBinding, itemView: View) {
    }

    object DiffCallback : DiffUtil.ItemCallback<InstallmentLetterListModel>() {
        override fun areItemsTheSame(
            oldItem: InstallmentLetterListModel,
            newItem: InstallmentLetterListModel
        ) =  oldItem.debitNumber == newItem.debitNumber

        override fun areContentsTheSame(
            oldItem: InstallmentLetterListModel,
            newItem: InstallmentLetterListModel
        ) = oldItem == newItem


    }
}