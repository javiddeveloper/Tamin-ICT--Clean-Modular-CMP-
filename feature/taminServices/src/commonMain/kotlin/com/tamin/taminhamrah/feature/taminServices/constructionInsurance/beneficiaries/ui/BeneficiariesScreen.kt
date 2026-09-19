package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesUiState
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.paging.OnLoadMore
import com.tamin.taminhamrah.ui.paging.PagingFooter
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.beneficiaries_count
import taminx.core.core_ui.beneficiaries_empty
import taminx.core.core_ui.beneficiaries_title
import taminx.core.core_ui.ic_person_profile
import taminx.core.core_ui.label_mobile
import taminx.core.core_ui.label_national_code
import taminx.core.core_ui.owner_type_applicant
import taminx.core.core_ui.owner_type_owner
import taminx.core.core_ui.Res as CoreRes

@Composable
fun BeneficiariesRoute(
    viewModel: BeneficiariesViewModel,
    requestNumber: Long?,
    fileNumber: Long?,
    requestDate: String?,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) {
        viewModel.sendIntent(BeneficiariesIntent.Load(requestNumber, fileNumber, requestDate))
    }

    BeneficiariesEvents(events = viewModel.events, toaster = toaster, onBackClicked = onBackClicked)

    BeneficiariesScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
fun BeneficiariesEvents(
    events: Flow<BeneficiariesEvent>,
    toaster: ToasterState,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            BeneficiariesEvent.NavigateBack -> onBackClicked()
            is BeneficiariesEvent.ShowError -> toaster.error(event.message)
        }
    }
}

@Composable
fun BeneficiariesScreen(
    state: BeneficiariesUiState,
    onIntent: (BeneficiariesIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val listState = rememberLazyListState()

    listState.OnLoadMore(
        enabled = !state.endReached && state.paginationError == null,
    ) {
        onIntent(BeneficiariesIntent.LoadNextPage)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(CoreRes.string.beneficiaries_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(CoreRes.string.action_back),
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                state.isLoading && state.items.isEmpty() -> BeneficiariesSkeleton(
                    modifier = Modifier.fillMaxSize(),
                )

                state.items.isEmpty() && state.paginationError != null -> PagingFooter(
                    isLoadingNextPage = false,
                    error = state.paginationError,
                    onRetry = { onIntent(BeneficiariesIntent.RetryNextPage) },
                    modifier = Modifier.align(Alignment.Center),
                )

                state.items.isEmpty() -> EmptyStateMessage(
                    icon = Icons.Filled.Groups,
                    title = stringResource(CoreRes.string.beneficiaries_empty),
                    showIconTile = true,
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = Spacing.page,
                        vertical = Spacing.md
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    item {
                        SectionLabel(
                            text = stringResource(
                                CoreRes.string.beneficiaries_count,
                                state.items.size.toString().toPersianDigits(),
                            ),
                            modifier = Modifier.padding(bottom = Spacing.xs),
                        )
                    }
                    items(
                        items = state.items,
                        key = { it.nationalCode ?: it.hashCode() }) { beneficiary ->
                        BeneficiaryCard(item = beneficiary)
                    }
                    item {
                        PagingFooter(
                            isLoadingNextPage = state.isLoadingNextPage,
                            error = state.paginationError,
                            onRetry = { onIntent(BeneficiariesIntent.RetryNextPage) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BeneficiaryCard(item: BeneficiaryConstructionPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val fullName = listOfNotNull(item.name, item.lastName).joinToString(" ").ifBlank { "-" }
    val avatarGradient = if (item.isOwner) colors.iconGradientPrimary else colors.alertGradient

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 20.dp,
                offsetY = 8.dp
            )
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BeneficiaryAvatar(background = avatarGradient)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                StatusPill(
                    text = stringResource(
                        if (item.isOwner) CoreRes.string.owner_type_owner else CoreRes.string.owner_type_applicant,
                    ),
                    containerColor = if (item.isOwner) colors.blueBg else colors.orangeBg,
                    contentColor = if (item.isOwner) colors.blueText else colors.orangeText,
                )
            }
        }
        TaminDivider()
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.none)) {
            DetailRow(
                label = stringResource(CoreRes.string.label_national_code),
                value = item.nationalCode ?: "-"
            )
            DetailRow(
                label = stringResource(CoreRes.string.label_mobile),
                value = item.mobile ?: "-"
            )
        }
    }
}

/** Avatar tile, tinted by ownership role so it reads the same signal as the pill beside it. */
@Composable
private fun BeneficiaryAvatar(background: Brush, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(IconSize.xlarge)
            .background(background, RoundedCornerShape(CornerRadius.avatarTile)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.size(18.dp),
            painter = painterResource(CoreRes.drawable.ic_person_profile),
            contentDescription = ""
        )
    }
}


// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewBeneficiaries = kotlinx.collections.immutable.persistentListOf(
    BeneficiaryConstructionPR(
        nationalCode = "0930123451",
        ownerType = "01",
        name = "علی",
        lastName = "توکلی",
        mobile = "09123456701",
    ),
    BeneficiaryConstructionPR(
        nationalCode = "0930123452",
        ownerType = "02",
        name = "زهرا",
        lastName = "احمدی",
        mobile = "09123456702",
    ),
)

@PreviewRtlTheme
@Composable
private fun BeneficiariesScreenPreview() {
    PreviewRtlThemeContent {
        BeneficiariesScreen(
            state = BeneficiariesUiState(items = PreviewBeneficiaries),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun BeneficiariesScreenEmptyPreview() {
    PreviewRtlThemeContent {
        BeneficiariesScreen(
            state = BeneficiariesUiState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun BeneficiariesScreenLoadingPreview() {
    PreviewRtlThemeContent {
        BeneficiariesScreen(
            state = BeneficiariesUiState(isLoading = true),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
