package com.tamin.taminhamrah.data.remote.models.user

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class EligibilityStatusResponse(
    var nationalId: String? = "",
    @SerializedName(value = "referenceCode", alternate = ["refrenceCode"])
    var referenceCode: String? = "",
    @SerializedName(value = "result")
    var reault: Boolean? = null,
    var expireDate: Any? = null,
    var credit: Any? = null,
    var message: Any? = null,
    var illness: Any? = null,
    var isForeigner:Boolean=false
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nationalId)
        parcel.writeString(referenceCode)
        parcel.writeValue(reault)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EligibilityStatusResponse> {
        override fun createFromParcel(parcel: Parcel): EligibilityStatusResponse {
            return EligibilityStatusResponse(parcel)
        }

        override fun newArray(size: Int): Array<EligibilityStatusResponse?> {
            return arrayOfNulls(size)
        }
    }
}
