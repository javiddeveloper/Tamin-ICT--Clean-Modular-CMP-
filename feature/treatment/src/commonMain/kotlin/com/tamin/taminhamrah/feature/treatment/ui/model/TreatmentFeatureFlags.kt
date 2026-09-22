package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * Which server flag switches each part of the treatment hub.
 *
 * «سوابق پزشکی» and «نسخه‌ها» follow the electronic-prescription service; everything that reads the
 * insured person's treatment entitlement — the insurance card, «هزینه‌ها» and «تاییدیه‌ها» — follows
 * «استحقاق درمان». The health profile and the contracted-centers page have no flag and stay open.
 */
object TreatmentFeatureFlags {
    val records = FeatureFlag.PRESCRIPTION
    val prescriptions = FeatureFlag.PRESCRIPTION
    val insuranceCard = FeatureFlag.DESERVED_TREATMENT_101
    val miscClaims = FeatureFlag.DESERVED_TREATMENT_101
    val approvals = FeatureFlag.DESERVED_TREATMENT_101

    /** Every flag the hub reads, for a single lookup of the menu. */
    val all: Set<FeatureFlag> = setOf(records, prescriptions, insuranceCard, miscClaims, approvals)
}
