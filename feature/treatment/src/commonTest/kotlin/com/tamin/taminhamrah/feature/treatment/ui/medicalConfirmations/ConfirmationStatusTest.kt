package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import com.tamin.taminhamrah.model.treatment.ConfirmationStatus
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationPR
import com.tamin.taminhamrah.model.treatment.confirmationStatus
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The verdict arrives only as Persian prose, and the wordings below are the ones the service
 * actually sends -- they are transcribed from the status table the old app documents, mixed
 * Arabic/Persian letters and misspellings intact.
 *
 * Every rejected and pending wording here also *contains* «تایید» in one spelling or another,
 * which is exactly why matching approval first classified all of them as approved.
 */
class ConfirmationStatusTest {

    @Test
    fun testApprovedWordings() {
        assertStatus(ConfirmationStatus.APPROVED, "تایید شده")
        assertStatus(ConfirmationStatus.APPROVED, "تائید شده")
        assertStatus(ConfirmationStatus.APPROVED, "تايئد شده")
        assertStatus(ConfirmationStatus.APPROVED, "تایید شعبه")
    }

    @Test
    fun testRejectedWordingsAreNotReadAsApproved() {
        assertStatus(ConfirmationStatus.REJECTED, "تايئد نشده")
        assertStatus(ConfirmationStatus.REJECTED, "تایید نشده")
        assertStatus(ConfirmationStatus.REJECTED, "عدم تایید")
        assertStatus(ConfirmationStatus.REJECTED, "قابل بررسي نمي باشد")
    }

    @Test
    fun testPendingWordingsAreNotReadAsApproved() {
        assertStatus(ConfirmationStatus.PENDING, "در انتظار تایید")
        assertStatus(ConfirmationStatus.PENDING, "در حال بررسی در شعبه")
        assertStatus(ConfirmationStatus.PENDING, "به کميسيون پزشکي ارجاع شود")
        assertStatus(ConfirmationStatus.PENDING, "بخشي از دوره بيماري تايئد شده")
        assertStatus(ConfirmationStatus.PENDING, "نیاز  به تکميل مدارک")
    }

    @Test
    fun testUnknownWhenServiceSendsNothing() {
        assertStatus(ConfirmationStatus.UNKNOWN, "")
        assertStatus(ConfirmationStatus.UNKNOWN, "   ")
    }

    private fun assertStatus(expected: ConfirmationStatus, statusDesc: String) {
        assertEquals(expected, confirmationOf(statusDesc).confirmationStatus, "for «$statusDesc»")
    }

    private fun confirmationOf(statusDesc: String) = MedicalConfirmationPR(
        repId = "",
        supportType = "",
        treatmentCenter = "",
        outpatientRestStartDate = "",
        outpatientRestEndDate = "",
        numberOfOutpatientDays = "0",
        inpatientRestStartDate = "",
        inpatientRestEndDate = "",
        numberOfInpatientDays = "0",
        unapprovedFromDate = "",
        unapprovedToDate = "",
        branchName = "",
        branchStatus = "",
        description = "",
        statusDesc = statusDesc,
    )
}
