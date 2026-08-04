package com.tamin.taminhamrah.feature.profile.ui.dependents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListEvent
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListIntent
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListState
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun DependentsListRoute(
    viewModel: DependentsListViewModel,
    onNavigateToAddDependent: () -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(DependentsListIntent.InitData)
    }

    HandleDependentsListEvents(
        events = viewModel.events,
        onNavigateToAddDependent = onNavigateToAddDependent,
        onBackClicked = onBackClicked
    )

    DependentsListScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked
    )
}

@Composable
fun HandleDependentsListEvents(
    events: Flow<DependentsListEvent>,
    onNavigateToAddDependent: () -> Unit,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            DependentsListEvent.NavigateToAddDependentWizard -> onNavigateToAddDependent()
            is DependentsListEvent.ShowToast -> {}
            is DependentsListEvent.ShowErrorDialog -> {}
        }
    }
}

@Composable
fun DependentsListScreen(
    modifier: Modifier = Modifier,
    state: DependentsListState,
    onIntent: (DependentsListIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TaminTopAppBar(
                title = "لیست افراد تبعی",
                centerTitle = true,
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = "بازگشت",
                        onClick = onBackClicked
                    )
                }
            )

            when {
                state.isLoading -> LoadingStateOverlay()
                state.error != null -> ErrorStateView(
                    message = state.error,
                    onRetry = { onIntent(DependentsListIntent.OnRefreshClicked) }
                )
                state.dependentsList.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateMessage(
                            icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            title = "هیچ فرد تبعی ثبت نشده است",
                            actionLabel = "تلاش مجدد",
                            onAction = { onIntent(DependentsListIntent.OnRefreshClicked) }
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        overscrollEffect = rememberJellyOverscroll(),
                        contentPadding = PaddingValues(
                            top = Spacing.lg,
                            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.xxl,
                            start = Spacing.page,
                            end = Spacing.page
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(
                            items = state.dependentsList,
                            key = { it.id }
                        ) { dependent ->
                            DependentItemCard(dependent = dependent)
                        }
                    }
                }
            }

            // Bottom Action Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.page)
            ) {
                Button(
                    onClick = { onIntent(DependentsListIntent.OnAddDependentClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(CornerRadius.lg),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.teal
                    )
                ) {
                    Text(
                        text = "افزودن فرد تبعی",
                        color = colors.bgSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DependentItemCard(
    dependent: SubdominantItemPR,
    modifier: Modifier = Modifier
) {
    val colors = LocalTaminColors.current
    val isActive = dependent.status.contains("فعال", true) || dependent.status == "1"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dependent.fullName.ifBlank { "نام ثبت نشده" },
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusPill(
                    text = dependent.status.ifBlank { if (isActive) "فعال" else "غیرفعال" },
                    containerColor = if (isActive) colors.greenBg else colors.dangerBg,
                    contentColor = if (isActive) colors.greenText else colors.dangerText
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "نسبت: ${dependent.relationDescription.ifBlank { "-" }}",
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )
                Row {
                    Text(
                        text = "کد ملی: ",
                        color = colors.textSecondary,
                        fontSize = 14.sp
                    )
                    NumericText(
                        text = dependent.nationalCode,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}
