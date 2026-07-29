package com.tamin.taminhamrah.ui.home.services.disabilityPension.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityDependentModel
import com.tamin.taminhamrah.databinding.ItemDependentDisabilityPensionBinding
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class DependentInfoAdapter() :
    RecyclerView.Adapter<DependentInfoAdapter.ItemViewHolder>() {

    private lateinit var binding: ItemDependentDisabilityPensionBinding
    private val mItems = ArrayList<DisabilityDependentModel>()

    fun setItems(items: List<DisabilityDependentModel>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(mItems, items), true)
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = ItemDependentDisabilityPensionBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return ItemViewHolder()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        val context = binding.root.context
        val deceasedInfoAdapter by lazy { KeyValueAdapter() }

        binding.apply {
            tvFullName.text = context.getString(R.string.space,
                item.relationWithTamin.personal.firstName,
                item.relationWithTamin.personal.lastName)
            tvRelation.text = item.relationWithTamin.personal.relation?.ifBlank { "_" }
            recyclerDetail.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = deceasedInfoAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(context))
                isVisible = false
            }
            deceasedInfoAdapter.setItems(item.relationWithTamin.getPersonalInfo())
            btnShowDetail.setOnClickListener {
                btnShowDetail.text = if (recyclerDetail.isVisible)
                    context.getString(R.string.show_detail)
                else
                    context.getString(R.string.hide_detail)
                recyclerDetail.isVisible = !recyclerDetail.isVisible
            }
        }
    }

    override fun getItemCount() = mItems.size

    inner class ItemViewHolder : RecyclerView.ViewHolder(binding.root)

    class DiffCallback(
        private val mOldList: List<DisabilityDependentModel>,
        private val mNewList: List<DisabilityDependentModel>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize()= mOldList.size


        override fun getNewListSize()= mNewList.size


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            mOldList[oldItemPosition].relationWithTamin.personal.nationalId == mNewList[newItemPosition].relationWithTamin.personal.nationalId


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition].relationWithTamin
            val newItem = mNewList[newItemPosition].relationWithTamin

            return (newItem == oldItem)
        }

    }
}