package com.tamin.taminhamrah.feature.agent

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.agent.ui.AgentScreen
import kotlinx.serialization.Serializable

/**
 * Dedicated route for the Agent module.
 */
@Serializable
data object AgentRoute

/**
 * Helper method to navigate to the Agent screen.
 */
fun NavController.navigateToAgent() {
    navigate(AgentRoute)
}

/**
 * Stable ids for screens the assistant can hand the user off to.
 *
 * The agent module must not depend on other feature modules, so it emits one of
 * these ids in a [com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent.DeepLink]
 * bubble and the host (nav graph) maps it to the real route. Only add an id here
 * once the destination screen actually exists.
 */
object AgentDestination {
    const val DISABILITY_PENSION      = "disability_pension"
    const val DEFERRED_INSTALLMENT    = "deferred_installment"
    const val CONTRACTS               = "contracts"
    const val WORKSHOPS               = "workshops"
    const val PRESCRIPTION            = "prescription"

    // Illness / Short-term
    const val ILLNESS_COMPENSATION    = "illness_compensation"
    const val ILLNESS_REPORT          = "illness_report"

    // Social benefits
    const val WEDDING_PRESENT         = "wedding_present"
    const val FUNERAL_ALLOWANCE       = "funeral_allowance"
    const val PREGNANCY_PAY           = "pregnancy_pay"
    const val SHORT_TERM_ORTHOSIS     = "short_term_orthosis"
    const val OCCURRENCE_REPORT       = "occurrence_report"
    const val PENSION_SURVIVOR        = "pension_survivor"

    // Health
    const val MEDICAL_AUTHORITIES     = "medical_authorities"
    const val DESERVED_TREATMENT      = "deserved_treatment"

    // Profile edits
    const val EDIT_PHONE              = "edit_phone"
    const val EDIT_BANK_ACCOUNT       = "edit_bank_account"
    const val EDIT_ADDRESS            = "edit_address"
    const val EDIT_PROFILE            = "edit_profile"
    const val EXTEND_EDUCATION        = "extend_education"

    // Workers
    const val WORKERS_PAYMENT         = "workers_payment"

    // Message / Inbox
    const val PERSONAL_INBOX          = "personal_inbox"
}

/**
 * Registers the Agent screen in the NavGraph.
 *
 * @param onNavigateToDestination Maps an [AgentDestination] id to a real route.
 */
fun NavGraphBuilder.agentScreen(
    onNavigateToDestination: (String) -> Unit = {}
) {
    composable<AgentRoute> {
        AgentScreen(onNavigateToDestination = onNavigateToDestination)
    }
}
