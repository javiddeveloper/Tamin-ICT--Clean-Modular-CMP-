package com.tamin.taminhamrah.ui.home.services.deservedTreatment.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved.DeservedTreatment
import com.tamin.taminhamrah.databinding.ListItemDeservedTreatmentStatusBinding

class DeservedTreatmentStatusAdapter:
    RecyclerView.Adapter<DeservedTreatmentStatusAdapter.ItemViewHolder>()  {
    private var mItems = emptyList<DeservedTreatment>()

    fun setItems(
        items: List<DeservedTreatment>
    ) {
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemDeservedTreatmentStatusBinding.inflate(inflater, parent, false))
    }


    override fun onBindViewHolder(holder:ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.item = item

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemDeservedTreatmentStatusBinding) :
        RecyclerView.ViewHolder(binding.root)




    class DiffCallback(
        private val mOldList: List<DeservedTreatment>,
        private val mNewList: List<DeservedTreatment>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].trackingCode == mNewList[newItemPosition].trackingCode
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }
}
