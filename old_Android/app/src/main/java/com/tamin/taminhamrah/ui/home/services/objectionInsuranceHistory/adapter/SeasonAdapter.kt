package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.ListItemSeasonDetailHistoryBinding
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.model.SeasonEnumClass
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.model.SeasonModel
import com.tamin.taminhamrah.utils.UiUtils

class SeasonAdapter :
    RecyclerView.Adapter<SeasonAdapter.ItemViewHolder>() {

    private lateinit var diffResult: DiffUtil.DiffResult
    private var mItems = ArrayList<SeasonModel>()
    private var orgInfo =  ArrayList<SeasonModel>()
    lateinit var context: Context
    private lateinit var monthAdapter: MonthsHistoryAdapter
    fun setItems(items: ArrayList<SeasonModel>) {
        val diffCallback = DiffCallback(mItems, items)
        diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        orgInfo = items
        diffResult.dispatchUpdatesTo(this)
    }

    fun resetInfo(){
        mItems.forEach { season ->
            season.monthValues.forEach { month ->
                month.editedValue = "00"
            }
        }

        // Clear the existing data in the MonthsHistoryAdapter and update it with the new data
        monthAdapter.setItems(mItems.flatMap { it.monthValues }) { monthPosition, editedValue ->
            val seasonPosition = mItems.indexOfFirst { season ->
                monthPosition in season.monthValues.indices
            }
            mItems[seasonPosition].monthValues[monthPosition].editedValue = editedValue
        }
        notifyDataSetChanged()

    }

    fun getItems() = mItems

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        monthAdapter = MonthsHistoryAdapter()
        context = parent.context

        val holder = ItemViewHolder(
            ListItemSeasonDetailHistoryBinding.inflate(
                inflater,
                parent,
                false
            )
        )
        holder.binding.detailRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = monthAdapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(context))
        }
        return holder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        val drawable = when (item.season) {
            SeasonEnumClass.SPRING -> {
                R.drawable.bg_item_spring
            }

            SeasonEnumClass.SUMMER -> {
                R.drawable.bg_item_summer
            }

            SeasonEnumClass.FALL -> {
                R.drawable.bg_item_fall
            }

            SeasonEnumClass.WINTER -> {
                R.drawable.bg_item_winter
            }
        }
        holder.binding.root.background = ContextCompat.getDrawable(context, drawable)

        val dataList = mItems[position].monthValues
        if (dataList.isNotEmpty()) {
            monthAdapter.setItems(dataList) { monthPosition, editedValue ->
                mItems[position].monthValues[monthPosition].editedValue = editedValue
            }
        }
    }

    override fun getItemCount() = mItems.size

    class ItemViewHolder(var binding: ListItemSeasonDetailHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<SeasonModel>,
        private val mNewList: List<SeasonModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].season == mNewList[newItemPosition].season
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]
            return (newItem == oldItem)
        }
    }
}
