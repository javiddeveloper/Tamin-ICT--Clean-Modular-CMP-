package com.tamin.taminhamrah.ui.home.services.inquirePensionStatus.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.inquirePensionStatus.InquirePensionStatusModel
import com.tamin.taminhamrah.databinding.ListItemInquirePensionStatusBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.extentions.setTextViewDrawableColor


class InquirePensionStatusAdapter (var onItemClickListener: AdapterInterface.OnItemClickListener<Boolean>? = null):
    BasePagingAdapter<InquirePensionStatusModel,ListItemInquirePensionStatusBinding>(
    DiffCallback
) {


    override fun getLayoutResId(): Int {
        return R.layout.list_item_inquire_pension_status
    }

    override fun bindItem(
        binding: ListItemInquirePensionStatusBinding,
        itemInput: InquirePensionStatusModel?,
        position: Int
    ) {
        binding.apply {
            item = itemInput
            val statusDesc = item?.statusDesc ?: ""
            val background = when (statusDesc) {
                "01" -> {
                    R.drawable.bg_border_green
                }

                else -> {
                    R.drawable.bg_border_red
                }
            }

            val drawableColor = when (statusDesc) {
                "01" -> {
                    R.color.text_color_dialog_green
                }

                else -> {
                    R.color.text_color_dialog_red
                }
            }
            valueStatus.setTextColor(
                ContextCompat.getColor(
                    binding.valueStatus.context,
                    drawableColor
                )
            )
            valueStatus.setTextViewDrawableColor(drawableColor)
            valueStatus.setBackgroundResource(background)

            tvSendRequest.setOnClickListener {
                onItemClickListener?.onItemClick(true)
            }
        }
    }

    override fun initViewHolder(binding: ListItemInquirePensionStatusBinding, itemView: View) {
    }

    object DiffCallback : DiffUtil.ItemCallback<InquirePensionStatusModel>() {
        override fun areItemsTheSame(
            oldItem: InquirePensionStatusModel,
            newItem: InquirePensionStatusModel
        ): Boolean {
            return oldItem.insuranceNumber == newItem.insuranceNumber
        }
        override fun areContentsTheSame(
            oldItem: InquirePensionStatusModel,
            newItem: InquirePensionStatusModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}
