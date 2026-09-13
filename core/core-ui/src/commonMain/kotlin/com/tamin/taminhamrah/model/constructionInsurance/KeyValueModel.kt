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
    val textColor: EnumTextColor = EnumTextColor.DEFAULT
)
