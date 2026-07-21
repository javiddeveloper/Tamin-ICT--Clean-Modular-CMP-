package com.tamin.taminhamrah.widget.edittext

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.R.styleable
import com.tamin.taminhamrah.R.styleable.CustomEditText
import com.tamin.taminhamrah.R.styleable.customTextType
import com.tamin.taminhamrah.databinding.WidgetEdittextDayBinding
import com.tamin.taminhamrah.widget.BaseWidget
import saman.zamani.persiandate.PersianDate


class EditTextDay(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {

    private lateinit var viewBinding : WidgetEdittextDayBinding
    var attributeHint : String? = null
    var inputYear : String? = null
    var MaxDay : Int? = null
    override fun initLayout(context: Context?, attrs: AttributeSet?) {
        context?.let {
            viewBinding = WidgetEdittextDayBinding.inflate(LayoutInflater.from(it), this, true)
            attrs?.let { it1 -> setAttribute(it, it1) }
        }
    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {

        viewBinding.apply {

            val cv = context.obtainStyledAttributes(attrs, CustomEditText, 0, 0)
            val attributeTextColor =
                cv.getResourceId(styleable.CustomEditText_editText_text_color, 0)
            val attributeHintColor =
                cv.getResourceId(styleable.CustomEditText_editText_hint_color, 0)
            attributeHint = cv.getString(styleable.CustomEditText_editText_hint)
            val attributeTextSize = cv.getDimension(styleable.CustomEditText_editText_text_size, 0f)
            val attributeMaxLen = cv.getInt(styleable.CustomEditText_editText_max_len, 30)
            val attributeMaxDay = cv.getInt(styleable.CustomEditText_editText_max_day, 31)
            val attributText = cv.getString(styleable.CustomEditText_editText_text)

            val ta = context.obtainStyledAttributes(attrs,customTextType)


            MaxDay = attributeMaxDay


            textWatcher(context)
            if (attributeTextSize > 0)
                labelDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, attributeTextSize)

            if (attributeTextColor != 0)
                labelDay.setTextColor(ContextCompat.getColor(context, attributeTextColor))

            if (attributeHintColor != 0)
                labelDay.setHintTextColor(ContextCompat.getColor(context, attributeTextColor))

            if (!attributeHint.isNullOrEmpty())
                labelDay.hint = attributeHint

            if (!attributText.isNullOrEmpty())
                labelDay.setText(attributText)


            cv.recycle()
        }

    }
        fun textWatcher(context: Context) {
            viewBinding.labelDay.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (!s.isNullOrBlank()) {
                            inputYear?.let {
                                MaxDay = isLeapYear(it.toInt())
                            }
                            MaxDay?.let {
                                if (s.toString().toInt() > it) {
                                    setText(it.toString())
                                }
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                })
            }
    }
    fun isLeapYear(year: Int?): Int {
        year?.let {
            if (PersianDate.isJalaliLeap(year)) {
                return 30
            }
        }
        return 29
    }

    fun getEditText(): AppCompatEditText {
        return viewBinding.labelDay
    }
    fun setYear(year: String?) {
        inputYear = year
    }
    fun setInitText(str: String?) {
        viewBinding.strText= str
    }

    fun enableView(enabled: Boolean) {

        viewBinding.strText.apply {
            isEnabled = enabled
            isClickable = enabled
            background = if (isEnabled)
                ContextCompat.getDrawable(context, R.drawable.bg_border_rectangle_grey)
            else
                ContextCompat.getDrawable(context, R.drawable.bg_lable_normal)
        }
    }
}