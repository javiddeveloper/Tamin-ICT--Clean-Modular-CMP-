package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerWorkshop
import com.tamin.taminhamrah.databinding.ListItemWorkshopContractSelectorBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class WorkshopContractListAdapter(
    var onItemClickListener: AdapterInterface.OnItemClickListener<ArrayList<String?>>? = null,
    private val selectedContractRowList: ArrayList<String?>
) :
    BasePagingAdapter<EmployerWorkshop, ListItemWorkshopContractSelectorBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_workshop_contract_selector
    }

    override fun bindItem(
        binding: ListItemWorkshopContractSelectorBinding,
        item: EmployerWorkshop?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            for (row in selectedContractRowList){
                if (item.pymseq == row)
                    item.isSelectedItem=true
            }

            if (item.isSelectedItem) {
                with(btnSelect){
                    backgroundTintList = ContextCompat.getColorStateList(context, R.color.green)
                    setTextColor(ContextCompat.getColor(context, R.color.green))
                    text = context.getString(R.string.label_selected)
                }

                parent.background = ContextCompat.getDrawable(parent.context, R.drawable.bg_item_green)

            }else{
                with(btnSelect){
                    backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                    setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    text = context.getString(R.string.label_select)

                }
                parent.background = ContextCompat.getDrawable(parent.context, R.drawable.bg_item_blue)
            }

            btnSelect.setOnClickListener {
                item.isSelectedItem=!item.isSelectedItem

                if (selectedContractRowList.contains(item.pymseq) && !item.isSelectedItem)
                    selectedContractRowList.remove(item.pymseq)
                else if (item.isSelectedItem) selectedContractRowList.add(item.pymseq)

                onItemClickListener?.onItemClick(selectedContractRowList)

                binding.groupDetails.gone()
                notifyItemChanged(position)
            }

        }
    }

    override fun initViewHolder(binding: ListItemWorkshopContractSelectorBinding, itemView: View) {

        binding.btnShowMore.setOnClickListener {
            if (binding.groupDetails.isVisible) {
                binding.groupDetails.gone()
                binding.btnShowMore.text =
                    binding.btnShowMore.context.getString(R.string.label_see_detail)
            } else {
                binding.groupDetails.visible()
                binding.btnShowMore.text =
                    binding.btnShowMore.context.getString(R.string.hide_detail)
            }


        }

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<EmployerWorkshop>() {
        override fun areItemsTheSame(
            oldItem: EmployerWorkshop,
            newItem: EmployerWorkshop
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: EmployerWorkshop,
            newItem: EmployerWorkshop
        ): Boolean {
            return oldItem == newItem
        }
    }
}