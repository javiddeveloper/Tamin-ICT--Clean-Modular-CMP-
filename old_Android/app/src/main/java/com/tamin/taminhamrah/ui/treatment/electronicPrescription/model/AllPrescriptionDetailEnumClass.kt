package com.tamin.taminhamrah.ui.treatment.electronicPrescription.model

import com.tamin.taminhamrah.R

enum class AllPrescriptionDetailEnumClass(val idBgColor:Int, val idTextColor:Int, val title:Int) {
        ORANGE(R.color.light_orange_square_button,R.color.text_color_orange_filter,R.string.prescription_cast),
        PRIMARY(R.color.light_primary_square_button,R.color.colorPrimary,R.string.share_organization),
        GREEN(R.color.light_green_square_button,R.color.tint_color_green_icon,R.string.insured_share)
}