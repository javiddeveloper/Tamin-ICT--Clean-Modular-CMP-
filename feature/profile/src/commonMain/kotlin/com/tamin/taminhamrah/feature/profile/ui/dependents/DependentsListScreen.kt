package com.tamin.taminhamrah.feature.profile.ui.dependents

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListEvent
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListIntent
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListState
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import androidx.compose.ui.draw.rotate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_show_more
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_user
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_retry
import taminx.core.core_ui.identity_field_birth_date
import taminx.core.core_ui.identity_field_father_name
import taminx.core.core_ui.identity_field_national_code
import taminx.core.core_ui.dependents_list_title
import taminx.core.core_ui.dependents_list_header_subtitle
import taminx.core.core_ui.dependents_list_info_banner
import taminx.core.core_ui.dependents_list_btn_add_new
import taminx.core.core_ui.dependents_list_section_covered
import taminx.core.core_ui.dependents_list_empty_title
import taminx.core.core_ui.dependents_list_unregistered_name
import taminx.core.core_ui.dependents_list_status
import taminx.core.core_ui.dependents_list_insurance_id

@Composable
fun DependentsListRoute(
    viewModel: DependentsListViewModel = koinViewModel(),
    onNavigateToAddDependent: () -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(DependentsListIntent.InitData)
    }

    HandleDependentsListEvents(
        events = viewModel.events,
        onNavigateToAddDependent = onNavigateToAddDependent,
        snackbarHostState = snackbarHostState
    )

    DependentsListScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun HandleDependentsListEvents(
    events: Flow<DependentsListEvent>,
    onNavigateToAddDependent: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            DependentsListEvent.NavigateToAddDependentWizard -> onNavigateToAddDependent()
            is DependentsListEvent.ShowToast -> snackbarHostState.showSnackbar(event.message)
            is DependentsListEvent.ShowErrorDialog -> snackbarHostState.showSnackbar(event.message)
        }
    }
}

@Composable
fun DependentsListScreen(
    state: DependentsListState,
    onIntent: (DependentsListIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.dependents_list_title),
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onBackClicked,
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
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_tamin_user))
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.dependents_list_header_subtitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .navigationBarsPadding()
        ) {
            when {
                state.isLoading && state.dependentsList.isEmpty() -> LoadingStateOverlay()
                state.error != null -> ErrorStateView(
                    message = state.error,
                    onRetry = { onIntent(DependentsListIntent.OnRefreshClicked) }
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = Spacing.lg,
                        bottom = Spacing.xxl,
                        start = Spacing.page,
                        end = Spacing.page
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    item {
                        BannerCard(
                            message = stringResource(Res.string.dependents_list_info_banner),
                            type = BannerType.Info
                        )
                    }
                    item {
                        TaminFilledButton(
                            text = stringResource(Res.string.dependents_list_btn_add_new),
                            onClick = { onIntent(DependentsListIntent.OnAddDependentClicked) },
                            icon = Icons.Filled.Add
                        )
                    }
                    item {
                        SectionLabel(text = stringResource(Res.string.dependents_list_section_covered))
                    }
                    if (state.dependentsList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                EmptyStateMessage(
                                    icon = vectorResource(Res.drawable.ic_tamin_user),
                                    title = stringResource(Res.string.dependents_list_empty_title),
                                    actionLabel = stringResource(Res.string.action_retry),
                                    onAction = { onIntent(DependentsListIntent.OnRefreshClicked) }
                                )
                            }
                        }
                    } else {
                        items(
                            items = state.dependentsList,
                            key = { it.id }
                        ) { dependent ->
                            DependentCard(
                                dependent = dependent,
                                isExpanded = dependent.id in state.expandedIds,
                                onToggle = { onIntent(DependentsListIntent.OnDependentCardToggled(dependent.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DependentCard(
    dependent: SubdominantItemPR,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTaminColors.current
    val rotation by animateFloatAsState(targetValue = if (isExpanded) -90f else 0f, label = "ChevronRotation")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.lg))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column( modifier = Modifier.fillMaxWidth().weight(0.8f),verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = dependent.fullName.ifBlank { stringResource(Res.string.dependents_list_unregistered_name) },
                    color = colors.textPrimary,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                StatusPill(
                    text = dependent.relationDescription.ifBlank { "-" },
                    containerColor = colors.blueBg,
                    contentColor = colors.blueText
                )
            }
            Box(modifier = Modifier.fillMaxWidth().weight(0.2f) , contentAlignment = Alignment.CenterEnd){
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colors.bgPage)
                        .border(1.dp, colors.border, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_show_more),
                        contentDescription = null,
                        tint = colors.chevron,
                        modifier = Modifier
                            .size(17.dp)
                            .rotate(rotation)
                    )
                }
            }
        }

        if (isExpanded) {
            TaminDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
            ) {
                DetailRow(label = stringResource(Res.string.identity_field_national_code), value = dependent.nationalCode, numeric = true)
                DetailRow(label = stringResource(Res.string.identity_field_birth_date), value = dependent.birthDate.ifBlank { "-" }, numeric = true)
                DetailRow(label = stringResource(Res.string.identity_field_father_name), value = dependent.fatherName.ifBlank { "-" }, numeric = false)
                DetailRow(label = stringResource(Res.string.dependents_list_insurance_id), value = dependent.insuranceId.ifBlank { "-" }, numeric = true)
                DetailRow(label = stringResource(Res.string.dependents_list_status), value = dependent.status.ifBlank { "-" }, numeric = false)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(colors.buttonGradient)
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun DependentsListScreenPreview() {
    PreviewRtlThemeContent {
        DependentsListScreen(
            state = DependentsListState(
                dependentsList = listOf(
                    SubdominantItemPR(
                        id = 1L,
                        fullName = "منصوره آزادی",
                        relationDescription = "همسر",
                        nationalCode = "0073160997",
                        fatherName = "علی",
                        insuranceId = "1346",
                        status = "فعال"
                    ),
                    SubdominantItemPR(
                        id = 2L,
                        fullName = "روناک موسوی",
                        relationDescription = "فرزند دختر",
                        nationalCode = "0052213341",
                        status = "فعال"
                    )
                ),
                expandedIds = setOf(1L)
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DependentsListScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        DependentsListScreen(
            state = DependentsListState(
                dependentsList = listOf(
                    SubdominantItemPR(
                        id = 1L,
                        fullName = "منصوره آزادی",
                        relationDescription = "همسر",
                        nationalCode = "0073160997",
                        fatherName = "علی",
                        insuranceId = "1346",
                        status = "فعال"
                    )
                ),
                expandedIds = setOf(1L)
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
