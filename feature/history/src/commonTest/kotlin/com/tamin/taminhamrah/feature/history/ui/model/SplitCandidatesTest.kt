package com.tamin.taminhamrah.feature.history.ui.model

import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.WageDetailPR
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * «تفکیک کارگاه» — the employers the chart can be broken out by, and the timeline drawn per employer.
 *
 * The two scopes fold to different columns on purpose: a career is read year by year, a single year
 * month by month. Getting that wrong is invisible in a screenshot — the cells still draw, they just
 * stop lining up with the bars above them — so it is pinned here instead.
 */
class SplitCandidatesTest {

    @Test
    fun allScope_foldsEachEmployerIntoOneCellPerYear() {
        val candidates = splitCandidates(
            wageByYear = persistentMapOf(
                "1400" to listOf(row(year = "1400", workshop = "A", days = 10)).toImmutableList(),
                "1401" to listOf(row(year = "1401", workshop = "A", days = 20)).toImmutableList(),
            ),
            year = null,
            yearOrder = listOf("1400", "1401"),
            nameOf = { it.rwshname },
        )

        assertEquals(1, candidates.size, "one employer across both years")
        val employer = candidates.single()
        assertEquals(
            listOf(120, 240),
            employer.cellDays,
            "one column per year, in the order the chart draws them",
        )
        assertEquals(360, employer.totalDays)
    }

    @Test
    fun allScope_ordersCellsByTheChartsColumns_notByTheServersOrder() {
        val candidates = splitCandidates(
            wageByYear = persistentMapOf(
                "1402" to listOf(row(year = "1402", workshop = "A", days = 1)).toImmutableList(),
                "1400" to listOf(row(year = "1400", workshop = "A", days = 2)).toImmutableList(),
            ),
            year = null,
            // The chart draws the oldest first; the map arrived the other way round.
            yearOrder = listOf("1400", "1402"),
            nameOf = { it.rwshname },
        )

        assertEquals(listOf(24, 12), candidates.single().cellDays)
    }

    /** A year the chart has no column for cannot be drawn, so it must not shift the ones that follow. */
    @Test
    fun allScope_dropsAYearTheChartDoesNotDraw() {
        val candidates = splitCandidates(
            wageByYear = persistentMapOf(
                "1400" to listOf(row(year = "1400", workshop = "A", days = 5)).toImmutableList(),
                "1399" to listOf(row(year = "1399", workshop = "A", days = 9)).toImmutableList(),
            ),
            year = null,
            yearOrder = listOf("1400"),
            nameOf = { it.rwshname },
        )

        assertEquals(listOf(60), candidates.single().cellDays)
        assertEquals(60, candidates.single().totalDays, "1399 is not drawn, so it is not counted")
    }

    @Test
    fun yearScope_foldsEachEmployerIntoOneCellPerMonth() {
        val candidates = splitCandidates(
            wageByYear = persistentMapOf(
                "1400" to listOf(
                    row(year = "1400", workshop = "A", monthDays = listOf(31, 0, 15)),
                ).toImmutableList(),
            ),
            year = "1400",
            yearOrder = listOf("1400"),
            nameOf = { it.rwshname },
        )

        val cells = candidates.single().cellDays
        assertEquals(12, cells.size, "always twelve months")
        assertEquals(listOf(31, 0, 15), cells.take(3))
        assertEquals(46, candidates.single().totalDays)
    }

    /**
     * The design's own key is the workshop number *and* the history type: one workshop can report a
     * person under two schemes, and the schemes with no number of their own would otherwise all
     * collapse into a single nameless entry.
     */
    @Test
    fun groupsByWorkshopAndSchemeTogether() {
        val candidates = splitCandidates(
            wageByYear = persistentMapOf(
                "1400" to listOf(
                    row(year = "1400", workshop = "A", id = "11", scheme = "اجباري", days = 10),
                    row(year = "1400", workshop = "A", id = "11", scheme = "اختياري", days = 10),
                    row(year = "1400", workshop = "A", id = "11", scheme = "اجباري", days = 10),
                ).toImmutableList(),
            ),
            year = "1400",
            yearOrder = listOf("1400"),
            nameOf = { it.rwshname },
        )

        assertEquals(2, candidates.size, "the same workshop under two schemes is two entries")
    }

    @Test
    fun ranksByDaysAndKeepsAtMostFour() {
        val rows = (1..6).map { index ->
            row(year = "1400", workshop = "W$index", id = index.toString(), days = index)
        }
        val candidates = splitCandidates(
            wageByYear = persistentMapOf("1400" to rows.toImmutableList()),
            year = "1400",
            yearOrder = listOf("1400"),
            nameOf = { it.rwshname },
        )

        assertEquals(MAX_SPLIT_SOURCES, candidates.size)
        assertEquals(listOf("W6", "W5", "W4", "W3"), candidates.map { it.label })
        assertTrue(
            candidates.zipWithNext().all { (a, b) -> a.totalDays >= b.totalDays },
            "largest first, so the split shows who actually accounts for the career",
        )
    }

    @Test
    fun noRowsIsNoCandidates() {
        assertTrue(
            splitCandidates(
                wageByYear = persistentMapOf(),
                year = null,
                yearOrder = listOf("1400"),
                nameOf = { it.rwshname },
            ).isEmpty(),
        )
    }

    @Test
    fun sumsWagesAlongsideDays() {
        val candidates = splitCandidates(
            wageByYear = persistentMapOf(
                "1400" to listOf(
                    row(year = "1400", workshop = "A", monthDays = listOf(30, 30), wage = "1000"),
                ).toImmutableList(),
            ),
            year = "1400",
            yearOrder = listOf("1400"),
            nameOf = { it.rwshname },
        )

        assertEquals(2000L, candidates.single().totalWage)
        assertEquals(listOf(1000L, 1000L), candidates.single().cellWages.take(2))
    }

    private fun row(
        year: String,
        workshop: String,
        id: String = "1",
        scheme: String = "اجباري",
        days: Int = 0,
        monthDays: List<Int> = List(12) { days },
        wage: String = "0",
    ) = DastmozdInfoItemPR(
        wageDetails = monthDays.map { WageDetailPR(month = it.toString(), wage = wage) },
        hisyear = year, id = 0, risufname = "", risubirthdate = "", risuidserial2 = "",
        risuidserial1 = "", rwshname = workshop, expcitycode = "", brhcode = "", risuidno = "",
        risudname = "", risuid = "", risulname = "", risunatcode = "", brhname = "",
        historytypedesc = scheme, rwshid = id,
    )
}
