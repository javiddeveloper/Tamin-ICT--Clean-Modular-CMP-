package com.tamin.taminhamrah.feature.profile.ui.identity.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.identity.IdentityInfoPR
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.identity_field_birth_city
import taminx.core.core_ui.identity_field_birth_date
import taminx.core.core_ui.identity_field_email
import taminx.core.core_ui.identity_field_father_name
import taminx.core.core_ui.identity_field_first_name
import taminx.core.core_ui.identity_field_gender
import taminx.core.core_ui.identity_field_id_number
import taminx.core.core_ui.identity_field_id_serial
import taminx.core.core_ui.identity_field_id_series
import taminx.core.core_ui.identity_field_issue_city
import taminx.core.core_ui.identity_field_last_name
import taminx.core.core_ui.identity_field_mobile
import taminx.core.core_ui.identity_field_national_code
import taminx.core.core_ui.identity_field_nationality
import taminx.core.core_ui.identity_section_birth_certificate
import taminx.core.core_ui.identity_section_contact
import taminx.core.core_ui.identity_section_identifier
import taminx.core.core_ui.identity_section_personal

/**
 * One labeled line on the identity screen.
 *
 * The label is a [StringResource] rather than resolved text so that building the whole screen's
 * contents stays a pure function — it runs inside a `remember`, where composition is not available.
 */
@Immutable
data class IdentityField(
    val label: StringResource,
    val value: String,
    /** Renders muted: the service returned nothing for this line. */
    val isAbsent: Boolean = false,
)

/** A titled card holding a run of [fields]. */
@Immutable
data class IdentitySection(
    val title: StringResource,
    val fields: ImmutableList<IdentityField>,
)

/**
 * Lays the identity record out as the screen shows it.
 *
 * Resolved once for the whole screen rather than per row, and immutable all the way down, so a
 * fold of the header — or anything else recomposing the screen — leaves every card skippable.
 *
 * [nationality] and [absentValue] come in already resolved for the same reason the labels do not:
 * this is called from a `remember` block. [mobile] and [email] come from the user profile: the
 * identity endpoint has never carried them.
 */
fun IdentityInfoPR.toSections(
    nationality: String,
    absentValue: String,
    mobile: String?,
    email: String?,
): ImmutableList<IdentitySection> = persistentListOf(
    IdentitySection(
        title = Res.string.identity_section_personal,
        fields = persistentListOf(
            IdentityField(Res.string.identity_field_first_name, firstName.orAbsent(absentValue)),
            IdentityField(Res.string.identity_field_last_name, lastName.orAbsent(absentValue)),
            IdentityField(Res.string.identity_field_father_name, fatherName.orAbsent(absentValue)),
            IdentityField(Res.string.identity_field_gender, genderDisplay.orAbsent(absentValue)),
            IdentityField(
                label = Res.string.identity_field_birth_date,
                value = dateOfBirthFormatted.toPersianDigits().orAbsent(absentValue),
            ),
            IdentityField(Res.string.identity_field_nationality, nationality),
        ),
    ),
    IdentitySection(
        title = Res.string.identity_section_identifier,
        fields = persistentListOf(
            IdentityField(
                label = Res.string.identity_field_national_code,
                value = nationalId.toPersianDigits().orAbsent(absentValue),
            ),
        ),
    ),
    IdentitySection(
        title = Res.string.identity_section_birth_certificate,
        fields = persistentListOf(
            IdentityField(
                label = Res.string.identity_field_id_number,
                value = idCardNumber.toPersianDigits().orAbsent(absentValue),
            ),
            IdentityField(
                label = Res.string.identity_field_id_series,
                value = idCardSerial1.toPersianDigits().orAbsent(absentValue),
            ),
            IdentityField(
                label = Res.string.identity_field_id_serial,
                value = idCardSerial2.toPersianDigits().orAbsent(absentValue),
            ),
            IdentityField(Res.string.identity_field_birth_city, cityOfBirthName.orAbsent(absentValue)),
            IdentityField(Res.string.identity_field_issue_city, cityOfIssueName.orAbsent(absentValue)),
        ),
    ),
    IdentitySection(
        title = Res.string.identity_section_contact,
        fields = persistentListOf(
            IdentityField(
                label = Res.string.identity_field_mobile,
                value = mobile?.toPersianDigits().orAbsent(absentValue),
                isAbsent = mobile.isNullOrBlank(),
            ),
            IdentityField(
                label = Res.string.identity_field_email,
                value = email.orAbsent(absentValue),
                isAbsent = email.isNullOrBlank(),
            ),
        ),
    ),
).toImmutableList()

/** A field the service left empty reads as [absentValue], never as a blank line. */
private fun String?.orAbsent(absentValue: String): String =
    if (isNullOrBlank()) absentValue else this
