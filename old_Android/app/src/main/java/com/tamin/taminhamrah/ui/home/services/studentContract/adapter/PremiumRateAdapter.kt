package com.tamin.taminhamrah.ui.home.services.studentContract.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.PremiumOptionsModel
import com.tamin.taminhamrah.databinding.ListRatePremiumBinding
import com.tamin.taminhamrah.ui.appinterface.MenuInterface

class PremiumRateAdapter(private val onItemClickListener: MenuInterface.OnResult) :
    RecyclerView.Adapter<PremiumRateAdapter.ItemViewHolder>() {

    private var mItems = emptyList<PremiumOptionsModel>()

    fun setItems(items: List<PremiumOptionsModel>) {
        /*val diffCallback = DiffCallback(mItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        mItems = items
        diffResult.dispatchUpdatesTo(this)*/

        mItems = items
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListRatePremiumBinding.inflate(inflater, parent, false))
    }


    fun changeCheck(id: String) {
        for (item in mItems)
            if (item.spcrateCode == id)
                item.idSelected = true

        notifyItemRangeChanged(0, itemCount)
    }

    fun setEnabledItem(id: String) {
        for (item in mItems) {
            if (item.spcrateCode == id) {
                item.idSelected = true
                item.isDisable = false
            } else {
                item.isDisable = true
                item.idSelected = false
            }
        }
        notifyItemRangeChanged(0, itemCount)
    }

    fun enableSelectAllItem() {
        for (item in mItems) {
            item.isDisable = false
        }
        notifyItemRangeChanged(0, itemCount)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.binding.item = item
        holder.binding.radioButton.isChecked = item.idSelected

        if (item.isDisable) {
            holder.binding.apply {
                disableRadioButton(radioButton)
            //    tvValue.setTextColor(ContextCompat.getColor(root.context, R.color.gray))
            }
        }
        holder.binding.parent.setOnClickListener {
            if (item.isDisable)
                return@setOnClickListener
            if (!item.idSelected) {
                for (itemSelected in mItems)
                    itemSelected.idSelected = false


                item.idSelected = true
                notifyItemRangeChanged(0, itemCount)
                onItemClickListener.onResult(
                    MenuModel(
                        id = item.spcrateCode,
                        description = item.spcrateDescription,
                        description2 = "${item.insurDpercent} درصد"
                    )
                )
            }
        }


        // setAnimation(holder.itemView, position);

    }

    /*    private fun setAnimation(viewToAnimate: View, position: Int) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                val animation: Animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.slide_out_right)
                viewToAnimate.startAnimation(animation)
                lastPosition = position
            }
        }*/
    override fun getItemCount(): Int {
        return mItems.size
    }

    fun disableRadioButton(radioButton: AppCompatRadioButton) {
       // radioButton.isEnabled = false
        radioButton.isClickable = false
       // radioButton.buttonTintList = ContextCompat.getColorStateList(radioButton.context, R.color.gray)
    }

    class ItemViewHolder(var binding: ListRatePremiumBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<PremiumOptionsModel>,
        private val mNewList: List<PremiumOptionsModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]
            return (newItem == oldItem)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]
            return (newItem == oldItem)
        }
    }


}
