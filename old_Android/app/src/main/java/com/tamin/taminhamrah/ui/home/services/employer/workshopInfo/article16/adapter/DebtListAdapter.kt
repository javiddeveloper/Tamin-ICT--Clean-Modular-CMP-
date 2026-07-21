package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListModel
import com.tamin.taminhamrah.databinding.ListItemDebtListDefinitiveDebtBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility

class DebtListAdapter : BasePagingAdapter<WorkshopsDebtListModel,ListItemDebtListDefinitiveDebtBinding>(DiffCallback) {
    override fun getLayoutResId() = R.layout.list_item_debt_list_definitive_debt

    var onItemClickListener: AdapterInterface.OnItemClickListener<WorkshopsDebtListModel>? = null
    var mContext : Context? = null
    override fun bindItem(
        binding: ListItemDebtListDefinitiveDebtBinding,
        item: WorkshopsDebtListModel?,
        position: Int
        ) {
        binding.apply {
            mContext = binding.root.context
            val detailAdapter = ExpandableListAdapter(expandingIndex = 2)
                binding.recycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = detailAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(context))
            }
            detailAdapter.setItems(item?.getDetailInfoMainPage() ?: emptyList())
            tvValueDebitNumber.text = item?.debitNumber ?: "-"
            tvValueExecutiveNotificationDate.text = Utility.getDateSeparator(item?.dateExecutiveNotification)
            btnShowDetail.setOnClickListener {
                detailAdapter.toggleMinifyMode()
                btnShowDetail.text = if (detailAdapter.isMinifyMode())
                    mContext?.getString(R.string.show_detail) else mContext?.getString(R.string.hide_detail)
            }

            btnAction.setOnClickListener {
                if (item!=null)
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    override fun initViewHolder(binding: ListItemDebtListDefinitiveDebtBinding, itemView: View) {
    }

    object DiffCallback : DiffUtil.ItemCallback<WorkshopsDebtListModel>() {
        override fun areItemsTheSame(
            oldItem: WorkshopsDebtListModel,
            newItem: WorkshopsDebtListModel
        ) =  oldItem.debitNumber == newItem.debitNumber

        override fun areContentsTheSame(
            oldItem: WorkshopsDebtListModel,
            newItem: WorkshopsDebtListModel
        ) = oldItem == newItem

    }
}