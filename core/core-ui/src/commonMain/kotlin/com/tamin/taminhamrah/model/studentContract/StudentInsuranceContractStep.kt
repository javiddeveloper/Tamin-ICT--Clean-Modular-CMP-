package com.tamin.taminhamrah.model.studentContract

enum class StudentInsuranceContractStep(val title: String) {
    STEP_REGISTRATION("نام نویسی"),
    STEP_AUTHORIZATION("احراز شرایط انعقاد قرارداد"),
    STEP_CONTRACT_TERMS("مقررات و ضوابط انعقاد قرارداد"),
    STEP_USER_INFO("اطلاعات کاربر"),
    STEP_CONTRACT_APPLICANT("متقاضی قرارداد"),
    STEP_SELECT_BRANCH("انتخاب شعبه تأمین اجتماعی"),
    STEP_UPLOAD_IMAGE("بارگذاری مدارک"),
    STEP_TREATMENT_SUPPORT("حمایت درمانی"),
    STEP_INSURANCE_PREMIUM("تعیین حق بیمه ماهانه"),
    STEP_SALARY("محاسبه حق بیمه"),
    STEP_SUBMIT_CONTRACT("ثبت قرارداد"),
}
