package com.tamin.taminhamrah.data.remote.models.employer

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.HelperDate
import com.tamin.taminhamrah.utils.Utility
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class LetterListResponse : ListDataModel<LetterInfo>()

@Parcelize
data class LetterInfo(
    var requestid: String? = "",
    var letetsubjectcode: Letetsubjectcode? = null,
    var leterrequestId: Long? = 0,
    var rwshid: String? = "",
    var rcntrow: @RawValue Any? = null,
    var requestDate: Long? = 0,
    var userId: String? = "",
    var status: String? = "",
    var editDate:@RawValue Any? = null,
    var editUser:@RawValue Any? = null,
    var brchCode: String? = "",
    var celphone:@RawValue Any? = null,
    var relationwithworkshop: String? = "",
    var leterImage: String? = "",
    var descriptions: String? = "",
    var descriptionsBranch:@RawValue Any? = null,
    var letterRequestDetailCollection: ArrayList<LetterRequestDetailCollection>? = null,
):Parcelable {
    fun getTitle(title: String?) = title ?: "-"
    fun getTitle(title: Long?) = "$title" ?: "-"

    fun getLocalDate(date: Long) = ConvertDate.convertTimestampToPersianDate(date)

    fun getStatus(status:String) = when (status) {
        "0001" ->
            "ثبت اولیه";
        "0005" ->
            "در حال بررسی";
        "0018" ->
            "مختومه و تایید نهایی";
        "0019" ->
            "مختومه و عدم تایید نهایی";
        else->"-"

    }


}
@Parcelize
data class Letetsubjectcode(
    var code: Long? = 0,
    var subjectDesc: String? = null,
    var status: String? = "",
    var createdBy:@RawValue Any? = null,
    var creationTime:@RawValue Any? = null,
    var lastModificationTime:@RawValue Any? = null,
    var lastModifiedBy:@RawValue Any? = null,
    var contract: String? = null,
    var subjectCode: Long? = null
):Parcelable
@Parcelize
data class LetterRequestDetailCollection(
    var id: Long? = 0,
    var leterNo: String? = "",
    var leterDate: String? = "",
    var risuid: String? = "",
    var dbtno: String? = "",
    var ordno: String? = "",
    var year: String? = "",
    var month: String? = "",
    var listno: String? = "",
    var sdate: String? = "",
    var edate: String? = "",
    var requertType: String? = "00",
    var destinationRcntrow: String? = "",
    var amount: Long? = 0,
    var f1:@RawValue Any? = null,
    var f2:@RawValue Any? = null,
    var f3:@RawValue Any? = null,
    var f4:@RawValue Any? = null,
    var day: String? = "",
    var address: String? = ""
) : Cloneable, Parcelable  {

    fun getRial(rialStr:Long?)= Utility.getRialWithSeparator(rialStr)

    fun getLocalFormat(dateStr:String?): String {
        var localDate:String?=null
        dateStr?.let {
            val date = HelperDate.convertStringToDate(dateStr,"yyyy-MM-dd'T'HH:mm:ss.sss")?.time
             localDate = date?.let { it1 -> ConvertDate.convertTimestampToPersianDate(it1) }
        }

        return localDate?:"-"
    }

    fun clearData() {
        id = 0
        leterNo = ""
        leterDate = ""
        risuid = ""
        dbtno = ""
        ordno = ""
        year = ""
        month = ""
        listno = ""
        sdate = ""
        edate = ""
        requertType = ""
        destinationRcntrow = ""
        amount = 0
        f1 = ""
        f2 = ""
        f3 = ""
        f4 = ""
        day = ""
        address = ""
    }

    fun isEmpty(viewId: String?): Boolean {
        return when (viewId) {
            "1" -> risuid == "" || dbtno == "" || ordno == "" || year == "" || month == ""
            "2" -> ordno == "" || dbtno == ""
            "3" -> risuid == "" || leterDate == "" || leterNo == ""
            "4" -> risuid == "" || leterDate == "" || leterNo == "" || sdate == "null" || edate == ""
            "5" -> risuid == "" || requertType == "00"
            "6" -> risuid == ""
            "7" -> risuid == "" || year == "" || month == "" || listno == ""
            "8" -> requertType == "00" || leterNo == "" || leterDate == "" || address == ""
            "9" -> sdate == "" || edate == ""
            "10" -> requertType == "00" || sdate == "" || edate == ""
            "11" -> sdate == "" || edate == "" || day == ""
            "12" -> sdate == "" || edate == "" || day == "" || risuid == ""
            "13" -> sdate == "" || edate == "" || risuid == "" || requertType == "00"
            "14" -> sdate == "" || edate == "" || risuid == "" || requertType == "00"
            "15" -> sdate == "" || risuid == ""
            "16" -> dbtno == "" || amount == 0L
            "17" -> edate == ""
            "18", "21", "22", "27", "28" -> false
            "19" -> requertType == "00"
            "20" -> dbtno == "" || amount == 0L
            "23" -> dbtno == ""
            "24" -> destinationRcntrow == null || ordno == "" || amount == 0L
            "25" -> sdate == "" || ordno == "" || amount == 0L
            "26" -> risuid == "" || f1 == null || sdate == "" || edate == ""
            else -> false
        }
    }

    public override fun clone(): LetterRequestDetailCollection {
        return LetterRequestDetailCollection(
            id,
            leterNo,
            leterDate,
            risuid,
            dbtno,
            ordno,
            year,
            month,
            listno,
            sdate,
            edate,
            requertType,
            destinationRcntrow,
            amount,
            f1,
            f2,
            f3,
            f4,
            day,
            address
        ) //for example
    }
}

