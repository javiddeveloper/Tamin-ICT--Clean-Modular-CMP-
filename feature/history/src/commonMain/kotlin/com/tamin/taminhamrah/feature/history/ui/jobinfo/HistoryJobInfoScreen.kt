package com.tamin.taminhamrah.feature.history.ui.jobinfo

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
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.history.ui.jobinfo.components.JobInfoCard
import com.tamin.taminhamrah.feature.history.ui.jobinfo.components.JobInfoStatsCard
import com.tamin.taminhamrah.model.history.HistoryJobInfoItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
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
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.unit.lerp as dpLerp

@Composable
fun HistoryJobInfoScreen(
    onBackClicked: () -> Unit,
    viewModel: HistoryJobInfoViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)

    LaunchedEffect(motionState, lazyListState) {
        motionState.observeLazyListState(lazyListState)
    }

    HistoryJobInfoContent(
        uiState = uiState,
        lazyListState = lazyListState,
        motionState = motionState,
        onBackClicked = onBackClicked,
        onRetry = { viewModel.sendIntent(HistoryJobInfoIntent.Retry) }
    )
}

@Composable
fun HistoryJobInfoContent(
    uiState: HistoryJobInfoUiState,
    lazyListState: LazyListState,
    motionState: ScrollMotionState,
    onBackClicked: () -> Unit,
    onRetry: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val clipboardManager = LocalClipboardManager.current
    val headerProgress = motionState.progress
    val decaySpec = rememberSplineBasedDecay<Float>()
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
    val snapFlingBehavior = rememberMotionSnapFlingBehavior(
        lazyListState = lazyListState,
        motionState = motionState,
        decayAnimationSpec = decaySpec
    )

    val workshopCount = uiState.jobInfos.map { it.rwshId }.distinct().size
    val firstEmploymentYear = uiState.jobInfos
        .mapNotNull { it.startDate.takeIf { s -> s.length >= 4 }?.take(4) }
        .minOrNull() ?: ""
    val jobTitleCount = uiState.jobInfos.size

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TaminTopAppBar(
                        title = "",
                        navigationIcon = {
                            TaminTopAppBarButton(
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
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
                                text = "عناوین شغلی",
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
                                AnimatedRingHeaderIcon(icon = Icons.Outlined.Work)
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "عناوین شغلی ثبت‌شده در سوابق بیمه‌ای شما",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier.height(
                                dpLerp(
                                    Spacing.sm,
                                    Spacing.none,
                                    headerProgress
                                )
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(dpLerp(Spacing.xxxl, 25.dp, headerProgress)))
                }

                JobInfoStatsCard(
                    workshopCount = workshopCount,
                    firstEmploymentYear = firstEmploymentYear,
                    jobTitleCount = jobTitleCount,
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
        },
        containerColor = taminColors.bgPage
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.error != null && uiState.jobInfos.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = Spacing.page),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    TextButton(onClick = onRetry) {
                        Text(text = "تلاش مجدد")
                    }
                }
            }

            else -> {
                LazyColumn(
                    state = lazyListState,
                    flingBehavior = snapFlingBehavior,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        horizontal = Spacing.page,
                        vertical = Spacing.md
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    if (uiState.jobInfos.isNotEmpty()) {
                        item {
                            Text(
                                text = "لیست عناوین شغلی",
                                style = MaterialTheme.typography.titleMedium,
                                color = taminColors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = Spacing.md)
                            )
                        }
                    }

                    if (uiState.jobInfos.isEmpty() && !uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = Spacing.xxl),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "عنوان شغلی یافت نشد",
                                    color = taminColors.textMuted,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    items(uiState.jobInfos, key = { it.id }) { jobInfo ->
                        JobInfoCard(
                            jobInfo = jobInfo,
                            onCopy = { text -> clipboardManager.setText(AnnotatedString(text)) }
                        )
                    }
                }
            }
        }
    }
}


@PreviewRtlTheme
@Composable
private fun HistoryJobInfoScreenPreview() {
    PreviewRtlThemeContent {
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        HistoryJobInfoContent(
            uiState = HistoryJobInfoUiState(
                jobInfos = listOf(
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 1,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0081631829",
                        rwshName = "شرکت صنایع دما بخار مشهد",
                        brhcode = "6400",
                        id = 2,
                        jobDesc = "کارمند اداری ۱",
                        startDate = "139810",
                        rwshId = "6393610019"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 3,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 4,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 5,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 6,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 7,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 8,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                )
            ),
            lazyListState = lazyListState,
            motionState = motionState,
            onBackClicked = {},
            onRetry = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryJobInfoScreenPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        HistoryJobInfoContent(
            uiState = HistoryJobInfoUiState(
                jobInfos = listOf(
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 1,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0081631829",
                        rwshName = "شرکت صنایع دما بخار مشهد",
                        brhcode = "6400",
                        id = 2,
                        jobDesc = "کارمند اداری ۱",
                        startDate = "139810",
                        rwshId = "6393610019"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 3,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 4,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 5,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 6,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 7,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 8,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                )
            ),
            lazyListState = lazyListState,
            motionState = motionState,
            onBackClicked = {},
            onRetry = {}
        )
    }
}
