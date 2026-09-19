package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.impl.AverageWageAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.DastmozdInfosAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.JobHistoryAgentService
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoItemDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetHistoryJobInfosUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FakeHistoryRepository(
    private val wages: List<DastmozdInfoItemDN> = emptyList(),
    private val jobs: List<HistoryJobInfoItemDN> = emptyList(),
) : HistoryRepository {
    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>) = DastmozdInfoDN(list = wages, total = wages.size)
    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        flowOf(HistoryJobInfoDN(list = jobs, total = jobs.size))

    override suspend fun getUserInfos(): UserInfoDN = error("unused")
    override suspend fun getUserRole(): UserRoleDN = UserRoleDN.INSURED
    override fun downloadHistoryReport(type: HistoryCertificateType): Flow<PdfDownloadDN> = flowOf(PdfDownloadDN())
    override suspend fun sendHistoryNotice(): String = ""
    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) = Unit
    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>) = TalfighInfoDN(list = emptyList(), total = 0)
}

/** The wage-history answers, checked against the native services' business rules. */
class DastmozdInfosAgentServiceTest {

    private fun wageService(vararg years: DastmozdInfoItemDN) =
        DastmozdInfosAgentService(GetDastmozdInfosUseCase(FakeHistoryRepository(years.toList())), testStrings)

    private fun averageService(vararg years: DastmozdInfoItemDN) =
        AverageWageAgentService(GetDastmozdInfosUseCase(FakeHistoryRepository(years.toList())), testStrings)

    private fun markdown(result: AgentServiceResult): String =
        assertIs<ChatBubbleContent.Markdown>(assertIs<AgentServiceResult.Success>(result).bubbles.first()).text

    private val y1402 = wageYear("1402", 30 to 1_000L, 31 to 2_000L, 0 to 0L, 25 to 4_000L)
    private val y1403 = wageYear("1403", 30 to 5_000L, 30 to 6_000L, 30 to 7_000L)

    @Test
    fun `monthly history starts with the server title and lists every paid month with days and wage`() = runTest {
        val text = markdown(wageService(y1402).execute(agentParams(AgentActionKey.DASTMOZD_INFOS, message = "سوابق شما")))

        assertTrue(text.startsWith("### سوابق شما"))
        assertTrue("| فروردین | 30 | agent_value_rial:۱٬۰۰۰ |" in text, text)
        assertTrue("| تیر | 25 | agent_value_rial:۴٬۰۰۰ |" in text)
        // A month without days or wage is not a paid month.
        assertFalse("خرداد" in text)
    }

    @Test
    fun `the first and last year of the range are trimmed to the range's months`() = runTest {
        val text = markdown(
            wageService(y1402, y1403).execute(
                agentParams(AgentActionKey.DASTMOZD_INFOS_SALARY, filters = listOf("startDate:14020201", "endDate:14030229"))
            )
        )
        assertFalse("| فروردین | 30 | agent_value_rial:۱٬۰۰۰ |" in text, "1402 starts at اردیبهشت")
        assertTrue("| اردیبهشت | 31 | agent_value_rial:۲٬۰۰۰ |" in text)
        assertTrue("agent_value_rial:۶٬۰۰۰" in text)
        assertFalse("agent_value_rial:۷٬۰۰۰" in text, "1403 ends at اردیبهشت")
    }

    @Test
    fun `last variant shows only the newest paid month inside the range`() = runTest {
        val text = markdown(wageService(y1402, y1403).execute(agentParams(AgentActionKey.DASTMOZD_INFOS_LAST)))
        assertTrue("- **agent_label_month:** خرداد" in text, text)
        assertTrue("agent_value_rial:۷٬۰۰۰" in text)
        assertFalse("agent_value_rial:۱٬۰۰۰" in text)
    }

    @Test
    fun `per year lists days worked per year`() = runTest {
        val text = markdown(wageService(y1402, y1403).execute(agentParams(AgentActionKey.DASTMOZD_INFOS_PER_YEAR)))
        assertTrue("| 1402 | کارگاه 1402 | عادی | شعبه | 86 |" in text, text)
        assertTrue("| 1403 | کارگاه 1403 | عادی | شعبه | 90 |" in text)
    }

