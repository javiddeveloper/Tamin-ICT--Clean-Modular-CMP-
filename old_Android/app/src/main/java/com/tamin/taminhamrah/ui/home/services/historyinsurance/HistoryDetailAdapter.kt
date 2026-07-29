package com.tamin.taminhamrah.ui.home.services.historyinsurance
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryInsuranceResponseModel
import com.tamin.taminhamrah.databinding.ListItemHistoryInsuranceByMonthBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface


class HistoryDetailAdapter :
    RecyclerView.Adapter<HistoryDetailAdapter.ItemViewHolder>() {

     var onShowMoreClickListener: AdapterInterface.OnShowMoreClickListener<Int>? = null

    private var mItems = emptyList<AllHistoryInsuranceResponseModel>()
    fun setItems(
        items: ArrayList<AllHistoryInsuranceResponseModel>,
        onShowMoreClickListener: AdapterInterface.OnShowMoreClickListener<Int>? = null,
    ) {

        this.onShowMoreClickListener = onShowMoreClickListener
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemHistoryInsuranceByMonthBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = mItems[position]
        currentItem.let {
            holder.binding.item = currentItem
        }
    }



    override fun getItemCount() = mItems.size

    class ItemViewHolder(var binding: ListItemHistoryInsuranceByMonthBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<AllHistoryInsuranceResponseModel>,
        private val mNewList: List<AllHistoryInsuranceResponseModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].id == mNewList[newItemPosition].id
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]
            return (newItem == oldItem)
        }
    }


}
