package com.tamin.taminhamrah.data.remote.models.services

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AiChartItem(
    val label: String,
    val value: Float,
    val description: String? = null
) : Parcelable