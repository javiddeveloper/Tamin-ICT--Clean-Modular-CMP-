package com.tamin.taminhamrah.ui.home.services.employer.contract.computationalBase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBase
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBaseSection
import com.tamin.taminhamrah.databinding.ListItemComputationalBaseSectionBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.scaleY

class ComputationalBaseSectionAdapter(private val onItemClickListener: AdapterInterface.OnItemClickListener<ComputationalBase.DataDetail>? = null): RecyclerView.Adapter<ComputationalBaseSectionAdapter.ItemViewHolder>() {

    private var mItems = emptyList<ComputationalBaseSection>()
    fun setItems(
        items: List<ComputationalBaseSection>
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
            ListItemComputationalBaseSectionBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            this.item =  mItems[position]

            if (item?.dataList?.isNullOrEmpty()!=null) {
                val adapter = KeyValueAdapter()
                recyclerInfo.apply {
                    if (itemDecorationCount == 0) {
                        addItemDecoration(UiUtils.createDivider(context))
                    }
                    this.adapter = adapter

                    item?.dataList?.let { adapter.setItems(it) }
                }
            }else if (item?.attachmentList?.isNullOrEmpty()!=null){

                val adapter = ComputationalBaseImageAdapter(onItemClickListener/*,sectionTitle= item.title*/)
                recyclerInfo.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, true)
                    if (itemDecorationCount == 0) {
                        addItemDecoration(UiUtils.HorizontalItemMarginDecoration(60))
                    }
                    this.adapter = adapter

                    item?.attachmentList?.let { adapter.setItems(it) }
                }
            }

            btnExpand.setOnClickListener {
                btnExpand.scaleY(state = (btnExpand.scaleY == -1f))
                if (recyclerInfo.visibility==View.VISIBLE){
                    recyclerInfo.visibility =View.GONE
                }else{
                    recyclerInfo.visibility =View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemComputationalBaseSectionBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<ComputationalBaseSection>,
        private val mNewList: List<ComputationalBaseSection>
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
