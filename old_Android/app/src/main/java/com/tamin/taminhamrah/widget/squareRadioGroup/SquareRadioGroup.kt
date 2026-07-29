package com.tamin.taminhamrah.widget.squareRadioGroup

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.tamin.taminhamrah.data.local.models.SquareRadioButtonModel

class SquareRadioGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val buttonMargin = 2.dpToPx()
    private val buttons: MutableList<AppCompatButton> = mutableListOf()
    private val buttonModels : ArrayList<SquareRadioButtonModel> = ArrayList()
    private var onClickListener: OnClickListener? = null

    private val buttonLayoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
        setMargins(buttonMargin, buttonMargin*4, buttonMargin, buttonMargin*4)
    }
    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    fun setItems(buttonInfo: List<SquareRadioButtonModel>,listener : OnClickListener) {
        onClickListener = listener
        removeAllViews()
        buttons.clear()
        buttonModels.clear()
        buttonModels.addAll(buttonInfo)
        weightSum = ((buttonInfo.size)+0.5).toFloat()
        initViews()
    }

    private fun initViews() {
        buttonModels.forEach { item->
            val layoutParams =  LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)

            layoutParams.setMargins(buttonMargin, buttonMargin*4, buttonMargin, buttonMargin*4)
            val button = AppCompatButton(context).apply {
                text = item.label
                setTextColor(item.textColor)
                maxLines=1
                textSize = 9.5F
                ellipsize = TextUtils.TruncateAt.END
                setBackgroundResource(item.backgroundColor)
                tag = item.id
                isSelected = item.isSelected
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    item.iconResId,
                    0,
                    0
                )
                this.layoutParams = layoutParams
                setOnClickListener{
                    selectButton(item)
                    onClickListener?.onClick(item)
                }
            }

            buttons.add(button)

            if (item.isSelected){
                selectButton(item)
            }
            addView(button)
        }

    }


    fun selectButton(item: SquareRadioButtonModel) {
        buttons.forEach { child->
            if (child.tag == item.id) {
                child.setBackgroundResource(item.selectedBackground)
                select(child)
                item.isSelected=true
            } else {
                unSelect(child)
                child.setBackgroundResource(item.backgroundColor)
                item.isSelected=false
            }
        }

    }

    fun select(button:AppCompatButton) {
        val oldWidth = button.layoutParams.width
        val layoutParams = button.layoutParams as LayoutParams

        layoutParams.apply {
            width = 0
            weight = 1.5f
            gravity = Gravity.CENTER
        }
        layoutParams.setMargins(buttonMargin, buttonMargin, buttonMargin, buttonMargin)

        button.layoutParams = layoutParams
        if (button.layoutParams.width != oldWidth) {
            requestLayout()
        }
    }

    fun unSelect(button:AppCompatButton) {
        val oldWidth = button.layoutParams.width
        val layoutParams = button.layoutParams as LayoutParams
        layoutParams.apply {
            width = 0
            weight = 1f
            gravity = Gravity.CENTER
        }
        layoutParams.setMargins(buttonMargin, buttonMargin*4, buttonMargin, buttonMargin*4)
        button.layoutParams = layoutParams
        if (button.layoutParams.width != oldWidth) {
            requestLayout()
        }
    }

    private fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics).toInt()
    }

    interface OnClickListener {
        fun onClick(button: SquareRadioButtonModel)
    }


}