package com.tamin.taminhamrah.feature.userRequest.ui
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.userRequest.ui.components.RequestErrorsBottomSheet
import com.tamin.taminhamrah.feature.userRequest.ui.components.RequestInfoDialog
import com.tamin.taminhamrah.feature.userRequest.ui.components.SmartGuideBottomSheet
import com.tamin.taminhamrah.feature.userRequest.ui.components.UserRequestCard
import com.tamin.taminhamrah.feature.userRequest.ui.components.UserRequestFilterPanel
import com.tamin.taminhamrah.feature.userRequest.ui.components.UserRequestStatusTabs
import com.tamin.taminhamrah.feature.userRequest.ui.contract.RequestStatusTab
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsEvent
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsIntent
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsUiState
import com.tamin.taminhamrah.model.userRequest.UserRequestTabCategory
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.paging.OnLoadMore
import com.tamin.taminhamrah.ui.paging.PagingFooter
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.collapseWhileImeVisible
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_default_error_title
import taminx.feature.userrequest.generated.resources.user_request_default_guide_title
import taminx.feature.userrequest.generated.resources.user_request_empty_list
import taminx.feature.userrequest.generated.resources.user_request_header_subtitle
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.profile_requests

@Composable
fun UserRequestsScreen(
    onBackClick: () -> Unit,
    onNavigateToDetail: (Long, String, Long, String, String) -> Unit,
    modifier: Modifier = Modifier,
    refCodeFilter: String? = null,
    requestTypeIdFilter: String? = null,
    viewModel: UserRequestsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(refCodeFilter, requestTypeIdFilter) {
        viewModel.sendIntent(UserRequestsIntent.InitFilters(refCodeFilter, requestTypeIdFilter))
        viewModel.sendIntent(UserRequestsIntent.LoadRequestTypes)
    }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is UserRequestsEvent.ShowToast -> snackbarHostState.showSnackbar(event.message)
                is UserRequestsEvent.NavigateToDetail -> {
                    onNavigateToDetail(
                        event.requestId,
                        event.refCode,
                        event.requestTypeId,
                        event.title,
                        event.referenceId,
                    )
                }
            }
        }
    }

    UserRequestsContent(
        state = state,
        onIntent = viewModel::sendIntent,
        onBackClick = onBackClick,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )

}

