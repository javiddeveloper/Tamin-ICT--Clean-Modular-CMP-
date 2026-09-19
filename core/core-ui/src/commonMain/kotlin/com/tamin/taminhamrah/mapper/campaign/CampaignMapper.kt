package com.tamin.taminhamrah.mapper.campaign

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tamin.taminhamrah.model.campaign.CampaignKind
import com.tamin.taminhamrah.model.campaign.CampaignPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.campaign_freelance_badge
import taminx.core.core_ui.campaign_freelance_body
import taminx.core.core_ui.campaign_freelance_caption
import taminx.core.core_ui.campaign_freelance_cta
import taminx.core.core_ui.campaign_freelance_title
import taminx.core.core_ui.campaign_housewife_badge
import taminx.core.core_ui.campaign_housewife_body
import taminx.core.core_ui.campaign_housewife_caption
import taminx.core.core_ui.campaign_housewife_cta
import taminx.core.core_ui.campaign_housewife_title
import taminx.core.core_ui.campaign_student_badge
import taminx.core.core_ui.campaign_student_body
import taminx.core.core_ui.campaign_student_caption
import taminx.core.core_ui.campaign_student_cta
import taminx.core.core_ui.campaign_student_title

/**
 * Fills a [CampaignKind] in with the copy it prints.
 *
 * **This is the seam the campaigns web service will replace.** There is no campaigns endpoint yet,
 * so the copy is bundled and resolved here, in the UI — a ViewModel cannot call `getString()`
 * reliably under the unit-test runtime. When the endpoint arrives, the ViewModel builds
 * [CampaignPR] straight from the wire and callers stop going through this file; the carousel, the
 * palette table, the gating and the navigation all stay as they are.
 */
@Composable
fun CampaignKind.toPresentation(): CampaignPR = when (this) {
    CampaignKind.HOUSEWIFE -> CampaignPR(
        kind = this,
        badge = stringResource(Res.string.campaign_housewife_badge),
        title = stringResource(Res.string.campaign_housewife_title),
        body = stringResource(Res.string.campaign_housewife_body),
        ctaLabel = stringResource(Res.string.campaign_housewife_cta),
        caption = stringResource(Res.string.campaign_housewife_caption),
    )

    CampaignKind.FREELANCE -> CampaignPR(
        kind = this,
        badge = stringResource(Res.string.campaign_freelance_badge),
        title = stringResource(Res.string.campaign_freelance_title),
        body = stringResource(Res.string.campaign_freelance_body),
        ctaLabel = stringResource(Res.string.campaign_freelance_cta),
        caption = stringResource(Res.string.campaign_freelance_caption),
    )

    CampaignKind.STUDENT -> CampaignPR(
        kind = this,
        badge = stringResource(Res.string.campaign_student_badge),
        title = stringResource(Res.string.campaign_student_title),
        body = stringResource(Res.string.campaign_student_body),
        ctaLabel = stringResource(Res.string.campaign_student_cta),
        caption = stringResource(Res.string.campaign_student_caption),
    )
}

/**
 * Resolves a whole visible set and remembers it, so the carousel is handed the same list instance
 * on every recomposition rather than a fresh one built at the call site.
 *
 * Keyed on the resolved rows rather than on the kinds: `remember` compares keys structurally, and
 * [CampaignPR] is a data class, so the instance survives every recomposition that did not change
 * the copy — including a locale change, which the kinds alone would not catch.
 */
@Composable
fun ImmutableList<CampaignKind>.toPresentation(): ImmutableList<CampaignPR> {
    val resolved = map { it.toPresentation() }
    return remember(resolved) { resolved.toImmutableList() }
}
