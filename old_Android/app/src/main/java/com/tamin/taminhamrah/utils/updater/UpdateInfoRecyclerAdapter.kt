package com.tamin.taminhamrah.utils.updater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R


class UpdateInfoRecyclerAdapter(
    private val list: List<String>
) :
    RecyclerView.Adapter<UpdateInfoRecyclerAdapter.SoresViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoresViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.download_direct_item, parent, false)
        return SoresViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SoresViewHolder, position: Int) {
        holder.onBind(list[position])
    }


    class SoresViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        /**
         * Binds data to layout
         */
        fun onBind(item: String) {
            val txtDirect = view.findViewById<TextView>(R.id.txtDirect)
            txtDirect.text = item

        }
    }
}