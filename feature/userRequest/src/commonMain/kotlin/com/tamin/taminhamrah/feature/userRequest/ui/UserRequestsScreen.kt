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
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
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
    viewModel: UserRequestsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(UserRequestsIntent.LoadRequests)
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

    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = taminColors.bgPage,
        topBar = {
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
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_request))
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            // Status Tabs section (header is now handled by TaminTopAppBar)

            // Search Filter Panel
            if (state.isFilterOpen) {
                item {
                    UserRequestFilterPanel(
                        refCode = state.refCode,
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
            } else if (state.filteredRequests.isEmpty()) {
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



