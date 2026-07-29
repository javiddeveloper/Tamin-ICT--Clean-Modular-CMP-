package com.tamin.taminhamrah.widget.edittext.nationalcode

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.WidgetEdittextNationalCodeBinding
import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.widget.BaseWidget
import timber.log.Timber

class EditTextNationalCode(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {
    private lateinit var viewBinding: WidgetEdittextNationalCodeBinding

    override fun initLayout(context: Context?, attrs: AttributeSet?) {

//        inflateLayout(context, R.layout.widget_edittext_national_code)
        context?.let {
            viewBinding =
                WidgetEdittextNationalCodeBinding.inflate(LayoutInflater.from(it), this, true)
            attrs?.let { it1 -> setAttribute(it, it1) }
        }

    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {
        val cv = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0)
        val attributeTextColor = cv.getResourceId(R.styleable.CustomEditText_editText_text_color, 0)
        val attributeBoxStrokeColor =
            cv.getResourceId(R.styleable.CustomEditText_editText_box_stroke_color, 0)
        val attributeHint = cv.getString(R.styleable.CustomEditText_editText_hint)
        val attributeMaxLine = cv.getInteger(R.styleable.CustomEditText_editText_max_line, 1)
        val attributeMaxLength = cv.getInteger(R.styleable.CustomEditText_editText_max_len, 10)
        val attributeTextSize = cv.getDimension(R.styleable.CustomEditText_editText_text_size, 0f)
        /////////////////////////

        viewBinding.apply {
            if (attributeTextSize > 0)
                edtNationalCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, attributeTextSize)
            if (attributeTextColor != 0)
                edtNationalCode.setTextColor(ContextCompat.getColor(context, attributeTextColor))
            if (attributeBoxStrokeColor != 0)
                ContextCompat.getColorStateList(context, attributeBoxStrokeColor)?.let {
                    layInputNationalCode.setBoxBackgroundColorStateList(it)
                }
            if (!attributeHint.isNullOrEmpty())
                layInputNationalCode.hint = attributeHint
            if (attributeMaxLine > 1)
                edtNationalCode.maxLines = attributeMaxLine
            if (attributeMaxLength != 10) {
                val fArray = arrayOfNulls<InputFilter>(1)
                fArray[0] = InputFilter.LengthFilter(attributeMaxLength)
                edtNationalCode.filters = fArray
            }


            edtNationalCode.doOnTextChanged { text, start, before, count ->
                if (!text.isNullOrBlank()){
                    viewBinding.layInputNationalCode.isErrorEnabled=false
                }
            }
        }

        val attributeMaxLen = cv.getInt(R.styleable.CustomEditText_editText_max_len, 10)
        val filter = InputFilter { source, start, end, dest, dstart, dend -> source }
        viewBinding.edtNationalCode.filters =
            arrayOf(filter, InputFilter.LengthFilter(attributeMaxLen))
        /*val filter =
            InputFilter { source, start, end, dest, dstart, dend ->
                for (i in start until end) {
                    if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*")
                            .matcher(
                                source[i].toString()
                            ).matches()
                    ) {
                        return@InputFilter ""
                    }
                }
                null
            }
        viewBinding.edtNationalCode.filters = arrayOf(filter,
            InputFilter.LengthFilter(attributeMaxLen)
        )*/

        cv.recycle()
    }

    fun getValueNationalCode(showError: Boolean = true): String {
        val nationalCode =
            ValidationUtil.persianToEnglish(viewBinding.edtNationalCode.text.toString())
        val model = ValidationUtil.nationalCode(context, nationalCode)

        Timber.tag("ValidationTagDebug")
            .i("getValueNationalCode: status=${model.status}  message=${model.message}   showError=$showError")
        return if (model.status) {
            viewBinding.layInputNationalCode.isErrorEnabled= false
            nationalCode.trim()
        }else {
            if (showError)
                viewBinding.layInputNationalCode.error = model.message
            ""
        }
    }

    fun getPureValue(): String {
        return viewBinding.edtNationalCode.text.toString()
    }

    fun setTextWidget(value: String) {
        viewBinding.edtNationalCode.setText(value)
    }

    fun setHintWidget(value: String) {
        viewBinding.layInputNationalCode.hint = value
    }

    fun getLayout(): TextInputLayout {
        return viewBinding.layInputNationalCode
    }

    fun getInput(): TextInputEditText {
        return viewBinding.edtNationalCode
    }

    fun enableView(enabled: Boolean) {

        viewBinding.edtNationalCode.apply {
            isEnabled = enabled
            isClickable = enabled
            background = if (isEnabled)
                ContextCompat.getDrawable(context, R.drawable.bg_border_rectangle_grey)
            else
                ContextCompat.getDrawable(context, R.drawable.bg_lable_normal)
        }
    }
}