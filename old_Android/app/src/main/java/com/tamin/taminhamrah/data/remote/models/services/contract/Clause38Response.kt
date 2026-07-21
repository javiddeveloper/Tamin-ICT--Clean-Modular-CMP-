package com.tamin.taminhamrah.data.remote.models.services.contract

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import kotlinx.parcelize.Parcelize

class Clause38Response : ListDataModel<Clause38Info>()

@Parcelize
data class Clause38Info(
    val branchCode: String? = null,
    val clearanceSerial: String? = null,
    val contractAmount: Long? = null,
    val contractNumber: String? = null,
    val contractRow: String? = null,
    val workshopId: String? = null

) : Parcelable
