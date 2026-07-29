package com.tamin.taminhamrah.data.remote.models.services

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import kotlinx.parcelize.Parcelize

import saman.zamani.persiandate.PersianDate

class ObjectionInsuranceHistoryResponse : ListDataModel<ObjectionInsuranceHistoryModel>()


@Parcelize
data class ObjectionInsuranceHistoryModel(
    @Transient
    var id: Int = 0,
    var branchCode: String? = null,
    var branchname: String? = null,
    var confirmed: Boolean? = null,
    val deleted: Boolean? = false,
    var historyTypeCode: String? = null,
    var historyTypeName: String? = null,
    var isDeleted: Boolean? = null,
    @SerializedName("mm1")
    var newMonth1: String? = null,
    @SerializedName("mm2")
    var newMonth2: String? = null,
    @SerializedName("mm3")
    var newMonth3: String? = null,
    @SerializedName("mm4")
    var newMonth4: String? = null,
    @SerializedName("mm5")
    var newMonth5: String? = null,
    @SerializedName("mm6")
    var newMonth6: String? = null,
    @SerializedName("mm7")
    var newMonth7: String? = null,
    @SerializedName("mm8")
    var newMonth8: String? = null,
    @SerializedName("mm9")
    var newMonth9: String? = null,
    @SerializedName("mm10")
    var newMonth10: String? = null,
    @SerializedName("mm11")
    var newMonth11: String? = null,
    @SerializedName("mm12")
    var newMonth12: String? = null,
    @SerializedName("om1")
    var oldMonth1: String? = null,
    @SerializedName("om2")
    var oldMonth2: String? = null,
    @SerializedName("om3")
    var oldMonth3: String? = null,
    @SerializedName("om4")
    var oldMonth4: String? = null,
    @SerializedName("om5")
    var oldMonth5: String? = null,
    @SerializedName("om6")
    var oldMonth6: String? = null,
    @SerializedName("om7")
    var oldMonth7: String? = null,
    @SerializedName("om8")
    var oldMonth8: String? = null,
    @SerializedName("om9")
    var oldMonth9: String? = null,
    @SerializedName("om10")
    var oldMonth10: String? = null,
    @SerializedName("om11")
    var oldMonth11: String? = null,
    @SerializedName("om12")
    var oldMonth12: String? = null,
    var prow: String? = null,
    var reqno: String? = null,
    var reqtype: String? = null,
    var risuid: String? = null,
    var rwshid: String? = null,
    var userDesc: String? = null,
    var workShopName: String? = null,
    var year: String = "",
    @Transient
    var isEdited: Boolean = false,
    @Transient
    var detailInfo: MenuModel = MenuModel()

) : Parcelable {
    fun isLeapYear(): Boolean {
            if (PersianDate.isJalaliLeap(year.toInt())) {
                return true
            }
        return false
    }

    fun deletedEditItem() {
        newMonth1 = ""
        newMonth2 = ""
        newMonth3 = ""
        newMonth4 = ""
        newMonth5 = ""
        newMonth6 = ""
        newMonth7 = ""
        newMonth8 = ""
        newMonth9 = ""
        newMonth10 = ""
        newMonth11 = ""
        newMonth12 = ""
        isEdited = false
    }

    fun setDefaultValue() {
        userDesc = ""
        prow = null
        reqno = null
        reqtype = null
        isDeleted = false
    }


    fun editedItem(item: ObjectionInsuranceHistoryModel) {
        newMonth1 = item.newMonth1
        newMonth2 = item.newMonth2
        newMonth3 = item.newMonth3
        newMonth4 = item.newMonth4
        newMonth5 = item.newMonth5
        newMonth6 = item.newMonth6
        newMonth7 = item.newMonth7
        newMonth8 = item.newMonth8
        newMonth9 = item.newMonth9
        newMonth10 = item.newMonth10
        newMonth11 = item.newMonth11
        newMonth12 = item.newMonth12
        isEdited = true
    }
}
