package com.tamin.taminhamrah.ui.home.services

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.databinding.ListItemImagePreviewBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.extentions.invisible

class ImagePreviewAdapter(private val onItemClickListener: AdapterInterface.OnItemClickListener<UploadedImageModel> , private val viewOnly: Boolean? = false) :
    RecyclerView.Adapter<ImagePreviewAdapter.ItemViewHolder>() {

    private var mItems = emptyList<UploadedImageModel>()
    //tODO:: replace notifyDataSetChanged with notifyItemChange and notifyItemRemove and ....

    private var isVisibility =true
    fun setItems(items: List<UploadedImageModel>) {
        mItems = items.toMutableList()
        notifyDataSetChanged()
    }
    fun setVisibilityDeleteItem(isVisible:Boolean){
        isVisibility = isVisible
    }

    fun addItem(item: UploadedImageModel) {
        mItems.toMutableList().add(item)
        notifyItemInserted(0)
    }
    fun clearItems(){
        mItems = emptyList()
        notifyDataSetChanged()    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(item: UploadedImageModel){
        mItems = mItems.filterNot { it == item }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListItemImagePreviewBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.item = item

        holder.binding.apply {
            if (!isVisibility) {
                btnDeleteImage.isVisible = isVisibility
            }
            if (item.isBase64)
                ImageUtils.loadImageBase64(view = imagePreview, item.Base64Value)
            else
                ImageUtils.loadImage(imagePreview, item.imageUri)

            imagePreview.setOnClickListener {
                onItemClickListener.onItemClick(item, tag = Constants.IMAGE_PREVIEW_TAG)
            }

            if (viewOnly==true){
                btnDeleteImage.invisible()
            }else {
                btnDeleteImage.setOnClickListener {
                    onItemClickListener.onItemClick(item, tag = Constants.DELETE_IMAGE_TAG)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun getItems(): ArrayList<UploadedImageModel>? {
        return mItems as? ArrayList<UploadedImageModel>
    }

    inner class DiffCallback(
        private val mOldList: List<UploadedImageModel>,
        private val mNewList: List<UploadedImageModel>
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