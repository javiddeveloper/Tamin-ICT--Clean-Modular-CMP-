package com.tamin.taminhamrah.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The whole of what `users/current-user` answers with.
 *
 * [UserProfileDto] describes the same endpoint but names only the seven fields the profile and
 * change-mobile screens read. The کارفرما flows need more than that — which roles the account
 * holds, and which organization it belongs to — so this is the complete picture and the narrower
 * view is derived from it rather than fetched separately.
 *
 * `old_android` reached the same endpoint through two parallel models (`ProfileResponse` and
 * `CurrentUserResponse`); the fields below are `CurrentUserResponse`'s, which is the smaller and
 * more honest of the two. Everything is nullable because the service omits freely.
 */
@Serializable
data class CurrentUserDto(
    @SerialName("entityId") val entityId: String? = null,
    @SerialName("login") val login: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("birthDate") val birthDate: Long? = null,
    @SerialName("accountStatus") val accountStatus: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("organizationKey") val organizationKey: Int? = null,
    @SerialName("organization") val organization: CurrentUserOrganizationDto? = null,
    @SerialName("roles") val roles: List<CurrentUserRoleDto?>? = null,
    @SerialName("userDetail") val userDetail: CurrentUserDetailDto? = null,
)

/** The employer entity the account acts for. */
@Serializable
data class CurrentUserOrganizationDto(
    @SerialName("entityId") val entityId: String? = null,
    @SerialName("code") val code: String? = null,
    @SerialName("organizationName") val organizationName: String? = null,
    @SerialName("organizationStatus") val organizationStatus: String? = null,
    @SerialName("organizationCustomerType") val organizationCustomerType: String? = null,
)

/** One role grant. `roleCategory` is what separates a کارفرما account from an insured one. */
@Serializable
data class CurrentUserRoleDto(
    @SerialName("id") val id: String? = null,
    @SerialName("roleName") val roleName: String? = null,
    @SerialName("roleDisplayName") val roleDisplayName: String? = null,
    @SerialName("roleUniqueName") val roleUniqueName: String? = null,
    @SerialName("roleDescription") val roleDescription: String? = null,
    @SerialName("roleCategory") val roleCategory: String? = null,
)

@Serializable
data class CurrentUserDetailDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("oimUserId") val oimUserId: String? = null,
    @SerialName("geoUnit") val geoUnit: CurrentUserGeoUnitDto? = null,
)

/**
 * Where the account sits geographically.
 *
 * `old_android` modelled `parent` and `type` several levels deep, each carrying another `parent`.
 * Only the code and title are ever read, so the nesting stops here rather than reproducing a
 * chain of near-identical classes nothing looks at.
 */
@Serializable
data class CurrentUserGeoUnitDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("code") val code: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
)
