package com.tamin.taminhamrah.model.legalRepresentative

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

data class LegalRepresentativeContractDN(
    val contractRow: String,
    val title: String? = null,
    val nationalCode: String? = null,
)

data class LegalRepresentativeContractListDN(
    val list: List<LegalRepresentativeContractDN>,
    val total: Int,
)
