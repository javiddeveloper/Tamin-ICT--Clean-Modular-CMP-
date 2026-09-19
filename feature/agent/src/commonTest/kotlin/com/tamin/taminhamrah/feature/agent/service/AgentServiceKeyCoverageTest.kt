package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.deeplink.DeepLinkKey
import com.tamin.taminhamrah.feature.agent.service.base.AgentMarkdownBuilder
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.dateRange
import com.tamin.taminhamrah.feature.agent.service.base.getFilters
import com.tamin.taminhamrah.feature.agent.service.impl.DeepLinkAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.DependentsAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.GeneralResponseAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.LawAgentService
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownBlock
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownInlineParser
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownParser
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Registry wiring, the simple services, and the markdown helpers every service builds on. */
class AgentServiceKeyCoverageTest {

    private fun text(result: AgentServiceResult): String =
        assertIs<ChatBubbleContent.Markdown>(assertIs<AgentServiceResult.Success>(result).bubbles.single()).text

    private class TitledFeatureManager(private val titles: Map<FeatureFlag, String>) : com.tamin.taminhamrah.feature.FeatureManager {
        override fun getFeatureStatus(flag: FeatureFlag): Flow<com.tamin.taminhamrah.model.common.FeatureStatus> =
            flowOf(com.tamin.taminhamrah.model.common.FeatureStatus.Enabled)
        override suspend fun isFeatureEnabled(flag: FeatureFlag) = true
        override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
        override suspend fun getFeatureTitle(flag: FeatureFlag): String? = titles[flag]
    }

    @Test
    fun `no action key is claimed by more than one service`() {
        val allKeyLists: List<List<AgentActionKey>> = listOf(
            GeneralResponseAgentService(testStrings).supportedKeys,
            DeepLinkAgentService(TitledFeatureManager(emptyMap())).supportedKeys,
            LAW_KEYS, DASTMOZD_KEYS, AVERAGE_WAGE_KEYS, PROFILE_KEYS, DEPENDENT_KEYS, PENSION_INQUIRY_KEYS,
            PAYROLL_KEYS, EDICT_KEYS, BOOKLET_KEYS, JOB_HISTORY_KEYS, TRACKING_KEYS, PRESCRIPTION_KEYS,
            TREATMENT_COST_KEYS, APPOINTMENT_KEYS, ELIGIBLE_KEYS,
        )
        val duplicates = allKeyLists.flatten().groupingBy { it }.eachCount().filterValues { it > 1 }.keys
        assertTrue(duplicates.isEmpty(), "These action keys are handled by more than one service: $duplicates")
    }

    @Test
    fun `the native workers_payment spelling and the old one both resolve`() {
        assertEquals(AgentActionKey.WORKER_PAYMENT, AgentActionKey.fromString("workers_payment"))
        assertEquals(AgentActionKey.WORKER_PAYMENT, AgentActionKey.fromString(" worker_payment"))
        assertEquals(AgentActionKey.UNKNOWN, AgentActionKey.fromString("nope"))
    }

    @Test
    fun `a screen key answers with the title and one button labelled with the menu name`() = runTest {
        val service = DeepLinkAgentService(TitledFeatureManager(mapOf(FeatureFlag.DISABILITY_PENSION to "مستمری از کارافتادگی")))
        assertIs<DeepLinkAgentService>(AgentServiceRegistry(listOf(service)).get(AgentActionKey.DISABILITY_PENSION))

        val markdown = text(service.execute(agentParams(AgentActionKey.DISABILITY_PENSION, message = "درخواست مستمری")))
        assertEquals("### درخواست مستمری\n\n- [مستمری از کارافتادگی](@${DeepLinkKey.DISABILITY_PENSION.key})", markdown)
        val actions = assertIs<MarkdownBlock.Actions>(MarkdownParser.parse(markdown).last())
        assertEquals("@disability_pension", actions.links.single().url)
    }

