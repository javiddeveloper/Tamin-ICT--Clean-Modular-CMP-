package com.tamin.taminhamrah.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class HelperDate {

    companion object {
        fun convertToLastDaysAgo(date: Date, lastDays: Int): String {
            val year: Int
            val month: Int
            val day: Int
            val calendar: Calendar = GregorianCalendar()
            calendar.time = date
            return if (lastDays > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, -lastDays)
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH) + 1
                day = calendar.get(Calendar.DAY_OF_MONTH)
                "$year-$month-$day"
            } else ""
        }

        private fun splitDate(date: String): IntArray? {
            val dates = IntArray(3)
            dates[0] = date.substring(0, 4).toInt()
            dates[1] = date.substring(5, 7).toInt()
            dates[2] = date.substring(8, 10).toInt()
            return dates
        }

        fun convertStringToDate(strDate: String, format: String?): Date? {
            val dateFormat = SimpleDateFormat(format)
            return try {
                dateFormat.parse(strDate)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }

        fun convertServerDateFormatToMobileDateFormat(dateFromServer: Date): String {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.US)
            return dateFormat.format(dateFromServer)
        }

        fun getCurrentDate(): String? {
            val calendar = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return dateFormat.format(calendar)
        }
    }
}