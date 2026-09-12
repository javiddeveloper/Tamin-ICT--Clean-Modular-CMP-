package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The employment flag on a کارکنان row.
 *
 * `leavingWorkDate` is nullable on the wire but the domain model flattens it with `orEmpty()`, so
 * "still employed" is a *blank* date, never a null one. A null-check compiles and is always false,
 * which marked every member as having left the workshop — these tests are here to keep that from
 * coming back.
 */
class WorkshopMemberUiMapperTest {

    @Test
    fun `member with no leaving date is employed`() {
        val member = WorkshopMemberDN(
            insuranceNumber = "12345678",
            firstName = "سروین",
            lastName = "نامی",
            leavingWorkStatus = "شاغل",
            leavingWorkDate = "",
        )

        assertTrue(member.toPresentation().isEmployed)
    }

    @Test
    fun `member with a leaving date is not employed`() {
        val member = WorkshopMemberDN(
            insuranceNumber = "12345678",
            leavingWorkStatus = "ترک کار",
            leavingWorkDate = "14040407",
        )

        assertFalse(member.toPresentation().isEmployed)
    }

    /**
     * The status is free text the service composes, and it spells the same word several ways. The
     * flag must not depend on it — only the date decides.
     */
    @Test
    fun `status wording does not decide employment`() {
        val leftButStatusSaysWorking = WorkshopMemberDN(
            leavingWorkStatus = "شاغل",
            leavingWorkDate = "14040407",
        )
        val presentButStatusSaysLeft = WorkshopMemberDN(
            leavingWorkStatus = "ترك كار",
            leavingWorkDate = "",
        )

        assertFalse(leftButStatusSaysWorking.toPresentation().isEmployed)
        assertTrue(presentButStatusSaysLeft.toPresentation().isEmployed)
    }
}
