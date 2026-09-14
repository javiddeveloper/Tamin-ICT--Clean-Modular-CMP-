package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui


import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionFileCard
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionInsuranceListSkeleton
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionSearchCard
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionSearchEmptyState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionSearchFilterChipRow
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionUserInfoCard
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.motion.ScrollMotionState
import com.tamin.taminhamrah.ui.motion.motionFade
import com.tamin.taminhamrah.ui.motion.motionParallax
import com.tamin.taminhamrah.ui.motion.motionScale
import com.tamin.taminhamrah.ui.motion.rememberMotionSnapFlingBehavior
import com.tamin.taminhamrah.ui.motion.rememberScrollMotionState
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.construction_insurance_subtitle
import taminx.core.core_ui.construction_insurance_title
import taminx.core.core_ui.no_construction_files_found
import androidx.compose.ui.unit.lerp as dpLerp
import taminx.core.core_ui.Res as CoreRes

@Composable
fun ConstructionInsuranceRoute(
    viewModel: ConstructionInsuranceViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
    val toaster = LocalToaster.current

    LaunchedEffect(motionState, lazyListState) {
        motionState.observeLazyListState(lazyListState)
    }

    ConstructionInsuranceEvents(
        events = viewModel.events,
        toaster = toaster
    )

    ConstructionInsuranceScreen(
        state = uiState,
        lazyListState = lazyListState,
        motionState = motionState,
        onBackClicked = onBackClicked,
        onIntent = viewModel::sendIntent,
        modifier = modifier
    )
}

@Composable
fun ConstructionInsuranceEvents(
    events: Flow<ConstructionInsuranceEvent>,
    toaster: ToasterState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is ConstructionInsuranceEvent.ShowToast -> {
                toaster.error(event.message)
            }

            is ConstructionInsuranceEvent.NavigateToDetails -> {
            }
        }
    }
}

