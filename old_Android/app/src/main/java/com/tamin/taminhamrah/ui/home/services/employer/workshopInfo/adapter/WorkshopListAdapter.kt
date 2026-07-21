package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.Constants.WORKSHOP_STATUS_ENABLE
import com.tamin.taminhamrah.Constants.WORKSHOP_STATUS_SEMI_ACTIVE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.databinding.ListItemWorkshopDefinitiveDebtBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.disableButton
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.setTextViewDrawableColor
import com.tamin.taminhamrah.utils.extentions.visible

class WorkshopListAdapter : BasePagingAdapter<EmployerAgreement,ListItemWorkshopDefinitiveDebtBinding>(DiffCallback) {
    override fun getLayoutResId() = R.layout.list_item_workshop_definitive_debt

    var onItemClickListener: AdapterInterface.OnItemClickListener<EmployerAgreement>? = null
    var mContext : Context? = null


    override fun bindItem(
        binding: ListItemWorkshopDefinitiveDebtBinding,
        item: EmployerAgreement?,
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
            tvValueWorkShopName.text = item?.workshop?.workshopName ?: "-"
            tvWorkShopCode.text = item?.workshop?.workshopId?: "-"
            detailAdapter.setItems(item?.getDetailWorkShop() ?: emptyList())

            val statusColor = when(item?.workshop?.workshopStatus?.workshopStatusCode ?: 0) {
                WORKSHOP_STATUS_SEMI_ACTIVE -> {
                    R.color.text_color_dialog_orange
                }
                WORKSHOP_STATUS_ENABLE -> {
                    R.color.text_color_dialog_green
                }
                else -> {
                    R.color.text_color_dialog_red
                }
            }

            val background = when(item?.workshop?.workshopStatus?.workshopStatusCode ?: 0) {
                WORKSHOP_STATUS_SEMI_ACTIVE -> {
                    R.drawable.bg_border_orange
                }
                WORKSHOP_STATUS_ENABLE -> {
                    R.drawable.bg_border_green
                }
                else -> {
                    R.drawable.bg_border_red
                }
            }

            tvTitleStatus.setTextColor(ContextCompat.getColor(binding.tvTitleStatus.context,statusColor))
            tvTitleStatus.setBackgroundResource(background)
            tvTitleStatus.setTextViewDrawableColor(statusColor)
            tvTitleStatus.text = "وضعیت: ${item?.workshop?.workshopStatus?.workshopStatusDesc}"

            btnShowDetail.setOnClickListener {
                detailAdapter.toggleMinifyMode()
                btnShowDetail.text = if (detailAdapter.isMinifyMode())
                    mContext?.getString(R.string.show_detail) else mContext?.getString(R.string.hide_detail)
                if (detailAdapter.isMinifyMode()) btnAction.gone() else btnAction.visible()
            }

            btnAction.setOnClickListener {
                if (item!=null)
                onItemClickListener?.onItemClick(item=item)
                else
                    btnAction.disableButton()
            }
        }
    }

    override fun initViewHolder(binding: ListItemWorkshopDefinitiveDebtBinding, itemView: View) {
    }

    object DiffCallback : DiffUtil.ItemCallback<EmployerAgreement>() {
        override fun areItemsTheSame(
            oldItem: EmployerAgreement,
            newItem: EmployerAgreement
        ) =  oldItem.workshop?.workshopId == newItem.workshop?.workshopId

        override fun areContentsTheSame(
            oldItem: EmployerAgreement,
            newItem: EmployerAgreement
        ) = oldItem == newItem


    }
}