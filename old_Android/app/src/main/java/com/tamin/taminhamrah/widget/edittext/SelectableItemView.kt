package com.tamin.taminhamrah.widget.edittext

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.R.styleable
import com.tamin.taminhamrah.R.styleable.CustomEditText
import com.tamin.taminhamrah.databinding.WidgetSelectableItemBinding
import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.widget.BaseWidget


class SelectableItemView(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {

    interface OnClickListener {
        fun onclick()
    }

    private var mListener: OnClickListener? = null
    private var hint: String? = null

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    private lateinit var binding: WidgetSelectableItemBinding
    override fun initLayout(context: Context?, attrs: AttributeSet?) {

//        inflateLayout(context, R.layout.widget_selectable_item)
        context?.let {
            binding = WidgetSelectableItemBinding.inflate(LayoutInflater.from(context), this, true)
            attrs?.let { it1 -> setAttribute(it, it1) }
        }
    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {
        binding.apply {
            val cv = context.obtainStyledAttributes(attrs, CustomEditText, 0, 0)
            val attributeTextColor =
                cv.getResourceId(styleable.CustomEditText_editText_text_color, 0)
            val attributeBoxStrokeColor =
                cv.getResourceId(styleable.CustomEditText_editText_box_stroke_color, 0)
            val attributeHint = cv.getString(styleable.CustomEditText_editText_hint)
            val attributeMaxLine = cv.getInteger(styleable.CustomEditText_editText_max_line, 1)
            val attributeTextSize = cv.getDimension(styleable.CustomEditText_editText_text_size, 0f)
            if (attributeTextSize > 0)
                selectableInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, attributeTextSize)
            if (attributeTextColor != 0)
                selectableInput.setTextColor(ContextCompat.getColor(context, attributeTextColor))
            if (attributeBoxStrokeColor != 0)
                ContextCompat.getColorStateList(context, attributeBoxStrokeColor)?.let {
                    binding.tilSelectableInput.setBoxBackgroundColorStateList(it)
                }
            hint = attributeHint
            val attributeHintColor =
                cv.getResourceId(styleable.CustomEditText_editText_hint_color, 0)

            if (attributeHintColor != 0)
                selectableInput.setHintTextColor(
                    ContextCompat.getColor(
                        context,
                        attributeTextColor
                    )
                )


            if (!attributeHint.isNullOrEmpty())
                selectableInput.hint = attributeHint
            else
                selectableInput.hint = "یک گزینه را انتخاب کنید"
            if (attributeMaxLine > 1)
                selectableInput.maxLines = attributeMaxLine

            selectableInput.isClickable = false
            selectableInput.isFocusable = false

            binding.selectableInput.setOnClickListener {
                mListener?.onclick()
            }

            selectableInput.doOnTextChanged { text, start, before, count ->
                if (!text.isNullOrBlank()) {
                    tilSelectableInput.isErrorEnabled = false
                }
            }

            cv.recycle()

        }


    }

    fun getValue(showError: Boolean = true): String {
        binding.apply {
            val expression = selectableInput.text.toString()
            val model = ValidationUtil.expression(context, expression)
            return if (model.status) {
                tilSelectableInput.isErrorEnabled = false
                expression
            } else {
                if (showError) {
                    tilSelectableInput.isErrorEnabled = false
                    tilSelectableInput.error = model.message
                }

                ""
            }
        }

    }

    fun getIt(): TextInputEditText {
        return binding.selectableInput
    }

    fun getLayout(): TextInputLayout {
        return binding.tilSelectableInput
    }

    fun setValue(str: String) {
        binding.selectableInput.setText(str)
    }

    fun hideDrawable() {
        binding.selectableInput.setCompoundDrawables(null, null, null, null)
    }

    fun enableView(enabled: Boolean) {

        binding.selectableInput.isEnabled = enabled
        binding.selectableInput. isClickable = enabled
        if (enabled){
            binding.selectableInput.setTextColor(ContextCompat.getColor(context, R.color.textColorTitle))
            binding.selectableInput.setCompoundDrawables(null, null, context.getDrawable(R.drawable.ic_arrow_down), null)
        } else{
            binding.selectableInput.setTextColor(ContextCompat.getColor(context, R.color.textColorSubTitle))
            binding.selectableInput.setCompoundDrawables(null, null, null, null)
        }


    }

    fun setHint(hint: String) {
        binding.selectableInput.hint = hint
    }

    fun getHint() = hint?:""

    fun setError(message: String) {
        binding.tilSelectableInput.isErrorEnabled=true
        binding.tilSelectableInput.error = message
    }

    fun disableError(){
        binding.tilSelectableInput.isErrorEnabled=false
        binding.tilSelectableInput.error = ""
    }
}