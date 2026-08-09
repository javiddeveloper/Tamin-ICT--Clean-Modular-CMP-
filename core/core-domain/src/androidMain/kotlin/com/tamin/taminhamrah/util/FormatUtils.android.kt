package com.tamin.taminhamrah.util

actual fun Double.formatDecimal(decimals: Int): String {
    return "%.${decimals}f".format(this)
}
