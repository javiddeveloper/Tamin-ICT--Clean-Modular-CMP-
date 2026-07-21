package com.tamin.taminhamrah.ui.treatment.electronicPrescription.model

import com.tamin.taminhamrah.R

enum class PrescriptionItemsEnumClass(val idBgColor:Int, val idTextColor:Int, val title:Int) {
        ORANGE(R.color.light_orange_square_button,R.color.text_color_orange_filter,R.string.patient_share),
        PRIMARY(R.color.light_primary_square_button,R.color.colorPrimary,R.string.organization_share),
        GREEN(R.color.light_green_square_button,R.color.tint_color_green_icon,R.string.total)
}