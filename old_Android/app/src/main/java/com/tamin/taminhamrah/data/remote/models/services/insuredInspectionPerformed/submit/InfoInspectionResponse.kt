package com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate
import kotlinx.parcelize.Parcelize

data class InfoInspectionResponse(
    val data: InfoInspectionModel? = null,
) : BaseResponseNew()

@Parcelize
class InfoInspectionModel(
    val currentOrganization: CurrentOrganization? = null,
    val list:  UserDataModel? = null
):Parcelable

@Parcelize
data class UserDataModel(
    val brchCode: String? = null,
    val email: String? = "-",
    val inspectionNumber: String? = null,
    val insuranceFamily: String? = null,
    val insuranceFatherName: String? = null,
    val insuranceId: String? = null,
    val insuranceName: String? = null,
    val insuranceNo: String? = null,
    val jobcode: String? = null,
//    val mobile:  String? = "0",
//    val nationalCode: String? = "0",
    val serialNo1: String? = null,
    val serialNo2: String? = null,
    val ssn:  String? = null,
    val userTel:  String? = "0",
    val wageDay: Int? = null,
    val workDay: Int? = null,
    val workStartDate: Long? = 0,
    val workshopAddress: String? = "-",
    val workshopManager: String? = "-",
    val workshopName: String? = "-",
    val workshopNumber: String? = "-",
    val workshopTel: String? = "-"
):Parcelable
{
    fun getInspectionDatePersian(): String {
        return ConvertDate.convertTimestampToPersianDate(workStartDate?:0)
    }
}

@Parcelize
data class CurrentOrganization(
    val actKey: Int? = null,
    val children: String? = null,
    val code:  String? = null,
    val entityId: String? = null,
    val organizationName: String? = "-",
    val type: String? = null
):Parcelable

