package com.tamin.taminhamrah.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual fun Double.formatDecimal(decimals: Int): String {
    val formatter = NSNumberFormatter().apply {
        maximumFractionDigits = decimals.toULong()
        minimumFractionDigits = decimals.toULong()
    }
    return formatter.stringFromNumber(NSNumber(this)) ?: this.toString()
}