    @Test
    fun `sum total adds the days in range and draws them per year`() = runTest {
        val result = assertIs<AgentServiceResult.Success>(
            wageService(y1402, y1403).execute(agentParams(AgentActionKey.DASTMOZD_INFOS_SUM_TOTAL))
        )
        val text = assertIs<ChatBubbleContent.Markdown>(result.bubbles[0]).text
        assertTrue("agent_value_days:176" in text, text)
        val chart = assertIs<ChatBubbleContent.Chart>(result.bubbles[1])
        assertEquals(listOf("1402", "1403"), chart.labels)
        assertEquals(listOf(86.0, 90.0), chart.series.single().values)
    }

    @Test
    fun `a single year gets the total but no chart`() = runTest {
        val result = assertIs<AgentServiceResult.Success>(
            wageService(y1403).execute(agentParams(AgentActionKey.DASTMOZD_INFOS_SUM_TOTAL))
        )
        assertEquals(1, result.bubbles.size)
        assertTrue("agent_value_days:90" in assertIs<ChatBubbleContent.Markdown>(result.bubbles.single()).text)
    }

    @Test
    fun `no history in range answers with the empty message under the title`() = runTest {
        val text = markdown(wageService(y1402).execute(agentParams(AgentActionKey.DASTMOZD_INFOS, filters = listOf("startDate:14100101"))))
        assertEquals("### عنوان\n\nagent_empty_wage_history", text)
    }

    @Test
    fun `average over N years walks newest months back until N x 365 days and divides by N x 12`() = runTest {
        // One year = 365 days: 1403's 90 days are not enough, so 1402's months join, newest first.
        val text = markdown(averageService(y1402, y1403).execute(agentParams(AgentActionKey.AVERAGE_DASTMOZD_INFOS, filters = listOf("averageSalary:1"))))
        // All paid months together are 176 days (< 365): every wage counts. (1000+2000+4000+5000+6000+7000)/12 = 2083.33 → 2084
        assertTrue("agent_value_rial:۲٬۰۸۴" in text, text)
    }

    @Test
    fun `average per date is wages divided by days worked, times 30, inside the month bounds`() = runTest {
        val text = markdown(
            averageService(y1402, y1403).execute(
                agentParams(AgentActionKey.AVERAGE_DASTMOZD_INFOS_PER_DATE, filters = listOf("startDate:14030101", "endDate:14030231"))
            )
        )
        // (5000 + 6000) / 60 days × 30 = 5500
        assertTrue("agent_value_rial:۵٬۵۰۰" in text, text)
    }

    @Test
    fun `last pay is the newest paid month of the newest paid year`() = runTest {
        val text = markdown(averageService(y1402, y1403, wageYear("1404")).execute(agentParams(AgentActionKey.DASTMOZD_INFOS_LAST_PAY)))
        assertTrue("خرداد 1403" in text, text)
        assertTrue("agent_value_rial:۷٬۰۰۰" in text)
    }

    @Test
    fun `job titles from the date filter, falling back to the latest title when none match`() = runTest {
        val jobs = listOf(
            HistoryJobInfoItemDN(risuid = null, rwshName = "الف", brhcode = null, id = 1, jobDesc = "کارگر", startDate = "13950101", rwshId = null),
            HistoryJobInfoItemDN(risuid = null, rwshName = "ب", brhcode = null, id = 2, jobDesc = "سرپرست", startDate = "14010601", rwshId = null),
        )
        val service = JobHistoryAgentService(GetHistoryJobInfosUseCase(FakeHistoryRepository(jobs = jobs)), testStrings)

        val filtered = markdown(service.execute(agentParams(AgentActionKey.HISTORY_JOB_INFOS, filters = listOf("startDate:14000101"))))
        assertTrue("سرپرست" in filtered && "کارگر" !in filtered, filtered)

        val fallback = markdown(service.execute(agentParams(AgentActionKey.HISTORY_JOB_INFOS, filters = listOf("startDate:14050101"))))
        assertTrue("سرپرست" in fallback && "1401/06" in fallback, fallback)

        val last = markdown(service.execute(agentParams(AgentActionKey.HISTORY_JOB_INFOS_LAST)))
        assertTrue("سرپرست" in last && "کارگر" !in last)
    }
}