    @Test
    fun `illness keys open the illness screens`() = runTest {
        val service = DeepLinkAgentService(TitledFeatureManager(mapOf(FeatureFlag.CALCULATE_WAGE_ILL_DAYS to "محاسبه")))
        assertTrue("@calculate_wage_ill_days" in text(service.execute(agentParams(AgentActionKey.CALCULATE_ILLNESS))))
    }

    @Test
    fun `general response shows the server markdown as sent, without a greeting`() = runTest {
        val result = GeneralResponseAgentService(testStrings).execute(agentParams(AgentActionKey.GENERAL_RESPONSE, message = "**سلام**"))
        assertEquals("**سلام**", text(result))
    }

    @Test
    fun `general response without text falls back, unless the entity carries items`() = runTest {
        assertEquals("agent_not_understood", text(GeneralResponseAgentService(testStrings).execute(agentParams(AgentActionKey.MESSAGE, message = null))))
        val withItems = GeneralResponseAgentService(testStrings).execute(
            agentParams(AgentActionKey.MESSAGE, message = null, rawData = Json.parseToJsonElement("""[{"item_type":"prompt_item","prompt":"x"}]"""))
        )
        assertTrue(assertIs<AgentServiceResult.Success>(withItems).bubbles.isEmpty())
    }

    @Test
    fun `law items become titled sections, and a missing law says so`() = runTest {
        val service = LawAgentService(Json { ignoreUnknownKeys = true }, testStrings)
        val laws = Json.parseToJsonElement("""[{"item_type":"law_item","name":"ماده ۷۶","content":"متن ماده"},{"item_type":"other","name":"x"}]""")
        val markdown = text(service.execute(agentParams(AgentActionKey.LAW, rawData = laws)))
        assertTrue("#### ماده ۷۶\n\nمتن ماده" in markdown, markdown)
        assertTrue("x" !in markdown.removePrefix("### عنوان"))

        assertTrue("agent_empty_law" in text(service.execute(agentParams(AgentActionKey.LAW))))
    }

    @Test
    fun `dependents filter by relation code, by gender, and by a question about children`() = runTest {
        val people = SubdominantDN(
            list = listOf(
                SubdominantItemDN(firstName = "پسر", tendencyCode = "101", genderCode = "01"),
                SubdominantItemDN(firstName = "مادر", tendencyCode = "106", genderCode = "02"),
                SubdominantItemDN(firstName = "پدر", tendencyCode = "106", genderCode = "01"),
                SubdominantItemDN(firstName = "همسر", tendencyCode = "100", genderCode = "02"),
            ),
            total = "4",
        )
        val service = DependentsAgentService(SubdominantUseCase(FakeDependentsUserRepository(people)), testStrings)

        val mother = text(service.execute(agentParams(AgentActionKey.GET_DEPENDENT, filters = listOf("tendencyCode:106", "genderCode:02"))))
        assertTrue("مادر" in mother && "پدر" !in mother, mother)

        val parents = text(service.execute(agentParams(AgentActionKey.GET_DEPENDENT, filters = listOf("tendencyCode:110"))))
        assertTrue("مادر" in parents && "پدر" in parents && "همسر" !in parents, parents)

        val children = text(service.execute(agentParams(AgentActionKey.GET_DEPENDENT, message = "فرزندان من")))
        assertTrue("پسر" in children && "همسر" !in children, children)

        val nobody = text(service.execute(agentParams(AgentActionKey.GET_DEPENDENT, filters = listOf("tendencyCode:123"))))
        assertTrue("agent_empty_dependents" in nobody)
    }

