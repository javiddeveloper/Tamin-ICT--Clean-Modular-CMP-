package com.tamin.taminhamrah.ui.home.services.viewEdictPensioner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.databinding.ListItemEdictPensionBinding
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.scaleY

class EdictDetailInfoAdapter : RecyclerView.Adapter<EdictDetailInfoAdapter.ItemViewHolder>() {

    private lateinit var binding: ListItemEdictPensionBinding

    private var mItems : MutableMap<String, List<KeyValueModel>> = mutableMapOf()

    fun setItems(items : MutableMap<String,List<KeyValueModel>>){
        val diffResult = DiffUtil.calculateDiff(DiffCallback(mItems, items), true)
        mItems.clear()
        mItems.putAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = ListItemEdictPensionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems.toList()[position]
        val context = holder.itemView.context
        val rowAdapter= EdictRowAdapter()

        binding.apply {
            if(position == 0) {
                rcvDetail.isVisible =true
                btnExpand.scaleY(state = (btnExpand.scaleY == -1f))
            }
            tvTitle.text = item.first
            rcvDetail.apply {
                adapter = rowAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(context))
                }
            }

            rowAdapter.setItems(item.second)

            tvTitle.setOnClickListener {
                btnExpand.scaleY(state = (btnExpand.scaleY == -1f))
                rcvDetail.isVisible = !(rcvDetail.isVisible)
            }
            btnExpand.setOnClickListener {
                    btnExpand.scaleY(state = (btnExpand.scaleY == -1f))
                    rcvDetail.isVisible = !(rcvDetail.isVisible)
            }
        }
    }

    override fun getItemCount()= mItems.size

    inner class ItemViewHolder : RecyclerView.ViewHolder(binding.root)

    class DiffCallback(
        private val mOldList: MutableMap<String, List<KeyValueModel>>,
        private val mNewList: MutableMap<String, List<KeyValueModel>>
    ) : DiffUtil.Callback() {

        override fun getOldListSize()= mOldList.size

        override fun getNewListSize()= mNewList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)=
            mOldList.toList()[oldItemPosition].second == mNewList.toList()[oldItemPosition].second


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList.toList()[oldItemPosition].second
            val newItem =mNewList.toList()[oldItemPosition].second

            return (newItem == oldItem)
        }

    }

}