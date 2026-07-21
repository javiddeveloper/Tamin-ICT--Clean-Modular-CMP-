package com.tamin.taminhamrah.utils

import java.util.Calendar
import java.util.GregorianCalendar

class DateConverter {

    /**
     * getIranianYear:
     * Returns the 'year' part of the Iranian createdAt.
     *
     * @return int
     */
    var iranianYear: Int = 0
        private set // Year part of a Iranian createdAt
    /**
     * getIranianMonth:
     * Returns the 'month' part of the Iranian createdAt.
     *
     * @return int
     */
    var iranianMonth: Int = 0
        private set // Month part of a Iranian createdAt
    /**
     * getIranianDay:
     * Returns the 'day' part of the Iranian createdAt.
     *
     * @return int
     */
    var iranianDay: Int = 0
        private set // Day part of a Iranian createdAt
    /**
     * getGregorianYear:
     * Returns the 'year' part of the Gregorian createdAt.
     *
     * @return int
     */
    var gregorianYear: Int = 0
        private set // Year part of a Gregorian createdAt
    /**
     * getGregorianMonth:
     * Returns the 'month' part of the Gregorian createdAt.
     *
     * @return int
     */
    var gregorianMonth: Int = 0
        private set // Month part of a Gregorian createdAt
    /**
     * getGregorianDay:
     * Returns the 'day' part of the Gregorian createdAt.
     *
     * @return int
     */
    var gregorianDay: Int = 0
        private set // Day part of a Gregorian createdAt
    var hijriDay: Int = 0
        private set
    var hijriMonth: Int = 0
        private set
    var hijriYear: Int = 0
        private set
    /**
     * getJulianYear:
     * Returns the 'year' part of the Julian createdAt.
     *
     * @return int
     */
    var julianYear: Int = 0
        private set // Year part of a Julian createdAt
    /**
     * getJulianMonth:
     * Returns the 'month' part of the Julian createdAt.
     *
     * @return int
     */
    var julianMonth: Int = 0
        private set // Month part of a Julian createdAt
    /**
     * getJulianDay()
     * Returns the 'day' part of the Julian createdAt.
     *
     * @return int
     */
    var julianDay: Int = 0
        private set // Day part of a Julian createdAt
    private var leap: Int = 0 // Number of years since the last leap year (0 to 4)
    private var JDN: Int = 0 // Julian Day Number
    private var march: Int = 0 // The march day of Farvardin the first (First day of jaYear)

    private val weekDayNames = arrayOf("یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنج شنبه", "جمعه", "شنبه")
    private val monthNames = arrayOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")

    private val monthNamesGreg = arrayOf("ژانویه", "فوریه", "مارچ", "آپریل", "می", "ژوئن", "جولای", "آگوست", "سپتامبر", "اکتبر", "نوامبر", "دسامبر")

    private val monthNamesHijri = arrayOf("", "محرم", "صفر", "ربيع‌الاول", "ربيع‌الثاني", "جمادي‌الاول", "جمادي‌الثاني", "رجب", "شعبان", "رمضان", "شوال", "ذي‌القعده", "ذي‌الحجه")

    private val daysOfMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

    //return 0;
    val daysInMonth: Int?
        get() = this.daysOfMonth[this.iranianMonth]

    ////
    val iranianDayOfWeekName: String
        get() {
            val cal = GregorianCalendar(gregorianYear, gregorianMonth - 1, gregorianDay)
            val currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1

            return this.weekDayNames[currentWeekDay]
        }

    /**
     * getIranianDate:
     * Returns a string version of Iranian createdAt
     *
     * @return String
     */
    val iranianDate: String
        get() = "$iranianYear-$iranianMonth-$iranianDay"

    /**
     * getGregorianDate:
     * Returns a string version of Gregorian createdAt
     *
     * @return String
     */
    val gregorianDate: String
        get() = "$gregorianYear-$gregorianMonth-$gregorianDay"

    /**
     * getJulianDate:
     * Returns a string version of Julian createdAt
     *
     * @return String
     */
    val julianDate: String
        get() = "$julianYear-$julianMonth-$julianDay"

    /**
     * getWeekDayStr:
     * Returns the week day name.
     *
     * @return String
     */
    val weekDayStr: String
        get() {
            val weekDayStr = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            return weekDayStr[dayOfWeek]
        }

    /**
     * getDayOfWeek:
     * Returns the week day number. Monday=0..Sunday=6;
     *
     * @return int
     */
    val dayOfWeek: Int
        get() = JDN % 7

