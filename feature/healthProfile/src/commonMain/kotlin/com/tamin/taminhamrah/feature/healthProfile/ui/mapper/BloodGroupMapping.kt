package com.tamin.taminhamrah.feature.healthProfile.ui.mapper

import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR

// com.tamin.taminhamrah.feature.healthProfile.ui.model/BloodGroupMapping.kt

private val BLOOD_GROUP_LETTER_ORDER = listOf("O", "AB", "B", "A") // matches design order

fun LookupItemPR.isUnknownBloodGroup(): Boolean =
    label.contains("نامشخص") || label.contains("نمی دانم") || label.contains("نمیدانم")

fun extractLetter(label: String): String =
    label.removeSuffix("+").removeSuffix("-")

fun extractRh(label: String): String? = when {
    label.endsWith("+") -> "+"
    label.endsWith("-") -> "-"
    else -> null
}

/** Distinct letters (A, B, AB, O) present in the backend list, in the fixed UI order. */
fun bloodGroupLetters(options: List<LookupItemPR>): List<String> {
    val available = options.filterNot { it.isUnknownBloodGroup() }
        .map { extractLetter(it.label) }
        .toSet()
    return BLOOD_GROUP_LETTER_ORDER.filter { it in available }
}

/** Combine letter + rh to resolve the real backend id, e.g. "A" + "+" -> id=1. */
fun findBloodGroupId(options: List<LookupItemPR>, letter: String?, rh: String?): Int? {
    if (letter == null || rh == null) return null
    return options.firstOrNull { it.label == "$letter$rh" }?.id
}
