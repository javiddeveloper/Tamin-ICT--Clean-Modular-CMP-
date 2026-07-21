package com.tamin.taminhamrah.utils.extentions

import java.util.UUID

fun String?.formattedDate() = if (this != null && this.length > 7) "${this.substring(0, 4)}/${
    this.substring(
        4,
        6
    )
}/${this.substring(6, 8)}" else "-"

fun String?.isNumericString(): Boolean {
    val numericRegex = Regex("[0-9]+")
    return this?.matches(numericRegex) ?: false
}

fun randomUUID(): String {
    return UUID.randomUUID().toString()
}

fun String.ifCreateValueIsBlank(): String {
    return this.ifBlank {
        "---"
    }
}
