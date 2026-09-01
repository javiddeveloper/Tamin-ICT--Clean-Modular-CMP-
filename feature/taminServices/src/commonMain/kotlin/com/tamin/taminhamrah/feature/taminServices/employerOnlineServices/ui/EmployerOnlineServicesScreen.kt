package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.AgreementsListUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesErrorSource
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesEvent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesIntent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesScreen
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerAgreementCard
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesFilterChipRow
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesHeader
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesListSkeleton
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesSearchEmptyState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerOnlineServicesSearchSheet
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.IdentityInfoCard
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementRowPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementSearch
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.IdentityCardPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.Toast
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_coming_soon_toast
import taminx.core.core_ui.employer_online_services_count_label
import taminx.core.core_ui.employer_online_services_empty_subtitle
import taminx.core.core_ui.employer_online_services_empty_title
import taminx.core.core_ui.employer_online_services_error_retry
import taminx.core.core_ui.employer_online_services_list_error_title
import taminx.core.core_ui.employer_online_services_request_button

private val HeaderCollapseDistance = 160.dp

/**
 * How far the identity card hangs below the header gradient. The rest of the card sits *on* the
 * gradient, so it straddles the bottom edge — the same overlap [com.tamin.taminhamrah.feature.profile]
 * gives its `ValidationStatusCard`.
 */
private val IdentityCardOverhang = 48.dp

@Composable
fun EmployerOnlineServicesRoute(
    viewModel: EmployerOnlineServicesViewModel,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    val comingSoonMessage = stringResource(Res.string.employer_online_services_coming_soon_toast)

    EmployerOnlineServicesEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onShowComingSoon = { toaster.error(comingSoonMessage) },
        onNavigateBack = onBackClicked,
    )

    when (uiState.currentScreen) {
        EmployerOnlineServicesScreen.AGREEMENTS_LIST -> EmployerAgreementsListScreen(
            uiState = uiState,
            onIntent = viewModel::sendIntent,
            onBackClicked = onBackClicked,
        )

        EmployerOnlineServicesScreen.CONTRACT_ROWS -> {
            BackHandler(onBack = { viewModel.sendIntent(EmployerOnlineServicesIntent.CloseContractRows) })
            EmployerContractRowsScreen(
                uiState = uiState,
                onIntent = viewModel::sendIntent,
                onBackClicked = { viewModel.sendIntent(EmployerOnlineServicesIntent.CloseContractRows) },
            )
        }
    }
}

@Composable
private fun EmployerOnlineServicesEvents(
    events: Flow<EmployerOnlineServicesEvent>,
    onShowToast: (String) -> Toast,
    onShowComingSoon: () -> Toast,
    onNavigateBack: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is EmployerOnlineServicesEvent.ShowToast -> onShowToast(event.message)
            EmployerOnlineServicesEvent.ShowComingSoon -> onShowComingSoon()
            EmployerOnlineServicesEvent.NavigateBack -> onNavigateBack()
        }
    }
}

@Composable
internal fun EmployerAgreementsListScreen(
    uiState: EmployerOnlineServicesUiState,
    onIntent: (EmployerOnlineServicesIntent) -> Unit,
    onBackClicked: () -> Unit,
    initialSearchCriteria: EmployerAgreementSearch = EmployerAgreementSearch(),
) {
    val taminColors = LocalTaminColors.current
    val collapse = rememberCollapsingHeaderState(HeaderCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var searchCriteria by remember { mutableStateOf(initialSearchCriteria) }

    val list = uiState.agreementsList
    val agreementsError = uiState.errors[EmployerOnlineServicesErrorSource.AGREEMENTS]

    // Client-side filter over the already-loaded list — exactly inspection's search: nothing is
    // re-fetched, the criteria just narrows what the list shows.
    val visibleAgreements = remember(list.agreements, searchCriteria) {
        if (searchCriteria.isEmpty) list.agreements
        else list.agreements.filter { searchCriteria.matches(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            }

            item {
                LandingActionRow(
                    count = list.agreementCount,
                    onRequestClicked = { onIntent(EmployerOnlineServicesIntent.OpenAgreementRequest) },
                    modifier = Modifier.padding(horizontal = Spacing.lg),
                )
            }

            when {
                uiState.isLoading && list.agreements.isEmpty() -> item {
                    EmployerOnlineServicesListSkeleton()
                }

                agreementsError != null && list.agreements.isEmpty() -> item {
                    EmptyStateMessage(
                        icon = Icons.Outlined.Description,
                        title = stringResource(Res.string.employer_online_services_list_error_title),
                        subtitle = agreementsError,
                        actionLabel = stringResource(Res.string.employer_online_services_error_retry),
                        onAction = {
                            onIntent(
                                EmployerOnlineServicesIntent.RetrySource(
                                    EmployerOnlineServicesErrorSource.AGREEMENTS,
                                ),
                            )
                        },
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = Spacing.xlg),
                    )
                }

                list.agreements.isEmpty() -> item {
                    EmptyStateMessage(
                        icon = Icons.Outlined.Description,
                        title = stringResource(Res.string.employer_online_services_empty_title),
                        subtitle = stringResource(Res.string.employer_online_services_empty_subtitle),
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = Spacing.xlg),
                    )
                }

                else -> {
                    if (!searchCriteria.isEmpty) {
                        item {
                            EmployerOnlineServicesFilterChipRow(
                                criteria = searchCriteria,
                                onClear = { searchCriteria = EmployerAgreementSearch() },
                                modifier = Modifier.padding(horizontal = Spacing.lg),
                            )
                        }
                    }

                    if (visibleAgreements.isEmpty()) {
                        item {
                            EmployerOnlineServicesSearchEmptyState(
                                modifier = Modifier.padding(horizontal = Spacing.lg),
                            )
                        }
                    }

                    itemsIndexed(
                        visibleAgreements,
                        key = { _, item -> item.workshopId + "-" + item.branchCode },
                    ) { _, item ->
                        EmployerAgreementCard(
                            item = item,
                            onContractRowsClicked = {
                                onIntent(EmployerOnlineServicesIntent.OpenContractRows(item))
                            },
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                        )
                    }
                }
            }
        }

        // Pinned header + identity card overlapping its bottom edge, measured as one block so the
        // list reserves exactly the space it occupies (mirrors profile's top-bar / status-card).
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .onSizeChanged { headerHeightPx = it.height },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                EmployerOnlineServicesHeader(
                    collapseProgress = collapse.progressProvider,
                    onBackClicked = onBackClicked,
                    onSearchClicked = { showSearchSheet = true },
                )
                Spacer(modifier = Modifier.height(IdentityCardOverhang))
            }

            IdentityInfoCard(
                identity = list.identity,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = Spacing.lg),
            )
        }

        if (uiState.isLoading && list.agreements.isNotEmpty()) {
            LoadingStateOverlay()
        }
    }

    if (showSearchSheet) {
        EmployerOnlineServicesSearchSheet(
            initial = searchCriteria,
            onDismiss = { showSearchSheet = false },
            onApply = { criteria ->
                searchCriteria = criteria
                showSearchSheet = false
            },
            onClear = {
                searchCriteria = EmployerAgreementSearch()
                showSearchSheet = false
            },
        )
    }
}

