package com.tamin.taminhamrah.feature.userRequest.ui

import UserRequestsViewModel
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_default_error_title
import taminx.feature.userrequest.generated.resources.user_request_default_guide_title
import taminx.feature.userrequest.generated.resources.user_request_demo_details_dialog
import taminx.feature.userrequest.generated.resources.user_request_empty_list
import taminx.feature.userrequest.generated.resources.user_request_header_subtitle
import taminx.feature.userrequest.generated.resources.user_request_keyword_action
import taminx.feature.userrequest.generated.resources.user_request_keyword_approved
import taminx.feature.userrequest.generated.resources.user_request_keyword_closed
import taminx.feature.userrequest.generated.resources.user_request_keyword_completed
import taminx.feature.userrequest.generated.resources.user_request_keyword_defect
import taminx.feature.userrequest.generated.resources.user_request_keyword_disapproval
import taminx.feature.userrequest.generated.resources.user_request_keyword_in_progress
import taminx.feature.userrequest.generated.resources.user_request_keyword_pregnancy
import taminx.feature.userrequest.generated.resources.user_request_keyword_review
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

    LaunchedEffect(Unit) {
        viewModel.sendIntent(UserRequestsIntent.LoadRequests)
        viewModel.sendIntent(UserRequestsIntent.LoadRequestTypes)
    }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is UserRequestsEvent.ShowToast -> { /* Handle Toast */ }
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
        modifier = modifier
    )

}

@Composable
fun UserRequestsContent(
    state: UserRequestsUiState,
    onIntent: (UserRequestsIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    val kwInProgress = stringResource(UserRequestRes.string.user_request_keyword_in_progress)
    val kwReview = stringResource(UserRequestRes.string.user_request_keyword_review)
    val kwDefect = stringResource(UserRequestRes.string.user_request_keyword_defect)
    val kwDisapproval = stringResource(UserRequestRes.string.user_request_keyword_disapproval)
    val kwAction = stringResource(UserRequestRes.string.user_request_keyword_action)
    val kwApproved = stringResource(UserRequestRes.string.user_request_keyword_approved)
    val kwClosed = stringResource(UserRequestRes.string.user_request_keyword_closed)
    val kwCompleted = stringResource(UserRequestRes.string.user_request_keyword_completed)
    val kwPregnancy = stringResource(UserRequestRes.string.user_request_keyword_pregnancy)

    val allCount = state.requests.size
    val inProgressCount = state.requests.count {
        it.statusDesc.contains(kwInProgress) || it.statusDesc.contains(kwReview)
    }
    val actionRequiredCount = state.requests.count {
        it.statusDesc.contains(kwDefect) || it.statusDesc.contains(kwDisapproval) || it.statusDesc.contains(kwAction)
    }
    val completedCount = state.requests.count {
        it.statusDesc.contains(kwApproved) || it.statusDesc.contains(kwClosed) || it.statusDesc.contains(kwCompleted)
    }

    val counts = mapOf(
        RequestStatusTab.ALL to allCount,
        RequestStatusTab.IN_PROGRESS to inProgressCount,
        RequestStatusTab.ACTION_REQUIRED to actionRequiredCount,
        RequestStatusTab.COMPLETED to completedCount,
    )

    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = taminColors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.profile_requests),
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = 40.dp,
                    bottomEnd = 40.dp
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
                        CircularProgressIndicator(color = Color(0xFF1F4FA3))
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
                    val demoDetailsMsg = stringResource(UserRequestRes.string.user_request_demo_details_dialog, request.refCode)
                    UserRequestCard(
                        request = request,
                        onViewDetails = {
                            if (request.title.contains(kwPregnancy)) {
                                onIntent(UserRequestsIntent.ShowInfoDialog(demoDetailsMsg))
                            } else {
                                onIntent(UserRequestsIntent.ViewDetails(request))
                            }
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
            onBackClick = {}
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
            onBackClick = {}
        )
    }
}



