package com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.mafasahesab

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.UploadedFileModel
import com.tamin.taminhamrah.databinding.ListItemPdfPreviewBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class PDFPreviewAdapter(private val onItemClickListener: AdapterInterface.OnItemClickListener<UploadedFileModel>) :
    RecyclerView.Adapter<PDFPreviewAdapter.ItemViewHolder>() {

    private var mItems = emptyList<UploadedFileModel>()
    //tODO:: replace notifyDataSetChanged with notifyItemChange and notifyItemRemove and ....

    fun setItems(items: List<UploadedFileModel>) {

        mItems = items.toMutableList()
        notifyDataSetChanged()
    }

    fun addItem(item: UploadedFileModel) {
        mItems.toMutableList().add(item)
        notifyItemInserted(0)
    }

    fun clearItems() {
        mItems = emptyList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(item: UploadedFileModel) {
        mItems = mItems.filterNot { it == item }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemPdfPreviewBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.item = item

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item, tag = Constants.PDF_PREVIEW_TAG)
        }

        holder.binding.btnDeleteImage.setOnClickListener {
            onItemClickListener.onItemClick(item, tag = Constants.PDF_DELETE_TAG)
        }

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemPdfPreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun getItems(): ArrayList<UploadedFileModel>? {
        return mItems as? ArrayList<UploadedFileModel>
    }

    inner class DiffCallback(
        private val mOldList: List<UploadedFileModel>,
        private val mNewList: List<UploadedFileModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].guid == mNewList[newItemPosition].guid
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]
            return (newItem.guid == oldItem.guid)
        }
    }
}