package com.tamin.taminhamrah.ui.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.databinding.ListItemKeyValueBinding

class ExpandableListAdapter(
    items: ArrayList<KeyValueModel> = arrayListOf(),
    private var expandingIndex: Int = -1,
) : RecyclerView.Adapter<ExpandableListAdapter.ItemViewHolder>() {
    private val mItems = ArrayList<KeyValueModel>()
    private val baseItems: ArrayList<KeyValueModel> = arrayListOf()
    private var minifyEnable = true

    init {
        setItems(items)
    }

    fun setItems(items: List<KeyValueModel>) {
        baseItems.clear()
        baseItems.addAll(items)
        makeMinifyList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = ItemViewHolder(ListItemKeyValueBinding.inflate(LayoutInflater.from(parent.context),
        parent,
        false))


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val context = holder.binding.root.context
        val item = mItems[position]

        if (item._key.isBlank() && item._keyStringResId != 0)
            item._key = context.getString(item._keyStringResId)

        if (item._value.isBlank())
            item._value = "_"

        if (item._valueStringResId != 0)
            item._value = context.getString(item._valueStringResId)

        holder.binding.item = item

        if (item._isKeyBold)
            holder.binding.tvKey.setTypeface(holder.binding.tvKey.typeface, Typeface.BOLD)

        if (item._isValueBold)
            holder.binding.tvValue.setTypeface(holder.binding.tvValue.typeface, Typeface.BOLD)

        val localeContext = holder.binding.root.context
        holder.binding.tvValue.setTextColor(
            ContextCompat.getColor(
                localeContext,
                getTextColor(item._textColor)
            )
        )
    }

    override fun getItemCount() = mItems.size

    fun toggleMinifyMode() {
        minifyEnable = !minifyEnable
        makeMinifyList()
    }

    fun isMinifyMode() = minifyEnable

    fun setExpendingIndex(index: Int) {
        expandingIndex = index
        makeMinifyList()
    }

    private fun makeMinifyList() {
        mItems.clear()
        if (expandingIndex < 0 || !minifyEnable)
            mItems.addAll(baseItems)
        else
            for (i in 0 until (if (expandingIndex < baseItems.size) expandingIndex else baseItems.size))
                mItems.add(baseItems[i])
        notifyDataSetChanged()
    }
   // UiUtils.getAttributeColor(context, android.R.attr.colorBackground)

    private fun getTextColor(color: EnumTextColor): Int {
        return when (color) {
            EnumTextColor.BLUE ->  R.color.text_color_blue
            EnumTextColor.BLUE_GREEN -> R.color.green_blue
            EnumTextColor.GREEN -> R.color.textGreenColorSubTitle
            else -> R.color.textColorTitle
        }
    }

    class ItemViewHolder(var binding: ListItemKeyValueBinding) :
        RecyclerView.ViewHolder(binding.root)

}
