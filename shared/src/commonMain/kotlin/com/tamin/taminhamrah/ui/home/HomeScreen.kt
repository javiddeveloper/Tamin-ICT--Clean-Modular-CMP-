package com.tamin.taminhamrah.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.stories.ui.rail.StoryRail
import com.tamin.taminhamrah.mapper.campaign.toCampaignKinds
import com.tamin.taminhamrah.mapper.campaign.toPresentation
import com.tamin.taminhamrah.mapper.home.toHomeSections
import com.tamin.taminhamrah.mapper.home.toMainServices
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.home.CampaignDN
import com.tamin.taminhamrah.model.home.HomeContentDN
import com.tamin.taminhamrah.model.home.HomeServiceSection
import com.tamin.taminhamrah.model.home.QuickAccessDN
import com.tamin.taminhamrah.model.home.RequestDN
import com.tamin.taminhamrah.model.home.SpecialServiceDN
import com.tamin.taminhamrah.model.home.UserInfoDN
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.repository.home.HomeContentPlaceholders
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CampaignCarousel
import com.tamin.taminhamrah.ui.components.HeaderSuggestionChip
import com.tamin.taminhamrah.ui.components.HistorySummaryCard
import com.tamin.taminhamrah.ui.components.HistorySummaryCardSkeleton
import com.tamin.taminhamrah.ui.components.HistorySummaryCardUnavailable
import com.tamin.taminhamrah.ui.components.HomeAgentAskBar
import com.tamin.taminhamrah.ui.components.HomeFeaturedSection
import com.tamin.taminhamrah.ui.components.HomeHeader
import com.tamin.taminhamrah.ui.components.HomeLastRequestsSection
import com.tamin.taminhamrah.ui.components.HomeQuickAccessSection
import com.tamin.taminhamrah.ui.home.contract.HomeEvent
import com.tamin.taminhamrah.ui.home.contract.HomeIntent
import com.tamin.taminhamrah.ui.home.contract.HomeUiState
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_load_menu_failed
import taminx.core.core_ui.home_ask_agent_cd
import taminx.core.core_ui.home_ask_agent_hint
import taminx.core.core_ui.home_header_fallback_name
import taminx.core.core_ui.home_suggestion_booklet
import taminx.core.core_ui.home_suggestion_history
import taminx.core.core_ui.home_suggestion_retirement
import taminx.core.core_ui.retry


/** What the placeholder home column insets its content by; the carousel needs to know it. */
private val HomeContentPadding = 16.dp

/** Top-level so it is the same instance on every recomposition, not a fresh modifier each time. */
private val HistorySummaryPadding = Modifier.padding(top = Spacing.xlg)

/**
 * How far the AI ask-bar drops below the header's bottom edge — half its own height
 * ([com.tamin.taminhamrah.ui.theme.ButtonDimens.height] / 2), matching the profile screen's status
 * card. This is the trailing spacer under the header, so the bar (bottom-aligned over it) ends up
 * straddling the gradient edge. The header carries enough bottom padding that its chips clear it.
 */
private val HomeAskBarOverlap = 28.dp

/**
 * Measures the content [inset] wider than the column allows, so a full-bleed child can reach the
 * screen edge from inside a padded, center-aligned column. Placement is symmetric, which is what
 * cancels the padding — the parent's own width is fixed, so nothing else moves.
 *
 * Local to this screen on purpose: it exists only because the placeholder home column pads all of
 * its children, and it goes away with the placeholder.
 */
private fun Modifier.ignoreHorizontalPadding(inset: Dp) = layout { measurable, constraints ->
    val width = constraints.maxWidth + inset.roundToPx() * 2
    val placeable = measurable.measure(
        constraints.copy(minWidth = width, maxWidth = width)
    )
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

@Composable
fun HomeScreen(
    onNavigateToService: (FeatureFlag) -> Unit,
    onNavigateToWeb: (String) -> Unit,
    // No default: a disabled feature says why through this, and a caller that omitted it used to
    // drop the message silently — the tap then did nothing at all.
    onShowMessage: (String) -> Unit,
    onNavigateToAllServices: () -> Unit,
    onNavigateToAgent: () -> Unit,
    onNavigateToUserRequests: (String?) -> Unit,
    onNavigateToUserRequestDetail: (Long, String, Long, String, String) -> Unit,
    /** Where tapping a channel on the «تازه‌ها» rail leads. */
    onOpenStory: (channelIndex: Int) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.NavigateToService -> onNavigateToService(event.flag)
                is HomeEvent.NavigateToWeb -> onNavigateToWeb(event.url)
                is HomeEvent.ShowMessage -> onShowMessage(event.message)
                is HomeEvent.NavigateToUserRequestDetail -> onNavigateToUserRequestDetail(
                    event.requestId,
                    event.refCode,
                    event.requestTypeId,
                    event.title,
                    event.referenceId,
                )
            }
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToAgent = onNavigateToAgent,
        onNavigateToAllServices = onNavigateToAllServices,
        onNavigateToUserRequests = { onNavigateToUserRequests(null) },
        onRequestClick = { request ->
            onNavigateToUserRequests(request.refCode)
        },
        onCampaignClick = { viewModel.sendIntent(HomeIntent.OnCampaignClick(it)) },
        onSectionSelected = { viewModel.sendIntent(HomeIntent.OnSectionSelected(it)) },
        onServiceClick = { viewModel.sendIntent(HomeIntent.OnServiceClick(it)) },
        onRetry = { viewModel.sendIntent(HomeIntent.Retry) },
        // Hoisted: the card's three actions are one action, and a lambda built at the call site
        // would capture `viewModel` — not a stable type, so the compiler cannot memoize it and the
        // card would recompose on every emission of `uiState` instead of when its own year changes.
        onHistorySummaryClick = remember(viewModel) {
            { viewModel.sendIntent(HomeIntent.OnHistorySummaryClick) }
        },
        onRetryHistorySummary = remember(viewModel) {
            { viewModel.sendIntent(HomeIntent.LoadHistorySummary) }
        },
        storyRail = {
            // «تازه‌ها» sits directly above the campaigns, as on the design, and is full-bleed for
            // the same reason: a row that scrolls has to be able to run a ring off the screen edge.
            StoryRail(
                onOpenViewer = onOpenStory,
                modifier = Modifier
                    .ignoreHorizontalPadding(HomeContentPadding)
                    .padding(top = Spacing.xlg),
            )
        },
    )
}

