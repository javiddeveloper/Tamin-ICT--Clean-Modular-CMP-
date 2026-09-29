package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * Which flag switches each part of the treatment hub.
 *
 * «سوابق پزشکی» and «نسخه‌ها» follow the electronic-prescription service; everything that reads the
 * insured person's treatment entitlement — the insurance card, «هزینه‌ها» and «تاییدیه‌ها» — follows
 * «استحقاق درمان». [healthProfile], [contractedCenters] and [currentYearCosts] have no server menu
 * id at all, so each is its own client-only flag (see `FeatureFlag`'s "Client-only" block).
 */
object TreatmentFeatureFlags {
    val records = FeatureFlag.PRESCRIPTION
    val prescriptions = FeatureFlag.PRESCRIPTION
    val insuranceCard = FeatureFlag.DESERVED_TREATMENT_PENSIONER
    val miscClaims = FeatureFlag.DESERVED_TREATMENT_PENSIONER
    val approvals = FeatureFlag.DESERVED_TREATMENT_PENSIONER
    val healthProfile = FeatureFlag.HEALTH_PROFILE
    val contractedCenters = FeatureFlag.CONTRACTED_CENTERS
    val currentYearCosts = FeatureFlag.CURRENT_YEAR_TREATMENT_COSTS

    /** Every flag the hub reads, for a single lookup of the menu. */
    val all: Set<FeatureFlag> = setOf(
        records, prescriptions, insuranceCard, miscClaims, approvals,
        healthProfile, contractedCenters, currentYearCosts,
    )
}
