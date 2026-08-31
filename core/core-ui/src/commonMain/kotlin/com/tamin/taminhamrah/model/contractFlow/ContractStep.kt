package com.tamin.taminhamrah.model.contractFlow

enum class ContractStep(val title: String) {
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
    ;

    companion object {
        val SPECIAL_INSURED_STEPS: List<ContractStep> = entries

        val OPTIONAL_STEPS: List<ContractStep> = listOf(
            STEP_REGISTRATION,
            STEP_AUTHORIZATION,
            STEP_CONTRACT_TERMS,
            STEP_USER_INFO,
            STEP_CONTRACT_APPLICANT,
            STEP_SELECT_BRANCH,
            STEP_SALARY,
            STEP_SUBMIT_CONTRACT,
        )

        fun stepsFor(
            includeUploadImage: Boolean,
            includeTreatmentSupport: Boolean,
            includePremiumRate: Boolean,
        ): List<ContractStep> = entries.filter { step ->
            when (step) {
                STEP_UPLOAD_IMAGE -> includeUploadImage
                STEP_TREATMENT_SUPPORT -> includeTreatmentSupport
                STEP_INSURANCE_PREMIUM -> includePremiumRate
                else -> true
            }
        }
    }
}

fun List<ContractStep>.nextStep(current: ContractStep): ContractStep? {
    val currentIndex = indexOf(current)
    if (currentIndex == -1) return firstOrNull()
    return getOrNull(currentIndex + 1)
}

fun List<ContractStep>.previousStep(current: ContractStep): ContractStep? {
    val currentIndex = indexOf(current)
    if (currentIndex <= 0) return null
    return getOrNull(currentIndex - 1)
}

fun List<ContractStep>.displayNumber(step: ContractStep): Int {
    val index = indexOf(step)
    return if (index == -1) 0 else index + 1
}

fun List<ContractStep>.isFirstStep(step: ContractStep): Boolean = firstOrNull() == step

fun List<ContractStep>.isLastStep(step: ContractStep): Boolean = lastOrNull() == step
