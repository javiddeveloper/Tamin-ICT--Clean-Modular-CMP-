package com.tamin.taminhamrah.ui.menuOthers

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.ListItemContactBinding
import com.tamin.taminhamrah.databinding.ListItemSocialNetworksBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.utils.ImageUtils
import java.util.Collections.emptyList


class ContactAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mItems = emptyList<MenuModel>()
    var listener: AdapterInterface.OnActionResultContactList<MenuModel>? = null
    fun setItems(
        items: List<MenuModel>,
        listener: AdapterInterface.OnActionResultContactList<MenuModel>
    ) {
        mItems = items
        notifyItemRangeChanged(0, mItems.size)
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                ItemViewHolderSocialNetwork(
                    ListItemSocialNetworksBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                ItemViewHolder(
                    ListItemContactBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                0
            }
            else -> {
                1
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }


    inner class DiffCallback(
        private val mOldList: List<MenuModel>,
        private val mNewList: List<MenuModel>
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            (holder as ItemViewHolderSocialNetwork).binding.apply {
              /*  imgInstagram.setOnClickListener {
                    listener?.onSocialNetworkClick(Uri.parse(Constants.LINK_INSTAGRAM))
                }*/

                imgBle.setOnClickListener {
                    listener?.onSocialNetworkClick(Uri.parse(Constants.LINK_BLE))
                }

                imgAparat.setOnClickListener {
                    listener?.onSocialNetworkClick(Uri.parse(Constants.LINK_APARAT))
                }

                imgSplus.setOnClickListener {
                    listener?.onSocialNetworkClick(Uri.parse(Constants.LINK_SPLUS))

                }

                imgEeta.setOnClickListener {
                    listener?.onSocialNetworkClick(Uri.parse(Constants.LINK_EETA))

                }
                val item = mItems[position]
                rootItem.setOnClickListener {
                    listener?.onItemClick(item)
                }
                ImageUtils.imageDrawable(holder.binding.icon, item?.iconRes)
            }
        } else {
            val item = mItems[position]
            (holder as ItemViewHolder).binding.apply {
                this.item = item
                rootItem.setOnClickListener {
                    listener?.onItemClick(item)
                }
            }
            ImageUtils.imageDrawable(holder.binding.icon, item.iconRes)
        }
    }

    class ItemViewHolderSocialNetwork(var binding: ListItemSocialNetworksBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ItemViewHolder(var binding: ListItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)
}
