package com.tamin.taminhamrah.data.entity

import android.os.Parcel
import android.os.Parcelable

data class UserInfo(
    var serialnumber: String? = null,
    var militaryServiceCode: String? = null,
    var fatherName: String? = null,
    var fullName: String? = null,
    var creationTime: Long? = null,//timeStamp
    var cityCode: String? = null,
    var socialSecurityNumber: String? = null,
    var issueplaceName: String? = null,
    var birthDate: String? = null,
    var insuranceNumber: String? = null,
    var genderCode: String? = null,
    var nationalID: String? = null,
    var identityNumber: String? = null,
    var countryCode: String? = null,
    var id: String? = null,
    var issueplace: String? = null,
    var nationCode: String? = null

) : Parcelable {
    var username: String? = ""
    var imageUrl: String? = ""
    var isEnable: Boolean? = false
    var modeList = ArrayList<MenuModel>()

    fun createKeyValue(list: List<UserInfo>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            keyValueList.add(KeyValueModel("نام و نام خانوادگی", it.fullName ?: "-"))
            keyValueList.add(KeyValueModel("نام پدر", it.fatherName ?: "-"))
            keyValueList.add(KeyValueModel("جنسیت", if (it.genderCode == "01") "مرد" else "زن"))
            keyValueList.add(KeyValueModel("کد ملی", it.nationalID ?: "-"))
            keyValueList.add(KeyValueModel("شماره شناسنامه", it.identityNumber ?:"0"))
            keyValueList.add(KeyValueModel("سری و سریال شناسنامه", it.serialnumber ?: "-"))
            keyValueList.add(KeyValueModel("تاریخ تولد", it.birthDate ?: "-"))
            keyValueList.add(
                KeyValueModel(
                    "ملیت",
                    if (it.nationCode == "01") "ایرانی" else "غیر ایرانی"
                )
            )
            keyValueList.add(KeyValueModel("کشور محل تولد", it.countryCode ?: "-"))
            keyValueList.add(KeyValueModel("شهر محل تولد", it.issueplaceName ?: "-"))
            keyValueList.add(KeyValueModel("شهر محل صدور", it.issueplaceName ?: "-"))

        }

        return keyValueList
    }

    fun createKeyValue(it: UserInfo): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نام و نام خانوادگی", it.fullName ?: "-"))
        keyValueList.add(KeyValueModel("نام پدر", it.fatherName ?: "-"))
        keyValueList.add(KeyValueModel("جنسیت", if (it.genderCode == "01") "مرد" else "زن"))
        keyValueList.add(KeyValueModel("کد ملی", it.nationalID ?: "-"))
        keyValueList.add(KeyValueModel("شماره شناسنامه", it.identityNumber ?: "0"))
        keyValueList.add(KeyValueModel("سری و سریال شناسنامه", it.serialnumber ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ تولد", it.birthDate ?: "-"))
        keyValueList.add(KeyValueModel("ملیت", if (it.nationCode == "01") "ایرانی" else "غیر ایرانی"))
        keyValueList.add(KeyValueModel("کشور محل تولد", if (it.countryCode == "0001") "ایران" else ""))
        keyValueList.add(KeyValueModel("شهر محل تولد", it.cityCode ?: "-"))
        keyValueList.add(KeyValueModel("شهر محل صدور", it.issueplaceName ?: "-"))
        return keyValueList
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        /*TODO("issueplace"),
        TODO("nationCode")*/
    ) {
        username = parcel.readString()
        imageUrl = parcel.readString()
        isEnable = parcel.readValue(Boolean::class.java.classLoader) as? Boolean

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(serialnumber)
        parcel.writeString(militaryServiceCode)
        parcel.writeString(fatherName)
        parcel.writeString(fullName)
        parcel.writeValue(creationTime)
        parcel.writeString(cityCode)
        parcel.writeString(socialSecurityNumber)
        parcel.writeString(issueplaceName)
        parcel.writeString(birthDate)
        parcel.writeString(insuranceNumber)
        parcel.writeString(genderCode)
        parcel.writeString(nationalID)
        parcel.writeString(identityNumber)
        parcel.writeString(countryCode)
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(imageUrl)
        parcel.writeValue(isEnable)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }
}