@Composable
private fun LandingActionRow(
    count: Int,
    onRequestClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier.fillMaxWidth().height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(brush = Brush.linearGradient(colors = listOf(TaminNavy300, TaminNavy900)))
                .padding(vertical = 2.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            onClick = onRequestClicked,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TaminText(text = "+", color = Color.White)
                Spacer(Modifier.width(4.dp))
                TaminText(
                    text = stringResource(Res.string.employer_online_services_request_button),
                    color = Color.White,
                )
            }
        }

        Spacer(Modifier.width(Spacing.sm))

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color = taminColors.bgSurface, shape = RoundedCornerShape(16.dp))
                .border(width = 1.dp, color = taminColors.border, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            TaminText(text = count.toString(), color = taminColors.textPrimary)
            TaminText(
                text = stringResource(Res.string.employer_online_services_count_label),
                color = taminColors.textMuted,
                fontSize = 12.sp,
            )
        }
    }
}

// ----------------------------------------------------------------------- previews

private val PreviewIdentity = IdentityCardPR(
    fullName = "حسین توکلی کرمانی",
    nationalCode = "۴۴۷۹۸۹۰۸۸۲",
)

private val PreviewAgreements = listOf(
    EmployerAgreementRowPR(
        workshopId = "0081631829",
        branchCode = "1202",
        hasIdentity = true,
        isActive = true,
        workshopName = "شرکت صنایع دما بخار مشهد",
        branchLabel = "شعبهٔ ۲ مشهد · ۱۲۰۲",
        workshopCodeLabel = "۰۰۸۱۶۳۱۸۲۹",
        commitmentDate = "۱۴۰۳/۰۵/۱۹",
        startDate = "۱۴۰۳/۰۱/۰۱",
        address = "مشهد، بلوار وکیل‌آباد",
        mobile = "۰۹۱۲۳۴۵۶۷۸۹",
        email = "info@damabokhar.ir",
    ),
    EmployerAgreementRowPR(
        workshopId = "0219117640",
        branchCode = "1201",
        hasIdentity = true,
        isActive = true,
        workshopName = "شرکت مهندسی پویا صنعت شرق",
        branchLabel = "شعبهٔ ۱ مشهد · ۱۲۰۱",
        workshopCodeLabel = "۰۰۲۱۹۱۱۷۶۴",
        commitmentDate = "۱۴۰۴/۰۲/۲۷",
        startDate = "۱۴۰۳/۱۱/۱۰",
        address = "مشهد، احمدآباد",
        mobile = "۰۹۱۵۱۱۱۲۲۳۳",
        email = "-",
    ),
)

private val PreviewLoadedState = EmployerOnlineServicesUiState(
    agreementsList = AgreementsListUiState(
        identity = PreviewIdentity,
        agreements = PreviewAgreements,
        agreementCount = 3,
    ),
)

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesScreenPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            EmployerAgreementsListScreen(uiState = PreviewLoadedState, onIntent = {}, onBackClicked = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            EmployerAgreementsListScreen(uiState = PreviewLoadedState, onIntent = {}, onBackClicked = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesScreenEmptyPreview() {
    PreviewRtlThemeContent {
        AppToastHost {
            EmployerAgreementsListScreen(
                uiState = EmployerOnlineServicesUiState(
                    agreementsList = AgreementsListUiState(identity = PreviewIdentity),
                ),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesScreenLoadingPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            EmployerAgreementsListScreen(
                uiState = EmployerOnlineServicesUiState(isLoading = true),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

private val PreviewNoMatchSearchCriteria = EmployerAgreementSearch(workshopCode = "00000000000")

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesScreenSearchEmptyPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            EmployerAgreementsListScreen(
                uiState = PreviewLoadedState,
                onIntent = {},
                onBackClicked = {},
                initialSearchCriteria = PreviewNoMatchSearchCriteria,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesScreenSearchEmptyPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            EmployerAgreementsListScreen(
                uiState = PreviewLoadedState,
                onIntent = {},
                onBackClicked = {},
                initialSearchCriteria = PreviewNoMatchSearchCriteria,
            )
        }
    }
}
