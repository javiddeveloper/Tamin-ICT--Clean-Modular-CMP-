package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RelatedPersonModel(
    var id: Long? = null,
    var fullname: String? = null,
    var relation: String? = null,
    var nationalCode: String? = null

):Parcelable