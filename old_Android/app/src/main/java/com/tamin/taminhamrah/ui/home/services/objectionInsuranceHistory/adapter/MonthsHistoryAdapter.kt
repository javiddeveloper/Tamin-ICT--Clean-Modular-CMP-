package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.databinding.ListItemMonthHistoryBinding
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.model.MonthModel
import com.tamin.taminhamrah.utils.extentions.isNumericString

class MonthsHistoryAdapter() :
    RecyclerView.Adapter<MonthsHistoryAdapter.ItemViewHolder>() {
    private var mItems = emptyList<MonthModel>()
    lateinit var context: Context
    lateinit var onValueChanged: ((monthPosition: Int, editedValue: String) -> Unit)
    fun setItems(
        items: List<MonthModel>,
        listener: (monthPosition: Int, editedValue: String) -> Unit
    ) {
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
        onValueChanged = listener
    }
    fun getItems() = mItems
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return ItemViewHolder(
            ListItemMonthHistoryBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = mItems[position]
        holder.binding.apply  {
          //  tvTitleMonth.text = DateConverter().getMonthName(month-1)
            tvTitleMonth.text =  currentItem.monthName
            tvValueMonth.text = currentItem.registeredValue
            if(currentItem.editedValue.isNotBlank() && currentItem.editedValue.isNumericString()) {
                if (currentItem.editedValue.toInt() > 0)
                    tvDayObjection.setInitText(currentItem.editedValue)
            }
            tvDayObjection.getEditText().doAfterTextChanged {
                var newValue = tvDayObjection.getEditText().text.toString()
                if (newValue.isNotBlank() && newValue.isNumericString()) {
                    if (newValue.toInt()>currentItem.maxDayAvailable) {
                        newValue = currentItem.maxDayAvailable.toString()
                        tvDayObjection.setInitText(currentItem.maxDayAvailable.toString())
                    }

                    mItems[position].editedValue = newValue
                    onValueChanged(position , newValue) // Notify the listener that a value has changed
                }else{
                    mItems[position].editedValue = ""
                    onValueChanged(position , "")
                }
            }

        }
    }

    override fun getItemCount() = mItems.size

    class ItemViewHolder(var binding: ListItemMonthHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DiffCallback(
        private val mOldList: List<MonthModel>,
        private val mNewList: List<MonthModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].monthName == mNewList[newItemPosition].monthName
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]
            return (newItem == oldItem)
        }
    }
}
