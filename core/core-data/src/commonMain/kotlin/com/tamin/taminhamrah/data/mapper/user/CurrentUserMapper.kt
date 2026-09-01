package com.tamin.taminhamrah.data.mapper.user

import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.CurrentUserDto
import com.tamin.taminhamrah.model.user.CurrentUserGeoUnitDN
import com.tamin.taminhamrah.model.user.CurrentUserGeoUnitDto
import com.tamin.taminhamrah.model.user.CurrentUserOrganizationDN
import com.tamin.taminhamrah.model.user.CurrentUserOrganizationDto
import com.tamin.taminhamrah.model.user.CurrentUserRoleDN
import com.tamin.taminhamrah.model.user.CurrentUserRoleDto

/**
 * Nulls collapse to empty strings here so nothing above this line repeats `?: ""`.
 *
 * `birthDate` stays nullable: zero is a real epoch value, so an absent birthdate cannot be
 * flattened into one without inventing 1970.
 */
internal fun CurrentUserDto.toDomain(): CurrentUserDN = CurrentUserDN(
    entityId = entityId.orEmpty(),
    login = login.orEmpty(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    email = email.orEmpty(),
    nationalCode = nationalCode.orEmpty(),
    mobile = mobile.orEmpty(),
    gender = gender.orEmpty(),
    birthDate = birthDate,
    accountStatus = accountStatus ?: status.orEmpty(),
    organization = organization?.toDomain(),
    // A null entry in the list is the service's, not ours — drop it rather than carry a hole.
    roles = roles?.filterNotNull()?.map { it.toDomain() }.orEmpty(),
    geoUnit = userDetail?.geoUnit?.toDomain(),
)

private fun CurrentUserOrganizationDto.toDomain(): CurrentUserOrganizationDN =
    CurrentUserOrganizationDN(
        entityId = entityId.orEmpty(),
        code = code.orEmpty(),
        name = organizationName.orEmpty(),
        status = organizationStatus.orEmpty(),
        customerType = organizationCustomerType.orEmpty(),
    )

private fun CurrentUserRoleDto.toDomain(): CurrentUserRoleDN = CurrentUserRoleDN(
    id = id.orEmpty(),
    name = roleName.orEmpty(),
    displayName = roleDisplayName.orEmpty(),
    uniqueName = roleUniqueName.orEmpty(),
    description = roleDescription.orEmpty(),
    category = roleCategory.orEmpty(),
)

private fun CurrentUserGeoUnitDto.toDomain(): CurrentUserGeoUnitDN = CurrentUserGeoUnitDN(
    id = id,
    code = code.orEmpty(),
    title = title.orEmpty(),
    description = description.orEmpty(),
)
