package com.tamin.taminhamrah.data.remote.models.user

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.MyRequestModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class RequestErrorResponse : ListDataModel<RequestError>()
@Parcelize
data class RequestError(

    var createdBy:@RawValue Any? = null,
    var creationTime: Long? = null,
    var lastModifiedBy:@RawValue Any? = null,
    var lastModificationTime:@RawValue Any? = null,
    var id: Long? = null,
    var request: @RawValue MyRequestModel? = null,
    var errorMassage: String? = null,
    var errorType:@RawValue Any? = null,
    var errorStatus:@RawValue Any? = null

) : Parcelable
