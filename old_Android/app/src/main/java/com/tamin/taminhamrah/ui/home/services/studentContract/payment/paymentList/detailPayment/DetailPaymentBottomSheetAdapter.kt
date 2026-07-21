package com.tamin.taminhamrah.ui.home.services.studentContract.payment.paymentList.detailPayment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.detailPayment.DetailPaymentListModel
import com.tamin.taminhamrah.databinding.ListItemDetailPaymentBinding

class DetailPaymentBottomSheetAdapter : RecyclerView.Adapter<DetailPaymentBottomSheetAdapter.ItemViewHolder>() {

        private var mItems = emptyList<DetailPaymentListModel>()
        fun setItems(
            items: List<DetailPaymentListModel>
        ) {
            val diffCallback = DiffCallback(mItems, items)
            val diffResult = DiffUtil.calculateDiff(diffCallback, true)
            mItems = items
            diffResult.dispatchUpdatesTo(this)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ItemViewHolder(ListItemDetailPaymentBinding.inflate(inflater, parent, false))
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val currentItem = mItems[position]
            currentItem.let {
                holder.binding.item = currentItem
            }
        }



        override fun getItemCount() = mItems.size

        class ItemViewHolder(var binding: ListItemDetailPaymentBinding) :
            RecyclerView.ViewHolder(binding.root)


        inner class DiffCallback(
            private val mOldList: List<DetailPaymentListModel>,
            private val mNewList: List<DetailPaymentListModel>
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
