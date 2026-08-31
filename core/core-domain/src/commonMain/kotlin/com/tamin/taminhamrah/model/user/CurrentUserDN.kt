package com.tamin.taminhamrah.model.user

/**
 * The signed-in account, in full.
 *
 * [UserProfileDN] is the same account seen through a narrower window — the seven fields the
 * profile and change-mobile screens use. It is produced from this by [toUserProfile] rather than
 * fetched on its own, so `users/current-user` is called once and there is one description of who
 * is signed in.
 */
data class CurrentUserDN(
    val entityId: String = "",
    val login: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val nationalCode: String = "",
    val mobile: String = "",
    val gender: String = "",
    /** Epoch milliseconds, as the service sends it; format at the presentation edge. */
    val birthDate: Long? = null,
    val accountStatus: String = "",
    val organization: CurrentUserOrganizationDN? = null,
    val roles: List<CurrentUserRoleDN> = emptyList(),
    val geoUnit: CurrentUserGeoUnitDN? = null,
) {
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    /**
     * Whether this account may act as a کارفرما.
     *
     * The grant is a role, not a flag: `old_android` decided the same thing by looking for an
     * employer role rather than by asking a dedicated endpoint.
     */
    val isEmployer: Boolean
        get() = roles.any { it.isEmployerRole }
}

/** The employer entity the account acts for; null when the account is not attached to one. */
data class CurrentUserOrganizationDN(
    val entityId: String = "",
    val code: String = "",
    val name: String = "",
    val status: String = "",
    val customerType: String = "",
)

data class CurrentUserRoleDN(
    val id: String = "",
    val name: String = "",
    val displayName: String = "",
    val uniqueName: String = "",
    val description: String = "",
    val category: String = "",
) {
    /**
     * Matched on the role name rather than an id, because the ids differ between environments
     * while the names do not. Case-insensitive: the service is inconsistent about it.
     */
    val isEmployerRole: Boolean
        get() = EMPLOYER_ROLE_MARKERS.any { marker ->
            name.contains(marker, ignoreCase = true) ||
                uniqueName.contains(marker, ignoreCase = true)
        }

    private companion object {
        val EMPLOYER_ROLE_MARKERS = listOf("employer", "karfarma", "کارفرما")
    }
}

data class CurrentUserGeoUnitDN(
    val id: Int? = null,
    val code: String = "",
    val title: String = "",
    val description: String = "",
)

/**
 * The narrow view the existing profile screens take of this account.
 *
 * A projection, not a second request: whatever [UserProfileDN] shows is a subset of what is
 * already here.
 */
fun CurrentUserDN.toUserProfile(): UserProfileDN = UserProfileDN(
    entityId = entityId.takeIf { it.isNotBlank() },
    login = login.takeIf { it.isNotBlank() },
    firstName = firstName.takeIf { it.isNotBlank() },
    lastName = lastName.takeIf { it.isNotBlank() },
    email = email.takeIf { it.isNotBlank() },
    nationalCode = nationalCode.takeIf { it.isNotBlank() },
    mobile = mobile.takeIf { it.isNotBlank() },
)
