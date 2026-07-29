package com.tamin.taminhamrah.ui.home.services.disabilityPension.medicalCommission.registerdRequest.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.medicalCommission.RegisteredMedicalCommissionModel
import com.tamin.taminhamrah.databinding.ListItemRegisteredRequestMedicalCommissionBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils

class RegisteredRequestAdapter (var onItemClickListener: AdapterInterface.OnItemClickListener<RegisteredMedicalCommissionModel>? = null) :
    BasePagingAdapter<RegisteredMedicalCommissionModel, ListItemRegisteredRequestMedicalCommissionBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId() = R.layout.list_item_registered_request_medical_commission


    override fun bindItem(
        binding: ListItemRegisteredRequestMedicalCommissionBinding,
        item: RegisteredMedicalCommissionModel?,
        position: Int
    ) {
        with(binding) {
            val detailAdapter = ExpandableListAdapter(expandingIndex = 2)
            binding.recycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = detailAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(context))
            }
            detailAdapter.setItems(item?.getMainInfo() ?: emptyList())
            this.dataModel = item ?: return
            binding.btnShowDetail.setOnClickListener {
                val context = binding.root.context
                detailAdapter.toggleMinifyMode()
                btnShowDetail.text = if (detailAdapter.isMinifyMode())
                    context.getString(R.string.show_detail) else context.getString(R.string.hide_detail)
            }
        }
    }

    override fun initViewHolder(binding: ListItemRegisteredRequestMedicalCommissionBinding, itemView: View) {
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<RegisteredMedicalCommissionModel>() {
        override fun areItemsTheSame(
            oldItem: RegisteredMedicalCommissionModel,
            newItem: RegisteredMedicalCommissionModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RegisteredMedicalCommissionModel,
            newItem: RegisteredMedicalCommissionModel
        ): Boolean {
            return oldItem == newItem
        }
    }


}
