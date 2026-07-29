package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.databinding.ListItemDistantCorrespondenceRequestBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.invisible

class CorrespondenceAdapter(
    private val onItemClickListener: AdapterInterface.OnDeleteClickListener<KeyValueModel>? = null,
    private val viewOnly: Boolean? = false
) :
    RecyclerView.Adapter<CorrespondenceAdapter.ItemViewHolder>() {
    var diffCallback= DiffCallback(emptyList(), emptyList())


    private var mItems: MutableList<KeyValueModel> = ArrayList()
    fun setItems(
        items: ArrayList<KeyValueModel>
    ) {
        diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearData() {
        mItems.clear()
      //  diffCallback.notifyAll()
    }

    fun deleteItem(item: KeyValueModel) {
        mItems.remove(item)
     //   diffCallback.notifyAll()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(
            ListItemDistantCorrespondenceRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]

        holder.binding.apply {

            recyclerInfo.apply {
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.createDivider(context))
                KeyValueAdapter()
                setItems(ArrayList(mItems))
            }
            if (viewOnly==true){
             btnDelete.invisible()
            }else {
                btnDelete.setOnClickListener { onItemClickListener?.onDelete(item) }
            }
        }

    }

    override fun getItemCount(): Int {
        return mItems.size

    }

    class ItemViewHolder(var binding: ListItemDistantCorrespondenceRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DiffCallback(
        private val mOldList: List<KeyValueModel>,
        private val mNewList: List<KeyValueModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition] == mNewList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }

}