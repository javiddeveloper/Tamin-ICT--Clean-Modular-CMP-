package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UpdateAddressInfoRequest(
    val address: String? = null,
    val cityId: String? = null,
    val mobile: String? = null,
    val personal: Personal? = null,
    val phoneNumber: String? = null,
    val zipCode: String? = null
):Parcelable

@Parcelize
data class Personal(
    val ssn: String? = null
):Parcelable