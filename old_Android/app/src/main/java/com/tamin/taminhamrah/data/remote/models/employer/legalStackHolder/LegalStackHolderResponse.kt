package com.tamin.taminhamrah.data.remote.models.employer.legalStackHolder

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import kotlinx.parcelize.Parcelize


class LegalStackHolderResponse() : ListDataModel<LegalStackHolder>()

@Parcelize
data class LegalStackHolder(


    var accessCode: String? = null,
    var accessDesc: String? = null,
    var nationalId: String? = null,
    var stakeId: Long? = null,
    var startDate: Long? = null,
    var workshopId: String? = null,
    var workshopName: String? = null,
    var branchCode: String? = null,
    var special:Boolean?=null

    ) : Parcelable {

    fun getTitle(str: String?) = str ?: "-"

    fun getLocalDate() = ConvertDate.convertTimestampToPersianDate(startDate ?: 0)

    fun getAccessCode(elecNotif:Boolean, hasInternetList:Boolean,registration:Boolean) =  "${if(elecNotif) "1" else "0"}${if(hasInternetList) "1" else "0"}${if(registration) "1" else "0"}00000"
    fun isInternetList() = accessCode =="01000000"
    fun isElecNotif() = accessCode =="10000000"
    fun isRegistration() = accessCode =="00100000"
}
