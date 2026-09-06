package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model

/**
 * The کارفرما identity block shown under the header on the Employer Online Services landing screen
 * ([com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.EmployerOnlineServicesScreen]).
 *
 * Both fields are already display-ready — the full name is joined, the national code carries Persian
 * digits, and anything the profile service omitted is the design's dash — so the card never formats
 * per recomposition.
 */
data class IdentityCardPR(
    val fullName: String = "",
    val nationalCode: String = "",
)
