package com.tamin.taminhamrah.ui.dialog

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.ListItemMenuServiceBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.base.BasePagingListDialogFragment


class MenuAdapter(private var onItemClickListener: AdapterInterface.OnItemClickListener<MenuModel>? = null) :
    BasePagingAdapter<MenuModel, ListItemMenuServiceBinding>(DiffUtilCallBack) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_menu_service
    }

    override fun bindItem(binding: ListItemMenuServiceBinding, item: MenuModel?, position: Int) {
        with(binding) {
            this.item = item

            labelNew.visibility = if (item?.isNew == true) View.VISIBLE else View.GONE

            if (item?.iconRes != null && item.iconRes > 0) {
                icon.visibility = View.VISIBLE
                icon.setImageResource(item.iconRes)
            }

            item?.isEdited?.let {
                if (it) {
                    cbEdited.visibility = View.VISIBLE
                }
            }
            parent.setOnClickListener {
                item?.let { it1 -> onItemClickListener?.onItemClick(it1) }
            }
        }
    }

    override fun initViewHolder(binding: ListItemMenuServiceBinding, itemView: View) {

    }

    fun <T> setItems(
        toList: List<MenuModel>,
        basePagingListDialogFragment: BasePagingListDialogFragment<T>
    ) {

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<MenuModel>() {
        override fun areItemsTheSame(
            oldItem: MenuModel,
            newItem: MenuModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MenuModel,
            newItem: MenuModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}