package com.tamin.taminhamrah.ui.home.services.retirementRequest.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RetirementRequestStateModel (
    var titleStringResId: Int,
    var state : EnumRequestState?=EnumRequestState.IS_NOT_PASSED
):Parcelable