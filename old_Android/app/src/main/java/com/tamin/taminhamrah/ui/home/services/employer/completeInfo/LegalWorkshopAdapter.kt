package com.tamin.taminhamrah.ui.home.services.employer.completeInfo

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.databinding.ListItemLegalWorkshopBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class LegalWorkshopAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<EmployerAgreement>? = null) :
    BasePagingAdapter<EmployerAgreement, ListItemLegalWorkshopBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_legal_workshop
    }

    override fun bindItem(
        binding: ListItemLegalWorkshopBinding,
        item: EmployerAgreement?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            when(item.workshop?.character?.characterCode){
                "01"->{
                    btnCompleteInfo.visibility = View.INVISIBLE
                    labelInfo.visibility = View.VISIBLE
                }
                "02"->{
                    btnCompleteInfo.visibility = View.VISIBLE
                    labelInfo.visibility = View.GONE
                }
            }

            btnCompleteInfo.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    override fun initViewHolder(binding: ListItemLegalWorkshopBinding, itemView: View) {
        binding.btnShowMore.setOnClickListener {

                if (binding.groupDetail.visibility == View.VISIBLE){
                    binding.groupDetail.visibility =View.GONE
                    binding.btnShowMore.text = binding.btnShowMore.context.getString(R.string.label_see_detail)
                }
                else {
                    binding.groupDetail.visibility = View.VISIBLE
                    binding.btnShowMore.text = binding.btnShowMore.context.getString(R.string.label_close)
                }
        }

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<EmployerAgreement>() {
        override fun areItemsTheSame(
            oldItem: EmployerAgreement,
            newItem: EmployerAgreement
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: EmployerAgreement,
            newItem: EmployerAgreement
        ): Boolean {
            return oldItem == newItem
        }
    }
}