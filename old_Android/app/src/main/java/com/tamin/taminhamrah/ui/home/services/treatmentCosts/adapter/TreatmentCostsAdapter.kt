package com.tamin.taminhamrah.ui.home.services.treatmentCosts.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.Constants.WORKSHOP_STATUS_ENABLE
import com.tamin.taminhamrah.Constants.WORKSHOP_STATUS_SEMI_ACTIVE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.costs.TreatmentCostsExpensesModel
import com.tamin.taminhamrah.databinding.ListItemTreatmentCostsExpensesBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.disableButton
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.setTextViewDrawableColor
import com.tamin.taminhamrah.utils.extentions.visible

class TreatmentCostsAdapter :
    BasePagingAdapter<TreatmentCostsExpensesModel, ListItemTreatmentCostsExpensesBinding>(
        DiffCallback
    ) {
    override fun getLayoutResId() = R.layout.list_item_treatment_costs_expenses
    var onItemClickListener: AdapterInterface.OnItemClickListener<TreatmentCostsExpensesModel>? =
        null
    var mContext: Context? = null

    override fun bindItem(
        binding: ListItemTreatmentCostsExpensesBinding,
        item: TreatmentCostsExpensesModel?,
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
            tvValuePatientName.text = item?.nameFamil ?: "-"
            tvValueReceptionNumber.text = item?.noPazir ?: "-"
            detailAdapter.setItems(item?.detailTreatmentCostInfo() ?: emptyList())

            val background = if (item?.payStatus == "4" ||item?.payStatus == "5" ) {
                R.drawable.bg_border_green
            } else {
                R.drawable.bg_border_red
                }

            val statusColor = if (item?.payStatus == "4" ||item?.payStatus == "5" ) {
                R.color.text_color_dialog_green
            } else {
                R.color.text_color_dialog_red
            }


            tvTitleStatus.setTextColor(ContextCompat.getColor(binding.tvTitleStatus.context,statusColor))
            tvTitleStatus.setBackgroundResource(background)
            tvTitleStatus.setTextViewDrawableColor(statusColor)
            tvTitleStatus.text = "وضعیت پرداخت: ${item?.payStatusDesc}"

            btnShowDetail.setOnClickListener {
                detailAdapter.toggleMinifyMode()
                btnShowDetail.text = if (detailAdapter.isMinifyMode())
                    mContext?.getString(R.string.show_detail) else mContext?.getString(R.string.hide_detail)
                if (detailAdapter.isMinifyMode()) btnAction.gone() else btnAction.visible()
            }

            btnAction.setOnClickListener {
                if (item != null)
                    onItemClickListener?.onItemClick(item = item)
                else
                    btnAction.disableButton()
            }
        }
    }

    override fun initViewHolder(binding: ListItemTreatmentCostsExpensesBinding, itemView: View) {
    }

    object DiffCallback : DiffUtil.ItemCallback<TreatmentCostsExpensesModel>() {
        override fun areItemsTheSame(
            oldItem: TreatmentCostsExpensesModel,
            newItem: TreatmentCostsExpensesModel
        ) = oldItem.noPazir == newItem.noPazir

        override fun areContentsTheSame(
            oldItem: TreatmentCostsExpensesModel,
            newItem: TreatmentCostsExpensesModel
        ) = oldItem == newItem


    }
}