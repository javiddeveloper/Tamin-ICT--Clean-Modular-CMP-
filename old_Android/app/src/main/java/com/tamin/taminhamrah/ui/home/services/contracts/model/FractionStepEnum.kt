package com.tamin.taminhamrah.ui.home.services.contracts.model

enum class FractionStepEnum(var title: String, var stepIndex: Int) {
    STEP_INSURANCE_INFO("اطلاعات بیمه‌ای کاربر", 1),
    STEP_AUTHORIZATION("احراز شرایط انعقاد قرارداد", 2),
    STEP_CONTRACT_TERMS("مقررات و ضوابط انعقاد قرارداد", 3),
    STEP_USER_INFO("اطلاعات کاربر", 4),
    STEP_SUBMIT_CONTRACT("ثبت قرارداد", 5)
}