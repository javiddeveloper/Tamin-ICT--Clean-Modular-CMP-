package com.tamin.taminhamrah.feature.userRequest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserRequestsScreen(
    onBackClick: () -> Unit,
    onNavigateToDetail: (Long, String, Long) -> Unit,
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
                    onNavigateToDetail(event.requestId, event.refCode, event.requestTypeId)
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
    val allCount = state.requests.size
    val inProgressCount = state.requests.count { it.statusDesc.contains("در جریان") || it.statusDesc.contains("بررسی") }
    val actionRequiredCount = state.requests.count { it.statusDesc.contains("نقص") || it.statusDesc.contains("عدم") || it.statusDesc.contains("اقدام") }
    val completedCount = state.requests.count { it.statusDesc.contains("تایید") || it.statusDesc.contains("مختومه") || it.statusDesc.contains("تکمیل") }

    val counts = mapOf(
        RequestStatusTab.ALL to allCount,
        RequestStatusTab.IN_PROGRESS to inProgressCount,
        RequestStatusTab.ACTION_REQUIRED to actionRequiredCount,
        RequestStatusTab.COMPLETED to completedCount,
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LocalTaminColors.current.bgPage
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            // Blue Header Arc
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(Color(0xFF173D7E))
                        .padding(horizontal = Spacing.page, vertical = Spacing.lg)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .clickable { onBackClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            TaminText(
                                text = "درخواست‌های من",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .clickable { onIntent(UserRequestsIntent.ToggleFilter) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        // Circular Document Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        TaminText(
                            text = "کارتابل پیگیری درخواست‌ها، اطلاع از نتیجه اقدامات و ...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

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
                            text = "درخواستی یافت نشد",
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
                            if (request.title.contains("بارداری")) {
                                onIntent(UserRequestsIntent.ShowInfoDialog("در نسخه نهایی، این دکمه شما را به صفحه جزئیات همین درخواست با کد پیگیری ${request.refCode} منتقل می‌کنند."))
                            } else {
                                onIntent(UserRequestsIntent.ViewDetails(request))
                            }
                        },
                        onOpenGuide = { onIntent(UserRequestsIntent.OpenSmartGuide(null, null, request.title)) },
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
                title = state.smartGuideTitle ?: "راهنمای عمومی",
                items = state.smartGuideItems,
                onDismissRequest = { onIntent(UserRequestsIntent.CloseSmartGuide) }
            )
        }

        if (state.isErrorsOpen) {
            RequestErrorsBottomSheet(
                title = state.errorTitle ?: "خطاهای درخواست",
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


