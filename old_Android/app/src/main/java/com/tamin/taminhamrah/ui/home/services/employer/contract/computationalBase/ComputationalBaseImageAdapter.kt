package com.tamin.taminhamrah.ui.home.services.employer.contract.computationalBase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBase.DataDetail
import com.tamin.taminhamrah.databinding.ListItemImageComputationalBaseBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.utils.ImageUtils

class ComputationalBaseImageAdapter(
    private val onItemClickListener: OnItemClickListener<DataDetail>? = null,
    val sectionTitle: String = ""
) :
    RecyclerView.Adapter<ComputationalBaseImageAdapter.ItemViewHolder>() {

    private var mItems = emptyList<DataDetail>()
    fun setItems(
        items: List<DataDetail>
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
            ListItemImageComputationalBaseBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]


        holder.binding.apply {
            this.item = item
            if (item.documentType == "1") { //is image
                ImageUtils.loadImageBase64(view = imagePreview, item.attachmentUrl)

            } else if (item.documentType == "2") {
                ImageUtils.setImageResourceDrawable(
                    view = imagePreview,
                    R.drawable.ic_pdf
                )
                ImageUtils.setImageResourceDrawable(
                    view = imagePreviewFullSize,
                    R.drawable.ic_download
                )
            }
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(item, tag = sectionTitle)
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemImageComputationalBaseBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<DataDetail>,
        private val mNewList: List<DataDetail>
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
