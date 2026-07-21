package com.tamin.taminhamrah.widget.edittext.string

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.R.styleable
import com.tamin.taminhamrah.R.styleable.CustomEditText
import com.tamin.taminhamrah.R.styleable.customTextType
import com.tamin.taminhamrah.databinding.WidgetEdittextStringBinding
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.widget.BaseWidget


class MultiLineEditTextString(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {

    private lateinit var viewBinding : WidgetEdittextStringBinding
    var attributeHint : String? = null
    var minLengthValue = 10
    override fun initLayout(context: Context?, attrs: AttributeSet?) {
        context?.let {
            viewBinding = WidgetEdittextStringBinding.inflate(LayoutInflater.from(it), this, true)
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
            val attributeBoxStrokeColor = cv.getResourceId(styleable.CustomEditText_editText_box_stroke_color, 0)
            attributeHint = cv.getString(styleable.CustomEditText_editText_hint)
            val attributeMaxLine = cv.getInteger(styleable.CustomEditText_editText_max_line, 3)
            val attributeTextSize = cv.getDimension(styleable.CustomEditText_editText_text_size, 0f)
          //  val attributeMaxLen = cv.getInt(styleable.CustomEditText_editText_max_len, 200)


            val filter = InputFilter { source, start, end, dest, dstart, dend -> source }
           // viewBinding.edtText.filters = arrayOf(filter, LengthFilter(attributeMaxLen))


            val ta = context.obtainStyledAttributes(attrs,customTextType)

            initView()

            /*val filter =
                InputFilter { source, start, end, dest, dstart, dend ->
                    for (i in start until end) {
                        if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*")
                                .matcher(
                                                            ++++++++++++++++++++++++++++++++++++++++++++++++  source[i].toString()
                                ).matches()
                        ) {
                            return@InputFilter ""
                        }
                    }
                    null
                }
            edtText.filters = arrayOf(filter, LengthFilter(attributeMaxLen))*/

            if (attributeTextSize > 0)
                edtText.setTextSize(TypedValue.COMPLEX_UNIT_PX, attributeTextSize)

            if (attributeTextColor != 0)
                edtText.setTextColor(ContextCompat.getColor(context, attributeTextColor))

            if (attributeHintColor != 0)
                edtText.setHintTextColor(ContextCompat.getColor(context, attributeTextColor))

            if (attributeBoxStrokeColor != 0)
                ContextCompat.getColorStateList(context, attributeBoxStrokeColor)?.let {
                    layInputText.setBoxBackgroundColorStateList(it)
                }
            if (!attributeHint.isNullOrEmpty())
                layInputText.hint = attributeHint
          if (attributeMaxLine > 1) {
              layInputText.editText?.maxLines = attributeMaxLine
              edtText.maxLines = attributeMaxLine
              edtText.minLines = 2
          }

   /*         val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = LengthFilter(attributeMaxLen)
            edtText.filters = fArray*/
            cv.recycle()
        }

    }

    private fun initView() {
        viewBinding.apply {
            edtText.apply {
                isSingleLine = false
                imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                setLines(3)
                isVerticalScrollBarEnabled = true
                movementMethod = ScrollingMovementMethod.getInstance()


             /*  val fArray = arrayOfNulls<InputFilter>(1)
                fArray[0] = LengthFilter(200)
                filters = fArray
*/
                if (!attributeHint.isNullOrEmpty())
                    layInputText.hint = attributeHint
                else
                    layInputText.hint = context.getString(R.string.label_description)
            }
        }
        textWatcher(context)
    }

    fun getMinLength() = minLengthValue
    fun getValue(showError: Boolean = true): String {
        val expression = ValidationUtil.persianToEnglish(viewBinding.edtText.text.toString())
        val model = ValidationUtil.expression(context, expression)
        return if (model.status)
            expression
        else {
            if (showError)
            viewBinding.layInputText.error = model.message

            ""
        }
    }

    fun getHint():String {
        return viewBinding.layInputText.hint.toString()
    }
    fun getInput(): TextInputEditText {
        return viewBinding.edtText
    }
    fun getBinding() = viewBinding

    fun getLayout(): TextInputLayout {
        return viewBinding.layInputText
    }

    fun textWatcher(context: Context) {
        var textAdded = ""
        var lastSpecialRequestsCursorPosition = 0
        viewBinding.edtText.apply {
                addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    lastSpecialRequestsCursorPosition = getSelectionStart()

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val str =s.toString()

                    viewBinding.layInputText.apply {
                        if (!Utility.checkInputIsValidAddress(str)) {
                            error = context.getString(R.string.error_input_is_address_not_valid)
                        }else{
                            if (isErrorEnabled) {
                                isErrorEnabled = false
                            }
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {
                /*   if (getLineCount() > 4) {
                        viewBinding.edtText.text?.let {edtText->
                            edtText.delete(edtText.length - 1, edtText.length)
                        }
                    }*/
                }
            })
        }
    }

    fun setError(message: String) {
        viewBinding.layInputText.isErrorEnabled = true
        viewBinding.layInputText.error = message
    }

    fun enableView(enabled: Boolean) {

        viewBinding.edtText.isEnabled = enabled
        viewBinding.edtText. isClickable = enabled
        if (enabled)
            viewBinding.edtText.setTextColor(ContextCompat.getColor(context, R.color.textColorTitle))
        else
            viewBinding.edtText.setTextColor(ContextCompat.getColor(context, R.color.textColorSubTitle))
    }

    fun enableError(errorMessage: String, showError: Boolean) {
        viewBinding.layInputText.isErrorEnabled = showError
        if (showError)
            viewBinding.layInputText.error = errorMessage
    }
}