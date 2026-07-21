package com.tamin.taminhamrah.ui.home.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.ServiceModel
import com.tamin.taminhamrah.databinding.ListItemServiceBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.utils.ImageUtils

class AppliedServiceAdapter : RecyclerView.Adapter<AppliedServiceAdapter.ItemViewHolder>() {
    companion object{
        const val tag ="AppliedServiceAdapterTag"
    }
     var onItemClickListener: AdapterInterface.OnItemClickListener<ServiceModel>? = null
//    set(value) {
//        onItemClickListener = value
//    }
    private var isEmployer: Boolean? = false
    private var isSearchList: Boolean = false

    private var itemTypeSearch = 0
    private var defaultItemType = 1

    private var mItems: MutableList<ServiceModel> = ArrayList()
    fun setItems(items: List<ServiceModel>) {

        this.isEmployer = isEmployer
        this.isSearchList = isSearchList
        this.onItemClickListener = onItemClickListener

        mItems.clear()
        mItems.addAll(items)
        notifyItemChanged(0, items.size)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(
            ListItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]

        holder.binding.apply {

            this.item = item
            this.isEmployer = isEmployer
            setItemIsActive(item.active, holder.itemView)
            ImageUtils.loadImage(imgIcon, item.getImageUrl())

            bg.setBackgroundResource(R.drawable.bg_applied_service)
            container.setOnClickListener {
                onItemClickListener?.onItemClick(
                    item,
                    (holder as? ItemViewHolder)?.binding?.imgIcon
                )
            }
        }

    }

    private fun setItemIsActive(active: Boolean, itemView: View) {
        if (active) {
            itemView.alpha = 1f
        } else {
            itemView.alpha = 0.5f
        }
    }

    override fun getItemCount(): Int {
        return mItems.size

    }


    class ItemViewHolder(var binding: ListItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root)


}