    @Test
    fun `markdown builder escapes values so data cannot become formatting`() {
        val markdown = agentMarkdown {
            heading("  ")
            fields(listOf("کد" to "a*b*c", "نام" to null, "file_name" to "x"))
            table(listOf("ستون"), listOf(listOf("a|b"), listOf(null)))
        }
        assertEquals("- **کد:** a\\*b\\*c\n- **نام:** -\n- **file_name:** x\n\n| ستون |\n|---|\n| a\\|b |\n| - |", markdown)
        val field = assertIs<MarkdownBlock.ListItem>(MarkdownParser.parse(markdown).first())
        assertEquals("کد: a*b*c", MarkdownInlineParser.parse(field.text).joinToString("") { it.text })
        assertTrue(AgentMarkdownBuilder().isEmpty)
    }

    @Test
    fun `date filters read year, month and day and ignore malformed values`() {
        val range = agentParams(AgentActionKey.FISH, filters = listOf("startDate:14020501", "endDate:1403")).dateRange()
        assertEquals(1402, range.start?.year)
        assertEquals(140205, range.start?.yearMonth)
        assertNull(range.end?.month)
        assertEquals(5..12, range.monthsOf(1402))
        assertEquals(1..12, range.monthsOf(1403))
        assertTrue(agentParams(AgentActionKey.FISH, filters = listOf("startDate:14")).dateRange().isEmpty)
    }

    @Test
    fun `getFilters parses the colon separated filter array`() {
        val payload = Json.parseToJsonElement("""{"filter":["startDate:14020101","endDate:14031229"],"year":"1403"}""")
        val filters = AgentServiceParams(payload, null, null, AgentSessionContext(), AgentActionKey.DASTMOZD_INFOS).getFilters()
        assertEquals("14020101", filters["startDate"])
        assertEquals("14031229", filters["endDate"])
        assertEquals("1403", filters["year"])
    }

    private companion object {
        val LAW_KEYS = listOf(AgentActionKey.LAW)
        val DASTMOZD_KEYS = listOf(
            AgentActionKey.DASTMOZD_INFOS, AgentActionKey.DASTMOZD_INFOS_LAST, AgentActionKey.DASTMOZD_INFOS_PER_YEAR,
            AgentActionKey.DASTMOZD_INFOS_SALARY, AgentActionKey.DASTMOZD_INFOS_SUM_TOTAL,
        )
        val AVERAGE_WAGE_KEYS = listOf(
            AgentActionKey.AVERAGE_DASTMOZD_INFOS, AgentActionKey.AVERAGE_DASTMOZD_INFOS_PER_DATE, AgentActionKey.DASTMOZD_INFOS_LAST_PAY,
        )
        val PROFILE_KEYS = listOf(AgentActionKey.PROFILE_INFO)
        val DEPENDENT_KEYS = listOf(AgentActionKey.GET_DEPENDENT)
        val PENSION_INQUIRY_KEYS = listOf(AgentActionKey.PENSION_INQUIRY_ALL, AgentActionKey.PENSION_INQUIRY_LAST)
        val PAYROLL_KEYS = listOf(AgentActionKey.FISH, AgentActionKey.FISH_LAST)
        val EDICT_KEYS = listOf(AgentActionKey.HOKM, AgentActionKey.HOKM_LAST)
        val BOOKLET_KEYS = listOf(AgentActionKey.BOOKLET)
        val JOB_HISTORY_KEYS = listOf(AgentActionKey.HISTORY_JOB_INFOS, AgentActionKey.HISTORY_JOB_INFOS_LAST)
        val TRACKING_KEYS = listOf(AgentActionKey.TRACKING_CODE, AgentActionKey.LAST_TRACKING_CODE)
        val PRESCRIPTION_KEYS = listOf(AgentActionKey.PATIENT_HISTORY, AgentActionKey.PATIENT_HISTORY_LAST)
        val TREATMENT_COST_KEYS = listOf(AgentActionKey.TREATMENT_COST)
        val APPOINTMENT_KEYS = listOf(AgentActionKey.APPOINTMENT)
        val ELIGIBLE_KEYS = listOf(AgentActionKey.ELIGIBLE_AMOUNT_PENSION)
    }
}
