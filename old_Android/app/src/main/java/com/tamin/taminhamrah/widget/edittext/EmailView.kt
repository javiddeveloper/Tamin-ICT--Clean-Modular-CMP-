package com.tamin.taminhamrah.widget.edittext

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.R.styleable
import com.tamin.taminhamrah.R.styleable.CustomEditText
import com.tamin.taminhamrah.databinding.WidgetEdittextEmailBinding
import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.widget.BaseWidget

class EmailView(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {

    private lateinit var viewBinding: WidgetEdittextEmailBinding
    override fun initLayout(context: Context?, attrs: AttributeSet?) {
//        inflateLayout(context, R.layout.widget_edittext_email)
        context?.let {
            viewBinding = WidgetEdittextEmailBinding.inflate(LayoutInflater.from(it), this, true)
            attrs?.let { it1 -> setAttribute(it, it1) }
        }

    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {
        viewBinding.apply {
            val cv = context.obtainStyledAttributes(attrs, CustomEditText, 0, 0)
            val attributeTextColor =
                cv.getResourceId(styleable.CustomEditText_editText_text_color, 0)
            val attributeBoxStrokeColor =
                cv.getResourceId(styleable.CustomEditText_editText_box_stroke_color, 0)
            val attributeHint = cv.getString(styleable.CustomEditText_editText_hint)
            val attributeMaxLine = cv.getInteger(styleable.CustomEditText_editText_max_line, 1)
            val attributeTextSize = cv.getDimension(styleable.CustomEditText_editText_text_size, 0f)
            if (attributeTextSize > 0)
                edtTextEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, attributeTextSize)
            if (attributeTextColor != 0)
                edtTextEmail.setTextColor(ContextCompat.getColor(context, attributeTextColor))
            if (attributeBoxStrokeColor != 0)
                ContextCompat.getColorStateList(context, attributeBoxStrokeColor)?.let {
                    layInputEmail.setBoxBackgroundColorStateList(it)
                }
            if (!attributeHint.isNullOrEmpty())
                layInputEmail.hint = attributeHint
            /*if (attributeMaxLine > 1)
                edtTextEmail.maxLines = attributeMaxLine*/

            edtTextEmail.doOnTextChanged { text, start, before, count ->
                if (!text.isNullOrBlank()) {
                    viewBinding.layInputEmail.isErrorEnabled = false
                }
            }

            cv.recycle()
        }

    }

    fun getValue(showError: Boolean = true): String {
        val expression = viewBinding.edtTextEmail.text.toString()
        val model = ValidationUtil.validEmail(context, expression)
        return if (model.status) {
            viewBinding.layInputEmail.isErrorEnabled = false
            expression
        } else {
            if (showError) {
                viewBinding.layInputEmail.isErrorEnabled = true
                viewBinding.layInputEmail.error = model.message
            }

            ""
        }

    }

    fun setTextWidget(value: String) {
        viewBinding.edtTextEmail.setText(value)
    }

    fun getInput(): TextInputEditText {
        return viewBinding.edtTextEmail
    }

    fun setError(message: String) {
        viewBinding.layInputEmail.error = message
    }

    fun enableView(enabled: Boolean) {

        viewBinding.edtTextEmail.apply {
            isEnabled = enabled
            isClickable = enabled
            setTextColor(
                if (isEnabled) ContextCompat.getColor(context, R.color.textColorTitle) else
                    ContextCompat.getColor(context, R.color.textColorSubTitle)
            )
//            background = if (isEnabled)
//                ContextCompat.getDrawable(context, R.drawable.bg_border_rectangle_grey)
//            else
//                ContextCompat.getDrawable(context, R.drawable.bg_lable_normal)
        }
    }


}