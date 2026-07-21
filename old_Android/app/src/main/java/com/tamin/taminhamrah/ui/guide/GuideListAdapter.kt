package com.tamin.taminhamrah.ui.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.ItemListGuideBinding

class GuideListAdapter(val data: List<String>) :
    RecyclerView.Adapter<GuideListAdapter.GuideViewHolder>() {


    inner class GuideViewHolder(val binding: ItemListGuideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvIndex.text = (position + 1).toString()
                tvGuide.text = data[position]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = DataBindingUtil.inflate<ItemListGuideBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_list_guide,
            parent,
            false
        )

        return GuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}