package com.tamin.taminhamrah.data.remote.models.services.workshop

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ObjectionType(

    var investigationItems :List<ObjectionTypeNameValue>,
    var items :List<ObjectionTypeNameValue>
) : Parcelable

@Parcelize
data class ObjectionTypeNameValue(
    var name:String,
    var value:String
) : Parcelable