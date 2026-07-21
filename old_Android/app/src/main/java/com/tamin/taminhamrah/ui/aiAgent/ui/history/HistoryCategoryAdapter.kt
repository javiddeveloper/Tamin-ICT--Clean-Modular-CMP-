package com.tamin.taminhamrah.ui.aiAgent.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import com.tamin.taminhamrah.databinding.ItemAiHistoryCategoryBinding
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

class HistoryCategoryAdapter(
    private val onItemClicked: (CategoryHistoryClickType) -> Unit
) : PagingDataAdapter<AiHistoryCategory, HistoryCategoryAdapter.HistoryCategoryViewHolder>(
    AiChatDiffCallback()
) {

    class AiChatDiffCallback : DiffUtil.ItemCallback<AiHistoryCategory>() {
        override fun areItemsTheSame(
            oldItem: AiHistoryCategory,
            newItem: AiHistoryCategory
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AiHistoryCategory,
            newItem: AiHistoryCategory
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryCategoryViewHolder {
        val binding = ItemAiHistoryCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryCategoryViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(
        holder: HistoryCategoryViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }


    class HistoryCategoryViewHolder(
        private val binding: ItemAiHistoryCategoryBinding,
        private val onItemClicked: (CategoryHistoryClickType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: AiHistoryCategory?) {
            binding.apply {
                if (category == null) {
                    root.visibility = View.GONE
                    return
                }
                root.visibility = View.VISIBLE
                txtCount.text = buildString {
                    append("(")
                    append(category.messageCount.toString())
                    append(") تعداد چت ")
                }
                txtTitle.text = category.title

                val date = PersianDate(category.date)
                val format = PersianDateFormat("Y/m/d - H:i")
                val result = format.format(date)

                txtDate.text = result ?: "-"

                root.setOnClickListener {
                    onItemClicked(CategoryHistoryClickType.LoadItems(category.id))
                }
                imgEdit.setOnClickListener {
                    onItemClicked(CategoryHistoryClickType.Edit(category))

                }
                imgDelete.setOnClickListener {
                    onItemClicked(CategoryHistoryClickType.Delete(category.id))
                }

            }
        }
    }
}
