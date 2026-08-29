package com.tamin.taminhamrah.feature.pregnancyPay.contract

import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PREGNANCY_TYPE_SINGLE
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PREGNANCY_TYPE_TRIPLET_OR_MORE
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PREGNANCY_TYPE_TWINS
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayOptionUi
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayUiState
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Pure computed-property coverage for [PregnancyPayUiState] — the per-step "can advance" gates and
 * the day-count/diff math they're built on. Deliberately excludes the document-upload/submit
 * properties (`canSubmitDocuments`, `documentSubmissionPayload`, ...), which need a real
 * [io.github.vinceglb.filekit.PlatformFile] and so live in the Robolectric-backed
 * `PregnancyPayDocumentUploadTest` under androidUnitTest instead.
 */
class PregnancyPayContractTest {

    private val singleType = PregnancyPayOptionUi(id = PREGNANCY_TYPE_SINGLE, label = "تک قلو")
    private val twinsType = PregnancyPayOptionUi(id = PREGNANCY_TYPE_TWINS, label = "دوقلو")
    private val tripletType = PregnancyPayOptionUi(id = PREGNANCY_TYPE_TRIPLET_OR_MORE, label = "سه قلو یا بیشتر")
    private val pregnancyStatus = PregnancyPayOptionUi(id = "1", label = "بارداری طبیعی")

    // --- restDaysCount / canGoNextFromBranchAndRest ---

    @Test
    fun restDaysCount_endAfterStart_returnsDayDifference() {
        val state = PregnancyPayUiState(
            restStartDateTimeStamp = 0L,
            restEndDateTimeStamp = 10 * MILLIS_PER_DAY,
        )

        assertTrue(state.restDaysCount == 10L)
    }

    @Test
    fun restDaysCount_endEqualsStart_returnsNull() {
        val state = PregnancyPayUiState(restStartDateTimeStamp = 1000L, restEndDateTimeStamp = 1000L)

        assertNull(state.restDaysCount)
    }

    @Test
    fun restDaysCount_endBeforeStart_returnsNull() {
        val state = PregnancyPayUiState(restStartDateTimeStamp = 10_000L, restEndDateTimeStamp = 1_000L)

        assertNull(state.restDaysCount)
    }

    @Test
    fun canGoNextFromBranchAndRest_missingBranch_isFalseEvenWithValidDates() {
        val state = PregnancyPayUiState(
            branch = null,
            restStartDateTimeStamp = 0L,
            restEndDateTimeStamp = 10 * MILLIS_PER_DAY,
        )

        assertFalse(state.canGoNextFromBranchAndRest)
    }

    // --- estimateRestDaysCount / canCalculateEstimate ---

    @Test
    fun canCalculateEstimate_endBeforeOrEqualStart_isFalse() {
        val state = PregnancyPayUiState(
            estimateRestStartDateTimeStamp = 5_000L,
            estimateRestEndDateTimeStamp = 5_000L,
        )

        assertNull(state.estimateRestDaysCount)
        assertFalse(state.canCalculateEstimate)
    }

    @Test
    fun canCalculateEstimate_whileAlreadyCalculating_isFalse() {
        val state = PregnancyPayUiState(
            estimateRestStartDateTimeStamp = 0L,
            estimateRestEndDateTimeStamp = 10 * MILLIS_PER_DAY,
            isCalculatingEstimate = true,
        )

        assertFalse(state.canCalculateEstimate)
    }

    // --- requiredChildNationalCodeCount ---

    @Test
    fun requiredChildNationalCodeCount_twins_isTwo() {
        val state = PregnancyPayUiState(pregnancyType = twinsType)

        assertTrue(state.requiredChildNationalCodeCount == 2)
    }

    @Test
    fun requiredChildNationalCodeCount_tripletOrMore_isThree() {
        val state = PregnancyPayUiState(pregnancyType = tripletType)

        assertTrue(state.requiredChildNationalCodeCount == 3)
    }

    @Test
    fun requiredChildNationalCodeCount_singleOrUnset_isOne() {
        assertTrue(PregnancyPayUiState(pregnancyType = singleType).requiredChildNationalCodeCount == 1)
        assertTrue(PregnancyPayUiState(pregnancyType = null).requiredChildNationalCodeCount == 1)
    }

    // --- hasDuplicateChildNationalCode / canGoNextFromPregnancyAndNewborn ---

