package com.tamin.taminhamrah.ui.menuOthers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.ListItemSocialResponsibilityBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.utils.ImageUtils
import java.util.Collections.emptyList


class SocialResponsibilityAdapter :
    RecyclerView.Adapter<ItemViewHolderSocialResponsibility>() {

    private var mItems = emptyList<MenuModel>()
    var listener: AdapterInterface.OnItemClickListener<MenuModel>? = null
    fun setItems(
        items: List<MenuModel>,
        listener: AdapterInterface.OnItemClickListener<MenuModel>
    ) {
        mItems = items
        notifyItemRangeChanged(0, mItems.size)
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolderSocialResponsibility {
        return ItemViewHolderSocialResponsibility(
                    ListItemSocialResponsibilityBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
             )
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

    override fun onBindViewHolder(holder:ItemViewHolderSocialResponsibility, position: Int) {
            holder.binding.apply {
                val item = mItems[position]
                rootItem.setOnClickListener {
                  listener?.onItemClick(item)
                }
                ImageUtils.imageDrawable(icon, item?.iconRes)
                tvTitle.text = item.title
            }
        }
    }

    class ItemViewHolderSocialResponsibility(var binding: ListItemSocialResponsibilityBinding) :
        RecyclerView.ViewHolder(binding.root)