@Composable
fun UserRequestsContent(
    state: UserRequestsUiState,
    onIntent: (UserRequestsIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
) {
    val taminColors = LocalTaminColors.current

    val counts = mapOf(
        RequestStatusTab.ALL to state.requests.size,
        RequestStatusTab.IN_PROGRESS to state.requests.count {
            it.tabCategory == UserRequestTabCategory.IN_PROGRESS
        },
        RequestStatusTab.ACTION_REQUIRED to state.requests.count {
            it.tabCategory == UserRequestTabCategory.ACTION_REQUIRED
        },
        RequestStatusTab.COMPLETED to state.requests.count {
            it.tabCategory == UserRequestTabCategory.COMPLETED
        },
    )

    // Folds from the list's own drag; measured against the real header so the drag budget can't
    // drift out of sync with a copy or font change.
    val topArea = rememberMeasuredTopAreaState { probeState ->
        UserRequestsHeader(onBackClick = {}, topAreaState = probeState)
    }
    // The filter panel's ref-code field keyboard must not leave the header stuck mid-fold.
    topArea.collapseWhileImeVisible()
    val listState = rememberLazyListState()

    // Re-keyed on the loaded count: OnLoadMore fires only when the end first comes into view, and
    // a status tab can hide a whole page, leaving the end in view with no new trigger.
    key(state.requests.size) {
        listState.OnLoadMore(enabled = !state.endReached && state.paginationError == null) {
            onIntent(UserRequestsIntent.LoadNextPage)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = taminColors.bgPage,
        topBar = { UserRequestsHeader(onBackClick = onBackClick, topAreaState = topArea) }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .driveTopArea(topArea, listState),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            // Status Tabs section (header is now handled by TaminTopAppBar)

            // Search Filter Panel
            if (state.isFilterOpen) {
                item {
                    UserRequestFilterPanel(
                        refCode = state.refCode,
                        selectedTypeId = state.selectedRequestTypeId,
                        selectedTypeName = state.selectedRequestTypeName,
                        requestTypes = state.requestTypes,
                        onRefCodeChanged = { onIntent(UserRequestsIntent.UpdateRefCode(it)) },
                        onTypeSelected = { typeId, typeName -> onIntent(UserRequestsIntent.SelectRequestType(typeId, typeName)) },
                        onSearch = { onIntent(UserRequestsIntent.SearchRequests) },
                        onClose = { onIntent(UserRequestsIntent.ToggleFilter) },
                        modifier = Modifier.padding(horizontal = Spacing.page)
                    )
                }
            }

            // Status Tabs
            item {
                UserRequestStatusTabs(
                    selectedTab = state.selectedTab,
                    counts = counts,
                    onTabSelected = { onIntent(UserRequestsIntent.SelectTab(it)) },
                    modifier = Modifier.padding(horizontal = Spacing.page)
                )
            }

            // Request Items List
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (state.filteredRequests.isEmpty() && state.endReached) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TaminText(
                            text = stringResource(UserRequestRes.string.user_request_empty_list),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalTaminColors.current.textTertiary
                        )
                    }
                }
            } else {
                items(state.filteredRequests, key = { it.id }) { request ->
                    UserRequestCard(
                        request = request,
                        onViewDetails = {
                            onIntent(UserRequestsIntent.ViewDetails(request))
                        },
                        onOpenGuide = {
                            onIntent(
                                UserRequestsIntent.OpenSmartGuide(
                                    requestType = request.requestTypeId.toInt(),
                                    requestStatus = request.statusCode,
                                    title = request.title
                                )
                            )
                        },
                        onOpenErrors = { onIntent(UserRequestsIntent.OpenErrors(request.id, request.title)) },
                        onCopyTrackingCode = { onIntent(UserRequestsIntent.CopyTrackingCode(it)) },
                        modifier = Modifier.padding(horizontal = Spacing.page)
                    )
                }
            }

            if (!state.isLoading) {
                item(key = PAGING_FOOTER_KEY) {
                    PagingFooter(
                        isLoadingNextPage = state.isLoadingNextPage,
                        error = state.paginationError,
                        onRetry = { onIntent(UserRequestsIntent.RetryNextPage) },
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }

        // Modals & Bottom Sheets
        if (state.isSmartGuideOpen) {
            SmartGuideBottomSheet(
                title = state.smartGuideTitle ?: stringResource(UserRequestRes.string.user_request_default_guide_title),
                items = state.smartGuideItems,
                onDismissRequest = { onIntent(UserRequestsIntent.CloseSmartGuide) }
            )
        }

        if (state.isErrorsOpen) {
            RequestErrorsBottomSheet(
                title = state.errorTitle ?: stringResource(UserRequestRes.string.user_request_default_error_title),
                items = state.errorItems,
                onDismissRequest = { onIntent(UserRequestsIntent.CloseErrors) }
            )
        }

        if (state.infoDialogMessage != null) {
            RequestInfoDialog(
                message = state.infoDialogMessage!!,
                onDismissRequest = { onIntent(UserRequestsIntent.ShowInfoDialog(null)) }
            )
        }
    }
}

/**
 * Its own composable so the probe in [rememberMeasuredTopAreaState] measures exactly what's shown,
 * and so the `topBar` lambda skips on unrelated state changes (e.g. ref-code keystrokes).
 */
@Composable
private fun UserRequestsHeader(onBackClick: () -> Unit, topAreaState: TopAreaState) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    TaminTopAppBar(
        title = stringResource(Res.string.profile_requests),
        background = profileGradientBrush,
        bottomPadding = Spacing.xl,
        shape = RoundedCornerShape(
            bottomStart = CornerRadius.x3l,
            bottomEnd = CornerRadius.x3l
        ),
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = null,
                onClick = onBackClick,
                bordered = true
            )
        }
    ) {
        // Only this furniture folds away; the title row stays put.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaHide(topAreaState)
        ) {
            DecorativeBackgroundCircle(
                size = 190.dp,
                xOffset = 450.dp,
                yOffset = (-150).dp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedRingHeaderIcon(
                    icon = vectorResource(Res.drawable.ic_request),
                    animated = !topAreaState.isMeasureProbe,
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = stringResource(UserRequestRes.string.user_request_header_subtitle),
                    style = MaterialTheme.typography.labelLarge,
                    color = taminColors.textHeaderSubtitle,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestsScreenPreviewLight() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent(darkTheme = false) {
        UserRequestsContent(
            state = UserRequestsUiState(
                requests = listOf(
                    com.tamin.taminhamrah.model.userRequest.UserRequestPR(
                        id = 101L,
                        refCode = "۱۰۴۸۴۰۱۸۴۹",
                        title = "غرامت دستمزد ایام بیماری",
                        comment = "",
                        creationTime = "۱۴۰۵/۰۳/۱۱",
                        createByName = "سیدرحمت اله میرفضلی",
                        statusDesc = "نقص مدارک ارسالی",
                        statusCode = "0021",
                        requestTypeId = 10L, // ILL_DAY — button visible at 0021
                        requestTypeTitle = "غرامت دستمزد ایام بیماری"
                    ),
                    com.tamin.taminhamrah.model.userRequest.UserRequestPR(
                        id = 102L,
                        refCode = "۱۰۴۸۳۹۷۲۱۵",
                        title = "درخواست بررسی مدارک ارسالی",
                        comment = "",
                        creationTime = "۱۴۰۵/۰۲/۲۸",
                        createByName = "سیدرحمت اله میرفضلی",
                        statusDesc = "عدم تایید",
                        statusCode = "0019",
                        requestTypeId = 0L, // other — no view button
                        requestTypeTitle = "سایر درخواست‌ها"
                    ),
                    com.tamin.taminhamrah.model.userRequest.UserRequestPR(
                        id = 103L,
                        refCode = "۱۰۴۸۳۸۴۰۰۲",
                        title = "غرامت دستمزد ایام بارداری",
                        comment = "",
                        creationTime = "۱۴۰۵/۰۲/۰۵",
                        createByName = "سیدرحمت اله میرفضلی",
                        statusDesc = "در انتظار تکمیل اطلاعات",
                        statusCode = "0014",
                        requestTypeId = 11L, // PREGNANCY — button visible at 0014
                        requestTypeTitle = "غرامت دستمزد ایام بارداری"
                    ),
                    com.tamin.taminhamrah.model.userRequest.UserRequestPR(
                        id = 104L,
                        refCode = "۱۰۴۸۳۶۵۱۱۴",
                        title = "گواهی کسر اقساط معوق",
                        comment = "",
                        creationTime = "۱۴۰۴/۱۲/۱۸",
                        createByName = "سیدرحمت اله میرفضلی",
                        statusDesc = "تایید نهایی",
                        statusCode = "0018",
                        requestTypeId = 22L, // DEFERRED_INSTALLMENT — button visible at 0018
                        requestTypeTitle = "گواهی کسر اقساط معوق"
                    )
                ),
                selectedTab = RequestStatusTab.ALL,
                isFilterOpen = false
            ),
            onIntent = {},
            onBackClick = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestsScreenPreviewDark() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent(darkTheme = true) {
        UserRequestsContent(
            state = UserRequestsUiState(
                requests = listOf(
                    com.tamin.taminhamrah.model.userRequest.UserRequestPR(
                        id = 104L,
                        refCode = "۱۰۴۸۳۶۵۱۱۴",
                        title = "گواهی کسر اقساط معوق",
                        comment = "",
                        creationTime = "۱۴۰۴/۱۲/۱۸",
                        createByName = "سیدرحمت اله میرفضلی",
                        statusDesc = "تایید نهایی",
                        statusCode = "0018",
                        requestTypeId = 22L,
                        requestTypeTitle = "گواهی کسر اقساط معوق"
                    )
                ),
                selectedTab = RequestStatusTab.COMPLETED
            ),
            onIntent = {},
            onBackClick = {},
            snackbarHostState = SnackbarHostState()

        )
    }
}

private const val PAGING_FOOTER_KEY = "paging_footer"
