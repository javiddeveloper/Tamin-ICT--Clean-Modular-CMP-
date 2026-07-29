package com.tamin.taminhamrah.ui.home.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.ServiceMainModel
import com.tamin.taminhamrah.data.entity.ServiceModel
import com.tamin.taminhamrah.databinding.ListItemServiceMainBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnShowMoreClickListener
import com.tamin.taminhamrah.utils.UiUtils
import timber.log.Timber


@Deprecated("Replaced with DashboardFragment")
class ServiceMainAdapter() :
    RecyclerView.Adapter<ServiceMainAdapter.ItemViewHolder>() {
    var onItemClickListener: OnShowMoreClickListener<ServiceMainModel>? = null
    var onChildItemClickListener: OnItemClickListener<ServiceModel>? = null


    //    private var isEmployer: Boolean? = false
    private var mItems : MutableList<ServiceMainModel> = ArrayList()

    fun setItems(
        items: List<ServiceMainModel>,
        clickListener: OnShowMoreClickListener<ServiceMainModel>?,
        childClickListener: OnItemClickListener<ServiceModel>?
    ) {
        Timber.tag("MainServiceAdapter").i("setItems:called")

        this.onItemClickListener = clickListener
        this.onChildItemClickListener = childClickListener
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
        notifyItemChanged(0, mItems.size)
    }

    fun getItems(): List<ServiceMainModel> {
        return mItems
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemServiceMainBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[holder.bindingAdapterPosition]
        holder.binding.item = item
        val serviceAdapter = ServiceAdapter()
        Timber.tag("MainServiceAdapter").i("onBindViewHolder: position=" + holder.bindingAdapterPosition)
        holder.binding.apply {
            recycler.apply {
                this.adapter = serviceAdapter
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.HorizontalItemMarginDecoration(40))
            }

            tvSeeAll.setOnClickListener {
                onItemClickListener?.onShowMoreClick(item)
            }

            recycler.clearOnScrollListeners()
            recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    Timber.tag("MainServiceScroll")
                        .i("onScrollStateChanged " + holder.bindingAdapterPosition + " is " + (recycler.layoutManager as LinearLayoutManager).onSaveInstanceState())
                    mItems[holder.bindingAdapterPosition].scrollState =
                        (recycler.layoutManager as LinearLayoutManager).onSaveInstanceState()
                }

            })


        }
        item.serviceList?.let {

            serviceAdapter.setItems(
                it,
                onChildItemClickListener,
                item.type == 1
            )
            if (item.scrollState != null) {
                Timber.tag("MainServiceScroll")
                    .e(" position=" + holder.bindingAdapterPosition + " scrollState : NOT NULL")

                (holder.binding.recycler.layoutManager as LinearLayoutManager).onRestoreInstanceState(
                    item.scrollState
                )
            } else
                Timber.tag("MainServiceScroll").e(" position=" + holder.bindingAdapterPosition + " scrollState :  NULL")

        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemServiceMainBinding) :
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
            return mOldList[oldItemPosition].title == mNewList[newItemPosition].title ||
                    mOldList[oldItemPosition].type == mNewList[newItemPosition].type
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }

}
