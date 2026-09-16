package com.tamin.taminhamrah.deeplink

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.repository.AgentAccessStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

/** What the app should do with a link once its feature flag has been read. */
sealed interface DeepLinkResolution {

    /**
     * Open the feature. [title] is the menu's name for it, for callers that label the target.
     * [notice] is set for a service the menu marks as working with errors: the message is shown,
     * and the service still opens — the same as tapping it on the home screen.
     */
    data class OpenFeature(
        val key: DeepLinkKey,
        val title: String?,
        val args: Map<String, String>,
        val notice: String? = null,
    ) : DeepLinkResolution

    data class OpenWeb(val url: String) : DeepLinkResolution

    data class SendPrompt(val text: String) : DeepLinkResolution

    /** The feature exists but may not be entered. [message] is the server's reason, if it gave one. */
    data class Blocked(val flag: FeatureFlag, val message: String?) : DeepLinkResolution

    data object Invalid : DeepLinkResolution
}

/**
 * The single gate every deep link passes through, whatever delivered it.
 *
 * The rule is the one the home screen applies to a menu tap, so a link can never reach a service
 * a tap could not: a disabled or temporarily disabled flag blocks, a web-view service opens its
 * url, a service missing from the menu is treated as disabled, and a menu that cannot be read
 * blocks rather than lets through. The assistant additionally needs the server's chat permission,
 * because its flag alone does not say whether this user may chat.
 */
class ResolveDeepLinkUseCase(
    private val featureManager: FeatureManager,
    private val agentAccessStore: AgentAccessStore,
) {
    suspend operator fun invoke(raw: String?, source: DeepLinkSource): DeepLinkResolution =
        when (val parsed = DeepLinkParser.parse(raw, source)) {
            is ParsedDeepLink.Feature -> resolveFeature(parsed)
            is ParsedDeepLink.Web -> DeepLinkResolution.OpenWeb(parsed.url)
            is ParsedDeepLink.Prompt -> DeepLinkResolution.SendPrompt(parsed.text)
            ParsedDeepLink.Invalid -> DeepLinkResolution.Invalid
        }

    private suspend fun resolveFeature(link: ParsedDeepLink.Feature): DeepLinkResolution {
        val flag = link.key.flag
        val status = try {
            featureManager.getFeatureStatus(flag).first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return DeepLinkResolution.Blocked(flag, null)
        }

        return when (status) {
            is FeatureStatus.Disabled -> DeepLinkResolution.Blocked(flag, status.message)
            is FeatureStatus.TemporaryDisabled -> DeepLinkResolution.Blocked(flag, status.message)
            is FeatureStatus.WebView -> DeepLinkResolution.OpenWeb(status.url)
            is FeatureStatus.Enabled -> openOrBlockAgent(link, notice = null)
            is FeatureStatus.EnabledWithError -> openOrBlockAgent(link, notice = status.message)
        }
    }

    private suspend fun openOrBlockAgent(link: ParsedDeepLink.Feature, notice: String?): DeepLinkResolution {
        if (link.key.flag == FeatureFlag.AGENT) {
            val access = agentAccessStore.access.value
            if (access?.canStartChat != true) {
                return DeepLinkResolution.Blocked(FeatureFlag.AGENT, access?.errorMessage)
            }
        }
        return DeepLinkResolution.OpenFeature(
            key = link.key,
            title = titleOf(link.key.flag),
            args = link.args,
            notice = notice,
        )
    }

    private suspend fun titleOf(flag: FeatureFlag): String? = try {
        featureManager.getFeatureTitle(flag)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
}