    /**
     * JavaSource_Calendar:
     * The default constructor uses the current Gregorian createdAt to initialize the
     * other private memebers of the class (Iranian and Julian dates).
     */
    constructor() {
        val calendar = GregorianCalendar()
        setGregorianDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH))
    }

    /**
     * JavaSource_Calendar:
     * This constructor receives a Gregorian createdAt and initializes the other private
     * members of the class accordingly.
     *
     * @param year  int
     * @param month int
     * @param day   int
     */
    constructor(year: Int, month: Int, day: Int) {
        setGregorianDate(year, month, day)
    }

    fun getMonthName(month: Int): String {
        return this.monthNames[month]
    }

    fun getDaysInMonth(month: Int): Int? {
        return this.daysOfMonth[month]
    }

    fun getHijriMonthName(month: Int): String {
        return this.monthNamesHijri[month - 1]
    }

    /**
     * toString:
     * Overrides the default toString() method to return all dates.
     *
     * @return String
     */
    override fun toString(): String {
        return weekDayStr +
                ", Gregorian:[" + gregorianDate +
                "], Julian:[" + julianDate +
                "], Iranian:[" + iranianDate + "]"
    }

    /**
     * nextDay:
     * Go to next julian day number (JDN) and adjusts the other dates.
     */
    fun nextDay() {
        JDN++
        JDNToIranian()
        JDNToJulian()
        JDNToGregorian()
    }

    /**
     * nextDay:
     * Overload the nextDay() method to accept the number of days to go ahead and
     * adjusts the other dates accordingly.
     *
     * @param days int
     */
    fun nextDay(days: Int) {
        JDN += days
        JDNToIranian()
        JDNToJulian()
        JDNToGregorian()
    }

    /**
     * previousDay:
     * Go to previous julian day number (JDN) and adjusts the otehr dates.
     */
    fun previousDay() {
        JDN--
        JDNToIranian()
        JDNToJulian()
        JDNToGregorian()
    }

    /**
     * previousDay:
     * Overload the previousDay() method to accept the number of days to go backward
     * and adjusts the other dates accordingly.
     *
     * @param days int
     */
    fun previousDay(days: Int) {
        JDN -= days
        JDNToIranian()
        JDNToJulian()
        JDNToGregorian()
    }

    /**
     * setIranianDate:
     * Sets the createdAt according to the Iranian calendar and adjusts the other dates.
     *
     * @param year  int
     * @param month int
     * @param day   int
     */
    fun setIranianDate(year: Int, month: Int, day: Int) {
        iranianYear = year
        iranianMonth = month
        iranianDay = day
        JDN = IranianDateToJDN()
        JDNToIranian()
        JDNToJulian()
        JDNToGregorian()
        JDNToIslamic()
    }

    /**
     * setGregorianDate:
     * Sets the createdAt according to the Gregorian calendar and adjusts the other dates.
     *
     * @param year  int
     * @param month int
     * @param day   int
     */
    fun setGregorianDate(year: Int, month: Int, day: Int) {
        gregorianYear = year
        gregorianMonth = month
        gregorianDay = day
        JDN = gregorianDateToJDN(year, month, day)
        JDNToIranian()
        JDNToJulian()
        JDNToGregorian()
        JDNToIslamic()
    }

    /**
     * setJulianDate:
     * Sets the createdAt according to the Julian calendar and adjusts the other dates.
     *
     * @param year  int
     * @param month int
     * @param day   int
     */
    fun setJulianDate(year: Int, month: Int, day: Int) {
        julianYear = year
        julianMonth = month
        julianDay = day
        JDN = julianDateToJDN(year, month, day)
        JDNToIranian()
        JDNToJulian()
        JDNToGregorian()
    }

    /**
     * IranianCalendar:
     * This method determines if the Iranian (Jalali) year is leap (366-day long)
     * or is the common year (365 days), and finds the day in March (Gregorian
     * Calendar)of the first day of the Iranian year ('irYear').Iranian year (irYear)
     * ranges from (-61 to 3177).This method will set the following private data
     * members as follows:
     * leap: Number of years since the last leap year (0 to 4)
     * Gy: Gregorian year of the begining of Iranian year
     * march: The March day of Farvardin the 1st (first day of jaYear)
     */
    private fun IranianCalendar() {
        // Iranian years starting the 33-year rule
        val Breaks = intArrayOf(-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178)
        var jm: Int
        var N: Int
        var leapJ: Int
        val leapG: Int
        var jp: Int
        var j: Int
        var jump: Int
        gregorianYear = iranianYear + 621
        leapJ = -14
        jp = Breaks[0]
        // Find the limiting years for the Iranian year 'irYear'
        j = 1
        do {
            jm = Breaks[j]
            jump = jm - jp
            if (iranianYear >= jm) {
                leapJ += jump / 33 * 8 + jump % 33 / 4
                jp = jm
            }
            j++
        } while (j < 20 && iranianYear >= jm)
        N = iranianYear - jp
        // Find the number of leap years from AD 621 to the begining of the current
        // Iranian year in the Iranian (Jalali) calendar
        leapJ += N / 33 * 8 + (N % 33 + 3) / 4
        if (jump % 33 == 4 && jump - N == 4)
            leapJ++
        // And the same in the Gregorian createdAt of Farvardin the first
        leapG = gregorianYear / 4 - (gregorianYear / 100 + 1) * 3 / 4 - 150
        march = 20 + leapJ - leapG
        // Find how many years have passed since the last leap year
        if (jump - N < 6)
            N = N - jump + (jump + 4) / 33 * 33
        leap = ((N + 1) % 33 - 1) % 4
        if (leap == -1)
            leap = 4
    }

    /**
     */
    fun getGregorianMonthName(month: Int): String {
        return this.monthNamesGreg[month - 1]
    }


    /**
     * IsLeap:
     * This method determines if the Iranian (Jalali) year is leap (366-day long)
     * or is the common year (365 days), and finds the day in March (Gregorian
     * Calendar)of the first day of the Iranian year ('irYear').Iranian year (irYear)
     * ranges from (-61 to 3177).This method will set the following private data
     * members as follows:
     * leap: Number of years since the last leap year (0 to 4)
     * Gy: Gregorian year of the begining of Iranian year
     * march: The March day of Farvardin the 1st (first day of jaYear)
     */
    fun IsLeap(irYear1: Int): Boolean {
        // Iranian years starting the 33-year rule
        val Breaks = intArrayOf(-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178)
        var jm: Int
        var N: Int
        var leapJ: Int
        val leapG: Int
        var jp: Int
        var j: Int
        var jump: Int
        gregorianYear = irYear1 + 621
        leapJ = -14
        jp = Breaks[0]
        // Find the limiting years for the Iranian year 'irYear'
        j = 1
        do {
            jm = Breaks[j]
            jump = jm - jp
            if (irYear1 >= jm) {
                leapJ += jump / 33 * 8 + jump % 33 / 4
                jp = jm
            }
            j++
        } while (j < 20 && irYear1 >= jm)
        N = irYear1 - jp
        // Find the number of leap years from AD 621 to the begining of the current
        // Iranian year in the Iranian (Jalali) calendar
        leapJ += N / 33 * 8 + (N % 33 + 3) / 4
        if (jump % 33 == 4 && jump - N == 4)
            leapJ++
        // And the same in the Gregorian createdAt of Farvardin the first
        leapG = gregorianYear / 4 - (gregorianYear / 100 + 1) * 3 / 4 - 150
        march = 20 + leapJ - leapG
        // Find how many years have passed since the last leap year
        if (jump - N < 6)
            N = N - jump + (jump + 4) / 33 * 33
        leap = ((N + 1) % 33 - 1) % 4
        if (leap == -1)
            leap = 4
        return leap == 4 || leap == 0

    }

    /**
     * IranianDateToJDN:
     * Converts a createdAt of the Iranian calendar to the Julian Day Number. It first
     * invokes the 'IranianCalender' private method to convert the Iranian createdAt to
     * Gregorian createdAt and then returns the Julian Day Number based on the Gregorian
     * createdAt. The Iranian createdAt is obtained from 'irYear'(1-3100),'irMonth'(1-12) and
     * 'irDay'(1-29/31).
     *
     * @return long (Julian Day Number)
     */
    private fun IranianDateToJDN(): Int {
        IranianCalendar()
        return gregorianDateToJDN(gregorianYear, 3, march) + (iranianMonth - 1) * 31 - iranianMonth / 7 * (iranianMonth - 7) + iranianDay - 1
    }

    /**
     * JDNToIranian:
     * Converts the current value of 'JDN' Julian Day Number to a createdAt in the
     * Iranian calendar. The caller should make sure that the current value of
     * 'JDN' is set correctly. This method first converts the JDN to Gregorian
     * calendar and then to Iranian calendar.
     */
    private fun JDNToIranian() {
        JDNToGregorian()
        iranianYear = gregorianYear - 621
        IranianCalendar() // This invocation will update 'leap' and 'march'
        val JDN1F = gregorianDateToJDN(gregorianYear, 3, march)
        var k = JDN - JDN1F
        if (k >= 0) {
            if (k <= 185) {
                iranianMonth = 1 + k / 31
                iranianDay = k % 31 + 1
                return
            } else
                k -= 186
        } else {
            iranianYear--
            k += 179
            if (leap == 1)
                k++
        }
        iranianMonth = 7 + k / 30
        iranianDay = k % 30 + 1
    }


    /**
     * julianDateToJDN:
     * Calculates the julian day number (JDN) from Julian calendar dates. This
     * integer number corresponds to the noon of the createdAt (i.e. 12 hours of
     * Universal Time). This method was tested to be good (valid) since 1 March,
     * -100100 (of both calendars) up to a few millions (10^6) years into the
     * future. The algorithm is based on D.A.Hatcher, Q.Jl.R.Astron.Soc. 25(1984),
     * 53-55 slightly modified by K.M. Borkowski, Post.Astron. 25(1987), 275-279.
     *
     * @param year  int
     * @param month int
     * @param day   int
     * @return int
     */
    private fun julianDateToJDN(year: Int, month: Int, day: Int): Int {
        return (year + (month - 8) / 6 + 100100) * 1461 / 4 + (153 * ((month + 9) % 12) + 2) / 5 + day - 34840408
    }

    /**
     * JDNToJulian:
     * Calculates Julian calendar dates from the julian day number (JDN) for the
     * period since JDN=-34839655 (i.e. the year -100100 of both calendars) to
     * some millions (10^6) years ahead of the present. The algorithm is based on
     * D.A. Hatcher, Q.Jl.R.Astron.Soc. 25(1984), 53-55 slightly modified by K.M.
     * Borkowski, Post.Astron. 25(1987), 275-279).
     */
    private fun JDNToJulian() {
        val j = 4 * JDN + 139361631
        val i = j % 1461 / 4 * 5 + 308
        julianDay = i % 153 / 5 + 1
        julianMonth = i / 153 % 12 + 1
        julianYear = j / 1461 - 100100 + (8 - julianMonth) / 6
    }

    /**
     * gergorianDateToJDN:
     * Calculates the julian day number (JDN) from Gregorian calendar dates. This
     * integer number corresponds to the noon of the createdAt (i.e. 12 hours of
     * Universal Time). This method was tested to be good (valid) since 1 March,
     * -100100 (of both calendars) up to a few millions (10^6) years into the
     * future. The algorithm is based on D.A.Hatcher, Q.Jl.R.Astron.Soc. 25(1984),
     * 53-55 slightly modified by K.M. Borkowski, Post.Astron. 25(1987), 275-279.
     *
     * @param year  int
     * @param month int
     * @param day   int
     * @return int
     */
    private fun gregorianDateToJDN(year: Int, month: Int, day: Int): Int {
        var jdn = (year + (month - 8) / 6 + 100100) * 1461 / 4 + (153 * ((month + 9) % 12) + 2) / 5 + day - 34840408
        jdn = jdn - (year + 100100 + (month - 8) / 6) / 100 * 3 / 4 + 752
        return jdn
    }

    /**
     * JDNToGregorian:
     * Calculates Gregorian calendar dates from the julian day number (JDN) for
     * the period since JDN=-34839655 (i.e. the year -100100 of both calendars) to
     * some millions (10^6) years ahead of the present. The algorithm is based on
     * D.A. Hatcher, Q.Jl.R.Astron.Soc. 25(1984), 53-55 slightly modified by K.M.
     * Borkowski, Post.Astron. 25(1987), 275-279).
     */
    private fun JDNToGregorian() {
        var j = 4 * JDN + 139361631
        j = j + ((4 * JDN + 183187720) / 146097 * 3 / 4 * 4 - 3908)
        val i = j % 1461 / 4 * 5 + 308
        gregorianDay = i % 153 / 5 + 1
        gregorianMonth = i / 153 % 12 + 1
        gregorianYear = j / 1461 - 100100 + (8 - gregorianMonth) / 6
    }

    /*
   * JDN To Islamic
   * */
    private fun JDNToIslamic() {

        var year = this.julianYear
        var month = this.julianMonth
        var day = this.julianDay

        var k = Math.floor(0.6 + ((year.toDouble() + (if (month % 2 == 0) month else month - 1) / 12.0
                + (day / 365f).toDouble()) - 1900) * 12.3685).toLong()

        var mjd: Double
        do {
            mjd = visibility(k)
            k = k - 1
        } while (mjd > this.JDN - 0.5)

        k = k + 1
        val hm = k - 1048

        year = 1405 + (hm / 12).toInt()
        month = (hm % 12).toInt() + 1

        if (hm != 0L && month <= 0) {
            month = month + 12
            year = year - 1
        }

        if (year <= 0)
            year = year - 1

        day = Math.floor(this.JDN - mjd + 0.5).toInt()

        this.hijriYear = year
        this.hijriMonth = month + 1
        this.hijriDay = day
    }

    private fun visibility(n: Long): Double {

        // parameters for Makkah: for a new moon to be visible after sunset on
        // a the same day in which it started, it has to have started before
        // (SUNSET-MINAGE)-TIMZ=3 A.M. local time.
        val TIMZ = 3f
        val MINAGE = 13.5f
        val SUNSET = 19.5f
        // approximate
        val TIMDIF = SUNSET - MINAGE

        val jd = tmoonphase(n, 0)
        val d = Math.floor(jd).toLong()

        var tf = jd - d

        if (tf <= 0.5)
        // new moon starts in the afternoon
            return jd + 1f
        else { // new moon starts before noon
            tf = (tf - 0.5) * 24 + TIMZ // local time
            return if (tf > TIMDIF)
                jd + 1.0 // age at sunset < min for visiblity
            else
                jd
        }

    }

    private fun tmoonphase(n: Long, nph: Int): Double {

        val RPD = 1.74532925199433E-02 // radians per degree
        // (pi/180)

        var xtra = 0.0

        val k = n + nph / 4.0
        val T = k / 1236.85
        val t2 = T * T
        val t3 = t2 * T
        val jd = (2415020.75933 + 29.53058868 * k - 0.0001178 * t2
                - 0.000000155 * t3) + 0.00033 * Math.sin(RPD * (166.56 + 132.87 * T - 0.009173 * t2))

        // Sun's mean anomaly
        val sa = RPD * (359.2242 + 29.10535608 * k - 0.0000333 * t2 - 0.00000347 * t3)

        // Moon's mean anomaly
        val ma = RPD * (306.0253 + 385.81691806 * k + 0.0107306 * t2 + 0.00001236 * t3)

        // Moon's argument of latitude
        val tf = (RPD
                * 2.0
                * (21.2964 + 390.67050646 * k - 0.0016528 * t2 - 0.00000239 * t3))

        // should reduce to interval 0-1.0 before calculating further
        when (nph) {
            0, 2 -> xtra = (0.1734 - 0.000393 * T) * Math.sin(sa) + 0.0021 * Math.sin(sa * 2) - 0.4068 * Math.sin(ma) + 0.0161 * Math.sin(2 * ma) - 0.0004 * Math.sin(3 * ma) + 0.0104 * Math.sin(tf) - 0.0051 * Math.sin(sa + ma) - 0.0074 * Math.sin(sa - ma) + 0.0004 * Math.sin(tf + sa) - 0.0004 * Math.sin(tf - sa) - 0.0006 * Math.sin(tf + ma) + 0.001 * Math.sin(tf - ma) + 0.0005 * Math.sin(sa + 2 * ma)
            1, 3 -> {
                xtra = ((0.1721 - 0.0004 * T) * Math.sin(sa) + 0.0021 * Math.sin(sa * 2) - 0.628 * Math.sin(ma) + 0.0089 * Math.sin(2 * ma) - 0.0004 * Math.sin(3 * ma) + 0.0079 * Math.sin(tf) - 0.0119 * Math.sin(sa + ma) - 0.0047 * Math.sin(sa - ma) + 0.0003 * Math.sin(tf + sa) - 0.0004 * Math.sin(tf - sa) - 0.0006 * Math.sin(tf + ma) + 0.0021 * Math.sin(tf - ma) + 0.0003 * Math.sin(sa + 2 * ma)
                        + 0.0004 * Math.sin(sa - 2 * ma)) - 0.0003 * Math.sin(2 * sa + ma)
                if (nph == 1)
                    xtra = xtra + 0.0028 - 0.0004 * Math.cos(sa) + 0.0003 * Math.cos(ma)
                else
                    xtra = xtra - 0.0028 + 0.0004 * Math.cos(sa) - 0.0003 * Math.cos(ma)
            }
            else -> return 0.0
        }
        // convert from Ephemeris Time (ET) to (approximate)Universal Time (UT)
        return jd + xtra - (0.41 + 1.2053 * T + 0.4992 * t2) / 1440
    }


}
