package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.PaymentSheetConstructionFilesModel
import com.tamin.taminhamrah.databinding.ListItemPaymentsheetBinding
import com.tamin.taminhamrah.utils.Utility

class PaymentSheetAdapter: RecyclerView.Adapter<PaymentSheetAdapter.ItemViewHolder>() {
    private lateinit var binding : ListItemPaymentsheetBinding
    private lateinit var mContext:Context
    private val mItems = ArrayList<PaymentSheetConstructionFilesModel>()
    fun setItems(items: List<PaymentSheetConstructionFilesModel>){
        val diffResult = DiffUtil.calculateDiff(DiffCallBack(mItems, items), true)
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }
    inner class ItemViewHolder:RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        mContext = parent.context
        binding = ListItemPaymentsheetBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder()
    }

    override fun getItemCount()=mItems.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
       val item = mItems[position]
        binding.apply {
            tvValuePaymentCode.text = item.paymentCode
            tvValueAmountPaymentSheet.text = Utility.getRialWithSeparator(item.paymentSheetAmount)
            tvValuePaymentDate.text = Utility.getDateSeparator(item.paymentDate)
            tvValuePaymentNumber.text = item.orderNumber
            if (item.status == "1"){
                tvValuePaymentStatus.setTextColor(ContextCompat.getColor(mContext,R.color.green))
                tvValuePaymentStatus.text = mContext.getString(R.string.label_status_paid)
            }else{
                tvValuePaymentStatus.setTextColor(ContextCompat.getColor(mContext,R.color.red))
                tvValuePaymentStatus.text = mContext.getString(R.string.payment_not_payed)
            }
        }
    }

    class DiffCallBack(
        private val mOldList: List<PaymentSheetConstructionFilesModel>,
        private val mNewList: List<PaymentSheetConstructionFilesModel>,
    ):DiffUtil.Callback(){
        override fun getOldListSize()= mOldList.size

        override fun getNewListSize()= mNewList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            mOldList[oldItemPosition].orderNumber == mNewList[newItemPosition].orderNumber

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            mOldList[oldItemPosition] == mNewList[newItemPosition]
    }
}