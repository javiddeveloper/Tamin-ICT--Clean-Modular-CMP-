package com.tamin.taminhamrah.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuModel(
    var title: String? = "",
    var id: String? = "",
    var iconPath: String? = "",
    var iconRes: Int = 0,
    var description: String? = null,
    var isSelected: Boolean = false,
    var description2: String? = "",
    var showDesc:Boolean? = false,
    var isEdited:Boolean? = false,
    var baseModel:@RawValue Any?=null,
    var textColor:String="",
    var isNew:Boolean=false,
    @StringRes
    var titleStringResId: Int = 0,
    @StringRes
    var descStringResId: Int = 0,
    var extraData: String = "",
    var tag:Int=0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
    )
}