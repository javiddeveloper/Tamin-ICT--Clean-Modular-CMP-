package com.tamin.taminhamrah.widget.edittext.string

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.R.styleable
import com.tamin.taminhamrah.R.styleable.*
import com.tamin.taminhamrah.databinding.WidgetEdittextStringBinding
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.widget.BaseWidget
import java.util.regex.Pattern


class EditTextString(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {

    lateinit var viewBinding: WidgetEdittextStringBinding
    var attributeHint: String? = null
    var attributeMaxLen: Int? = null
    private var minLength = 0

    override fun initLayout(context: Context?, attrs: AttributeSet?) {
        context?.let {
            viewBinding = WidgetEdittextStringBinding.inflate(LayoutInflater.from(it), this, true)
            attrs?.let { it1 ->
                setAttribute(it, it1)
            }
        }

    }

    lateinit var type: EditTextString.EditTextType
    private fun setAttribute(context: Context, attrs: AttributeSet) {

        viewBinding.apply {


            val cv = context.obtainStyledAttributes(attrs, CustomEditText, 0, 0)
            val attributeTextColor =
                cv.getResourceId(styleable.CustomEditText_editText_text_color, 0)
            val attributeHintColor =
                cv.getResourceId(styleable.CustomEditText_editText_hint_color, 0)
            val attributeBoxStrokeColor =
                cv.getResourceId(styleable.CustomEditText_editText_box_stroke_color, 0)
            attributeHint = cv.getString(styleable.CustomEditText_editText_hint)
            val attributeMaxLine = cv.getInteger(styleable.CustomEditText_editText_max_line, 1)
            val attributeTextSize = cv.getDimension(styleable.CustomEditText_editText_text_size, 0f)
            attributeMaxLen = cv.getInt(styleable.CustomEditText_editText_max_len, 50)
            minLength = cv.getInt(styleable.CustomEditText_editText_min_len, 3)


            val ta = context.obtainStyledAttributes(attrs, customTextType)
            type = EditTextType.values()[ta.getInt(customTextType_type, 6)]

            initView(type)
            textWatcher(context, type)
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
            /*   if (attributeMaxLine > 1)
                   edtText.maxLines = attributeMaxLine

               if (attributeMaxLine > 1)
                   edtText.maxLines = attributeMaxLine*/

            /*         val fArray = arrayOfNulls<InputFilter>(1)
                     fArray[0] = LengthFilter(attributeMaxLen)
                     edtText.filters = fArray*/
            val typeface = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_fa_num)
            viewBinding.layInputText.typeface = typeface
            viewBinding.edtText.typeface = typeface
            cv.recycle()
        }

    }
    fun getHint():String {
       return viewBinding.layInputText.hint.toString()
    }
    fun getMinLength() = minLength

    private fun initView(typeText: EditTextType) {
        viewBinding.apply {
            when (typeText) {
                EditTextType.USERNAME -> {
                    edtText.maxLines = 1
                    val fArray = arrayOfNulls<InputFilter>(1)
                    fArray[0] = LengthFilter(30)
                    edtText.filters = fArray
                    edtText.minWidth = 3
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                    else
                        layInputText.hint = context.getString(R.string.label_user_name)
                }
                EditTextType.ADDRESS -> {
                    edtText.maxLines = 3
                    val fArray = arrayOfNulls<InputFilter>(1)
                    fArray[0] = LengthFilter(120)
                    edtText.filters = fArray
                    edtText.minWidth = 50
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                    else
                        layInputText.hint = context.getString(R.string.label_address)
                }
                EditTextType.FIRSTNAME -> {
                    edtText.maxLines = 1
                    val fArray = arrayOfNulls<InputFilter>(1)
                    fArray[0] = LengthFilter(30)
                    edtText.filters = fArray
                    edtText.minWidth = 3
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                    else
                        layInputText.hint = context.getString(R.string.first_name)
                }
                EditTextType.LASTNAME -> {
                    edtText.maxLines = 1
                    val fArray = arrayOfNulls<InputFilter>(1)
                    fArray[0] = LengthFilter(30)
                    edtText.filters = fArray
                    edtText.minWidth = 3
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                    else
                        layInputText.hint = context.getString(R.string.last_name)
                }
                EditTextType.PLACENAME -> {
                    edtText.maxLines = 1
                    val fArray = arrayOfNulls<InputFilter>(1)
                    fArray[0] = LengthFilter(30)
                    edtText.filters = fArray
                    edtText.minWidth = 3
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                    else
                        layInputText.hint = context.getString(R.string.label_branch_name)
                }
                EditTextType.EMAIL -> {
                    edtText.maxLines = 1
                    attributeMaxLen?.let {
                        val fArray = arrayOfNulls<InputFilter>(1)
                        fArray[0] = LengthFilter(it)
                        edtText.filters = fArray
                    }
                    edtText.minWidth = 3
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                }
                EditTextType.CUSTOM -> {
                    edtText.maxLines = 1
                    attributeMaxLen?.let {
                        val fArray = arrayOfNulls<InputFilter>(1)
                        fArray[0] = LengthFilter(it)
                        edtText.filters = fArray
                    }
                    edtText.minWidth = 3
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                }
                EditTextType.SERIALNUMBER -> {
                    edtText.maxLines = 1
                    val fArray = arrayOfNulls<InputFilter>(1)
                    fArray[0] = LengthFilter(10)
                    edtText.filters = fArray
                    edtText.minWidth = 3
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                    else
                        layInputText.hint = context.getString(R.string.label_id_card_serial_number)
                }

                EditTextType.ENGLISH_INPUT -> {
                    edtText.maxLines = 1
                    val fArray = arrayOfNulls<InputFilter>(1)
                    fArray[0] = LengthFilter(attributeMaxLen ?: 30)
                    edtText.filters = fArray
                    edtText.minWidth = 3
                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                }


                EditTextType.TIME_FORMAT -> {

                    edtText.maxLines = 1
                    edtText.maxWidth = 5

                    if (!attributeHint.isNullOrEmpty())
                        layInputText.hint = attributeHint
                }
            }
        }
    }

    fun getValue(showError: Boolean = true): String {
        val expression = ValidationUtil.persianToEnglish(viewBinding.edtText.text.toString())
        val model = ValidationUtil.expression(context, expression)
        return if (model.status) {
            viewBinding.layInputText.isErrorEnabled = false
            if (type == EditTextType.USERNAME) expression.trim() else expression
        } else {
            if (showError)
                viewBinding.layInputText.error = model.message
            ""
        }
    }

    fun enableError(errorMessage: String, showError: Boolean) {
        viewBinding.layInputText.isErrorEnabled = showError
        if (showError)
            viewBinding.layInputText.error = errorMessage
    }


    fun setValueOfText(str: String?) {
        str?.let {
            viewBinding.edtText.setText(str)
        }
    }

    fun getInput(): TextInputEditText {
        return viewBinding.edtText
    }

    fun getLayout(): TextInputLayout {
        return viewBinding.layInputText
    }

    fun textWatcher(context: Context, typeText: EditTextType) {
        viewBinding.edtText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val str = checkValidInput(s.toString(), typeText)
                    viewBinding.layInputText.apply {
                        if (str != 0) {
                            error = context.getString(str)
                            /*btnCheckForm.setClickable(false)
                            btnCheckForm.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_workshop_code
                                )
                            )*/
                        } else {
                            if (isErrorEnabled) {
                                isErrorEnabled = false
                                /* setClickable(true)
                                  setBackground(ContextCompat.getDrawable(context,R.drawable.bg_main_button)
                                 )*/
                            }
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }
    }

    private fun checkValidInput(input: String, typeText: EditTextType): Int {
        viewBinding.apply {
            when (typeText) {
                EditTextType.ADDRESS -> {
                    if (!Utility.checkInputIsValidAddress(input)) {
                        return R.string.error_input_is_address_not_valid
                    }
                }
                EditTextType.FIRSTNAME -> {
                    if (!Utility.checkInputIsValidPersionName(input)) {
                        return R.string.error_input_first_name_not_valid
                    }
                }
                EditTextType.LASTNAME -> {
                    if (!Utility.checkInputIsValidPersionName(input)) {
                        return R.string.error_input_last_name_not_valid
                    }
                }
                EditTextType.PLACENAME -> {
                    if (!Utility.checkInputIsValidBranch(input)) {
                        return R.string.error_input_is_branch_name_not_valid
                    }
                }
                EditTextType.EMAIL -> {
                    if (!Utility.checkInputIsValidEMail(input)) {
                        return R.string.error_input_is_email_not_valid
                    }
                }
                EditTextType.CUSTOM -> {
                    if (!Utility.checkInputIsValidBranch(input)) {
                        return R.string.error_input_is_custom_not_valid
                    }
                }
                EditTextType.SERIALNUMBER -> {
                    if (!Utility.checkInputIsValidBranch(input)) {
                        return R.string.error_input_is_custom_not_valid
                    }
                }
                EditTextType.ENGLISH_INPUT -> {
                    if (!Utility.checkInputIsValidEnglishChar(input)) {
                        return R.string.error_input_is_custom_not_valid
                    }
                }

                EditTextType.TIME_FORMAT -> {

                    if (!Utility.checkTimeFormat(input)) {
                        return R.string.error_input_is_not_time_format
                    }
                }

                else -> {}
            }
        }
        return 0
    }

    fun enableView(enabled: Boolean) {

        viewBinding.edtText.isEnabled = enabled
        viewBinding.edtText. isClickable = enabled
        if (enabled)
            viewBinding.edtText.setTextColor(ContextCompat.getColor(context, R.color.textColorTitle))
        else
            viewBinding.edtText.setTextColor(ContextCompat.getColor(context, R.color.textColorSubTitle))

    }

    enum class EditTextType {
        USERNAME,
        ADDRESS,
        FIRSTNAME,
        LASTNAME,
        PLACENAME,
        EMAIL,
        CUSTOM,
        SERIALNUMBER,
        ENGLISH_INPUT,
        TIME_FORMAT
    }

    fun setError(message: String) {
        viewBinding.layInputText.isErrorEnabled = true
        viewBinding.layInputText.error = message
    }



}