    private fun validPregnancyAndNewbornBase(pregnancyType: PregnancyPayOptionUi) = PregnancyPayUiState(
        babyBirthDateTimeStamp = 100_000L,
        pregnancyStatus = pregnancyStatus,
        pregnancyType = pregnancyType,
    )

    @Test
    fun hasDuplicateChildNationalCode_twinsWithSameCodeTwice_isTrueAndBlocksAdvance() {
        val state = validPregnancyAndNewbornBase(twinsType).copy(
            childNationalCode = "0011122233",
            childNationalCode2 = "0011122233",
        )

        assertTrue(state.hasDuplicateChildNationalCode)
        assertFalse(state.canGoNextFromPregnancyAndNewborn)
    }

    @Test
    fun hasDuplicateChildNationalCode_twinsWithDistinctCodes_isFalseAndAllowsAdvance() {
        val state = validPregnancyAndNewbornBase(twinsType).copy(
            childNationalCode = "0011122233",
            childNationalCode2 = "0022233344",
        )

        assertFalse(state.hasDuplicateChildNationalCode)
        assertTrue(state.canGoNextFromPregnancyAndNewborn)
    }

    @Test
    fun canGoNextFromPregnancyAndNewborn_childCodeShorterThanExpectedLength_isFalse() {
        val state = validPregnancyAndNewbornBase(singleType).copy(childNationalCode = "123")

        assertFalse(state.canGoNextFromPregnancyAndNewborn)
    }

    @Test
    fun canGoNextFromPregnancyAndNewborn_twinsMissingSecondCode_isFalse() {
        val state = validPregnancyAndNewbornBase(twinsType).copy(
            childNationalCode = "0011122233",
            childNationalCode2 = "",
        )

        assertFalse(state.canGoNextFromPregnancyAndNewborn)
    }

    @Test
    fun canGoNextFromPregnancyAndNewborn_allValid_isTrue() {
        val state = validPregnancyAndNewbornBase(singleType).copy(childNationalCode = "0011122233")

        assertTrue(state.canGoNextFromPregnancyAndNewborn)
    }

    // --- canGoNextFromDoctorAndRequest ---

    @Test
    fun canGoNextFromDoctorAndRequest_doctorCodeShorterThanExpectedLength_isFalse() {
        val state = PregnancyPayUiState(
            requestType = PregnancyPayOptionUi(id = "1", label = "تا شش ماه"),
            doctorName = "دکتر رضایی",
            doctorCode = "123",
        )

        assertFalse(state.canGoNextFromDoctorAndRequest)
    }

    @Test
    fun canGoNextFromDoctorAndRequest_blankDoctorName_isFalse() {
        val state = PregnancyPayUiState(
            requestType = PregnancyPayOptionUi(id = "1", label = "تا شش ماه"),
            doctorName = "  ",
            doctorCode = "12345",
        )

        assertFalse(state.canGoNextFromDoctorAndRequest)
    }

    @Test
    fun canGoNextFromDoctorAndRequest_allValid_isTrue() {
        val state = PregnancyPayUiState(
            requestType = PregnancyPayOptionUi(id = "1", label = "تا شش ماه"),
            doctorName = "دکتر رضایی",
            doctorCode = "12345",
        )

        assertTrue(state.canGoNextFromDoctorAndRequest)
    }

    // --- babyBirthToRestStartDiffDays ---

    @Test
    fun babyBirthToRestStartDiffDays_birthBeforeRestStart_returnsAbsoluteDifference() {
        val state = PregnancyPayUiState(
            restStartDateTimeStamp = 10 * MILLIS_PER_DAY,
            babyBirthDateTimeStamp = 5 * MILLIS_PER_DAY,
        )

        assertTrue(state.babyBirthToRestStartDiffDays == 5L)
    }

    @Test
    fun babyBirthToRestStartDiffDays_birthAfterRestStart_returnsAbsoluteDifference() {
        val state = PregnancyPayUiState(
            restStartDateTimeStamp = 5 * MILLIS_PER_DAY,
            babyBirthDateTimeStamp = 10 * MILLIS_PER_DAY,
        )

        assertTrue(state.babyBirthToRestStartDiffDays == 5L)
    }

    @Test
    fun babyBirthToRestStartDiffDays_missingEitherDate_returnsNull() {
        assertNull(PregnancyPayUiState(restStartDateTimeStamp = null, babyBirthDateTimeStamp = 1000L).babyBirthToRestStartDiffDays)
        assertNull(PregnancyPayUiState(restStartDateTimeStamp = 1000L, babyBirthDateTimeStamp = null).babyBirthToRestStartDiffDays)
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
