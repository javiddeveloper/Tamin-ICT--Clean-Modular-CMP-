package com.tamin.taminhamrah.data.remote.models.services

import android.os.Parcel
import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate


data class IdentityInfoResponse(
    var data: IdentityInfo? = null
) : BaseResponseNew(), Parcelable {

    inner class IdentityInfo(
        var firstName: String? = null,
        var lastName: String? = null,
        var fatherName: String? = null,
        var cityOfBirthId: String? = null,//timeStamp
        var cityOfIssueId: String? = null,//timeStamp
        var countryId: String? = null,
        var dateOfBirth: Long? = null,
        var gender: String? = null,
        var idCardNumber: String? = null,
        var mobileNumber: String? = null,
        var email: String? = null,
        var idCardSerial1: String? = null,
        var idCardSerial2: String? = null,
        var nationalId: String? = null,
        var ssn: String? = null
    ) : BaseResponseNew() {

        var username: String? = ""
        var imageUrl: String? = ""
        var isEnable: Boolean? = false
        var modeList = ArrayList<MenuModel>()

        fun createKeyValue(it: IdentityInfo): List<KeyValueModel> {

            val keyValueList = ArrayList<KeyValueModel>()
            keyValueList.add(KeyValueModel("نام", it.firstName ?: "---"))
            keyValueList.add(KeyValueModel("نام خانوادگی", it.lastName ?: "---"))
            keyValueList.add(KeyValueModel("نام پدر", it.fatherName ?: "---"))
            keyValueList.add(KeyValueModel("جنسیت", if (it.gender == "01") "مرد" else "زن"))
            keyValueList.add(KeyValueModel("کد ملی", it.nationalId ?: "---"))
            keyValueList.add(KeyValueModel("شماره تلفن همراه", it.mobileNumber ?: "---"))
            keyValueList.add(KeyValueModel("پست الکترونیک", it.email ?: "---"))
            keyValueList.add(KeyValueModel("شماره شناسنامه", it.idCardNumber ?: "0"))
            keyValueList.add(KeyValueModel("سری شناسنامه", it.idCardSerial1 ?: "---"))
            keyValueList.add(KeyValueModel("سریال شناسنامه", it.idCardSerial2 ?: "---"))
            keyValueList.add(KeyValueModel("تاریخ تولد", getPersianDate(it.dateOfBirth ?: 0)))
            keyValueList.add(
                KeyValueModel(
                    "ملیت",
                    if (it.countryId == "0001") "ایرانی" else "غیر ایرانی"
                )
            )
            keyValueList.add(KeyValueModel("شهر محل تولد", it.cityOfBirthId ?: "---"))
            keyValueList.add(KeyValueModel("شهر محل صدور", it.cityOfIssueId ?: "---"))

            return keyValueList
        }

        fun getPersianDate(timeStamp: Long): String {
            return ConvertDate.convertTimestampToPersianDate(timeStamp)
        }

    }

    constructor(parcel: Parcel) : this()

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<IdentityInfoResponse> {
        override fun createFromParcel(parcel: Parcel): IdentityInfoResponse {
            return IdentityInfoResponse(parcel)
        }

        override fun newArray(size: Int): Array<IdentityInfoResponse?> {
            return arrayOfNulls(size)
        }
    }
}

