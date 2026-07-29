package com.tamin.taminhamrah.ui.home.services.employer.debt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.employer.debit.InstallmentPaymentModel
import com.tamin.taminhamrah.databinding.ListItemPaymentDebtBinding

//list_item_payment_debt


class PaymentDebtListAdapter: RecyclerView.Adapter<PaymentDebtListAdapter.ItemViewHolder>() {

    private lateinit var binding : ListItemPaymentDebtBinding
    private val mItems = ArrayList<InstallmentPaymentModel>()

    fun setItems(items:List<InstallmentPaymentModel>){
        val diffResult = DiffUtil.calculateDiff(DiffCallBack(mItems,items))
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = ListItemPaymentDebtBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder()
    }

    override fun getItemCount()= mItems.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
       val currentItem = mItems[position]
        binding.item = currentItem
    }

    inner class ItemViewHolder:  RecyclerView.ViewHolder(binding.root)

    class DiffCallBack(private val mOldList:List<InstallmentPaymentModel>,private val mNewList:List<InstallmentPaymentModel>):DiffUtil.Callback(){
        override fun getOldListSize() = mOldList.size

        override fun getNewListSize()= mNewList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)=
            mOldList[oldItemPosition].debitSubCode == mNewList[newItemPosition].debitSubCode


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)=
            mOldList[oldItemPosition] == mNewList[newItemPosition]

    }
}