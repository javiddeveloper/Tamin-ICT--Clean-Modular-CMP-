package com.tamin.taminhamrah.mapper.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestProgressPhase
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusDN
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusTone
import com.tamin.taminhamrah.model.userRequest.UserRequestTabCategory
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.model.userRequest.UserRequestViewCapability
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserRequestMapperTest {

    @Test
    fun `toPresentation fills list actions from type and status code`() {
        val pr = sampleRequest(
            typeId = UserRequestTypeIds.ILL_DAY,
            statusCode = "0021",
            statusDesc = "نقص مدارک ارسالی",
        ).toPresentation()

        assertEquals(UserRequestViewCapability.VIEW_DETAILS, pr.viewCapability)
        assertFalse(pr.showErrorsAction)
        assertEquals(UserRequestStatusTone.ERROR, pr.statusTone)
        assertEquals(UserRequestProgressPhase.ERROR, pr.progressPhase)
        assertEquals(UserRequestTabCategory.ACTION_REQUIRED, pr.tabCategory)
    }

    @Test
    fun `toPresentation marks follow-up objection as always viewable`() {
        val pr = sampleRequest(
            typeId = UserRequestTypeIds.FOLLOW_UP_OBJECTION,
            statusCode = "0001",
            statusDesc = "در حال بررسی",
        ).toPresentation()

        assertEquals(UserRequestViewCapability.FOLLOW_UP_OBJECTION, pr.viewCapability)
        assertFalse(pr.showErrorsAction)
        assertEquals(UserRequestTabCategory.IN_PROGRESS, pr.tabCategory)
    }

    @Test
    fun `toPresentation shows errors for other request at code 0006`() {
        val pr = sampleRequest(
            typeId = UserRequestTypeIds.OTHER_REQUEST,
            statusCode = "0006",
            statusDesc = "عدم تایید",
        ).toPresentation()

        assertTrue(pr.showErrorsAction)
        assertEquals(UserRequestViewCapability.NONE, pr.viewCapability)
        assertEquals(UserRequestStatusTone.ERROR, pr.statusTone)
    }

    private fun sampleRequest(
        typeId: Long,
        statusCode: String,
        statusDesc: String,
    ) = UserRequestDN(
        id = 101L,
        refCode = "1048401849",
        title = "request",
        comment = "",
        creationTime = null,
        createByName = "",
        status = UserRequestStatusDN(requestCode = statusCode, requestDesc = statusDesc),
        requestType = UserRequestTypeDN(id = typeId, title = "type", description = null),
        referenceId = "101",
    )
}
