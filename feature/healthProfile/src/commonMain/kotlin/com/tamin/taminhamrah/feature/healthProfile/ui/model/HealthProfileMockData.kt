package com.tamin.taminhamrah.feature.healthProfile.ui.model

data class PatientGeneralMock(
    val patientID: Long,
    val patientName: String,
    val patientFamily: String,
    val patientFather: String,
    val patientAge: String,
    val patientBirthDate: String,
    val patientGender: String,
    val patientNatCode: String,
    val patientMobile: String,
    val patientBloodGroup: String,
    val patientHeight: Double,
    val patientWeight: Double,
    val patientBMI: Double,
    val patientAddress: String,
    val emergencyName: String,
    val emergencyFamily: String,
    val emergencyMobile: String,
    val emergencyRelation: String
)

data class PatientSelfDeclarativeMock(
    val smokingStatusTitle: String,
    val smokingDesc: String,
    val alcoholUsageTitle: String,
    val alcoholDesc: String,
    val exerciseFreqTitle: String,
    val exerciseDesc: String,
    val substanceUsageTitle: String
)

data class PatientDrugAllergyMock(
    val drugId: Long,
    val drugName: String,
    val allergyComments: String
)

object HealthProfileMockData {
    val generalInfo = PatientGeneralMock(
        patientID = 12345,
        patientName = "رضا",
        patientFamily = "احمدی",
        patientFather = "علی",
        patientAge = "34",
        patientBirthDate = "1371/02/15",
        patientGender = "مرد",
        patientNatCode = "0012345678",
        patientMobile = "09123456789",
        patientBloodGroup = "O+",
        patientHeight = 180.0,
        patientWeight = 78.0,
        patientBMI = 24.1,
        patientAddress = "تهران، خیابان ولیعصر، نرسیده به میدان ونک، کوچه شقایق، پلاک ۱۰",
        emergencyName = "مریم",
        emergencyFamily = "احمدی",
        emergencyMobile = "09129876543",
        emergencyRelation = "خواهر"
    )

    val lifestyleInfo = PatientSelfDeclarativeMock(
        smokingStatusTitle = "خیر، سیگار نمی‌کشد",
        smokingDesc = "هرگز دخانیات مصرف نکرده است.",
        alcoholUsageTitle = "خیر، مصرف نمی‌کند",
        alcoholDesc = "بدون مصرف الکل.",
        exerciseFreqTitle = "۳ تا ۴ بار در هفته",
        exerciseDesc = "دویدن نرم صبحگاهی و تمرینات کششی سبک.",
        substanceUsageTitle = "خیر، مصرف نمی‌کند"
    )

    val drugAllergies = listOf(
        PatientDrugAllergyMock(
            drugId = 1,
            drugName = "پنی‌سیلین",
            allergyComments = "ایجاد راش‌های پوستی و تنگی نفس پس از تزریق."
        ),
        PatientDrugAllergyMock(
            drugId = 2,
            drugName = "آسپیرین",
            allergyComments = "ایجاد مشکلات گوارشی خفیف."
        )
    )
}
