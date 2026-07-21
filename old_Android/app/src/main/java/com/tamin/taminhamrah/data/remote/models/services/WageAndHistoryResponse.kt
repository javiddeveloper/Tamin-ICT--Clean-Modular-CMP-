package com.tamin.taminhamrah.data.remote.models.services

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.utils.Utility
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

class WageAndHistoryResponse : ListDataModel<WageAndHistoryModel>()

@Parcelize
data class WageAndHistoryModel(

    var id: String? = null,
    var hisyear: String = "",
    var historytypedesc: String? = null,
    var brhname: String? = null,
    var rwshid: String? = null,
    var rwshname: String? = null,
    var hismon1: String? = null,
    var hismon2: String? = null,
    var hismon3: String? = null,
    var hismon4: String? = null,
    var hismon5: String? = null,
    var hismon6: String? = null,
    var hismon7: String? = null,
    var hismon8: String? = null,
    var hismon9: String? = null,
    var hismon10: String? = null,
    var hismon11: String? = null,
    var hismon12: String? = null,
    var hiswage1: String? = null,
    var hiswage2: String? = null,
    var hiswage3: String? = null,
    var hiswage4: String? = null,
    var hiswage5: String? = null,
    var hiswage6: String? = null,
    var hiswage7: String? = null,
    var hiswage8: String? = null,
    var hiswage9: String? = null,
    var hiswage10: String? = null,
    var hiswage11: String? = null,
    var hiswage12: String? = null
) : Parcelable {

    @IgnoredOnParcel
    var isDuplicate = false
    @IgnoredOnParcel
    private val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    fun getCurrency(wage: String?): String? {
        wage?.let {
            return Utility.getNumberWithSeparatorForStringValue(it)
        }
        return "0"
    }

    fun getDay(day: String?): String? {
        day?.let {
            if (it.length == 1)
                return "0$day"
            return day
        }
        return "00"
    }

    fun getWageHistory(): List<Pair<String?, String?>> =
        listOf(
            hiswage1 to hismon1, hiswage2 to hismon2, hiswage3 to hismon3,
            hiswage4 to hismon4, hiswage5 to hismon5, hiswage6 to hismon6,
            hiswage7 to hismon7, hiswage8 to hismon8, hiswage9 to hismon9,
            hiswage10 to hismon10, hiswage11 to hismon11, hiswage12 to hismon12
        )

    fun getMonthlyWageHistory(month: Int): Pair<String?, String?> {
        return when (month) {
            1 -> hiswage1 to hismon1
            2 -> hiswage2 to hismon2
            3 -> hiswage3 to hismon3
            4 -> hiswage4 to hismon4
            5 -> hiswage5 to hismon5
            6 -> hiswage6 to hismon6
            7 -> hiswage7 to hismon7
            8 -> hiswage8 to hismon8
            9 -> hiswage9 to hismon9
            10 -> hiswage10 to hismon10
            11 -> hiswage11 to hismon11
            12 -> hiswage12 to hismon12
            else -> null to null
        }
    }

    fun getMonthlyKeyValue(month: Int): KeyValueModel? {
        val (wage, days) = getMonthlyWageHistory(month)
        if (!wage.isNullOrBlank() && wage != "0" && !days.isNullOrBlank() && days != "0") {
            val monthName = monthNames.getOrNull(month - 1) ?: "نامشخص"
            return KeyValueModel(
                monthName,
                "${getDay(days)} روز ${getCurrency(wage)} ریال "
            )
        }
        return null
    }

    fun createLastPayKeyValue(): List<KeyValueModel> {
        val items = mutableListOf(
            KeyValueModel("اطلاعات کارگاه", rwshname ?: "-"),
            KeyValueModel("نوع سابقه", historytypedesc ?: "-"),
            KeyValueModel("نام شعبه", brhname ?: "-")
        )

        val dataList = getWageHistory()
        val lastIndex = dataList.indexOfLast {
            val wage = it.first
            val day = it.second
            !wage.isNullOrBlank() && wage != "0" && !day.isNullOrBlank() && day != "0"
        }

        if (lastIndex != -1) {
            val (wage, dayCount) = dataList[lastIndex]
            val monthName = monthNames.getOrNull(lastIndex) ?: "نامشخص"
            val finalText =
                "$monthName ماه سال $hisyear تعداد ${getDay(dayCount)} روز مبلغ ${getCurrency(wage)} ریال"
            items.add(KeyValueModel("آخرین بیمه پردازی", finalText))
        } else {
            items.add(KeyValueModel("آخرین بیمه پردازی", "یافت نشد!!"))
        }
        return items
    }


    fun createKeyValue(month: Int? = null): MutableList<KeyValueModel> {
        val finalList: MutableList<KeyValueModel> = mutableListOf()
        finalList.add(KeyValueModel("سابقه سال", hisyear))
        finalList.add(KeyValueModel("اطلاعات کارگاه", rwshname ?: "-"))
        finalList.add(KeyValueModel("نوع سابقه", historytypedesc ?: "-"))
        finalList.add(KeyValueModel("نام شعبه", brhname ?: "-"))

        val monthsToProcess = month?.let { listOf(it) } ?: (1..12).toList()

        monthsToProcess.forEach { m ->
            val (wage, days) = getMonthlyWageHistory(m)
            if (!wage.isNullOrBlank() && wage != "0" && !days.isNullOrBlank() && days != "0") {
                val monthName = monthNames.getOrNull(m - 1) ?: "نامشخص"
                finalList.add(
                    KeyValueModel(
                        monthName,
                        "${getDay(days)} روز ${getCurrency(wage)} ریال "
                    )
                )
            }
        }
        return finalList
    }
}


