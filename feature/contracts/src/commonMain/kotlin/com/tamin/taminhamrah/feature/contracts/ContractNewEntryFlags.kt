package com.tamin.taminhamrah.feature.contracts

import com.tamin.taminhamrah.model.common.FeatureFlag

object ContractNewEntryFlags {
    val flags: Set<FeatureFlag> = setOf(
        FeatureFlag.FREELANCE_INSURANCE,
        FeatureFlag.STUDENT_INSURANCE,
        FeatureFlag.HOUSEWIFE_INSURANCE,
        FeatureFlag.OPTIONAL_INSURANCE,
    )
}
