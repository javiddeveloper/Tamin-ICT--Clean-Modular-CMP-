package com.tamin.taminhamrah.widget.edittext.number

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.R.styleable
import com.tamin.taminhamrah.databinding.WidgetEdittextNumberBinding
import com.tamin.taminhamrah.utils.NumberTextWatcherForThousand
import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianHelper
import com.tamin.taminhamrah.widget.BaseWidget

class EditTextNumber(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {
    private lateinit var viewBinding: WidgetEdittextNumberBinding
    private var minLength = 0
    lateinit var type: EditTextNumber.EditTextNumberType

    init {
        context?.let {
            viewBinding = WidgetEdittextNumberBinding.inflate(LayoutInflater.from(it), this, true)
            attrs?.let { it1 -> setAttribute(it, it1) }
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun setAttribute(context: Context, attrs: AttributeSet) {
        val cv = context.obtainStyledAttributes(attrs, styleable.CustomEditText, 0, 0)
        val attributeTextColor = cv.getResourceId(styleable.CustomEditText_editText_text_color, 0)
        val attributeBoxStrokeColor = cv.getResourceId(styleable.CustomEditText_editText_box_stroke_color, 0)
        val attributeHint = cv.getString(styleable.CustomEditText_editText_hint)
        val attributeMaxLine = cv.getInteger(styleable.CustomEditText_editText_max_line,1)
        val attributeMaxLength = cv.getInteger(styleable.CustomEditText_editText_max_len,10)
        val attributeTextSize = cv.getDimension(styleable.CustomEditText_editText_text_size, 0f)
        minLength = cv.getInt(styleable.CustomEditText_editText_min_len, 3)

        /////////////////////////
        viewBinding.apply {
            if (attributeTextSize > 0)
                edtTextNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, attributeTextSize)
            if (attributeTextColor != 0)
                edtTextNumber.setTextColor(ContextCompat.getColor(context, attributeTextColor))
            if (attributeBoxStrokeColor != 0)
                ContextCompat.getColorStateList(context, attributeBoxStrokeColor)?.let {
                    layInputNumber.setBoxBackgroundColorStateList(it)
                }
            if (!attributeHint.isNullOrEmpty())
                layInputNumber.hint = attributeHint
            if (attributeMaxLine > 1)
                edtTextNumber.maxLines = attributeMaxLine

       //     if (attributeMaxLength != 10) {
                val fArray = arrayOfNulls<InputFilter>(1)
                fArray[0] = LengthFilter(attributeMaxLength)
                edtTextNumber.filters = fArray
        //    }

            val ta = context.obtainStyledAttributes(attrs, styleable.customTextNumberType)
            type = EditTextNumber.EditTextNumberType.values()[ta.getInt(styleable.customTextNumberType_typeNumberInput, 0)]


            edtTextNumber.apply {
                if (type == EditTextNumberType.PRICE) {
                    addTextChangedListener(NumberTextWatcherForThousand(this))
                }
                doOnTextChanged { text, start, before, count ->
                    if (!text.isNullOrBlank()){
                        viewBinding.layInputNumber.isErrorEnabled=false
                    }
                }
            }

        }


        val attributeMaxLen = cv.getInt(styleable.CustomEditText_editText_max_len, 30)

        val filter =
             InputFilter { source, start, end, dest, dstart, dend ->
                 source
               /*  for (i in start until end) {
                     if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*")
                             .matcher(
                                 source[i].toString()
                             ).matches()
                     ) {
                         return@InputFilter ""
                     }
                 }
                 null*/
             }
         viewBinding.edtTextNumber.filters = arrayOf(filter, LengthFilter(attributeMaxLen))
        val typeface = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_fa_num)
        viewBinding.edtTextNumber.typeface = typeface
        viewBinding.layInputNumber.typeface = typeface

        cv.recycle()
    }

    fun setHint(hint: String) {
        viewBinding.layInputNumber.hint = hint
    }

    fun getHint():String = viewBinding.layInputNumber.hint.toString()

    fun getMinLength() = minLength

    fun setLength(_maxLength: Int) {
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = LengthFilter(_maxLength)
        viewBinding.edtTextNumber.filters = fArray
    }
    fun setMaxLenght(max:Int)
    {
        viewBinding.layInputNumber.counterMaxLength = max
    }

    fun setTextWidget(value: String) {
        viewBinding.edtTextNumber.setText(value)
    }

    fun getValue(showError:Boolean=true): String {
        val number = viewBinding.edtTextNumber.text.toString()
        val model = ValidationUtil.number(context, number)
        return if (model.status) {
            viewBinding.layInputNumber.isErrorEnabled=false
            MyPersianHelper.toEnglishNumber(number)
        }else {
            if (showError) {
                viewBinding.layInputNumber.isErrorEnabled=true
                viewBinding.layInputNumber.error = model.message
            }
            ""
        }
    }

    fun setValueOfText(str:String?){
        str?.let {
            viewBinding.edtTextNumber.setText(str)
        }
    }

    fun getNullableValue(): String {
        return viewBinding.edtTextNumber.text.toString()
    }

    fun checkStartZero(code: String): String {

        val model = ValidationUtil.startWithZero(context, code)
        return if (model.status)
            code
        else {
            viewBinding.edtTextNumber.error = model.message
            ""
        }
    }

    fun setError(message: String) {
        if(!viewBinding.layInputNumber.isErrorEnabled) {
            viewBinding.layInputNumber.isErrorEnabled = true
            viewBinding.layInputNumber.error = message
        }
    }

    fun disableError(){
        viewBinding.layInputNumber.isErrorEnabled=false
        viewBinding.layInputNumber.error = ""
    }

    fun getInput(): TextInputEditText {
        return viewBinding.edtTextNumber
    }


    fun getLayout(): TextInputLayout {
        return viewBinding.layInputNumber
    }

    override fun initLayout(context: Context?, attrs: AttributeSet?) {

    }

    fun enableView(enabled: Boolean) {

            viewBinding.edtTextNumber.isEnabled = enabled
            viewBinding.edtTextNumber. isClickable = enabled
        if (enabled)
            viewBinding.edtTextNumber.setTextColor(ContextCompat.getColor(context, R.color.textColorTitle))
        else
            viewBinding.edtTextNumber.setTextColor(ContextCompat.getColor(context, R.color.textColorSubTitle))
      /*  background = if (isEnabled)
            ContextCompat.getDrawable(context, R.drawable.bg_lable_normal)
        else
            ContextCompat.getDrawable(context, R.drawable.bg_grey)*/

    }

    enum class EditTextNumberType {
        NUM,
        PRICE
    }

}