@Composable
fun ConstructionInsuranceScreen(
    state: ConstructionInsuranceState,
    lazyListState: LazyListState,
    motionState: ScrollMotionState,
    onBackClicked: () -> Unit,
    onIntent: (ConstructionInsuranceIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val headerProgress = motionState.progress
    val hasActiveFilter = state.appliedFileNoQuery.isNotBlank() ||
        state.appliedReqNoQuery.isNotBlank() ||
        state.appliedWorkshopIdQuery.isNotBlank() ||
        state.appliedBranchCodeQuery.isNotBlank()
    val decaySpec = rememberSplineBasedDecay<Float>()
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
    val snapFlingBehavior = rememberMotionSnapFlingBehavior(
        lazyListState = lazyListState,
        motionState = motionState,
        decayAnimationSpec = decaySpec
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = taminColors.bgPage,
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TaminTopAppBar(
                        title = "",
                        navigationIcon = {
                            TaminTopAppBarButton(
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(CoreRes.string.action_back),
                                onClick = onBackClicked,
                                bordered = true
                            )
                        },
                        background = profileGradientBrush,
                        bottomPadding = dpLerp(Spacing.xxxl, Spacing.sm, headerProgress)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            DecorativeBackgroundCircle(
                                size = 190.dp,
                                xOffset = 450.dp,
                                yOffset = (-150).dp
                            )

                            Text(
                                text = stringResource(CoreRes.string.construction_insurance_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-30).dp)
                                    .motionFade(
                                        motionState,
                                        startProgress = 0.2f,
                                        endProgress = 0.7f
                                    )
                                    .motionParallax(motionState, parallaxDistance = 20.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dpLerp(Spacing.lg, Spacing.none, headerProgress))
                                    .motionParallax(motionState, parallaxDistance = 45.dp)
                                    .motionScale(
                                        state = motionState,
                                        minScale = 0.8f,
                                        maxScale = 1f,
                                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AnimatedRingHeaderIcon(icon = Icons.Outlined.Home)
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = stringResource(CoreRes.string.construction_insurance_subtitle),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier.height(
                                dpLerp(Spacing.sm, Spacing.none, headerProgress)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(dpLerp(Spacing.xxxl, 25.dp, headerProgress)))
                }

                ConstructionUserInfoCard(
                    userName = state.userName,
                    nationalCode = state.nationalCode,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = Spacing.page)
                        .coloredShadow(
                            color = taminColors.shadowSubtle,
                            borderRadius = CornerRadius.lg,
                            blurRadius = Elevation.lg,
                            offsetY = Spacing.xs
                        )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = lazyListState,
                flingBehavior = snapFlingBehavior,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = Spacing.page,
                    vertical = Spacing.md
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                overscrollEffect = rememberJellyOverscroll(),
            ) {
                item {
                    ConstructionSearchCard(
                        itemCount = state.items.size,
                        isExpanded = state.isSearchExpanded,
                        isNoticeVisible = state.isNoticeVisible,
                        fileNoQuery = state.fileNoQuery,
                        reqNoQuery = state.reqNoQuery,
                        workshopIdQuery = state.workshopIdQuery,
                        branchCodeQuery = state.branchCodeQuery,
                        onToggleExpanded = {
                            onIntent(
                                ConstructionInsuranceIntent.ToggleSearchExpanded(
                                    it
                                )
                            )
                        },
                        onFileNoChanged = {
                            onIntent(
                                ConstructionInsuranceIntent.OnFileNoQueryChanged(
                                    it
                                )
                            )
                        },
                        onReqNoChanged = {
                            onIntent(
                                ConstructionInsuranceIntent.OnReqNoQueryChanged(
                                    it
                                )
                            )
                        },
                        onWorkshopIdChanged = {
                            onIntent(
                                ConstructionInsuranceIntent.OnWorkshopIdQueryChanged(
                                    it
                                )
                            )
                        },
                        onBranchCodeChanged = {
                            onIntent(
                                ConstructionInsuranceIntent.OnBranchCodeQueryChanged(
                                    it
                                )
                            )
                        },
                        onExecuteSearch = { onIntent(ConstructionInsuranceIntent.ExecuteSearch) },
                        onResetSearch = { onIntent(ConstructionInsuranceIntent.ResetSearch) },
                        onInfoIconClicked = {
                            onIntent(ConstructionInsuranceIntent.ToggleNoticeVisibility)
                        }
                    )
                }

                if (state.isLoading && state.items.isEmpty()) {
                    item {
                        ConstructionInsuranceListSkeleton(modifier = Modifier.padding(top = Spacing.sm))
                    }
                }
                if (!state.isLoading && state.error == null) {
                    if (hasActiveFilter) {
                        item {
                            ConstructionSearchFilterChipRow(
                                fileNoQuery = state.appliedFileNoQuery,
                                reqNoQuery = state.appliedReqNoQuery,
                                workshopIdQuery = state.appliedWorkshopIdQuery,
                                branchCodeQuery = state.appliedBranchCodeQuery,
                                onClear = { onIntent(ConstructionInsuranceIntent.ResetSearch) },
                            )
                        }
                    }

                    items(
                        items = state.items,
                        key = { it.fileNumber ?: it.hashCode() }
                    ) { file ->
                        ConstructionFileCard(item = file)
                    }
                }

                if (!state.isLoading && state.error == null && state.items.isEmpty()) {
                    item {
                        if (hasActiveFilter) {
                            ConstructionSearchEmptyState(modifier = Modifier.padding(top = Spacing.md))
                        } else {
                            TaminEmptyState(
                                message = stringResource(CoreRes.string.no_construction_files_found),
                                modifier = Modifier.padding(top = Spacing.xxl),
                            )
                        }
                    }
                }

            }

            if (state.isLoading && state.items.isNotEmpty()) {
                LoadingStateOverlay()
            }
        }
    }
}


