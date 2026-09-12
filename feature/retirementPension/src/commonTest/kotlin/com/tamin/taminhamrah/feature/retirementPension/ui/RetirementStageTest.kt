package com.tamin.taminhamrah.feature.retirementPension.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The status-code table the track screen's pipeline is driven by. */
class RetirementStageTest {

    @Test
    fun everyServiceCodeLandsOnItsOwnStage() {
        assertEquals(RetirementStage.UploadIdentityDocuments.ordinal, RetirementStage.indexOf("0015"))
        assertEquals(RetirementStage.BranchReviewNeeded.ordinal, RetirementStage.indexOf("0017"))
        assertEquals(RetirementStage.BranchHistoryReview.ordinal, RetirementStage.indexOf("0045"))
        assertEquals(RetirementStage.UploadQuitLetter.ordinal, RetirementStage.indexOf("0046"))
        assertEquals(RetirementStage.BranchQuitLetterReview.ordinal, RetirementStage.indexOf("0048"))
        assertEquals(RetirementStage.IssueEdict.ordinal, RetirementStage.indexOf("0047"))
    }

    @Test
    fun anIssuedEdictSitsOnTheLastStage() {
        assertEquals(RetirementStage.IssueEdict.ordinal, RetirementStage.indexOf("0018"))
        assertTrue(RetirementStage.isComplete("0018"))
        assertFalse(RetirementStage.isComplete("0047"))
    }

    @Test
    fun noCodeMeansNothingHasHappenedYet() {
        assertEquals(RetirementStage.Authentication.ordinal, RetirementStage.indexOf(null))
        assertEquals(RetirementStage.Authentication.ordinal, RetirementStage.indexOf(""))
        assertEquals(RetirementStage.Authentication.ordinal, RetirementStage.indexOf("   "))
    }

    @Test
    fun anUnknownCodeDoesNotClaimProgress() {
        // The mock hardcoded index 4 here; an unrecognized code must not imply five stages done.
        assertEquals(RetirementStage.Authentication.ordinal, RetirementStage.indexOf("9999"))
    }

    @Test
    fun thePipelineHasTheEightStagesTheDesignLists() {
        assertEquals(8, RetirementStage.entries.size)
    }
}
