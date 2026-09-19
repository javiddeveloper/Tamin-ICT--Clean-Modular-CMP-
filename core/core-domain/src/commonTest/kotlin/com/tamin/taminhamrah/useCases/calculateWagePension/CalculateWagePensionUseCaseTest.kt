package com.tamin.taminhamrah.useCases.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.BASIC_WAGE
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.WageDetailDN
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateWagePensionUseCaseTest {

    private val useCase = CalculateWagePensionUseCase()

    @Test
    fun `applies min wage floor when premium years are below 20`() {
        val result = useCase(
            talfigh = talfigh(sumHistoryYears = 3650, historyYears = 10),
            dastmozd = dastmozdWithTwoFullYears(wage = "1000")
        )

        assertEquals(10.0, result.premiumPaymentHistoryYear)
        assertEquals(1000L, result.averageSalaryLastTwoYears)
        assertEquals(3_704_230L, result.eligibleAmountPension)
        assertEquals(10, result.historyYears)
        assertEquals(4, result.historyMonths)
        assertEquals(5, result.historyDays)
        assertEquals("1393", result.chartItems.first().hisYear)
        assertEquals(107, result.chartItems.first().sumYear)
    }

    @Test
    fun `applies basic wage floor when premium years are at least 20`() {
        val result = useCase(
            talfigh = talfigh(sumHistoryYears = 7300, historyYears = 20),
            dastmozd = dastmozdWithTwoFullYears(wage = "1000")
        )

        assertEquals(20.0, result.premiumPaymentHistoryYear)
        assertEquals(1000L, result.averageSalaryLastTwoYears)
        assertEquals(BASIC_WAGE, result.eligibleAmountPension)
    }

    @Test
    fun `includes oldest wage history year in two-year average`() {
        val oldest = yearItem(wage = "2000")
        val newest = yearItem(wage = "1000")
        val result = useCase(
            talfigh = talfigh(sumHistoryYears = 3650, historyYears = 10),
            dastmozd = DastmozdInfoDN(
                list = listOf(oldest, newest),
                total = 2
            )
        )

        assertEquals(1500L, result.averageSalaryLastTwoYears)
    }

    @Test
    fun `returns zeros when history lists are empty`() {
        val result = useCase(
            talfigh = TalfighInfoDN(list = emptyList(), total = 0),
            dastmozd = DastmozdInfoDN(list = emptyList(), total = 0)
        )

        assertEquals(0.0, result.premiumPaymentHistoryYear)
        assertEquals(0L, result.averageSalaryLastTwoYears)
        assertEquals(0L, result.eligibleAmountPension)
        assertEquals(emptyList(), result.chartItems)
    }

    @Test
    fun `normalizes history duration by carrying days into months and months into years`() {
        val result = useCase(
            talfigh = talfigh(
                sumHistoryYears = 3650,
                historyYears = 10,
                historyMonths = 13,
                historyDays = 35,
            ),
            dastmozd = dastmozdWithTwoFullYears(wage = "1000"),
        )

        // 35 days → +1 month + 5 days; 13+1 months → +1 year + 2 months → 11y 2m 5d
        assertEquals(11, result.historyYears)
        assertEquals(2, result.historyMonths)
        assertEquals(5, result.historyDays)
    }

    private fun talfigh(
        sumHistoryYears: Int,
        historyYears: Int,
        historyMonths: Int = 4,
        historyDays: Int = 5,
    ) = TalfighInfoDN(
        list = listOf(
            TalfighInfoItemDN(
                months = emptyList(),
                risuid = "0033261750",
                historyYears = historyYears,
                historyMonths = historyMonths,
                sumYear = 107,
                historyDays = historyDays,
                sumHistoryYears = sumHistoryYears,
                id = 1,
                hisYear = "1393"
            )
        ),
        total = 1
    )

    private fun dastmozdWithTwoFullYears(wage: String) = DastmozdInfoDN(
        list = listOf(yearItem(wage = wage), yearItem(wage = wage)),
        total = 2
    )

    private fun yearItem(wage: String) = DastmozdInfoItemDN(
        wageDetails = List(12) { WageDetailDN(month = "31", wage = wage) },
        hisyear = "1402",
        id = 1,
        risufname = null,
        risubirthdate = null,
        risuidserial2 = null,
        risuidserial1 = null,
        rwshname = null,
        expcitycode = null,
        brhcode = null,
        risuidno = null,
        risudname = null,
        risuid = null,
        risulname = null,
        risunatcode = null,
        brhname = null,
        historytypedesc = null,
        rwshid = null
    )
}
