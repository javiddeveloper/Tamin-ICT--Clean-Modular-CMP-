package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.utils.ListData
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The one place the sign-in service's relation codes become a role.
 *
 * `"05"` is the previous app's test for a مستمری‌بگیر, and «مجموع سوابق» turns exactly that person
 * away — so this mapping is what stands between a pensioner and a raw 500 from the server.
 */
class HistoryRoleMapperTest {

    @Test
    fun theFirstCodeBeingPensionerMakesTheRoleAPensioner() {
        assertEquals(UserRoleDN.PENSIONER, ListData(total = 1, list = listOf("05")).toUserRole())
    }

    @Test
    fun anyOtherFirstCodeIsAnInsuredPerson() {
        assertEquals(UserRoleDN.INSURED, ListData(total = 1, list = listOf("01")).toUserRole())
    }

    /** `"05"` anywhere but first is a different relation; only the first position decides. */
    @Test
    fun aPensionerCodeLaterInTheListDoesNotDecide() {
        assertEquals(
            UserRoleDN.INSURED,
            ListData(total = 2, list = listOf("01", "05")).toUserRole(),
        )
    }

    /** No list at all: the service has nothing on this person, so the screen does not decide. */
    @Test
    fun noListAtAllIsUnknownRatherThanAGuess() {
        assertEquals(UserRoleDN.UNKNOWN, ListData<String>(total = 0, list = null).toUserRole())
    }

    /** An empty list is a different answer from no list, and the previous app read it as insured. */
    @Test
    fun anEmptyListIsAnInsuredPerson() {
        assertEquals(UserRoleDN.INSURED, ListData(total = 0, list = emptyList<String>()).toUserRole())
    }
}
