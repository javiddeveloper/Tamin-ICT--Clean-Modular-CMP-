package com.tamin.taminhamrah.ui.treatment.electronicPrescription.model

import com.tamin.taminhamrah.R

enum class PrescriptionType(val id:String,val title:Int,val iconRes:Int) {
    PHARMACY("0",R.string.pharmacy_prescription, R.drawable.ic_medicinal),
    MEDICAL("1",R.string.medical, R.drawable.ic_medicinal),
    PARA_CLINIC("2",R.string.para_clinic, R.drawable.ic_paraclinic),
    VISIT("3",R.string.visit, R.drawable.ic_visit),
    MEDICAL_SERVICE("5",R.string.medical_service, R.drawable.ic_services)
}