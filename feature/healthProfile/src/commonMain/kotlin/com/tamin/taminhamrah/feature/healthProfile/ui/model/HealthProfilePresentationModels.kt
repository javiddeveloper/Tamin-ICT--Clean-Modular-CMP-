package com.tamin.taminhamrah.feature.healthProfile.ui.model

// ─────────────────────────────────────────────────────────────────────────────
// Generic lookup item used for ALL drop-down / chip lists coming from the API
// (blood groups, marital status, smoking status, provinces, cities, drugs…)
// ─────────────────────────────────────────────────────────────────────────────
data class LookupItemPR(
    val id: Int,
    val label: String
)

// ─────────────────────────────────────────────────────────────────────────────
// Patient — general / identity info (read-only, loaded once on init)
// ─────────────────────────────────────────────────────────────────────────────
data class PatientGeneralPR(
    val patientId: Int,
    val patientNatCode: String,
    val patientName: String,
    val patientFamily: String,
    val patientFather: String,
    val patientAge: String,
    val patientGender: String,
    val patientBirthDate: String,
    val patientMobile: String,
    val patientAddress: String,
    // Blood group is stored as a code + display string from UpdatePatientDN
    val patientBloodGroupCode: Int?,
    val patientBloodGroup: String,
    // Physical
    val patientHeight: Double,
    val patientWeight: Double,
    val patientBMI: Double,
    // Emergency contact (pre-filled from UpdatePatientDN / PatientGeneralDN)
    val emergencyName: String,
    val emergencyFamily: String,
    val emergencyMobile: String,
    val emergencyRelation: String,
    val emergencyRelationshipCode: Int?,
    // Insurance (display only)
    val insuranceNumber: String,
    val insuranceType: String,
    val lastVisitDate: String
)

// ─────────────────────────────────────────────────────────────────────────────
// Patient lifestyle / self-declarative (loaded on init to pre-fill wizard)
// ─────────────────────────────────────────────────────────────────────────────
data class PatientSelfDeclarativePR(
    val objectId: Int?,
    val smokingStatus: Int?,
    val smokingStatusLabel: String,
    val smokingDesc: String,
    val alcoholUsage: Int?,
    val alcoholUsageLabel: String,
    val alcoholDesc: String,
    val substanceUsage: Int?,
    val substanceUsageLabel: String,
    val substanceDesc: String,
    val exerciseFreq: Int?,
    val exerciseFreqLabel: String,
    val exerciseDesc: String
)

// ─────────────────────────────────────────────────────────────────────────────
// Drug allergy item (displayed in AllergyScreen list + added via dialog)
// ─────────────────────────────────────────────────────────────────────────────
data class DrugAllergyItemPR(
    val drugId: Int,
    val drugName: String,
    val allergyComments: String
)

// ─────────────────────────────────────────────────────────────────────────────
// Illness group (used for DiseasesScreen chip pickers)
// groupId matches the API group, illnesses are the selectable chips inside it
// ─────────────────────────────────────────────────────────────────────────────
data class IllnessGroupPR(
    val groupId: Int,
    val groupTitle: String,
    val forFamily: Boolean,         // true → shown in FamilyScreen, false → DiseasesScreen
    val illnesses: List<LookupItemPR>
)

// ─────────────────────────────────────────────────────────────────────────────
// A business-level problem surfaced by a mutation call (updatePatient,
// addSelfDeclarative, syncIllnessSelfDeclaratives, ...), mapped from
// HealthProblemDN. Carried in SelfDeclarationUiState.submitProblems so the
// wizard can show the backend's own (already localized) validation message.
// ─────────────────────────────────────────────────────────────────────────────
data class HealthProblemPR(
    val code: Int?,
    val message: String
)
