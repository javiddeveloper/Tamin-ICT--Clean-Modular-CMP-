package com.tamin.taminhamrah.ui.home.services.retirementRequest.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.ItemConfirmStepBinding
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.EnumRequestState
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.RetirementRequestStateModel


class RequestStatusAdapter : RecyclerView.Adapter<RequestStatusAdapter.ItemViewHolder>(){
    private lateinit var binding : ItemConfirmStepBinding
    inner class ItemViewHolder : RecyclerView.ViewHolder(binding.root)
    private val mItems = ArrayList<RetirementRequestStateModel>()
    lateinit var mContext : Context
    fun setItems(items: List<RetirementRequestStateModel>){
        mItems.clear()
        mItems.addAll(items)
        notifyItemRangeChanged(0,itemCount)
        Log.i("setItems: ","setItemCalled")
        Log.i("setItems: ",mItems.toString())
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        mContext = parent.context
        binding = ItemConfirmStepBinding.inflate(LayoutInflater.from(mContext),
        parent,
        false)
        return ItemViewHolder()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        binding.apply {
            val colorRes = when(item.state){
                EnumRequestState.IS_PASSED ->{
                    R.color.green
                }
                EnumRequestState.IS_CURRENT ->{
                    R.color.orange
                }
                EnumRequestState.IS_NOT_PASSED ->{
                    R.color.gray
                }
                else -> {
                    R.color.gray
                }
            }
            imgCheck.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, colorRes))
            tvTitle.text = mContext.getString(item.titleStringResId)
        }
    }
    override fun getItemCount() = mItems.size

}