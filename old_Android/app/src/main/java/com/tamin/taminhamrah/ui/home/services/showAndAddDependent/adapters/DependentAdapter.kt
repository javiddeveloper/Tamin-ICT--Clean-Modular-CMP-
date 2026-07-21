package com.tamin.taminhamrah.ui.home.services.showAndAddDependent.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentDataModel
import com.tamin.taminhamrah.databinding.ListItemDependentMainBinding
import com.tamin.taminhamrah.ui.adapters.ExpandableListAdapter
import com.tamin.taminhamrah.utils.UiUtils

class DependentAdapter : RecyclerView.Adapter<DependentAdapter.ItemViewHolder>() {

    private val mItems = ArrayList<DependentDataModel>()

    fun setItems(items: List<DependentDataModel>) {
        val oldSize = items.size
        mItems.clear()
        mItems.addAll(items)
        notifyItemRangeChanged(0, oldSize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(ListItemDependentMainBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val expandableAdapter = ExpandableListAdapter(expandingIndex = 2)
        holder.binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expandableAdapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(context))
        }

        expandableAdapter.setItems(mItems[position].getDependentInfo())

        holder.binding.btnShowDetail.setOnClickListener {
            expandableAdapter.toggleMinifyMode()
            holder.binding.btnShowDetail.text =
                if (expandableAdapter.isMinifyMode())
                    it.context.getString(R.string.show_detail) else it.context.getString(R.string.hide_detail)
        }
    }

    override fun getItemCount() = mItems.size

    class ItemViewHolder(var binding: ListItemDependentMainBinding) :
        RecyclerView.ViewHolder(binding.root)
}

