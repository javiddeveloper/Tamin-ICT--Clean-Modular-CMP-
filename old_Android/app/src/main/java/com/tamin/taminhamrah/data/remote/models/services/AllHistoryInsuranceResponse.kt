package com.tamin.taminhamrah.data.remote.models.services

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import kotlinx.parcelize.Parcelize
import saman.zamani.persiandate.PersianDate

class AllHistoryResponse : ListDataModel<AllHistoryInsuranceResponseModel>()

@Parcelize
data class AllHistoryInsuranceResponseModel(
    var id: String? = null,
    var year: String = "",
    var historyTypeName: String? = null,
    var branchname: String? = null,
    var workShopCode: String? = null,
    var workShopName: String? = null,
    var month1: String? = null,
    var month2: String? = null,
    var month3: String? = null,
    var month4: String? = null,
    var month5: String? = null,
    var month6: String? = null,
    var month7: String? = null,
    var month8: String? = null,
    var month9: String? = null,
    var month10: String? = null,
    var month11: String? = null,
    var month12: String? = null
) : Parcelable
{
    fun isLeapYear(year: String?): Boolean {
        year?.let {
            if (PersianDate.isJalaliLeap(year.toInt())) {
                return true
            }
        }
        return false
    }
}


@Parcelize
class AllHistoryInsuranceResponseModels : ArrayList<AllHistoryInsuranceResponseModel>(), Parcelable

