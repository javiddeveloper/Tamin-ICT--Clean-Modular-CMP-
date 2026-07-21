package com.tamin.taminhamrah.data.remote.models.user

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.RequestType
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class SmartGuideResponse : ListDataModel<SmartGuideItem>()

@Parcelize
data class SmartGuideItem(
    var isPublic: Boolean? = null,
    var question: String? = null,
    var reply: String? = null,
    var requestCode: String? = null,
    var requestDesc: String? = null,
    var createdBy: @RawValue Any? = null,
    var creationTime: @RawValue Any? = null,
    var description: String? = null,
    var id: Long? = null,
    var lastModificationTime: @RawValue Any? = null,
    var lastModifiedBy: @RawValue Any? = null,
    var title: String? = null,
    var requestStatus: RequestStatus? = null,
    var requestType:@RawValue RequestType? = null

) : Parcelable

@Parcelize
data class RequestStatus(
    var requestCode: String? = null,
    var requestDesc: String? = null
) : Parcelable

@Parcelize
data class RequestType(
    var createdBy: @RawValue Any? = null,
    var creationTime: @RawValue Any? = null,
    var lastModifiedBy: @RawValue Any? = null,
    var lastModificationTime: @RawValue Any? = null,
    var id: Long? = null,
    var description: String? = null,
    var title: String? = null ) : Parcelable


