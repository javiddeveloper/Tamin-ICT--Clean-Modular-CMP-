package com.tamin.taminhamrah.ui.home.services.contracts.model

enum class EnumEditOptionalContract constructor(var title: String, var step: Int) {
    STEP_CONTRACT_INFO("اطلاعات قرارداد", 1),
    STEP_USER_INFO("اطلاعات کاربر", 2),
    STEP_GUARDIANSHIP("متقاضی قرارداد", 3),
    STEP_SALARY("محاسبه حق بیمه", 4),
    STEP_EDIT_CONTRACT("ویرایش قرارداد", 5)
}