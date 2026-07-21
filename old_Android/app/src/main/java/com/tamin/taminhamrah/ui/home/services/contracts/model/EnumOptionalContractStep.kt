package com.tamin.taminhamrah.ui.home.services.contracts.model

enum class EnumOptionalContractStep constructor(var title: String, var stepIndex: Int) {
    STEP_REGISTRATION("نام نویسی", 1),
    STEP_AUTHORIZATION("احراز شرایط انعقاد قرارداد", 2),
    STEP_CONTRACT_TERMS("مقررات و ضوابط انعقاد قرارداد", 3),
    STEP_USER_INFO("اطلاعات کاربر", 4),
    STEP_CONTRACT_APPLICANT("متقاضی قرارداد", 5),
    STEP_SELECT_BRANCH("انتخاب شعبه تأمین اجتماعی", 6),
    STEP_SALARY("محاسبه حق بیمه", 7),
    STEP_SUBMIT_CONTRACT("ثبت قرارداد", 8)

}