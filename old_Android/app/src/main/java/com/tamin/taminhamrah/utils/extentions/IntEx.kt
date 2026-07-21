package com.tamin.taminhamrah.utils.extentions

import android.content.Context

fun Int.dpToPx(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Double.toPercentageString(): String {
    return "${(this * 100).toInt()}%"
}