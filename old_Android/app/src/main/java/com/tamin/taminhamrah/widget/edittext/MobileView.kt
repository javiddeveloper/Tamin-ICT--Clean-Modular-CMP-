package com.tamin.taminhamrah.widget.edittext

import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.R.styleable
import com.tamin.taminhamrah.databinding.WidgetEdittextMobileBinding

import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.widget.BaseWidget

class MobileView(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {

    private lateinit var viewbinding: WidgetEdittextMobileBinding
    override fun initLayout(context: Context?, attrs: AttributeSet?) {
//        inflateLayout(context, R.layout.widget_edittext_mobile)
        context?.let {
            viewbinding = WidgetEdittextMobileBinding.inflate(LayoutInflater.from(it), this, true)
            attrs?.let { it1 -> setAttribute(it, it1) }
        }

    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {
        viewbinding.apply {
            val cv = context.obtainStyledAttributes(attrs, styleable.CustomEditText, 0, 0)
            val attributeTextColor =
                cv.getResourceId(styleable.CustomEditText_editText_text_color, 0)
            val attributeBoxStrokeColor =
                cv.getResourceId(styleable.CustomEditText_editText_box_stroke_color, 0)
            val attributeHint = cv.getString(styleable.CustomEditText_editText_hint)
            val attributeMaxLine = cv.getInteger(styleable.CustomEditText_editText_max_line, 1)
            val attributeMaxLength = cv.getInteger(styleable.CustomEditText_editText_max_line, 10)
            val attributeTextSize = cv.getDimension(styleable.CustomEditText_editText_text_size, 0f)

            if (attributeTextSize > 0)
                edtTextMobile.setTextSize(TypedValue.COMPLEX_UNIT_PX, attributeTextSize)

            if (attributeTextColor != 0)
                edtTextMobile.setTextColor(ContextCompat.getColor(context, attributeTextColor))

            if (attributeBoxStrokeColor != 0)
                ContextCompat.getColorStateList(context, attributeBoxStrokeColor)?.let {
                    layInputMobile.setBoxBackgroundColorStateList(it)
                }

            if (!attributeHint.isNullOrEmpty())
                layInputMobile.hint = attributeHint

            /*  if (attributeMaxLine>1)
                  edtTextMobile.maxLines = attributeMaxLine
           */
            /* if(attributeMaxLength!=10){
                 val fArray = arrayOfNulls<InputFilter>(1)
                 fArray[0] = LengthFilter(attributeMaxLength)
                 edtTextMobile.filters = fArray
             }*/

            edtTextMobile.doOnTextChanged { text, start, before, count ->
                if (!text.isNullOrBlank()) {
                    layInputMobile.isErrorEnabled = false
                }
            }

            cv.recycle()
        }

    }

    fun setHint(hint: String) {
        viewbinding.layInputMobile.hint = hint
    }

    fun setLength(_maxLength: Int) {
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = LengthFilter(_maxLength)
        viewbinding.edtTextMobile.filters = fArray
    }

    fun setTextWidget(value: String) {
        viewbinding.edtTextMobile.setText(value)
    }

    fun getValue(showError: Boolean = true): String {
        viewbinding.apply {
            val expression = edtTextMobile.text.toString()
            val model = ValidationUtil.validMobile(context, expression)
            return if (model.status) {
                layInputMobile.isErrorEnabled = false
                expression
            } else {
                if (showError) {
                    layInputMobile.isErrorEnabled = false
                    layInputMobile.error = model.message
                }

                ""
            }
        }

    }

    fun checkStartZero(code: String): String {

        val model = ValidationUtil.startWithZero(context, code)
        return if (model.status)
            code
        else {
            viewbinding.layInputMobile.error = model.message
            ""
        }

    }

    fun getInput(): TextInputEditText {
        return viewbinding.edtTextMobile
    }

    fun setError(message: String) {
        viewbinding.edtTextMobile.error = message
    }

    fun enableView(enabled: Boolean) {

        viewbinding.edtTextMobile.apply {
            isEnabled = enabled
            isClickable = enabled
            if (enabled)
                setTextColor(ContextCompat.getColor(context, R.color.textColorTitle))
            else
                setTextColor(ContextCompat.getColor(context, R.color.textColorSubTitle))
        }
    }


}