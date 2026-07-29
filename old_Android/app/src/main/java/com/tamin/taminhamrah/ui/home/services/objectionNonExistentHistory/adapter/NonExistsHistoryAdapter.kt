package com.tamin.taminhamrah.ui.home.services.objectionNonExistentHistory.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.NotExistRequestsModel
import com.tamin.taminhamrah.databinding.ListItemObjectionNonExistsBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.extentions.gone
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat


class NonExistsHistoryAdapter :
    BasePagingAdapter<NotExistRequestsModel, ListItemObjectionNonExistsBinding>(DiffCallback) {

    var onClickListener: AdapterInterface.OnActionResultInterface<NotExistRequestsModel?>? = null


    override fun getLayoutResId(): Int {
        return R.layout.list_item_objection_non_exists
    }

    override fun bindItem(
        binding: ListItemObjectionNonExistsBinding,
        item: NotExistRequestsModel?,
        position: Int
    ) {
        binding.apply {
                itemInput = item
                isEmployer = isEmployer

            itemInsuranceCode.tvInsuranceCodeValue.text = item?.risuid ?: "0"

            if (item?.rwshid.isNullOrBlank())
                itemWorkshopCode.root.gone()
            else
                itemWorkshopCode.tvWorkShopCodeValue.text = item?.rwshid

            tvValueStartDate.text =
                    PersianDateFormat.format(PersianDate(item?.startDate), "y/m/d")
                tvValueEndDate.text =
                    PersianDateFormat.format(PersianDate(item?.endDate), "y/m/d")

                btnDelete.setOnClickListener { onClickListener?.onDeleteResult(item) }
                btnEdit.setOnClickListener { onClickListener?.onEditResult(item) }
            }

}

override fun initViewHolder(binding: ListItemObjectionNonExistsBinding, itemView: View) {
}
}
object DiffCallback : DiffUtil.ItemCallback<NotExistRequestsModel>() {
    override fun areItemsTheSame(
        oldItem: NotExistRequestsModel,
        newItem: NotExistRequestsModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: NotExistRequestsModel,
        newItem: NotExistRequestsModel
    ): Boolean {
        return oldItem == newItem
    }

}


