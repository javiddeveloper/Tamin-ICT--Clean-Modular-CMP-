package com.tamin.taminhamrah.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.IntegerRes
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.WidgetDatePickerNewBinding
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.HelperDate
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import org.jetbrains.annotations.NotNull
import java.text.SimpleDateFormat
import java.util.Date

class DatePickerWidget(mContext: Context, attrs: AttributeSet?) :
    BaseWidget(mContext, attrs) {

    private lateinit var viewBinding: WidgetDatePickerNewBinding
    interface DateSelectOrListener {
        fun onDateSelect(
            jalaliDate: String,
            gregorianDate: Date,
            timeStamp: Long,
            serverFormattedDate: String,
            serverFormattedDateWithDayOffset: String
        )
    }

    private var mListener: DateSelectOrListener? = null

    fun setListener(listener: DateSelectOrListener) {
        mListener = listener
    }

    fun getHint():String {
        return viewBinding.tilDatePicker.hint.toString()
    }


    override fun initLayout(context: Context?, attrs: AttributeSet?) {

        context?.let {
            viewBinding = WidgetDatePickerNewBinding.inflate(LayoutInflater.from(it), this, true)
            attrs?.let { it1 -> setAttribute(it, it1) }
        }
    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {

        val cv = context.obtainStyledAttributes(attrs, R.styleable.CustomDatePicker, 0, 0)
        val attributeHint = cv.getString(R.styleable.CustomDatePicker_date_piker_hint)

        viewBinding.apply {


            if (!attributeHint.isNullOrEmpty())
                tilDatePicker.hint = attributeHint
            else
                tilDatePicker.hint = context.getString(R.string.label_select_date)


            inputDatePicker.setOnClickListener {
                clearError()

                var shYear = 0
                var shMonth = 0
                var shDay = 0
                try {

                    val split = inputDatePicker.text.toString().split('/')
                    if (split.isNotEmpty() && split.size == 3) {
                        shYear = split[0].trim().toInt()
                        shMonth = split[1].trim().toInt()
                        shDay = split[2].trim().toInt()
                    }

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                Utility.getDatePicker(context, shYear, shMonth, shDay, maxYear)?.apply {
                    setListener(object : MyPersianPickerListener {
                        override fun onDateSelected(@NotNull pickerDate: MyPersianPickerDate) {

                            val month =
                                if (pickerDate.persianMonth in 1..9) "0${pickerDate.persianMonth}" else "${pickerDate.persianMonth}"
                            val day =
                                if (pickerDate.persianDay in 1..9) "0${pickerDate.persianDay}" else "${pickerDate.persianDay}"

                            val dateString = "${pickerDate.persianYear}/$month/$day"

                            inputDatePicker.setText(dateString)

                            val monthG =
                                if (pickerDate.gregorianMonth in 1..9) "0${pickerDate.gregorianMonth}" else "${pickerDate.gregorianMonth}"
                            val dayG =
                                if (pickerDate.gregorianDay in 1..9) "0${pickerDate.gregorianDay}" else "${pickerDate.gregorianDay}"


                            mListener?.onDateSelect(
                                dateString,
                                pickerDate.gregorianDate,
                                pickerDate.timestamp,
                                HelperDate.convertServerDateFormatToMobileDateFormat(Date(pickerDate.timestamp)),
                                "${pickerDate.gregorianYear}-${monthG}-${dayG}T19:30:00.000Z"
                            )
                        }

                        override fun onDismissed() {}
                    })
                    show()
                }
            }
        }
    }

    fun setDateString(date:String){
        viewBinding.inputDatePicker.setText(date)
    }
    fun getDateString() = viewBinding.inputDatePicker.text.toString()
    /* fun setDateString(dateStr: String) {

         var shYear = 0
         var shMonth = 0
         var shDay = 0
         try {

             val split = dateStr.split('/')
             if (split.isNotEmpty() && split.size == 3) {
                 shYear = split[0].trim().toInt()
                 shMonth = split[1].trim().toInt()
                 shDay = split[2].trim().toInt()
             }

         } catch (ex: Exception) {
             ex.printStackTrace()
         }

     } = viewBinding.inputDate.setText(dateStr)*/

    fun setLocalDate(gregorianDateStr:String?){

        gregorianDateStr?.let {
            val parsedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(it)
            val localDate = ConvertDate.convertTimestampToPersianDate(parsedDate?.time ?: 0L)

            viewBinding.inputDatePicker.setText(localDate)
        }

    }

    fun setJalaliDate(jalaliDateStr:String){
        viewBinding.inputDatePicker.setText(jalaliDateStr)
    }

    fun setLocalDate(timeStamp:Long){
        val localDate = ConvertDate.convertTimestampToPersianDate(timeStamp)
        viewBinding.inputDatePicker.setText(localDate)
    }

    fun setError(errorMessage: String) {
        viewBinding.tilDatePicker.isErrorEnabled = true
        viewBinding.tilDatePicker.error = errorMessage
    }

    fun clearError() {
        viewBinding.tilDatePicker.isErrorEnabled = false
        viewBinding.tilDatePicker.error = ""
    }

    private var maxYear: Int = 1
    fun setMaxYear(maxYear: Int) {
        this.maxYear = maxYear
    }


    fun setTextColor(@IntegerRes colorId:Int){
        viewBinding.inputDatePicker.setTextColor(colorId)
    }

    fun getLayout(): TextInputLayout {
        return viewBinding.tilDatePicker
    }

}
