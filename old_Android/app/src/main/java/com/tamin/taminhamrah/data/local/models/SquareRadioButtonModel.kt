package com.tamin.taminhamrah.data.local.models

data class SquareRadioButtonModel(
    val id: Int,
    val label: String,
    val backgroundColor: Int,
    val textColor: Int,
    val selectedBackground: Int,
    val iconResId: Int ,
    var isSelected: Boolean = false
)