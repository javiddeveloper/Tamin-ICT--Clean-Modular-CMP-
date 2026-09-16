package com.tamin.taminhamrah.model.constructionInsurance

import org.jetbrains.compose.resources.StringResource

enum class EnumTextColor {
    DEFAULT,
    AMBER,
    GREEN,
    RED,
    BLUE
}

data class KeyValueModel(
    val keyResId: StringResource? = null,
    val keyString: String? = null,
    val value: String,
    val valueResId: StringResource? = null,
    val textColor: EnumTextColor = EnumTextColor.DEFAULT,
    /** A unit (e.g. "ریال") drawn beside a numeric [value] — see `DetailRow`'s own `unit` param. */
    val unit: String? = null,
    /** Routes [value] through `NumericText` so codes/amounts stay LTR; false for free-text/prose values. */
    val numeric: Boolean = true,
)