/**
 * [storyRail] is a slot rather than an inline [StoryRail] call because [StoryRail] resolves its own
 * `StoryRailViewModel` through Koin, which is never started under Android Studio's `@Preview`
 * renderer — embedding it directly here would crash every preview of this composable. The real
 * screen supplies it via [HomeScreen]; a preview simply leaves it out.
 */
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToAgent: () -> Unit,
    onNavigateToAllServices: () -> Unit,
    onNavigateToUserRequests: () -> Unit,
    onRequestClick: (UserRequestPR) -> Unit,
    onCampaignClick: (FeatureFlag) -> Unit,
    onSectionSelected: (HomeServiceSection) -> Unit,
    onServiceClick: (MainServiceDN) -> Unit,
    onRetry: () -> Unit,
    /** Anywhere on خلاصهٔ سابقه — the card, its year pill and «جزئیات ماه‌به‌ماه» all open سوابق. */
    onHistorySummaryClick: () -> Unit = {},
    onRetryHistorySummary: () -> Unit = {},
    storyRail: @Composable () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        /*     if (uiState.isLoading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
             }*/

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HomeContentPadding)
                // Bottom padding so last item scrolls fully above the floating blur bar
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 100.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header + AI ask-bar. The bar straddles the header's bottom edge the way the profile
            // screen's status card does: the header sits in a Column with a trailing spacer that
            // reserves the bar's lower half, and the bar is bottom-aligned in the Box over it.
            Box(modifier = Modifier.ignoreHorizontalPadding(HomeContentPadding)) {
                Column {
                    HomeHeader(
                        // null here means "still loading" to HomeHeader (it shows a shimmer) — that
                        // is only true while homeContent itself hasn't arrived yet. Once it has, a
                        // blank/unavailable name is resolved to the localized fallback text right
                        // here rather than in core-data, which has no Compose-resources access.
                        fullName = uiState.homeContent?.let {
                            it.userInfo?.fullName?.takeIf { name -> name.isNotBlank() }
                                ?: stringResource(Res.string.home_header_fallback_name)
                        },
                        hasDarmanCoverage = uiState.homeContent?.userInfo?.hasDarmanCoverage,
                        hasActiveRelation = uiState.homeContent?.userInfo?.hasActiveRelation,
                    )
                    if (uiState.isAgentEnabled) {
                        Spacer(modifier = Modifier.height(HomeAskBarOverlap))
                    }
                }
                if (uiState.isAgentEnabled) {
                    HomeAgentAskBar(
                        hint = stringResource(Res.string.home_ask_agent_hint),
                        contentDescription = stringResource(Res.string.home_ask_agent_cd),
                        onClick = onNavigateToAgent,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = HomeContentPadding),
                    )
                }
            }

            if (uiState.isAgentEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(top = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    listOf(
                        stringResource(Res.string.home_suggestion_retirement),
                        stringResource(Res.string.home_suggestion_history),
                        stringResource(Res.string.home_suggestion_booklet),
                    ).forEach { suggestion ->
                        HeaderSuggestionChip(text = suggestion, onClick = onNavigateToAgent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            storyRail()

            // خلاصهٔ سابقه, between «تازه‌ها» and the campaigns exactly as the design orders them.
            //
            // Nothing at all once the load has answered with no year: someone not yet insured has
            // no summary, and neither has a کارفرما or a مستمری‌بگیر, whose premiums are not their
            // own — an empty card would say so at the size of a full one. A connection that never
            // answered is the one case that keeps the slot, to offer the retry.
            //
            // The warning row is icon-and-text, as the design draws it: «پیگیری» is not offered
            // here, so `onFollowUpClick` stays null.
            val summary = uiState.historySummary
            when {
                uiState.isHistorySummaryLoading ->
                    HistorySummaryCardSkeleton(modifier = HistorySummaryPadding)

                summary != null -> HistorySummaryCard(
                    summary = summary,
                    onCardClick = onHistorySummaryClick,
                    onYearClick = onHistorySummaryClick,
                    onDetailsClick = onHistorySummaryClick,
                    modifier = HistorySummaryPadding,
                )

                uiState.historySummaryFailed -> HistorySummaryCardUnavailable(
                    onRetry = onRetryHistorySummary,
                    modifier = HistorySummaryPadding,
                )
            }

            // The same for every role: campaigns are not filtered by the picker above.
            //
            // Full-bleed on purpose. A pager clips along its scroll axis, so leaving it inside this
            // column's 16dp inset would cut the peeking neighbor down from 34 to 18 and leave the
            // cards' merged shadow with a hard vertical edge 16dp in from the screen.
            CampaignCarousel(
                campaigns = (uiState.homeContent?.campaigns?.toCampaignKinds() ?: persistentListOf()).toPresentation(),
                onCampaignClick = onCampaignClick,
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .ignoreHorizontalPadding(HomeContentPadding)
                    .padding(top = Spacing.xlg),
            )

            Spacer(modifier = Modifier.height(8.dp))

            HomeQuickAccessSection(
                sections = uiState.homeContent?.quickAccess?.toHomeSections() ?: persistentListOf(),
                selectedSection = uiState.selectedSection,
                onSectionSelected = onSectionSelected,
                onServiceClick = onServiceClick,
                onSeeAll = onNavigateToAllServices,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(top = Spacing.lg),
            )

            HomeFeaturedSection(
                services = uiState.homeContent?.specialServices?.toMainServices() ?: persistentListOf(),
                onServiceClick = onServiceClick,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(top = Spacing.md),
            )

            val requests = uiState.homeContent?.requests?.map {
                UserRequestPR(
                    id = it.id.toLongOrNull() ?: 0L,
                    refCode = it.refCode,
                    title = it.title,
                    comment = "",
                    creationTime = it.date.toLongOrNull()
                        ?.let { ms -> PersianDateFormatter.formatTimestamp(ms) } ?: it.date,
                    createByName = "",
                    statusDesc = it.status,
                    statusCode = it.statusCode,
                    requestTypeId = it.requestTypeId,
                    requestTypeTitle = it.title
                )
            }

            HomeLastRequestsSection(
                requests = requests,
                onSeeAllClick = onNavigateToUserRequests,
                onRequestClick = onRequestClick,
                modifier = Modifier.padding(top = Spacing.md),
            )


            if (uiState.homeContent == null && !uiState.isLoading) {
                Text(
                    stringResource(Res.string.error_load_menu_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 32.dp)
                )
                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(Res.string.retry))
                }
            }
        }
    }
}

