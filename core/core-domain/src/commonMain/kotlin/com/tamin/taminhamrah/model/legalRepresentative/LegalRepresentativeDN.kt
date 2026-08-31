package com.tamin.taminhamrah.model.legalRepresentative

/** One workshop (کارگاه حقوقی) the current user has legal-representative rights on. */
data class LegalRepresentativeWorkshopDN(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String? = null,
    val branchName: String? = null,
    val nationalId: String? = null,
    val special: Boolean = false,
    val representativeCount: Int? = null,
)

data class LegalRepresentativeWorkshopListDN(
    val list: List<LegalRepresentativeWorkshopDN>,
    val total: Int,
)

/**
 * One representative (نماینده) already registered for a workshop.
 *
 * [accessCode] is the backend's 8-character bitmask: position 0 = electronic notification,
 * position 1 = internet list, position 2 = insured registration, positions 3-7 unused ("00000").
 * Read each position independently rather than matching the whole string — the legacy Android
 * app only ever detected a single active flag because it compared the whole string, so an agent
 * with two or three simultaneous flags silently showed as having none of them.
 */
data class LegalRepresentativeDN(
    val stakeId: Long,
    val nationalId: String,
    val accessCode: String,
    val mobile: String? = null,
    val fullName: String? = null,
    val startDate: Long? = null,
    val workshopId: String,
    val workshopName: String? = null,
    val branchCode: String,
    val special: Boolean = false,
) {
    val hasElectronicNotification: Boolean get() = accessCode.getOrNull(0) == '1'
    val hasInternetList: Boolean get() = accessCode.getOrNull(1) == '1'
    val hasInsuredRegistration: Boolean get() = accessCode.getOrNull(2) == '1'
}

data class LegalRepresentativeListDN(
    val list: List<LegalRepresentativeDN>,
    val total: Int,
)

/** Body of a submit (add or edit) request. */
data class LegalRepresentativeRequestDN(
    val nationalCode: String,
    val workshopId: String,
    val branchCode: String,
    val special: Boolean,
    val hasElectronicNotification: Boolean,
    val hasInternetList: Boolean,
    val hasInsuredRegistration: Boolean,
    val contractRows: List<String> = emptyList(),
)

/** One contract (پیمان) belonging to a "special" (پیمانکاری) workshop. */
data class LegalRepresentativeContractDN(
    val contractRow: String,
    val title: String? = null,
)

data class LegalRepresentativeContractListDN(
    val list: List<LegalRepresentativeContractDN>,
    val total: Int,
)
