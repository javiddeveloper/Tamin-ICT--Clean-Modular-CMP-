package com.tamin.taminhamrah.data.remote.models.services

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.TestOnly
import saman.zamani.persiandate.PersianDate

class CombinedRecordResponse :ListDataModel<CombinedRecordModel>()

@Parcelize
data class CombinedRecordModel(
    var hisMonth8: String? = null,
    var hisMonth9: String? = null,
    var hisMonth6: String? = null,
    var hisMonth7: String? = null,
    var hisMonth1: String? = null,
    var hisMonth4: String? = null,
    var hisMonth5: String? = null,
    var hisMonth2: String? = null,
    var hisMonth3: String? = null,
    var hisMonth10: String? = null,
    var risuid: String? = null,
    var hisMonth11: String? = null,
    var hisMonth12: String? = null,
    var historyYears: String? = null,
    var historyMonths: String? = null,
    var sumYear: Int? = null,
    var historyDays: String? = null,
    var sumHistoryYears: String? = null,
    var id: Int? = null,
    var hisYear: String? = null
) : Parcelable{
    fun getDay(day: String?): String{
        day?.let{
            if (it.length==1)
                return "0$day"
            return day
        }
        return "00"
    }
    @IgnoredOnParcel
    var isDuplicate = false


    fun isLeapYear(year: String?): Boolean {
        year?.let {
            if (PersianDate.isJalaliLeap(year.toInt())) {
                return true
            }
        }
        return false
    }

    @TestOnly
    override fun toString(): String {
        return "{\"status\":200,\"family1\":\"{SUCCESSFUL\"}\",\"reason\":\"OK\",\"data\":{\"total\":0,\"list\":[{\"hisMonth8\":\"0\",\"hisMonth9\":\"27\",\"hisMonth6\":\"27\",\"hisMonth7\":\"27\",\"hisMonth1\":\"0\",\"hisMonth4\":\"0\",\"hisMonth5\":\"0\",\"hisMonth2\":\"0\",\"hisMonth3\":\"0\",\"hisMonth10\":\"0\",\"risuid\":\"0033261750\",\"hisMonth11\":\"0\",\"hisMonth12\":\"26\",\"historyYears\":7,\"historyMonths\":4,\"sumYear\":107,\"historyDays\":0,\"sumHistoryYears\":2679,\"id\":1,\"hisYear\":\"1393\"},{\"hisMonth8\":\"30\",\"hisMonth9\":\"30\",\"hisMonth6\":\"17\",\"hisMonth7\":\"28\",\"hisMonth1\":\"0\",\"hisMonth4\":\"30\",\"hisMonth5\":\"15\",\"hisMonth2\":\"0\",\"hisMonth3\":\"31\",\"hisMonth10\":\"0\",\"risuid\":\"0033261750\",\"hisMonth11\":\"0\",\"hisMonth12\":\"0\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":181,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":2,\"hisYear\":\"1394\"},{\"hisMonth8\":\"30\",\"hisMonth9\":\"30\",\"hisMonth6\":\"31\",\"hisMonth7\":\"30\",\"hisMonth1\":\"18\",\"hisMonth4\":\"31\",\"hisMonth5\":\"31\",\"hisMonth2\":\"31\",\"hisMonth3\":\"31\",\"hisMonth10\":\"30\",\"risuid\":\"0033261750\",\"hisMonth11\":\"30\",\"hisMonth12\":\"30\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":353,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":3,\"hisYear\":\"1395\"},{\"hisMonth8\":\"30\",\"hisMonth9\":\"30\",\"hisMonth6\":\"0\",\"hisMonth7\":\"30\",\"hisMonth1\":\"31\",\"hisMonth4\":\"0\",\"hisMonth5\":\"0\",\"hisMonth2\":\"31\",\"hisMonth3\":\"0\",\"hisMonth10\":\"30\",\"risuid\":\"0033261750\",\"hisMonth11\":\"30\",\"hisMonth12\":\"0\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":212,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":4,\"hisYear\":\"1396\"},{\"hisMonth8\":\"30\",\"hisMonth9\":\"30\",\"hisMonth6\":\"31\",\"hisMonth7\":\"30\",\"hisMonth1\":\"0\",\"hisMonth4\":\"31\",\"hisMonth5\":\"31\",\"hisMonth2\":\"31\",\"hisMonth3\":\"31\",\"hisMonth10\":\"30\",\"risuid\":\"0033261750\",\"hisMonth11\":\"30\",\"hisMonth12\":\"29\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":334,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":5,\"hisYear\":\"1397\"},{\"hisMonth8\":\"30\",\"hisMonth9\":\"30\",\"hisMonth6\":\"31\",\"hisMonth7\":\"30\",\"hisMonth1\":\"31\",\"hisMonth4\":\"31\",\"hisMonth5\":\"31\",\"hisMonth2\":\"31\",\"hisMonth3\":\"31\",\"hisMonth10\":\"30\",\"risuid\":\"0033261750\",\"hisMonth11\":\"30\",\"hisMonth12\":\"29\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":365,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":6,\"hisYear\":\"1398\"},{\"hisMonth8\":\"30\",\"hisMonth9\":\"30\",\"hisMonth6\":\"31\",\"hisMonth7\":\"30\",\"hisMonth1\":\"31\",\"hisMonth4\":\"31\",\"hisMonth5\":\"31\",\"hisMonth2\":\"31\",\"hisMonth3\":\"31\",\"hisMonth10\":\"30\",\"risuid\":\"0033261750\",\"hisMonth11\":\"30\",\"hisMonth12\":\"30\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":366,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":7,\"hisYear\":\"1399\"},{\"hisMonth8\":\"30\",\"hisMonth9\":\"30\",\"hisMonth6\":\"31\",\"hisMonth7\":\"30\",\"hisMonth1\":\"31\",\"hisMonth4\":\"31\",\"hisMonth5\":\"31\",\"hisMonth2\":\"31\",\"hisMonth3\":\"31\",\"hisMonth10\":\"30\",\"risuid\":\"0033261750\",\"hisMonth11\":\"30\",\"hisMonth12\":\"29\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":365,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":8,\"hisYear\":\"1400\"},{\"hisMonth8\":\"30\",\"hisMonth9\":\"30\",\"hisMonth6\":\"31\",\"hisMonth7\":\"30\",\"hisMonth1\":\"31\",\"hisMonth4\":\"31\",\"hisMonth5\":\"31\",\"hisMonth2\":\"31\",\"hisMonth3\":\"31\",\"hisMonth10\":\"30\",\"risuid\":\"0033261750\",\"hisMonth11\":\"30\",\"hisMonth12\":\"29\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":365,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":9,\"hisYear\":\"1401\"},{\"hisMonth8\":\"0\",\"hisMonth9\":\"0\",\"hisMonth6\":\"0\",\"hisMonth7\":\"0\",\"hisMonth1\":\"31\",\"hisMonth4\":\"0\",\"hisMonth5\":\"0\",\"hisMonth2\":\"0\",\"hisMonth3\":\"0\",\"hisMonth10\":\"0\",\"risuid\":\"0033261750\",\"hisMonth11\":\"0\",\"hisMonth12\":\"0\",\"historyYears\":null,\"historyMonths\":null,\"sumYear\":31,\"historyDays\":null,\"sumHistoryYears\":null,\"id\":10,\"hisYear\":\"1402\"}]}}"
    }
}

class CombinedRecordModels : ArrayList<CombinedRecordModel>()


