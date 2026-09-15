package com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper

import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionDelta
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionYearSubtitle
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.YearCompletionStatus
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryPR
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun record(
    year: String,
    workshopName: String = "کارگاه",
    monthDays: List<Int> = List(12) { 31 },
): ObjectionInsuranceHistoryPR = ObjectionInsuranceHistoryPR(
    year = year,
    workshopName = workshopName,
    oldMonth1 = monthDays[0].toString(),
    oldMonth2 = monthDays[1].toString(),
    oldMonth3 = monthDays[2].toString(),
    oldMonth4 = monthDays[3].toString(),
    oldMonth5 = monthDays[4].toString(),
    oldMonth6 = monthDays[5].toString(),
    oldMonth7 = monthDays[6].toString(),
    oldMonth8 = monthDays[7].toString(),
    oldMonth9 = monthDays[8].toString(),
    oldMonth10 = monthDays[9].toString(),
    oldMonth11 = monthDays[10].toString(),
    oldMonth12 = monthDays[11].toString(),
)

class ObjectionInsuranceUiMapperTest {

    @Test
    fun buildYearCards_oneWorkshopWithFullYear_isCompleteWithDaysSubtitle() {
        val cards = buildYearCards(records = listOf(record(year = "1404", monthDays = List(12) { 31 })), edits = emptyMap())

        val card = cards.single()
        assertEquals(YearCompletionStatus.Complete, card.status)
        assertEquals(ObjectionYearSubtitle.TotalDays(372), card.subtitle)
        assertEquals(1f, card.fraction)
    }

    @Test
    fun buildYearCards_belowThreshold_isDeficit() {
        val cards = buildYearCards(
            records = listOf(record(year = "1403", monthDays = List(12) { 20 })),
            edits = emptyMap(),
        )

        assertEquals(YearCompletionStatus.Deficit, cards.single().status)
    }

    @Test
    fun buildYearCards_multipleWorkshopsSameYear_showsWorkshopCountSubtitle() {
        val cards = buildYearCards(
            records = listOf(
                record(year = "1389", workshopName = "کارگاه الف"),
                record(year = "1389", workshopName = "کارگاه ب"),
            ),
            edits = emptyMap(),
        )

        val card = cards.single()
        assertEquals(2, card.recordIndices.size)
        assertEquals(ObjectionYearSubtitle.WorkshopCount(2), card.subtitle)
    }

    @Test
    fun buildYearCards_withRealEdit_isEditedRegardlessOfDeclaredTotal() {
        val cards = buildYearCards(
            records = listOf(record(year = "1403", monthDays = List(12) { 31 })),
            edits = mapOf(0 to mapOf(0 to "5")),
        )

        val card = cards.single()
        assertEquals(YearCompletionStatus.Edited, card.status)
        assertEquals(ObjectionYearSubtitle.Edited, card.subtitle)
    }

    @Test
    fun buildYearCards_zeroBlankEdit_doesNotCountAsEdited() {
        val cards = buildYearCards(
            records = listOf(record(year = "1403", monthDays = List(12) { 31 })),
            edits = mapOf(0 to mapOf(0 to "")),
        )

        assertEquals(YearCompletionStatus.Complete, cards.single().status)
    }

    @Test
    fun sanitizeDayInput_convertsPersianDigitsAndClampsToMax() {
        assertEquals("30", sanitizeDayInput("۹۹", maxDays = 30))
        assertEquals("15", sanitizeDayInput("15", maxDays = 30))
        assertEquals("", sanitizeDayInput("abc", maxDays = 30))
    }

    @Test
    fun hasRealEdit_ignoresBlankAndZeroValues() {
        assertTrue(mapOf(0 to "5").hasRealEdit())
        assertTrue(!mapOf(0 to "0", 1 to "").hasRealEdit())
    }

    @Test
    fun buildEditedRecords_onlyIncludesRecordsWithRealEdits() {
        val records = listOf(record(year = "1403"), record(year = "1404"))
        val edited = buildEditedRecords(records, edits = mapOf(1 to mapOf(0 to "10")))

        assertEquals(1, edited.size)
        assertEquals("10", edited.single().newMonth1)
    }

    @Test
    fun buildSeasonGroups_groupsTwelveMonthsIntoFourSeasonsWithJalaliMaxDays() {
        val groups = buildSeasonGroups(
            record = record(year = "1403", monthDays = List(12) { 31 }),
            draft = mapOf(0 to "5"),
        )

        assertEquals(4, groups.size)
        assertEquals(3, groups[0].rows.size)
        val fallFirstRow = groups[2].rows[0]
        assertEquals(6, fallFirstRow.index)
        assertEquals(30, fallFirstRow.maxDays)
        assertEquals("5", groups[0].rows[0].value)
        assertTrue(groups[0].rows[0].isRegistered)
    }

    @Test
    fun buildMonthBars_stacksUserAddedDaysAboveRegisteredPortion() {
        val bars = buildMonthBars(
            record = record(year = "1403", monthDays = listOf(15, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31)),
            draft = mapOf(0 to "31"),
        )

        val partialMonth = bars[0]
        assertFalse(partialMonth.isFull)
        assertEquals(15f / 31f, partialMonth.recFraction)
        assertEquals(16f / 31f, partialMonth.extraFraction)

        val fullMonth = bars[1]
        assertTrue(fullMonth.isFull)
        assertEquals(0f, fullMonth.extraFraction)
    }

    @Test
    fun buildDetailDelta_reflectsAddedAndReducedTotals() {
        val record = record(year = "1403", monthDays = listOf(20) + List(11) { 31 })

        assertEquals(ObjectionDelta.None, buildDetailDelta(record, draft = emptyMap()))
        assertEquals(ObjectionDelta.Added(11), buildDetailDelta(record, draft = mapOf(0 to "31")))
        assertEquals(ObjectionDelta.Reduced(10), buildDetailDelta(record, draft = mapOf(0 to "10")))
    }

    @Test
    fun buildWorkshopPickerRows_filtersByYearAndFlagsEditedRecords() {
        val records = listOf(
            record(year = "1389", workshopName = "الف", monthDays = List(12) { 31 }),
            record(year = "1389", workshopName = "ب", monthDays = List(12) { 0 }),
            record(year = "1390", workshopName = "ج"),
        )

        val rows = buildWorkshopPickerRows(records, edits = mapOf(0 to mapOf(0 to "10")), year = "1389")

        assertEquals(2, rows.size)
        assertEquals(0, rows[0].recordIndex)
        assertTrue(rows[0].isEdited)
        assertFalse(rows[1].isEdited)
        assertEquals(372, rows[0].totalDays)
        assertEquals(1f, rows[0].monthOpacities[0])
        assertEquals(0f, rows[1].monthOpacities[0])
    }
}
