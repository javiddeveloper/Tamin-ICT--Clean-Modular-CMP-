package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.impl.EdictAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.PayRollAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.PensionInquiryAgentService
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.pension.EdictInfoDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDetailDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PensionAgentServicesTest {

    private fun markdown(result: AgentServiceResult): String =
        assertIs<ChatBubbleContent.Markdown>(assertIs<AgentServiceResult.Success>(result).bubbles.single()).text

    private fun inquiryRow(status: String, amount: Int) = PensionInquiryDN(
        branchCode = "17", insuranceNumber = "123", pensionerRisUid = "9", pensionerType = "101",
        paymentDate = "1403/01/30", pensionerBaseDate = "1390/01/01", fullName = "علی رضایی",
        statusDesc = status, sexDesc = "مرد", branchName = "شعبه ۱۷", pensionEndDate = null,
        nationalId = "001", paymentAmount = amount, pensionerTypeDesc = "بازنشستگی",
    )

    // ── Pension inquiry ─────────────────────────────────────────────────────

    @Test
    fun `a first row with status 00 means the user is not a pensioner`() = runTest {
        val repo = FakeAgentPensionRepository(inquiry = listOf(inquiryRow("00", 0)))
        val text = markdown(PensionInquiryAgentService(GetPensionInquiryUseCase(repo), testStrings).execute(agentParams(AgentActionKey.PENSION_INQUIRY_ALL)))
        assertTrue("agent_not_pensioner" in text)
        assertFalse("علی رضایی" in text)
    }

    @Test
    fun `all shows every row, last shows only the last row's paid amount`() = runTest {
        val repo = FakeAgentPensionRepository(inquiry = listOf(inquiryRow("01", 1000), inquiryRow("01", 2000)))
        val service = PensionInquiryAgentService(GetPensionInquiryUseCase(repo), testStrings)

        val all = markdown(service.execute(agentParams(AgentActionKey.PENSION_INQUIRY_ALL)))
        assertTrue("agent_value_rial:۱٬۰۰۰" in all && "agent_value_rial:۲٬۰۰۰" in all && "علی رضایی" in all, all)

        val last = markdown(service.execute(agentParams(AgentActionKey.PENSION_INQUIRY_LAST)))
        assertEquals("### عنوان\n\n- **agent_label_payment_amount:** agent_value_rial:۲٬۰۰۰", last)
    }

    @Test
    fun `no inquiry rows answers with the empty message`() = runTest {
        val text = markdown(PensionInquiryAgentService(GetPensionInquiryUseCase(FakeAgentPensionRepository()), testStrings).execute(agentParams(AgentActionKey.PENSION_INQUIRY_ALL)))
        assertTrue("agent_empty_pension_inquiry" in text)
    }

    // ── Payslip ─────────────────────────────────────────────────────────────

    private fun payslip(repo: FakeAgentPensionRepository) =
        PayRollAgentService(GetPensionerIdUseCase(repo), GetPensionerPayRollUseCase(repo), testStrings)

    private val slip = listOf(
        PayRollDN(clpType = "1", tprDesc = "مستمری", sumAmount = 90_000, hisYear = "30", hisMon = "2"),
        PayRollDN(clpType = "1", tprDesc = "صفر", sumAmount = 0),
        PayRollDN(clpType = "2", tprDesc = "بیمه درمان", sumAmount = -5_000),
        PayRollDN(clpType = "3", tprDesc = "وام", sumAmount = 1_000),
    )

    @Test
    fun `a non pensioner is told so without asking for payslips`() = runTest {
        val repo = FakeAgentPensionRepository(pensionerId = null)
        val text = markdown(payslip(repo).execute(agentParams(AgentActionKey.FISH_LAST)))
        assertTrue("agent_not_pensioner_access" in text)
        assertTrue(repo.payRollDates.isEmpty())
    }

    @Test
    fun `last payslip steps back month by month from today until one exists`() = runTest {
        val (year, month, _) = PersianDateFormatter.today()
        val previous = if (month == 1) "${year - 1}12" else "$year${(month - 1).toString().padStart(2, '0')}"
        val repo = FakeAgentPensionRepository(payRolls = mapOf(previous to slip))

        val text = markdown(payslip(repo).execute(agentParams(AgentActionKey.FISH_LAST)))

        assertEquals(2, repo.payRollDates.size, "the current month first, then the one before")
        assertTrue("agent_value_history_length:30,2" in text, text)
        assertTrue("- **مستمری:** agent_value_rial:۹۰٬۰۰۰" in text)
        assertFalse("صفر" in text, "a zero payment is not shown")
        assertTrue("- **بیمه درمان:** -agent_value_rial:۵٬۰۰۰" in text)
        assertTrue("#### agent_label_loans" in text)
    }

    @Test
    fun `the search stops after twelve months`() = runTest {
        val repo = FakeAgentPensionRepository()
        val text = markdown(payslip(repo).execute(agentParams(AgentActionKey.FISH_LAST)))
        assertEquals(12, repo.payRollDates.size)
        assertTrue("agent_empty_payroll" in text)
    }

    @Test
    fun `a payslip for a given month asks for that month only`() = runTest {
        val repo = FakeAgentPensionRepository()
        payslip(repo).execute(agentParams(AgentActionKey.FISH, filters = listOf("startDate:14020501")))
        assertEquals(listOf("140205"), repo.payRollDates)
    }

    // ── Edict ───────────────────────────────────────────────────────────────

    private fun edict(repo: FakeAgentPensionRepository) =
        EdictAgentService(GetPensionerIdUseCase(repo), GetEdictPensionerUseCase(repo), testStrings)

    private fun edictInfo() = EdictInfoDN(
        pensionerId = null, nationalCode = null, firstName = null, lastName = null, fatherName = null,
        birthDate = null, idNumber = null, gender = null, insuranceType = null, pensionStartDate = "1390/01/01",
        originalHistoryYear = "30", originalHistoryMonth = null, originalHistoryDay = null, additionalYear = "2",
        additionalMonth = null, additionalDay = null, basisImplementation = "بازنشستگی", pensionBeforeIncrease = null,
        pensionAfterIncrease = null, edictDescription = null, id = null, firstStageTotalPensionAndProportional = null,
        totalPensionBeforeIncrease = "120000", firstStageTotalProportional = null, totalAmount = null,
        payableMonthly = null, lettersPayableMonthly = null,
    )

    @Test
    fun `edict last asks for the first of farvardin this year and lists the native rows`() = runTest {
        val repo = FakeAgentPensionRepository(
            edict = EdictPensionerDN(
                edictInfo = edictInfo(),
                detail = listOf(
                    EdictPensionerDetailDN(fieldDesc = "مبلغ", fieldValue = "150000", index = "1", packageName = "تاریخ اجرا : 01/01/1403"),
                    EdictPensionerDetailDN(fieldDesc = "بدون تاریخ", fieldValue = "1", index = "2", packageName = "سایر"),
                ),
            )
        )
        val text = markdown(edict(repo).execute(agentParams(AgentActionKey.HOKM_LAST)))

        assertEquals(listOf("${PersianDateFormatter.currentJalaliYear()}0101"), repo.edictDates)
        assertTrue("- **agent_label_basis_implementation:** بازنشستگی" in text, text)
        assertTrue("agent_value_rial:۱۲۰٬۰۰۰" in text)
        assertTrue("- **agent_label_execution_date:** 1403/01/01" in text)
        assertTrue("- **مبلغ:** agent_value_rial:۱۵۰٬۰۰۰" in text)
        assertFalse("بدون تاریخ" in text)
    }

    @Test
    fun `edict for a date sends that date, and no edict information means no edict`() = runTest {
        val repo = FakeAgentPensionRepository(edict = EdictPensionerDN())
        val text = markdown(edict(repo).execute(agentParams(AgentActionKey.HOKM, filters = listOf("startDate:14020701"))))
        assertEquals(listOf("14020701"), repo.edictDates)
        assertTrue("agent_empty_edict" in text)
    }
}
