package com.tamin.taminhamrah.model.studentContract

enum class InsuranceContractKind(
    val premiumTypeCode: String,
    val screenTitle: String,
    val insuranceTypeLabel: String,
    val agreementContractLabel: String,
    val requiresFreeJob: Boolean,
    val usesFreelancePremiumRange: Boolean,
    val hasUploadImageStep: Boolean,
    val hasTreatmentSupportStep: Boolean,
) {
    STUDENT(
        premiumTypeCode = "01",
        screenTitle = "انعقاد قرارداد بیمه دانشجویی",
        insuranceTypeLabel = "بیمه دانشجویی",
        agreementContractLabel = "بیمه دانشجویی",
        requiresFreeJob = false,
        usesFreelancePremiumRange = true,
        hasUploadImageStep = true,
        hasTreatmentSupportStep = true,
    ),
    HOUSEWIFE(
        premiumTypeCode = "01",
        screenTitle = "انعقاد قرارداد بیمه زنان خانه‌دار",
        insuranceTypeLabel = "بیمه زنان خانه‌دار",
        agreementContractLabel = "بیمه زنان خانه‌دار",
        requiresFreeJob = false,
        usesFreelancePremiumRange = true,
        hasUploadImageStep = true,
        hasTreatmentSupportStep = true,
    ),
    FREELANCE(
        premiumTypeCode = "01",
        screenTitle = "انعقاد قرارداد بیمه صاحبان حرف و مشاغل آزاد",
        insuranceTypeLabel = "بیمه صاحبان حرف و مشاغل آزاد",
        agreementContractLabel = "بیمه صاحبان حرف و مشاغل آزاد",
        requiresFreeJob = true,
        usesFreelancePremiumRange = true,
        hasUploadImageStep = true,
        hasTreatmentSupportStep = true,
    ),
    OPTIONAL(
        premiumTypeCode = "02",
        screenTitle = "انعقاد قرارداد بیمه اختیاری",
        insuranceTypeLabel = "بیمه اختیاری",
        agreementContractLabel = "بیمه اختیاری",
        requiresFreeJob = false,
        usesFreelancePremiumRange = false,
        hasUploadImageStep = false,
        hasTreatmentSupportStep = false,
    ),
    ;

    val steps: List<StudentInsuranceContractStep>
        get() = StudentInsuranceContractStep.entries.filter { step ->
            when (step) {
                StudentInsuranceContractStep.STEP_UPLOAD_IMAGE -> hasUploadImageStep
                StudentInsuranceContractStep.STEP_TREATMENT_SUPPORT -> hasTreatmentSupportStep
                else -> true
            }
        }

    fun nextStep(current: StudentInsuranceContractStep): StudentInsuranceContractStep? {
        val currentIndex = steps.indexOf(current)
        if (currentIndex == -1) return steps.firstOrNull()
        return steps.getOrNull(currentIndex + 1)
    }

    fun previousStep(current: StudentInsuranceContractStep): StudentInsuranceContractStep? {
        val currentIndex = steps.indexOf(current)
        if (currentIndex <= 0) return null
        return steps[currentIndex - 1]
    }

    fun displayNumber(step: StudentInsuranceContractStep): Int {
        val index = steps.indexOf(step)
        return if (index == -1) 0 else index + 1
    }

    fun isFirstStep(step: StudentInsuranceContractStep): Boolean = steps.firstOrNull() == step

    fun isLastStep(step: StudentInsuranceContractStep): Boolean = steps.lastOrNull() == step
}
