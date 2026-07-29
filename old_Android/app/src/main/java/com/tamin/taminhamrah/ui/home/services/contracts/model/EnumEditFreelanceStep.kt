package com.tamin.taminhamrah.ui.home.services.contracts.model

enum class EnumEditFreelanceStep constructor(var title: String, var step: Int) {
    STEP_CONTRACT_INFO("اطلاعات قرارداد", 1),
    STEP_USER_INFO("اطلاعات کاربر", 2),
    STEP_UPLOAD_IMAGE("بارگذاری مدرک", 3),
    STEP_GUARDIANSHIP("متقاضی قرارداد", 4),
    STEP_TREATMENT_SUPPORT("حمایت درمانی", 5),
    STEP_INSURANCE_PREMIUM("تعیین حق بیمه ماهانه", 6),
    STEP_SALARY("محاسبه حق بیمه", 7),
    STEP_EDIT_CONTRACT("ویرایش قرارداد", 8)
}