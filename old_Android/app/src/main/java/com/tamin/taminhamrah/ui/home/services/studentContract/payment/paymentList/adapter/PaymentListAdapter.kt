package com.tamin.taminhamrah.ui.home.services.studentContract.payment.paymentList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.PaymentListModel
import com.tamin.taminhamrah.databinding.ListItemPaymentListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class PaymentListAdapter : RecyclerView.Adapter<PaymentListAdapter.ItemViewHolder>() {
    var onItemClickListener: AdapterInterface.OnItemClickListener<PaymentListModel>? = null
    private var mItems = emptyList<PaymentListModel>()
    fun setItems(
        items: List<PaymentListModel>,
        onItemClickListener: AdapterInterface.OnItemClickListener<PaymentListModel>? = null
    ) {
        this.onItemClickListener = onItemClickListener
        val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            ListItemPaymentListBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        (holder as? ItemViewHolder)?.binding?.apply {
            this.item = item
            btnShowDetail.setOnClickListener {
                if (groupDetails.visibility == View.GONE) {
                    groupDetails.visibility = View.VISIBLE
                    btnShowDetail.text =
                        btnShowDetail.context.getString(R.string.hide_detail)
                } else {
                    groupDetails.visibility = View.GONE
                    btnShowDetail.text =
                        btnShowDetail.context.getString(R.string.show_detail)
                }
            }

            btnViewDetailOfPayment.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: ListItemPaymentListBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DiffCallback(
        private val mOldList: List<PaymentListModel>,
        private val mNewList: List<PaymentListModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition] == mNewList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }
    }
}
