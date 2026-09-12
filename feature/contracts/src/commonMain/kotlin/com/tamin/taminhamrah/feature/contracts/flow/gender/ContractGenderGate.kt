package com.tamin.taminhamrah.feature.contracts.flow.gender

/**
 * Housewife (and any future female-only) contract types block male registrants.
 * Mirrors legacy `ContractBaseFragment` TYPE_WOMAN + isMan() check.
 */
fun isFemaleOnlyServiceBlocked(
    requiresFemaleGender: Boolean,
    isFemale: Boolean,
): Boolean = requiresFemaleGender && !isFemale
