package com.tamin.taminhamrah.ui.treatment.model

import com.tamin.taminhamrah.R

enum class CardBackgroundEnumClass(val backgroundResId:Int,val drawableResId:Int?=null,val statusTreatmentDescResId:Int?=null) {
    GREEN(R.drawable.bg_green_treatment,R.drawable.ic_dont_read,R.string.you_have),
    RED(R.drawable.bg_red_treatment, R.drawable.ic_close_red,R.string.you_dont_have),
    GRAY(R.drawable.bg_gray_treatment)
}