private fun previewHomeUiState() = HomeUiState(
    isLoading = false,
    homeContent = HomeContentDN(
        userInfo = UserInfoDN(
            fullName = "سنا حقیقی",
            hasDarmanCoverage = true,
            hasActiveRelation = true
        ),
        stories = null,
        campaigns = HomeContentPlaceholders.campaignFlags.map { flag ->
            CampaignDN(flag = flag, title = flag.name, bannerUrl = null, isOpenable = true)
        },
        quickAccess = HomeContentPlaceholders.quickAccessGroups.flatMap { (group, flags) ->
            flags.map { flag ->
                QuickAccessDN(flag = flag, title = flag.name, iconUrl = null, group = group, status = MenuServiceStatusDN.ACTIVE)
            }
        },
        specialServices = HomeContentPlaceholders.specialServiceFlags.map { flag ->
            SpecialServiceDN(flag = flag, title = flag.name, iconUrl = null, status = MenuServiceStatusDN.ACTIVE)
        },
        requests = listOf(
            RequestDN(
                id = "1048384001",
                title = "تأییدیه پزشکی",
                date = "۱۴۰۴/۰۳/۲۸",
                status = "تأیید شد",
                refCode = "1045678902",
                statusCode = "18",
                requestTypeId = 1L,
            ),
            RequestDN(
                id = "1048384002",
                title = "استعلام سوابق",
                date = "۱۴۰۴/۰۳/۲۵",
                status = "در حال بررسی",
                refCode = "1045698765",
                statusCode = "2",
                requestTypeId = 2L,
            )
        )
    ),
    isAgentEnabled = true,
)

@PreviewRtlTheme
@Composable
private fun HomeScreenPreview() {
    PreviewRtlThemeContent {
        HomeScreenContent(
            uiState = previewHomeUiState(),
            onNavigateToAgent = {},
            onNavigateToAllServices = {},
            onNavigateToUserRequests = {},
            onRequestClick = {},
            onCampaignClick = {},
            onSectionSelected = {},
            onServiceClick = {},
            onRetry = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HomeScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        HomeScreenContent(
            uiState = previewHomeUiState(),
            onNavigateToAgent = {},
            onNavigateToAllServices = {},
            onNavigateToUserRequests = {},
            onRequestClick = {},
            onCampaignClick = {},
            onSectionSelected = {},
            onServiceClick = {},
            onRetry = {},
        )
    }
}
