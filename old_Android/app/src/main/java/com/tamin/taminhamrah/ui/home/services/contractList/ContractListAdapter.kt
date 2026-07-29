package com.tamin.taminhamrah.ui.home.services.contractList

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.Constants.STUDENT_CONTRACT_CODE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.user.ContractItem
import com.tamin.taminhamrah.databinding.ListItemContractBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import timber.log.Timber

class ContractListAdapter() :
    BasePagingAdapter<ContractItem, ListItemContractBinding>(DiffUtilCallBack) {

    var onClickListener: AdapterInterface.OnItemClickListener<ContractItem>? = null

    object DiffUtilCallBack : DiffUtil.ItemCallback<ContractItem>() {
        override fun areItemsTheSame(
            oldItem: ContractItem,
            newItem: ContractItem
        ): Boolean {
            return oldItem.contractNumber == newItem.contractNumber
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: ContractItem,
            newItem: ContractItem
        ): Boolean {
            return oldItem == newItem
        }
    }


    override fun getLayoutResId() = R.layout.list_item_contract

    override fun bindItem(binding: ListItemContractBinding, item: ContractItem?, position: Int) {
        with(binding) {
            this.item = item
            item?.apply {

                if (item.contractStatusObject?.selfIsuContStatCode == 1) {
                    tvContractStatus.setTextColor(
                        ContextCompat.getColor(
                            tvContractStatus.context,
                            R.color.green
                        )
                    )
                    btnAction.visibility = View.VISIBLE

                }  else {
                    tvContractStatus.setTextColor(
                        ContextCompat.getColor(
                            tvContractStatus.context,
                            R.color.orange
                        )
                    )
                    btnAction.visibility = View.GONE

                }

             /*   if (premiumTypeCode == "01" || premiumTypeCode == "02") {
                    btnAction.visibility = View.VISIBLE
                }*/

                Timber.tag("debugFreeJob")
                    .i("cntFreeJobCode=$cntFreeJobCode , STUDENT_CONTRACT_CODE=$STUDENT_CONTRACT_CODE")
//                    btnViewContract.visible()
//                else
//                    btnViewContract.invisible()


                /*  if (contractStatusObject?.selfIsuContStatCode == 1 && premiumTypeCode != "38") {
                      btnCancelContract.visible()
                      btnViewContract.visible()
                      btnPaymentContract.visible()


                  } else {
                      btnCancelContract.gone()
                      btnViewContract.gone()
                      btnPaymentContract.gone()

                  }*/

//                if (cntFreeJobCode != STUDENT_CONTRACT_CODE)
//                    btnViewContract.gone()


                btnAction.setOnClickListener {
                    onClickListener?.onItemClick(this)
                }

                /*    btnViewContract.setOnClickListener {
                        onClickListener?.onViewContract(this)
                    }

                    btnCancelContract.setOnClickListener {
                        onClickListener?.onCancel(this)
                    }

                    btnPaymentContract.setOnClickListener {
                        onClickListener?.onPayment(this)
                    }*/

            }
        }
    }

    override fun initViewHolder(binding: ListItemContractBinding, itemView: View) {

    }
}