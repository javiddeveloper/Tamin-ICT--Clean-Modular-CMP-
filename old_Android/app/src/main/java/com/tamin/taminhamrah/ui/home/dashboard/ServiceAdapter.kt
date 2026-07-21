package com.tamin.taminhamrah.ui.home.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.ServiceModel
import com.tamin.taminhamrah.databinding.ListItemSearchServiceBinding
import com.tamin.taminhamrah.databinding.ListItemServiceBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.utils.ImageUtils
import timber.log.Timber

@Deprecated("We removed it")
class ServiceAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener<ServiceModel>? = null
    private var isEmployer: Boolean? = false
    private var isSearchList: Boolean = false

    private var itemTypeSearch = 0
    private var defaultItemType = 1

    private var mItems = emptyList<ServiceModel>()
    fun setItems(
        items: List<ServiceModel>,
        onItemClickListener: OnItemClickListener<ServiceModel>? = null,
        isEmployer: Boolean? = null,
        isSearchList: Boolean = false
    ) {
        Timber.tag("MainServiceAdapter").e("ServiceAdapter setItems : called ")

        this.isEmployer = isEmployer
        this.isSearchList = isSearchList
        this.onItemClickListener = onItemClickListener

        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSearchList) {
            itemTypeSearch
        } else
            defaultItemType
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == defaultItemType) {
            ItemViewHolder(
                ListItemServiceBinding.inflate(inflater, parent, false)
            )
        } else {
            return SearchItemViewHolder(
                ListItemSearchServiceBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mItems[position]
        if (isSearchList) {
            (holder as? SearchItemViewHolder)?.binding?.apply {
                this.item = item

                setItemIsActive(item.active, holder.itemView)

                 ImageUtils.loadImage(imgIcon, item.getImageUrl())


                    container.setOnClickListener {
                        onItemClickListener?.onItemClick(
                            item,
                            (holder as? ItemViewHolder)?.binding?.imgIcon
                        )
                    }
            }

        } else {
            (holder as? ItemViewHolder)?.binding?.apply {

                this.item = item
                this.isEmployer = isEmployer
                setItemIsActive(item.active, holder.itemView)
                ImageUtils.loadImage(imgIcon, item.getImageUrl())

                container.setOnClickListener {
                    onItemClickListener?.onItemClick(
                        item,
                        (holder as? ItemViewHolder)?.binding?.imgIcon
                    )
                }
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

    class SearchItemViewHolder(var binding: ListItemSearchServiceBinding) :
        RecyclerView.ViewHolder(binding.root)


    class ItemViewHolder(var binding: ListItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DiffCallback(
        private val mOldList: List<ServiceModel>,
        private val mNewList: List<ServiceModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].name == mNewList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }
    }
}
