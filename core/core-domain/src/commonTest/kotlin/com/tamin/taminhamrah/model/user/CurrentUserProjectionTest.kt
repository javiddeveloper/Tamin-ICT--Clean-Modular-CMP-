package com.tamin.taminhamrah.model.user

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `UserProfileDN` is a view of `CurrentUserDN`, not a separate fetch. These pin that the seven
 * fields carry across unchanged, and that the کارفرما decision is read off the roles.
 */
class CurrentUserProjectionTest {

    private val employer = CurrentUserDN(
        entityId = "1",
        login = "u",
        firstName = "سروین",
        lastName = "نامی",
        email = "a@b.c",
        nationalCode = "0012345678",
        mobile = "09120000000",
        roles = listOf(CurrentUserRoleDN(name = "EMPLOYER_ROLE", uniqueName = "employer")),
    )

    @Test
    fun `the narrow profile carries the same seven fields`() {
        val profile = employer.toUserProfile()

        assertEquals("1", profile.entityId)
        assertEquals("u", profile.login)
        assertEquals("سروین", profile.firstName)
        assertEquals("نامی", profile.lastName)
        assertEquals("a@b.c", profile.email)
        assertEquals("0012345678", profile.nationalCode)
        assertEquals("09120000000", profile.mobile)
    }

    @Test
    fun `absent fields become null rather than empty strings`() {
        // The narrow model is nullable where this one is blank-by-default; a screen checking for
        // null must not be handed "".
        val profile = CurrentUserDN().toUserProfile()

        assertEquals(null, profile.firstName)
        assertEquals(null, profile.nationalCode)
    }

    @Test
    fun `an employer role makes the account an employer`() {
        assertTrue(employer.isEmployer)
    }

    @Test
    fun `an account without an employer role is not one`() {
        val insured = employer.copy(
            roles = listOf(CurrentUserRoleDN(name = "INSURED", uniqueName = "insured")),
        )

        assertFalse(insured.isEmployer)
        assertFalse(CurrentUserDN().isEmployer)
    }

    @Test
    fun `fullName joins what is present without stray spaces`() {
        assertEquals("سروین نامی", employer.fullName)
        assertEquals("سروین", employer.copy(lastName = "").fullName)
        assertEquals("", CurrentUserDN().fullName)
    }
}
