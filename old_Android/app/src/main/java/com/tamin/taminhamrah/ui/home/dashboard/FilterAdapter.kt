package com.tamin.taminhamrah.ui.home.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ServiceMainModel
import com.tamin.taminhamrah.databinding.ListItemFilterBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

@Deprecated("We removed it")
class FilterAdapter(private val onItemClickListener: AdapterInterface.OnItemClickListener<MenuModel>):
    RecyclerView.Adapter<FilterAdapter.ItemViewHolder>() {

    private var mItems = emptyList<MenuModel>()
    fun setItems(
        items: List<MenuModel>
    ) {
        mItems = items
        notifyItemRangeChanged(0, mItems.size)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            ListItemFilterBinding.inflate(inflater, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.apply {
            this.item = item
            cbFilter.setOnCheckedChangeListener { buttonView, isChecked ->
               if (!isChecked){
                   onItemClickListener.onItemClick(item)
               }
            }
        }

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<ServiceMainModel>,
        private val mNewList: List<ServiceMainModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].title == mNewList[newItemPosition].title
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }
}
