package com.tamin.taminhamrah.model.agent

import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Regression guard for a real gap: these actions previously fell through `toFeatureFlag()`'s
 * `else -> null`, so the assistant could read/edit a dependent, a bank account or a mobile number
 * even with that service switched off in profile — `AgentActionDispatcher` only blocks an action
 * whose key maps to a flag at all.
 */
class AgentActionKeyFeatureFlagTest {

    @Test
    fun dependentActionsAreGatedByDependents() {
        listOf(
            AgentActionKey.GET_DEPENDENT,
            AgentActionKey.ADD_DEPENDENT,
            AgentActionKey.DEPENDENT_CANCELLATION,
            AgentActionKey.DEPENDENT_CANCELLATION_GET,
            AgentActionKey.DEPENDENT_CANCELLATION_CONFIRM,
            AgentActionKey.DEPENDENT_CANCELLATION_SUBMIT,
            AgentActionKey.DEPENDENT_CANCELLATION_CANCEL,
        ).forEach { key -> assertEquals(FeatureFlag.DEPENDENTS, key.toFeatureFlag(), "expected $key to be gated") }
    }

    @Test
    fun bankAccountActionsAreGatedByBankAccountList() {
        listOf(
            AgentActionKey.EDIT_BANK_ACCOUNT_NUMBER,
            AgentActionKey.EDIT_BANK_ACCOUNT_GET,
            AgentActionKey.EDIT_BANK_ACCOUNT_SUBMIT,
            AgentActionKey.EDIT_BANK_ACCOUNT_CANCEL,
        ).forEach { key -> assertEquals(FeatureFlag.BANK_ACCOUNT_LIST, key.toFeatureFlag(), "expected $key to be gated") }
    }

    @Test
    fun changeMobileActionsAreGatedByTheClientOnlyFlag() {
        listOf(
            AgentActionKey.EDIT_PHONE_NUMBER,
            AgentActionKey.EDIT_PHONE_NUMBER_GET,
            AgentActionKey.EDIT_PHONE_NUMBER_SEND_OTP,
            AgentActionKey.EDIT_PHONE_NUMBER_VERIFY_OTP,
            AgentActionKey.EDIT_PHONE_NUMBER_CANCEL,
        ).forEach { key -> assertEquals(FeatureFlag.CHANGE_MOBILE, key.toFeatureFlag(), "expected $key to be gated") }
    }

    @Test
    fun profileInfoActionsAreGatedByIdentityInfo() {
        listOf(
            AgentActionKey.PROFILE_INFO,
            AgentActionKey.EDIT_PROFILE,
            AgentActionKey.EDIT_PROFILE_INFO_SUBMIT,
            AgentActionKey.EDIT_PROFILE_INFO_CANCEL,
        ).forEach { key -> assertEquals(FeatureFlag.IDENTITY_INFO, key.toFeatureFlag(), "expected $key to be gated") }
    }

    @Test
    fun generalMessagesStillNeedNoFlag() {
        assertNull(AgentActionKey.GENERAL_RESPONSE.toFeatureFlag())
        assertNull(AgentActionKey.MESSAGE.toFeatureFlag())
        assertNull(AgentActionKey.UNKNOWN.toFeatureFlag())
    }
}
