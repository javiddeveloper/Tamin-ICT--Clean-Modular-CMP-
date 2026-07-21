package com.tamin.taminhamrah.ui.home.services.pensionSurvivor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SurvivorModel
import com.tamin.taminhamrah.databinding.ItemDependentSurvivorBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface

class SurvivorAdapter(private val onItemClickListener: AdapterInterface.OnItemClickListener<SurvivorModel>) :
    RecyclerView.Adapter<SurvivorAdapter.ItemViewHolder>() {

    private lateinit var binding: ItemDependentSurvivorBinding
    private val mItems = ArrayList<SurvivorModel>()

    fun setItems(items: List<SurvivorModel>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(mItems, items), true)
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = ItemDependentSurvivorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        val context = binding.root.context
        binding.apply {
            tvFullName.text = context.getString(R.string.space,
                item.userInfo.personal?.firstName,
                item.userInfo.personal?.lastName)
            tvRelation.text = item.userInfo.personal?.relationShip?.ifBlank { "_" }?:""
            var color = 0
            if (item.userInfo.isCompleted) {
                color = R.color.green
               // btnInfo.text = context.getString(R.string.edit_info)
                btnInfo.visibility = View.GONE
            } else {
                color = R.color.color_icon
                btnInfo.text = context.getString(R.string.label_complete_info)
            }
            imgCheck.setColorFilter(
                ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)

            btnInfo.setOnClickListener { onItemClickListener.onItemClick(item) }
        }
    }

    override fun getItemCount() = mItems.size

    inner class ItemViewHolder : RecyclerView.ViewHolder(binding.root)

    class DiffCallback(
        private val mOldList: List<SurvivorModel>,
        private val mNewList: List<SurvivorModel>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].userInfo.personal?.nationalId == mNewList[newItemPosition].userInfo.personal?.nationalId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition].userInfo
            val newItem = mNewList[newItemPosition].userInfo

            return (newItem == oldItem)
        }

    }
}