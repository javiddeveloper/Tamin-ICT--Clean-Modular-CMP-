package com.tamin.taminhamrah.feature.healthProfile.ui.mapper

import com.tamin.taminhamrah.feature.healthProfile.ui.model.DrugAllergyItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.IllnessGroupPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientGeneralPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientSelfDeclarativePR
import com.tamin.taminhamrah.model.health.BloodGroupDN
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.DrugItemDN
import com.tamin.taminhamrah.model.health.IllnessItemDN
import com.tamin.taminhamrah.model.health.MaritalStatusDN
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.ProvinceItemDN
import com.tamin.taminhamrah.model.health.ProvinceCityItemDN
import com.tamin.taminhamrah.model.health.SelfDeclarableIllnessGroupDN
import com.tamin.taminhamrah.model.health.SmokingStatusDN

// ─────────────────────────────────────────────────────────────────────────────
// Patient General
// ─────────────────────────────────────────────────────────────────────────────

fun PatientGeneralDN.toPresentation(): PatientGeneralPR = PatientGeneralPR(
    patientId = ptientID ?: 0,
    patientNatCode = patientNatCode ?: "",
    patientName = patientName ?: "",
    patientFamily = patientFamily ?: "",
    patientFather = patientFather ?: "",
    patientAge = patientAge ?: "",
    patientGender = patientGender ?: "",
    patientBirthDate = patientBirthDate ?: "",
    patientMobile = patientMobile ?: "",
    patientAddress = patientAddress ?: "",
    // BloodGroup, height, weight, emergency are not in PatientGeneralDN —
    // they come from UpdatePatientDN after a save. Use safe defaults.
    patientBloodGroupCode = null,
    patientBloodGroup = "",
    patientHeight = 0.0,
    patientWeight = 0.0,
    patientBMI = 0.0,
    emergencyName = "",
    emergencyFamily = "",
    emergencyMobile = "",
    emergencyRelation = "",
    emergencyRelationshipCode = null,
    insuranceNumber = "",
    insuranceType = "",
    lastVisitDate = ""
)

// ─────────────────────────────────────────────────────────────────────────────
// Patient Self-Declarative (lifestyle)
// ─────────────────────────────────────────────────────────────────────────────

fun PatientSelfDeclarativeDN.toPresentation(): PatientSelfDeclarativePR = PatientSelfDeclarativePR(
    objectId = objectID,
    smokingStatus = smokingStatus,
    smokingStatusLabel = smokingStatusTitle ?: "",
    smokingDesc = smokingDesc ?: "",
    alcoholUsage = alcoholUsage,
    alcoholUsageLabel = alcoholUsageTitle ?: "",
    alcoholDesc = alcoholDesc ?: "",
    substanceUsage = substanceUsage,
    substanceUsageLabel = substanceUsageTitle ?: "",
    substanceDesc = substanceDesc ?: "",
    exerciseFreq = exerciseFreq,
    exerciseFreqLabel = exerciseFreqTitle ?: "",
    exerciseDesc = exerciseDesc ?: ""
)

// ─────────────────────────────────────────────────────────────────────────────
// Drug Allergies
// ─────────────────────────────────────────────────────────────────────────────

fun DrugItemAllergiesDN.toPresentation(): DrugAllergyItemPR = DrugAllergyItemPR(
    drugId = drugId ?: 0,
    drugName = drugName ?: "",
    allergyComments = allergyComments ?: ""
)

// ─────────────────────────────────────────────────────────────────────────────
// Lookup lists → LookupItemPR
// ─────────────────────────────────────────────────────────────────────────────

fun BloodGroupDN.toPresentation(): LookupItemPR = LookupItemPR(
    id = key ?: 0,
    label = value ?: ""
)

fun MaritalStatusDN.toPresentation(): LookupItemPR = LookupItemPR(
    id = key ?: 0,
    label = value ?: ""
)

fun SmokingStatusDN.toPresentation(): LookupItemPR = LookupItemPR(
    id = key ?: 0,
    label = value ?: ""
)

fun ProvinceItemDN.toPresentation(): LookupItemPR = LookupItemPR(
    id = id ?: 0,
    label = name ?: ""
)

fun ProvinceCityItemDN.toPresentation(): LookupItemPR = LookupItemPR(
    id = id ?: 0,
    label = name ?: ""
)

fun DrugItemDN.toPresentation(): LookupItemPR = LookupItemPR(
    id = drugID ?: 0,
    label = drugName ?: ""
)

// ─────────────────────────────────────────────────────────────────────────────
// Illness groups (DiseasesScreen + FamilyScreen)
// ─────────────────────────────────────────────────────────────────────────────

fun IllnessItemDN.toPresentation(): LookupItemPR = LookupItemPR(
    id = illnessID ?: 0,
    label = illnessDesc ?: ""
)

fun SelfDeclarableIllnessGroupDN.toPresentation(): IllnessGroupPR = IllnessGroupPR(
    groupId = groupId ?: 0,
    groupTitle = groupTitle ?: "",
    forFamily = forFamily ?: false,
    illnesses = illnessList?.map { it.toPresentation() } ?: emptyList()
)
