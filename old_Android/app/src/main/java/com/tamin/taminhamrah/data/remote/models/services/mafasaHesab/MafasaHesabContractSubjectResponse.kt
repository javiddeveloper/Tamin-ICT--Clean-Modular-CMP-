package com.tamin.taminhamrah.data.remote.models.services.mafasaHesab

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class MafasaHesabContractSubjectResponse : ListDataModel< MafasaHesabContractSubject>()

@Parcelize
class  MafasaHesabContractSubject(
    var code: String? = null,
    var createdBy:@RawValue Any? = null,
    var creationTime:@RawValue Any? = null,
    var description: String? = null,
    var lastModificationTime:@RawValue Any? = null,
    var lastModifiedBy:@RawValue Any? = null,
    var status: String? = null):Parcelable




