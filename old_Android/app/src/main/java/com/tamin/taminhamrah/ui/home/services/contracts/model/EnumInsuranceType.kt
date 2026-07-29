package com.tamin.taminhamrah.ui.home.services.contracts.model

enum class EnumInsuranceType constructor(
    var insuranceName: String,
    var insuranceType: String,
    var systemType: String
) {
    TYPE_STUDENT("دانشجویی", "01", "03"),
    TYPE_FREELANCE("حرف و مشاغل آزاد", "01", "03"),
    TYPE_OPTIONAL("اختیاری", "02", "01"),
    TYPE_WOMAN("زنان خانه دار", "01", "03"),
    TYPE_FRACTION("تکمیل سوابق کسری از ماه", "38", "04"),
    TYPE_DEBT("بدهی کارفرما", "0", "11"),
}