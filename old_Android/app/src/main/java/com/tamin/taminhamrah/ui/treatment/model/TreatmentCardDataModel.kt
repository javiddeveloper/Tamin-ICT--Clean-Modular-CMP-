package com.tamin.taminhamrah.ui.treatment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TreatmentCardDataModel(
    var name: String? = "_",
    var nationalCode: String? = "_",
    var insuranceNumber: String? = "_",
    var isTreatmentSupport: Boolean? = false,
    var isMainUser: Boolean = true,
    var qrCodeFilePath: String? = null,
    var isChecked: Boolean = false,
    var treatmentSupportDescription: String? = null,
    val message: String? = null,
):Parcelable
