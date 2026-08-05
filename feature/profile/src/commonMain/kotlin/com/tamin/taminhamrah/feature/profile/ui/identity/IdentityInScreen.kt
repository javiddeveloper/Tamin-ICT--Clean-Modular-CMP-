package com.tamin.taminhamrah.feature.profile.ui.identity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import com.tamin.taminhamrah.feature.profile.ui.identity.components.IdentityCard
import com.tamin.taminhamrah.feature.profile.ui.identity.components.IdentityDimens
import com.tamin.taminhamrah.feature.profile.ui.identity.components.IdentitySections
import com.tamin.taminhamrah.feature.profile.ui.identity.components.RegistryVerifiedNotice
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInEvent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInIntent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInUiState
import com.tamin.taminhamrah.feature.profile.ui.identity.model.toSections
import com.tamin.taminhamrah.model.identity.IdentityInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toGenderLabel
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.identity_back
import taminx.core.core_ui.identity_nationality_value
import taminx.core.core_ui.identity_title
import taminx.core.core_ui.identity_value_not_registered

@Composable
fun IdentityInRoute(
    viewModel: IdentityInViewModel,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(IdentityInIntent.LoadIdentity)
    }

    HandleIdentityInEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
    )

    IdentityInScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun HandleIdentityInEvents(
    events: Flow<IdentityInEvent>,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            IdentityInEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
fun IdentityInScreen(
    modifier: Modifier = Modifier,
    state: IdentityInUiState,
    onIntent: (IdentityInIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    // Folds the card from the body's drag, snapping on release. Read only inside the card's
    // layout/draw lambdas, so the fold never recomposes the screen.
    val collapse = rememberCollapsingHeaderState(IdentityDimens.headerCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }

    // Hoisted so the header's lambda captures the callback rather than being rebuilt each time —
    // an inline lambda here is a new instance per recomposition and stops the header skipping.
    val onBack = remember(onIntent) { { onIntent(IdentityInIntent.OnBackClicked) } }

    // Resolved here because the record is laid out inside a remember, where composition — and so a
    // resource lookup — is not available.
    val nationality = stringResource(Res.string.identity_nationality_value)
    val absentValue = stringResource(Res.string.identity_value_not_registered)
    val info = state.identityInfo
    val gender = stringResource(info?.gender.toGenderLabel())
    val mobile = state.mobile
    val email = state.email
    val sections = remember(info, nationality, gender, absentValue, mobile, email) {
        info?.toSections(nationality, gender, absentValue, mobile, email)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // The drag folds the card first, then scrolls the body, and only what neither
                // wanted reaches the rubber band — so the fold always wins over the bounce.
                .nestedScroll(collapse.nestedScrollConnection)
                .verticalScroll(scrollState, overscrollEffect = rememberJellyOverscroll()),
        ) {
            // Stands in for the floating header, which is measured rather than fixed.
            Spacer(modifier = Modifier.reservedHeight { headerHeightPx })

            when {
                state.isLoading && sections == null -> LoadingStateOverlay()

                sections != null -> {
                    RegistryVerifiedNotice(
                        modifier = Modifier.padding(horizontal = Spacing.page),
                    )
                    IdentitySections(
                        sections = sections,
                        modifier = Modifier.padding(horizontal = Spacing.page),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }

        ErrorStateView(
            message = state.error,
            onRetry = { onIntent(IdentityInIntent.LoadIdentity) },
        )

        // The header floats on top so the body passes underneath it as it scrolls away.
        IdentityHeader(
            progress = collapse.progressProvider,
            info = info,
            nationality = nationality,
            gender = gender,
            photo = state.profileImage,
            onBack = onBack,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        )
    }
}

/**
 * The app bar with the insured-person card riding up into it.
 *
 * Takes the record's fields apart at this level so the card below is handed plain strings and can
 * skip whenever they are unchanged.
 */
@Composable
private fun IdentityHeader(
    progress: () -> Float,
    info: IdentityInfoPR?,
    nationality: String,
    gender: String,
    photo: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = stringResource(Res.string.identity_title),
            centerTitle = true,
            background = taminTopAppBarGradient(LocalTaminColors.current.profileGradientStops),
            cornerRadius = IdentityDimens.headerCorner,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(Res.string.identity_back),
                    onClick = onBack,
                )
            },
            bottomPadding = IdentityDimens.cardOverlap + IdentityDimens.cardHeaderGap,
        )
        if (info != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .rideUpIntoHeader(
                        progress = progress,
                        expandedOverlap = IdentityDimens.cardOverlap,
                        collapsedOverlap = IdentityDimens.cardCollapsedOverlap,
                    ),
            ) {
                IdentityCard(
                    firstName = info.firstName,
                    lastName = info.lastName,
                    fullName = info.fullName,
                    fatherName = info.fatherName,
                    ssn = info.ssn,
                    nationalId = info.nationalId,
                    dateOfBirth = info.dateOfBirthFormatted,
                    photo = photo,
                    collapseProgress = progress,
                    modifier = Modifier.padding(horizontal = Spacing.page),
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
fun PreviewIdentityInScreen() {
    PreviewRtlThemeContent {
        IdentityInScreen(
            state = IdentityInUiState(identityInfo = PreviewIdentity),
            onIntent = {},
        )
    }
}

private val PreviewIdentity = IdentityInfoPR(
    cityOfBirthId = "",
    cityOfIssueId = "",
    countryId = "",
    dateOfBirth = 0L,
    dateOfBirthFormatted = "1351/11/15",
    fatherName = "ابوالقاسم",
    firstName = "سعید",
    lastName = "مختاری اسفندواجانی",
    fullName = "سعید مختاری اسفندواجانی",
    gender = "M",
    id = 0,
    idCardNumber = "15324",
    idCardSerial = "",
    idCardSerial1 = "491",
    idCardSerial2 = "911600",
    nationalId = "0060241721",
    ssn = "2531731185",
    cityOfBirthName = "1691",
    cityOfIssueName = "1691",
)
