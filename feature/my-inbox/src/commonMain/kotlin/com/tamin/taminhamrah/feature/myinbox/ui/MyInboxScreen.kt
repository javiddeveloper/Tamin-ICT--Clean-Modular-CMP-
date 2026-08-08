package com.tamin.taminhamrah.feature.myinbox.ui

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FiberManualRecord
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.setText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.myinbox.ui.components.InboxItemCard
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxEvent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxIntent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.SegmentedRadialGauge
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.motion.rememberMotionSnapFlingBehavior
import com.tamin.taminhamrah.ui.motion.rememberScrollMotionState
import com.tamin.taminhamrah.ui.theme.ListShapes.bottom
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun MyInboxScreen(
    viewModel: MyInboxViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(MyInboxIntent.LoadInbox)
    }

    HandleMyInboxEvents(
        events = viewModel.events,
        onNavigateBack = onNavigateBack
    )

    MyInboxContent(
        state = uiState,
        onIntent = viewModel::sendIntent
    )

}

@Composable
private fun HandleMyInboxEvents(
    events: Flow<MyInboxEvent>,
    onNavigateBack: () -> Unit
) {
    val toaster = LocalToaster.current
    events.collectWithLifecycleAware { event ->
        when (event) {
            is MyInboxEvent.NavigateBack -> onNavigateBack()
            is MyInboxEvent.CopyToClipboard -> {
                toaster.success("کد پیگیری کپی شد")
            }
        }
    }
}

@Composable
private fun MyInboxContent(
    modifier: Modifier = Modifier,
    state: MyInboxUiState,
    onIntent: (MyInboxIntent) -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val lazyListState = rememberLazyListState()
    val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
    val decaySpec = rememberSplineBasedDecay<Float>()
    val snapFlingBehavior = rememberMotionSnapFlingBehavior(
        lazyListState = lazyListState,
        motionState = motionState,
        decayAnimationSpec = decaySpec
    )

    LaunchedEffect(motionState, lazyListState) {
        motionState.observeLazyListState(lazyListState)
    }

    val colors = LocalTaminColors.current
    val profileGradientBrush = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Column {
                TaminTopAppBar(
                    title = "صندوق شخصی من",
                    centerTitle = true,
                    background = profileGradientBrush,
                    bottomPadding = 20.dp,
                    shape = RoundedCornerShape(
                        bottomStart = 40.dp,
                        bottomEnd = 40.dp
                    ),
                    navigationIcon = {
                        TaminTopAppBarButton(
                            icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            contentDescription = null,
                            onClick = { onIntent(MyInboxIntent.OnBackClicked) },
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
                            Text(
                                text = "فضایی برای نگهداری و اشتراک‌گذاری اسناد و مکاتبات",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textHeaderSubtitle
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.page)
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val translationY = (-40).dp.roundToPx()
                            layout(placeable.width, placeable.height + translationY) {
                                placeable.placeRelative(0, translationY)
                            }
                        }
                        .taminSurface(cornerRadius = 24.dp)
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val dynamicAspectRatio = 1.9f + (motionState.progress * 3.2f)
                    val progress = remember(state.size) {
                        val usage = state.size?.usageMb?.toFloatOrNull() ?: 0f
                        val total = state.size?.totalMb?.toFloatOrNull() ?: 1f
                        if (total > 0) usage / total else 0f
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(dynamicAspectRatio)
                    ) {
                        SegmentedRadialGauge(
                            progress = progress,
                            morphProgress = motionState.progress,
                            modifier = Modifier.fillMaxSize(),
                            segmentCount = 25,
                            segmentWidth = 10.dp,
                            segmentHeight = 40.dp,
                            padding = 8.dp,
                            progressGradient = Brush.linearGradient(
                                colors = listOf(Color(0xFF6DCED0), Color(0xFF2FB9BC))
                            ),
                            inactiveGradient = Brush.linearGradient(
                                colors = listOf(Color(0xFFD1D9E6), Color(0xFFF2F4F8), Color(0xFFD1D9E6))
                            )
                        )
                        // حالت باز شده (Expanded)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(top = 40.dp)
                                .graphicsLayer {
                                    alpha = 1f - motionState.progress
                                    translationY = -motionState.progress * 40f
                                }
                        ) {
                            Text(
                                text = state.size?.usageLabel ?: "۰ مگابایت",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary,
                                modifier = Modifier.padding(bottom = 6.dp, top = 40.dp)
                            )
                            Text(
                                text = "از ${state.size?.totalLabel ?: "۱۰ مگابایت"} مصرف شده",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textMuted
                            )
                            val freePercent = ((1f - progress) * 100).toInt()
                            StatusPill(
                                text = "$freePercent٪ فضای آزاد",
                                containerColor = colors.greenBg,
                                contentColor = colors.greenText,
                                icon = Icons.Default.FiberManualRecord,
                                modifier = Modifier.padding(top = 20.dp)
                            )
                        }
                    }

                    // حالت جمع شده (Collapsed) - نمایش در زیر گیج
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = motionState.progress
                                translationY = (1f - motionState.progress) * 20f
                            }
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                // تغییر ارتفاع بر اساس میزان جمع شدن برای جلوگیری از ایجاد فضای خالی در حالت باز
                                val currentHeight = (placeable.height * motionState.progress).toInt()
                                layout(placeable.width, currentHeight) {
                                    placeable.placeRelative(0, 0)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "میزان فضای مصرف شده",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NumericText(
                                text = state.size?.usageLabel ?: "۰ مگابایت",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = " / ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            NumericText(
                                text = state.size?.totalLabel ?: "۱۰ مگابایت",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textMuted
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.isLoading && state.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (state.items.isEmpty()) {
            TaminEmptyState(
                message = "پیامی در صندوق شما یافت نشد",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                state = lazyListState,
                flingBehavior = snapFlingBehavior,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                items(state.items, key = { it.id }) { item ->
                    InboxItemCard(
                        item = item,
                        actions = item.actions,
                        onActionSelect = { actionValue ->
                            onIntent(MyInboxIntent.OnItemActionClicked(item.id, actionValue))
                        },
                        onCopyClick = {
                            clipboardManager.setText(AnnotatedString(item.id.toString()))
                            onIntent(MyInboxIntent.OnCopyClicked(item.id))
                        },
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun MyInboxScreenPreviewLight() {
    PreviewRtlThemeContent(darkTheme = false) {
        MyInboxContent(
            state = MyInboxUiState(
                items = listOf(
                    PersonalInboxItemPR(
                        id = 1,
                        refCode = "۳۱۸۶۲۲۶۲۱",
                        requestDate = "۱۴۰۴/۱۲/۱۹",
                        subject = "اعلام سابقه به مؤسسات",
                        seen = true,
                        system = "سازمان تأمین اجتماعی",
                        passwordCode = "۱۲۳۴۵۶",
                        natCode = "22222",
                        permissionPassword = "",
                        mobile = "",
                        email = "",
                    ),

                )
            ),
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun MyInboxScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        MyInboxContent(
            state = MyInboxUiState(

            ),
            onIntent = {}
        )
    }
}
