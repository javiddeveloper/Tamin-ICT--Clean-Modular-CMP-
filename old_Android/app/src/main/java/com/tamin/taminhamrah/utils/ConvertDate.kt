package com.tamin.taminhamrah.utils

import java.util.Calendar
import java.util.Date

object ConvertDate {

    fun convertTimeStampToDate(dateTime: String): String {
        val d = Date()
        d.time = java.lang.Long.parseLong(dateTime)
        /** 1000*/
        val c = Calendar.getInstance()
        c.time = d
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dc = DateConverter(year, month, day)
        return dc.gregorianDate
    }

    fun convertTimestampToPersianDate(dateTime: String): String {
        val d = Date()
        d.time = java.lang.Long.parseLong(dateTime)
        /** 1000*/
        val c = Calendar.getInstance()
        c.time = d
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dc = DateConverter(year, month, day)

        val irDay = dc.iranianDay
        val irMonth = dc.iranianMonth
        val irYear = dc.iranianYear

        val newDay: String
        val newMonth: String
        val newYear: String

        newDay = if (irDay < 10) {
            "0$irDay"
        } else {
            irDay.toString()
        }

        newMonth = if (irMonth < 10) {
            "0$irMonth"
        } else {
            irMonth.toString()
        }

        newYear = if (irYear < 10) {
            "0$irYear"
        } else {
            irYear.toString()
        }

        return "$newYear/$newMonth/$newDay"
    }

    fun convertTimestampToPersianDateAndTime(dateTime: String): String {

        val d = Date()
        d.time = java.lang.Long.parseLong(dateTime)
        /** 1000*/
        val c = Calendar.getInstance()
        c.time = d
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dc = DateConverter(year, month, day)

        val irDay = dc.iranianDay
        val irMonth = dc.iranianMonth
        val irYear = dc.iranianYear
        val dayNameOfWeek = dc.iranianDayOfWeekName
        val monthName = dc.getMonthName(irMonth - 1)

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val newHour: String
        val newMinute: String
        val newDay: String
        val newMonth: String
        val newYear: String


        newHour = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }
        newMinute = if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }

        newDay = if (irDay < 10) {
            "0$irDay"
        } else {
            irDay.toString()
        }

        newMonth = if (irMonth < 10) {
            "0$irMonth"
        } else {
            irMonth.toString()
        }

        newYear = if (irYear < 10) {
            "0$irYear"
        } else {
            irYear.toString()
        }

        val time = "$newHour:$newMinute"
        return "$newYear/$newMonth/$newDay  $time"
    }

    fun convertTimestampToPersianDate(dateTime: Long): String {
        if (dateTime==0L)
            return "-"

        val d = Date()
        d.time = /*java.lang.Long.parseLong(dateTime)*/ dateTime
        val c = Calendar.getInstance()
        c.time = d
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dc = DateConverter(year, month, day)

        val irDay = dc.iranianDay
        val irMonth = dc.iranianMonth
        val irYear = dc.iranianYear
        val dayNameOfWeek = dc.iranianDayOfWeekName
        val monthName = dc.getMonthName(irMonth - 1)

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val newHour: String
        val newMinute: String
        val newDay: String
        val newMonth: String
        val newYear: String


        newHour = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }
        newMinute = if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }

        newDay = if (irDay < 10) {
            "0$irDay"
        } else {
            irDay.toString()
        }

        newMonth = if (irMonth < 10) {
            "0$irMonth"
        } else {
            irMonth.toString()
        }

        newYear = if (irYear < 10) {
            "0$irYear"
        } else {
            irYear.toString()
        }


        val time = "$newHour:$newMinute"
        return "$newYear/$newMonth/$newDay"
    }

    fun convertTimeStampToHour(dateTime: String): String {

        val d = Date()
        d.time = java.lang.Long.parseLong(dateTime) * 1000
        val c = Calendar.getInstance()
        c.time = d
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dc = DateConverter(year, month, day)

        val irDay = dc.iranianDay
        val irMonth = dc.iranianMonth
        val irYear = dc.iranianYear
        val dayNameOfWeek = dc.iranianDayOfWeekName
        val monthName = dc.getMonthName(irMonth - 1)

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val newHour: String
        val newMinute: String
        val newDay: String
        val newMonth: String
        val newYear: String


        newHour = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }
        newMinute = if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }

        newDay = if (irDay < 10) {
            "0$irDay"
        } else {
            irDay.toString()
        }

        newMonth = if (irMonth < 10) {
            "0$irMonth"
        } else {
            irMonth.toString()
        }

        newYear = if (irYear < 10) {
            "0$irYear"
        } else {
            irYear.toString()
        }


        return "$newHour:$newMinute"
    }

    fun convertTimestampToHijriDate(dateTime: String): String {
        val d = Date()
        d.time = java.lang.Long.parseLong(dateTime)
        /** 1000*/
        val c = Calendar.getInstance()
        c.time = d
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
//        val hDate = com.ali.uneversaldatetools.date.DateConverter.GregorianToHijri(year, month, day)
        return ""/*"${hDate.year}/${hDate.month}/${hDate.day}"*/
    }
    fun getTrueFormateOfDate(dateTime: String): String {
        if (dateTime.trim().length>=6)
        return dateTime.let{
            it.substring(0, 4) + "/" + it.substring(4, 6) +  "/" + it.substring(6, 8)
        }
        return ""
    }
}
