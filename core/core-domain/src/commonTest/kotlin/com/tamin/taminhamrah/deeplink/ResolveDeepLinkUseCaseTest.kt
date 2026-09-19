package com.tamin.taminhamrah.deeplink

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.agent.AgentAccessDN
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.repository.FakeAgentAccessStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ResolveDeepLinkUseCaseTest {

    private class FakeFeatureManager(
        private val statuses: Map<FeatureFlag, FeatureStatus> = emptyMap(),
        private val titles: Map<FeatureFlag, String> = emptyMap(),
        private val failing: Boolean = false,
    ) : FeatureManager {
        override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> =
            if (failing) flow { throw IllegalStateException("menu unavailable") }
            // A flag the menu does not mention reads as disabled, as in FeatureStatus.kt.
            else flowOf(statuses[flag] ?: FeatureStatus.Disabled(null))

        override suspend fun isFeatureEnabled(flag: FeatureFlag) = statuses[flag] is FeatureStatus.Enabled
        override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
        override suspend fun getFeatureTitle(flag: FeatureFlag): String? = titles[flag]
    }

    private fun useCase(
        featureManager: FeatureManager,
        access: AgentAccessDN? = null,
    ) = ResolveDeepLinkUseCase(featureManager, FakeAgentAccessStore(access))

    @Test
    fun `enabled flag opens the feature with the menu title and arguments`() = runTest {
        val manager = FakeFeatureManager(
            statuses = mapOf(FeatureFlag.PRESCRIPTION to FeatureStatus.Enabled),
            titles = mapOf(FeatureFlag.PRESCRIPTION to "نسخ الکترونیک"),
        )
        val result = assertIs<DeepLinkResolution.OpenFeature>(
            useCase(manager)("@prescription_detail?ID=7", DeepLinkSource.AGENT)
        )
        assertEquals(DeepLinkKey.PRESCRIPTION_DETAIL, result.key)
        assertEquals("نسخ الکترونیک", result.title)
        assertEquals(mapOf("ID" to "7"), result.args)
        assertEquals(null, result.notice)
    }

    @Test
    fun `disabled and temporarily disabled flags block with the server message`() = runTest {
        val manager = FakeFeatureManager(
            statuses = mapOf(
                FeatureFlag.WEDDING_PRESENT to FeatureStatus.Disabled("غیرفعال"),
                FeatureFlag.PAY_ROLL to FeatureStatus.TemporaryDisabled("در حال بروزرسانی"),
            )
        )
        val blocked = assertIs<DeepLinkResolution.Blocked>(useCase(manager)("@wedding_present", DeepLinkSource.SYSTEM))
        assertEquals("غیرفعال", blocked.message)
        val temporary = assertIs<DeepLinkResolution.Blocked>(useCase(manager)("mytamin://feature/pensioner_pay_roll", DeepLinkSource.SYSTEM))
        assertEquals("در حال بروزرسانی", temporary.message)
    }

    @Test
    fun `a flag missing from the menu blocks`() = runTest {
        val result = useCase(FakeFeatureManager())("@wedding_present", DeepLinkSource.AGENT)
        assertEquals(DeepLinkResolution.Blocked(FeatureFlag.WEDDING_PRESENT, null), result)
    }

    @Test
    fun `an unreadable menu blocks rather than lets the link through`() = runTest {
        val result = useCase(FakeFeatureManager(failing = true))("@wedding_present", DeepLinkSource.AGENT)
        assertIs<DeepLinkResolution.Blocked>(result)
    }

    @Test
    fun `enabled with error still opens and carries the message`() = runTest {
        val manager = FakeFeatureManager(mapOf(FeatureFlag.CONTRACTS to FeatureStatus.EnabledWithError("اختلال")))
        val result = assertIs<DeepLinkResolution.OpenFeature>(useCase(manager)("@contract_list", DeepLinkSource.AGENT))
        assertEquals("اختلال", result.notice)
    }

    @Test
    fun `web view service opens its url instead of a screen`() = runTest {
        val manager = FakeFeatureManager(mapOf(FeatureFlag.LAWS to FeatureStatus.WebView("https://law.tamin.ir/")))
        assertEquals(
            DeepLinkResolution.OpenWeb("https://law.tamin.ir/"),
            useCase(manager)("@laws", DeepLinkSource.AGENT),
        )
    }

    @Test
    fun `assistant needs both its flag and the server chat permission`() = runTest {
        val manager = FakeFeatureManager(mapOf(FeatureFlag.AGENT to FeatureStatus.Enabled))
        val refused = AgentAccessDN(canStartChat = false, canSendVoice = false, chatToken = null, errorMessage = "مجاز نیستید")
        val allowed = refused.copy(canStartChat = true, errorMessage = null)

        assertEquals(
            DeepLinkResolution.Blocked(FeatureFlag.AGENT, "مجاز نیستید"),
            useCase(manager, refused)("tamin://feature/AGENT", DeepLinkSource.APP_CONTENT),
        )
        assertIs<DeepLinkResolution.Blocked>(useCase(manager, access = null)("@agent", DeepLinkSource.SYSTEM))
        assertIs<DeepLinkResolution.OpenFeature>(useCase(manager, allowed)("@agent", DeepLinkSource.SYSTEM))
    }

    @Test
    fun `disabled assistant flag blocks even with chat permission`() = runTest {
        val allowed = AgentAccessDN(canStartChat = true, canSendVoice = true, chatToken = "t", errorMessage = null)
        val result = useCase(FakeFeatureManager(), allowed)("@agent", DeepLinkSource.SYSTEM)
        assertIs<DeepLinkResolution.Blocked>(result)
    }

    @Test
    fun `invalid links and prompts pass through without touching flags`() = runTest {
        val manager = FakeFeatureManager(failing = true)
        assertEquals(DeepLinkResolution.Invalid, useCase(manager)("@nope", DeepLinkSource.AGENT))
        assertEquals(DeepLinkResolution.SendPrompt("hi"), useCase(manager)("agent://prompt?text=hi", DeepLinkSource.AGENT))
    }
}
