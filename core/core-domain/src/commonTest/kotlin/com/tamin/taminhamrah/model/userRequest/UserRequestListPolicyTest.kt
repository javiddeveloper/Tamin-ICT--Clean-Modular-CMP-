package com.tamin.taminhamrah.model.userRequest

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserRequestListPolicyTest {

    @Test
    fun `viewCapability matches legacy type and status matrix`() {
        assertEquals(
            UserRequestViewCapability.VIEW_DETAILS,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.ILL_DAY, "0021"),
        )
        assertEquals(
            UserRequestViewCapability.VIEW_DETAILS,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.ILL_DAY, "21"),
        )
        assertEquals(
            UserRequestViewCapability.NONE,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.ILL_DAY, "0010"),
        )
        assertEquals(
            UserRequestViewCapability.VIEW_DETAILS,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.PREGNANCY, "0014"),
        )
        assertEquals(
            UserRequestViewCapability.VIEW_DETAILS,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.ORTHOTICS_PROSTHESIS, "0019"),
        )
        assertEquals(
            UserRequestViewCapability.VIEW_DETAILS,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.ARTICLE_SIXTEEN, "2602"),
        )
        assertEquals(
            UserRequestViewCapability.VIEW_DETAILS,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.DEFERRED_INSTALLMENT, "0018"),
        )
        assertEquals(
            UserRequestViewCapability.FOLLOW_UP_OBJECTION,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.FOLLOW_UP_OBJECTION, "0001"),
        )
        assertEquals(
            UserRequestViewCapability.NONE,
            UserRequestListPolicy.viewCapability(UserRequestTypeIds.MEDICAL_COMMISSION, "0005"),
        )
    }

    @Test
    fun `canShowErrors is limited to document-check types at specific statuses`() {
        assertTrue(UserRequestListPolicy.canShowErrors(UserRequestTypeIds.OTHER_REQUEST, "0006"))
        assertTrue(UserRequestListPolicy.canShowErrors(UserRequestTypeIds.DOCUMENT_CHECK_9, "0019"))
        assertTrue(UserRequestListPolicy.canShowErrors(UserRequestTypeIds.DOCUMENT_CHECK_19, "0019"))
        assertFalse(UserRequestListPolicy.canShowErrors(UserRequestTypeIds.OTHER_REQUEST, "0019"))
        assertFalse(UserRequestListPolicy.canShowErrors(UserRequestTypeIds.FOLLOW_UP_OBJECTION, "0006"))
        assertFalse(UserRequestListPolicy.canShowErrors(UserRequestTypeIds.MEDICAL_COMMISSION, "0006"))
        assertFalse(UserRequestListPolicy.canShowErrors(UserRequestTypeIds.ILL_DAY, "0006"))
    }

    @Test
    fun `workflow status buckets tabs by request code`() {
        assertEquals(UserRequestTabCategory.ACTION_REQUIRED, UserRequestWorkflowStatus.tabCategory("0021"))
        assertEquals(UserRequestTabCategory.ACTION_REQUIRED, UserRequestWorkflowStatus.tabCategory("0014"))
        assertEquals(UserRequestTabCategory.COMPLETED, UserRequestWorkflowStatus.tabCategory("0018"))
        assertEquals(UserRequestTabCategory.COMPLETED, UserRequestWorkflowStatus.tabCategory("2602"))
        assertEquals(UserRequestTabCategory.IN_PROGRESS, UserRequestWorkflowStatus.tabCategory("0002"))
        assertEquals(UserRequestTabCategory.IN_PROGRESS, UserRequestWorkflowStatus.tabCategory("unknown"))
    }
}
