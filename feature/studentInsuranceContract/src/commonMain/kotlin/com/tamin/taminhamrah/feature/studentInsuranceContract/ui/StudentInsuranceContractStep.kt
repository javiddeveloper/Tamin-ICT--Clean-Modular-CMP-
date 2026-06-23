package com.tamin.taminhamrah.feature.studentInsuranceContract.ui

enum class StudentInsuranceContractStep(
    val title: String,
    val stepIndex: Int,
) {
    STEP_REGISTRATION("نام نویسی", 1),
    STEP_AUTHORIZATION("احراز شرایط انعقاد قرارداد", 2),
    STEP_CONTRACT_TERMS("مقررات و ضوابط انعقاد قرارداد", 3),
    STEP_USER_INFO("اطلاعات کاربر", 4),
    STEP_CONTRACT_APPLICANT("متقاضی قرارداد", 5),
    STEP_SELECT_BRANCH("انتخاب شعبه تأمین اجتماعی", 6),
    STEP_UPLOAD_IMAGE("بارگذاری مدارک", 7),
    STEP_TREATMENT_SUPPORT("حمایت درمانی", 8),
    STEP_INSURANCE_PREMIUM("تعیین حق بیمه ماهانه", 9),
    STEP_SALARY("محاسبه حق بیمه", 10),
    STEP_SUBMIT_CONTRACT("ثبت قرارداد", 11)
    ;

    companion object {
        val orderedSteps: List<StudentInsuranceContractStep> = entries
    }

}
