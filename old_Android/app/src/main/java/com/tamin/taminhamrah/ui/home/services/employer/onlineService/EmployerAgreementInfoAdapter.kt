package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.databinding.ListItemEmployerAgreementBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter

class EmployerAgreementInfoAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<EmployerAgreement>? = null) :
    BasePagingAdapter<EmployerAgreement, ListItemEmployerAgreementBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_employer_agreement
    }

    override fun bindItem(
        binding: ListItemEmployerAgreementBinding,
        item: EmployerAgreement?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            btnActions.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnActions.text.toString())
            }

            btnEmployee.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnEmployee.text.toString())
            }
            btnStackHolder.setOnClickListener {
                onItemClickListener?.onItemClick(item, tag = btnStackHolder.text.toString())
            }
        }
    }

    override fun initViewHolder(binding: ListItemEmployerAgreementBinding, itemView: View) {

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