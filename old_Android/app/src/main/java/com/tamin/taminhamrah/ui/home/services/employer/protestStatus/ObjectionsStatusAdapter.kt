package com.tamin.taminhamrah.ui.home.services.employer.protestStatus

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopObjection
import com.tamin.taminhamrah.databinding.ListItemObjectionDebtBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils

class ObjectionsStatusAdapter(
    private val hidePaymentButton: Boolean = false,
    var onItemClickListener: AdapterInterface.OnItemClickListener<WorkShopObjection>? = null
) : BasePagingAdapter<WorkShopObjection, ListItemObjectionDebtBinding>(
    DiffUtilCallBack
) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_objection_debt
    }

    override fun bindItem(
        binding: ListItemObjectionDebtBinding,
        item: WorkShopObjection?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            binding.recyclerMain.apply {
                val itemAdapter = KeyValueAdapter()
                adapter = itemAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(this.context))
                }

                itemAdapter.setItems(item.createKeyValueMain(item))
            }

             binding.recyclerChild.apply {
                 val itemAdapter = KeyValueAdapter()
                 adapter = itemAdapter
                 if (itemDecorationCount == 0) {
                     addItemDecoration(UiUtils.createDivider(this.context))
                 }

                 itemAdapter.setItems(item.createKeyValueChild(item))
             }

            binding.apply {

                btnAction.setOnClickListener {
                    onItemClickListener?.onItemClick(item, tag = btnAction.text.toString())
                }

                btnShowSms.setOnClickListener {
                    onItemClickListener?.onItemClick(item, tag = btnShowSms.text.toString())

                }
            }

        }
    }

    override fun initViewHolder(binding: ListItemObjectionDebtBinding, itemView: View) {


        binding.btnShowDetail.setOnClickListener {
            if (binding.groupChild.visibility == View.GONE) {
                binding.groupChild.visibility = View.VISIBLE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.hide_detail)
            } else {
                binding.groupChild.visibility = View.GONE
                binding.btnShowDetail.text =
                    binding.btnShowDetail.context.getString(R.string.show_detail)
            }
        }

        itemView.requestFocus()
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<WorkShopObjection>() {
        override fun areItemsTheSame(
            oldItem: WorkShopObjection,
            newItem: WorkShopObjection
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WorkShopObjection,
            newItem: WorkShopObjection
        ): Boolean {
            return oldItem == newItem
        }
    }
}