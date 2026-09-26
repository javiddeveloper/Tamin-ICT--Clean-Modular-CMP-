package com.tamin.taminhamrah.core.datastore

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.model.agent.AgentMockMode
import com.tamin.taminhamrah.model.payment.PaymentMockMode
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREF_KEY_PREFIX = "dev_opt_base_url_"
private const val PREF_KEY_PAYMENT_MOCK_MODE = "dev_opt_payment_mock_mode"
private const val PREF_KEY_AGENT_MOCK_MODE = "dev_opt_agent_mock_mode"

class DeveloperOptionsRepositoryImpl(
    private val settings: Settings,
    private val isDebug: Boolean = AppConfig.isDebug,
) : DeveloperOptionsRepository {

    private val _overrides = MutableStateFlow(loadOverrides())
    private val _paymentMockMode = MutableStateFlow(loadPaymentMockMode())
    private val _agentMockMode = MutableStateFlow(loadAgentMockMode())

    private fun loadOverrides(): Map<BaseUrlKey, String> =
        BaseUrlKey.entries.mapNotNull { key ->
            settings.getStringOrNull(prefKey(key))?.let { key to it }
        }.toMap()

    private fun prefKey(key: BaseUrlKey) = "$PREF_KEY_PREFIX${key.name}"

    /**
     * Overrides only ever apply in debug builds. A release build must never send OAuth
     * tokens or API traffic to a stored dev_opt_base_url_* value, whether it was set by a
     * previous debug install sharing the same app storage or written some other way.
     */
    override fun getEffectiveBaseUrl(key: BaseUrlKey): String =
        if (isDebug) _overrides.value[key] ?: key.defaultValue else key.defaultValue

    override fun observeOverrides(): Flow<Map<BaseUrlKey, String>> = _overrides.asStateFlow()

    override fun setOverride(key: BaseUrlKey, url: String) {
        val normalized = url.trim().let { if (it.endsWith("/")) it else "$it/" }
        settings.putString(prefKey(key), normalized)
        _overrides.value += (key to normalized)
    }

    override fun clearOverride(key: BaseUrlKey) {
        settings.remove(prefKey(key))
        _overrides.value -= key
    }

    private fun loadPaymentMockMode(): PaymentMockMode =
        settings.getStringOrNull(PREF_KEY_PAYMENT_MOCK_MODE)
            ?.let { stored -> PaymentMockMode.entries.firstOrNull { it.name == stored } }
            ?: PaymentMockMode.DISABLED

    /**
     * Guarded exactly like [getEffectiveBaseUrl]: a release build must never answer a payment call
     * from a fake, whatever a previous debug install left in shared app storage.
     */
    override fun getPaymentMockMode(): PaymentMockMode =
        if (isDebug) _paymentMockMode.value else PaymentMockMode.DISABLED

    override fun observePaymentMockMode(): Flow<PaymentMockMode> = _paymentMockMode.asStateFlow()

    override fun setPaymentMockMode(mode: PaymentMockMode) {
        settings.putString(PREF_KEY_PAYMENT_MOCK_MODE, mode.name)
        _paymentMockMode.value = mode
    }

    private fun loadAgentMockMode(): AgentMockMode =
        settings.getStringOrNull(PREF_KEY_AGENT_MOCK_MODE)
            ?.let { stored -> AgentMockMode.entries.firstOrNull { it.name == stored } }
            ?: AgentMockMode.DISABLED

    /** Same guard as [getPaymentMockMode]: a release build never answers the assistant from a fake. */
    override fun getAgentMockMode(): AgentMockMode =
        if (isDebug) _agentMockMode.value else AgentMockMode.DISABLED

    override fun observeAgentMockMode(): Flow<AgentMockMode> = _agentMockMode.asStateFlow()

    override fun setAgentMockMode(mode: AgentMockMode) {
        settings.putString(PREF_KEY_AGENT_MOCK_MODE, mode.name)
        _agentMockMode.value = mode
    }
}
