package com.tamin.taminhamrah.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


abstract class BasePagingAdapter<T : Any, S : ViewDataBinding>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, BasePagingAdapter<T, S>.BasePagingViewHolder>(diffCallback) {


    abstract fun getLayoutResId(): Int
    abstract fun bindItem(binding: S, item: T?,position: Int)
    abstract fun initViewHolder(binding: S, itemView: View)

    override fun onBindViewHolder(holder: BasePagingViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePagingViewHolder {
        val binding = DataBindingUtil.inflate<S>(
            LayoutInflater.from(parent.context),
            getLayoutResId(),
            parent,
            false
        )
        return BasePagingViewHolder(binding)
    }

    inner class BasePagingViewHolder(val binding: S) : RecyclerView.ViewHolder(binding.root) {

        init {
            initViewHolder(binding, itemView)
        }


         fun bind(item:T?,position:Int){
             if (item != null) {
                 bindItem(binding,item,position)
             }else{
                 binding.root.visibility = View.GONE
             }
        }
    }


}