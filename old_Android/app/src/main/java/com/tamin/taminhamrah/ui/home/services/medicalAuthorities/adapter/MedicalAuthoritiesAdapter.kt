package com.tamin.taminhamrah.ui.home.services.medicalAuthorities.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.medicalAuthorities.MedicalAuthoritiesModel
import com.tamin.taminhamrah.databinding.ListItemMedicalAuthoritiesUiBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.UiUtils

class MedicalAuthoritiesAdapter :
    BasePagingAdapter<MedicalAuthoritiesModel, ListItemMedicalAuthoritiesUiBinding>(DiffCallback) {

    var mContext: Context? = null

    override fun getLayoutResId(): Int {
        return R.layout.list_item_medical_authorities_ui
    }
    override fun bindItem(
        binding: ListItemMedicalAuthoritiesUiBinding,
        item: MedicalAuthoritiesModel?,
        position: Int
    ) {

        val detailAdapter = ExpandableListAdapter(expandingIndex = 3)
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = detailAdapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(context))
        }
        binding.apply {
            mContext = binding.root.context
            tvValueTreatmentCenter.text = item?.treatmentCenter ?: "-"
            tvValueStatusMedicalAuthority.text = item?.confirmStatus ?: "-"
            tvValueConfirmBranch.text = item?.confirmInBranch ?: "-"
            detailAdapter.setItems(item?.exportKayValue() ?: emptyList())

            btnShowDetail.setOnClickListener {
                detailAdapter.toggleMinifyMode()
                btnShowDetail.text = if (detailAdapter.isMinifyMode())
                    mContext?.getString(R.string.show_detail) else mContext?.getString(R.string.hide_detail)
            }

        }
    }

    override fun initViewHolder(binding: ListItemMedicalAuthoritiesUiBinding, itemView: View) {}
}

object DiffCallback : DiffUtil.ItemCallback<MedicalAuthoritiesModel>() {
    override fun areItemsTheSame(
        oldItem: MedicalAuthoritiesModel,
        newItem: MedicalAuthoritiesModel
    ): Boolean {
        return oldItem.insuranceNumber == newItem.insuranceNumber
    }

    override fun areContentsTheSame(
        oldItem: MedicalAuthoritiesModel,
        newItem: MedicalAuthoritiesModel
    ): Boolean {
        return oldItem == newItem
    }
}