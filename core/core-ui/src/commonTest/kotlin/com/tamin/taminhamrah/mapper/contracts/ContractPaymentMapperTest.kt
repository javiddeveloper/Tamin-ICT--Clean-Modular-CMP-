package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contracts.ContractDebitDN
import com.tamin.taminhamrah.model.contracts.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contracts.PaymentCalculationRowDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ContractPaymentMapperTest {

    @Test
    fun `debit toPresentation keeps raw money strings and flags past debt`() {
        val pr = ContractDebitDN(
            total = 53_866_782L,
            insurancePremiums = 50_000_000L,
            previousDebit = 1_250_000L,
            startDate = null,
            endDate = null,
            payPremiumDate = "14051001",
            infoMessage = "   ",
        ).toPresentation()

        assertEquals("53866782", pr.payableAmount)
        assertEquals("50000000", pr.periodPremiumAmount)
        assertEquals("1250000", pr.pastDebtAmount)
        assertTrue(pr.hasPastDebt)
        assertTrue(pr.deadlineLabel.contains("/"))
        assertNull(pr.infoMessage)
    }

    @Test
    fun `last payment toPresentation reports no history for a null timestamp`() {
        val pr = ContractLastPaymentDN(
            lastPaymentTimestamp = null,
            checkReloLap = "1",
            medicalResultResend = null,
        ).toPresentation()

        assertEquals("", pr.paidUntilLabel)
        assertFalse(pr.hasHistory)
        assertNull(pr.warningMessage)
    }

    @Test
    fun `last payment toPresentation surfaces a non-ok chekReloLap as a warning`() {
        val pr = ContractLastPaymentDN(
            lastPaymentTimestamp = 1_700_000_000_000L,
            checkReloLap = "قرارداد شما نیاز به بازبینی دارد",
            medicalResultResend = null,
        ).toPresentation()

        assertTrue(pr.hasHistory)
        assertEquals("قرارداد شما نیاز به بازبینی دارد", pr.warningMessage)
    }

    @Test
    fun `toMonthPresentation groups both lines of a month and derives base wage, rate and net`() {
        val months = listOf(
            PaymentCalculationRowDN("1405", "06", "23", "حق بيمه", 6_650_220.0, 45_886_518.0),
            PaymentCalculationRowDN("1405", "06", "23", "کمک دولت", 6_650_220.0, -4_588_652.0),
        ).toMonthPresentation()

        assertEquals(1, months.size)
        val month = months.first()
        assertTrue(month.monthTitle.startsWith("شهریور"))
        assertEquals(2, month.lines.size)
        assertEquals(45_886_518L, month.lines[0].amountRaw)
        assertEquals(-4_588_652L, month.lines[1].amountRaw)
        assertTrue(month.lines[1].isDeduction)
        assertEquals(41_297_866L, month.netAmountRaw)      // 45,886,518 − 4,588,652
        assertEquals(152_955_060L, month.baseWageRaw)      // primary wage × days
        assertEquals(30, month.ratePercent)                // primary amount ÷ base × 100
    }

    @Test
    fun `toMonthPresentation keeps one card per month`() {
        val months = listOf(
            PaymentCalculationRowDN("1405", "07", "30", "حق بيمه", 6_650_220.0, 53_866_782.0),
            PaymentCalculationRowDN("1405", "08", "30", "حق بيمه", 6_650_220.0, 53_866_782.0),
        ).toMonthPresentation()

        assertEquals(2, months.size)
        assertTrue(months[0].monthTitle.startsWith("مهر"))
        assertTrue(months[1].monthTitle.startsWith("آبان"))
    }
}