fun WageAndHistoryModel.asDomainModel(): WageAndHistoryModel {
    return WageAndHistoryModel(
        id = this.id,
        hisyear = this.hisyear,
        historytypedesc = this.historytypedesc,
        brhname = this.brhname,
        rwshid = this.rwshid,
        rwshname = this.rwshname,
        hismon1 = this.hismon1,
        hismon2 = this.hismon2,
        hismon3 = this.hismon3,
        hismon4 = this.hismon4,
        hismon5 = this.hismon5,
        hismon6 = this.hismon6,
        hismon7 = this.hismon7,
        hismon8 = this.hismon8,
        hismon9 = this.hismon9,
        hismon10 = this.hismon10,
        hismon11 = this.hismon11,
        hismon12 = this.hismon12,
        hiswage1 = this.hiswage1,
        hiswage2 = this.hiswage2,
        hiswage3 = this.hiswage3,
        hiswage4 = this.hiswage4,
        hiswage5 = this.hiswage5,
        hiswage6 = this.hiswage6,
        hiswage7 = this.hiswage7,
        hiswage8 = this.hiswage8,
        hiswage9 = this.hiswage9,
        hiswage10 = this.hiswage10,
        hiswage11 = this.hiswage11,
        hiswage12 = this.hiswage12
    )
}


fun List<WageAndHistoryModel>.asDomainModel(): List<WageAndHistoryModel> {
    return map {
        it.asDomainModel()
    }
}

fun WageAndHistoryModel.sumDayItems(): YearDays {
    var sumDays: Int = 0
    sumDays = this.hismon1?.toIntOrNull() ?: 0
    sumDays += this.hismon2?.toIntOrNull() ?: 0
    sumDays += this.hismon3?.toIntOrNull() ?: 0
    sumDays += this.hismon3?.toIntOrNull() ?: 0
    sumDays += this.hismon4?.toIntOrNull() ?: 0
    sumDays += this.hismon5?.toIntOrNull() ?: 0
    sumDays += this.hismon6?.toIntOrNull() ?: 0
    sumDays += this.hismon7?.toIntOrNull() ?: 0
    sumDays += this.hismon8?.toIntOrNull() ?: 0
    sumDays += this.hismon9?.toIntOrNull() ?: 0
    sumDays += this.hismon10?.toIntOrNull() ?: 0
    sumDays += this.hismon11?.toIntOrNull() ?: 0
    sumDays += this.hismon12?.toIntOrNull() ?: 0

    return YearDays(year = this.hisyear, sumDays = sumDays.toString())
}

data class YearDays(val year: String, val sumDays: String)

private fun sumDaysInTowMonth(monthOld: String?, monthNew: String?): String {
    var a = monthOld?.toInt() ?: 0
    val b = monthNew?.toInt() ?: 0
    a += b
    return a.toString()
}

@Parcelize
class WageAndHistoryModels : ArrayList<WageAndHistoryModel>(), Parcelable

fun List<WageAndHistoryModel>.findLastPaidYear(): WageAndHistoryModel? {
    return this
        .sortedByDescending { it.hisyear.toIntOrNull() ?: 0 }
        .firstOrNull { model ->
            model.getWageHistory().any { it.first != "0" && it.second != "0" }
        }
}