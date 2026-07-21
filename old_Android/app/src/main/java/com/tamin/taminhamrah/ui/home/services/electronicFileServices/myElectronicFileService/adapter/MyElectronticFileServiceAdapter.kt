package com.tamin.taminhamrah.ui.home.services.electronicFileServices.myElectronicFileService.adapter
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.electronicFile.myElectronicFile.ElectronicFileModel
import com.tamin.taminhamrah.databinding.ListItemImageDownloadedPreviewBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.ImageUtils


class MyElectronticFileServiceAdapter(private val token: String) :
    BasePagingAdapter<ElectronicFileModel, ListItemImageDownloadedPreviewBinding>(DiffCallback) {
     var onItemClickListener: OnItemClickListener<ElectronicFileModel?>? = null

    object DiffCallback : DiffUtil.ItemCallback<ElectronicFileModel>() {
        override fun areItemsTheSame(
            oldItem: ElectronicFileModel,
            newItem: ElectronicFileModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ElectronicFileModel,
            newItem: ElectronicFileModel
        ): Boolean {
            return oldItem == newItem
        }

    }
    override fun getLayoutResId(): Int {
        return R.layout.list_item_image_downloaded_preview
    }

    override fun initViewHolder(binding: ListItemImageDownloadedPreviewBinding, itemView: View) {

    }

    override fun bindItem(
        binding: ListItemImageDownloadedPreviewBinding,
        itemModel: ElectronicFileModel?,
        position: Int
    ) {
        binding.apply {
            itemModel?.let { image ->
                item = image
                binding.root.setOnClickListener {
                    onItemClickListener?.onItemClick(image)
                }
                ImageUtils.loadImage(view =imagePreview,imageUrl= image.thumb,token =token)

            }
        }
    }

}