private val previewItems = persistentListOf(
    ConstructionFilePR(
        fileNumber = 4479890882L,
        requestNumber = 123456789L,
        requestDate = "14020901",
        workshopInfo = WorkshopIdInfoPR(
            workshopRegisterDate = "14020901",
            workshopId = "9028222442",
            brhCode = "6400"
        ),
        postalCode = "9187955511",
        address = "مشهد - بلوار وکیل آباد",
        mainPlaque = 12,
        subPlaque = 3,
        block = 5L,
        propertyConstruction = 1400,
        apartment = 2,
        trade = 1,
        partPlaque = 4,
        sumOfComplications = 250_000L,
        debitNumber = "123456789012",
        totalPayment = 1_850_000L,
        meterage = 120,
        debitStatusCode = "51",
        protrusion = 50_000L,
        applicationFees = 200_000L,
        residentialServiceInfrastructureFees = 150_000L,
        excessDensitySurchargeFees = 100_000L,
        increasePropertyValue = 300_000L,
        issuanceFencingWallConstructionFees = 80_000L,
        coveredClause3Fees = 60_000L,
        article100 = 40_000L,
        paymentDeadLine = "14021001"
    ),
    ConstructionFilePR(
        fileNumber = 4479890883L,
        requestNumber = 123456790L,
        requestDate = "14020815",
        workshopInfo = WorkshopIdInfoPR(
            workshopRegisterDate = "14020815",
            workshopId = "6393610019",
            brhCode = "6400"
        ),
        postalCode = "9187955512",
        address = "مشهد - احمدآباد",
        mainPlaque = 45,
        subPlaque = 1,
        block = 2L,
        propertyConstruction = 1395,
        apartment = 4,
        trade = 0,
        partPlaque = 2,
        sumOfComplications = 180_000L,
        debitNumber = null,
        totalPayment = 1_200_000L,
        meterage = 95,
        debitStatusCode = "10",
        protrusion = 30_000L,
        applicationFees = 150_000L,
        residentialServiceInfrastructureFees = 120_000L,
        excessDensitySurchargeFees = 80_000L,
        increasePropertyValue = 220_000L,
        issuanceFencingWallConstructionFees = 60_000L,
        coveredClause3Fees = 40_000L,
        article100 = 20_000L,
        paymentDeadLine = "14020920"
    ),
)

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenPreview() {
    PreviewRtlThemeContent {
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = previewItems,
            ),
            lazyListState = lazyListState,
            motionState = motionState,
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = previewItems,
            ),
            lazyListState = lazyListState,
            motionState = motionState,
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenEmptyPreview() {
    PreviewRtlThemeContent {
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = emptyList<ConstructionFilePR>().toImmutableList(),
            ),
            lazyListState = lazyListState,
            motionState = motionState,
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenLoadingPreview() {
    PreviewRtlThemeContent {
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                isLoading = true,
                items = emptyList<ConstructionFilePR>().toImmutableList(),
            ),
            lazyListState = lazyListState,
            motionState = motionState,
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenSearchEmptyPreview() {
    PreviewRtlThemeContent {
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = emptyList<ConstructionFilePR>().toImmutableList(),
                fileNoQuery = "4479890882",
                workshopIdQuery = "9028222442",
                appliedFileNoQuery = "4479890882",
                appliedWorkshopIdQuery = "9028222442",
            ),
            lazyListState = lazyListState,
            motionState = motionState,
            onBackClicked = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceScreenSearchResultsPreview() {
    PreviewRtlThemeContent {
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        ConstructionInsuranceScreen(
            state = ConstructionInsuranceState(
                userName = "حسین توکلی کرمانی",
                nationalCode = "۴۴۷۹۸۹۰۸۸۲",
                items = previewItems,
                fileNoQuery = "4479890882",
                appliedFileNoQuery = "4479890882",
            ),
            lazyListState = lazyListState,
            motionState = motionState,
            onBackClicked = {},
            onIntent = {}
        )
    }
}
