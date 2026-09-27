package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
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
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.beneficiaries_count
import taminx.core.core_ui.beneficiaries_empty
import taminx.core.core_ui.beneficiaries_title
import taminx.core.core_ui.branch
import taminx.core.core_ui.file_number
import taminx.core.core_ui.ic_tamin_workshop
import taminx.core.core_ui.label_mobile
import taminx.core.core_ui.owner_type_applicant
import taminx.core.core_ui.owner_type_owner
import taminx.core.core_ui.workshop_number
import taminx.core.core_ui.Res as CoreRes

@Composable
fun BeneficiariesRoute(
    viewModel: BeneficiariesViewModel,
    requestNumber: Long?,
    fileNumber: Long?,
    requestDate: String?,
    workshopId: String?,
    branchCode: String?,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) {
        viewModel.sendIntent(
            BeneficiariesIntent.Load(requestNumber, fileNumber, requestDate, workshopId, branchCode)
        )
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
                        icon = vectorResource(CoreRes.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(CoreRes.string.action_back),
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
                content = {
                    BeneficiariesHeroCard(
                        fileNumber = state.fileNumber,
                        workshopId = state.workshopId,
                        branchCode = state.branchCode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.md),
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
                    itemsIndexed(
                        items = state.items,
                        key = { index, item -> "${item.nationalCode}-${item.ownerType}-$index" }
                    ) { _, beneficiary ->
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

/**
 * The پرونده/کارگاه summary sitting on the top bar's own gradient, under the title — same glass
 * treatment as `ViewDetailRequestScreen`'s `RequestSummaryHeroCard` (fixed white-alpha tones on
 * the brand gradient, not theme-varying), just carrying this screen's own fields.
 */
@Composable
private fun BeneficiariesHeroCard(
    fileNumber: Long?,
    workshopId: String?,
    branchCode: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(CornerRadius.xl))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.md, vertical = Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(HeroIconSize)
                .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(CornerRadius.lg)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(CoreRes.drawable.ic_tamin_workshop),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.small),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            HeroInfoLine(
                label = stringResource(CoreRes.string.file_number),
                value = (fileNumber ?: 0).toString(),
            )
            HeroInfoLine(
                label = stringResource(CoreRes.string.workshop_number),
                value = workshopId ?: "-",
                trailingLabel = branchCode?.takeIf { it.isNotBlank() }
                    ?.let { "${stringResource(CoreRes.string.branch)} $it" },
            )
        }
    }
}

private val HeroIconSize = 36.dp

@Composable
private fun HeroInfoLine(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailingLabel: String? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.65f),
        )
        NumericText(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
        if (trailingLabel != null) {
            Text(
                text = "·",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.5f),
            )
            Text(
                text = trailingLabel,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
        }
    }
}

@Composable
private fun BeneficiaryCard(item: BeneficiaryConstructionPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val fullName = listOfNotNull(item.name, item.lastName).joinToString(" ").ifBlank { "-" }
    val avatarBg = if (item.isOwner) colors.blueBg else colors.fuchsiaBlueBg
    val avatarText = if (item.isOwner) colors.blueText else colors.fuchsiaBlue

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BeneficiaryAvatar(
                    letter = fullName.firstOrNull()?.toString().orEmpty(),
                    containerColor = avatarBg,
                    contentColor = avatarText,
                )
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    NumericText(
                        text = item.nationalCode ?: "-",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
            }
            StatusPill(
                text = stringResource(
                    if (item.isOwner) CoreRes.string.owner_type_owner else CoreRes.string.owner_type_applicant,
                ),
                containerColor = if (item.isOwner) colors.blueBg else colors.fuchsiaBlueBg,
                contentColor = if (item.isOwner) colors.blueText else colors.fuchsiaBlue,
            )
        }
        Box(
            modifier = Modifier
                // No top padding: the header row's own bottom padding already spaces it.
                .padding(start = Spacing.md, end = Spacing.md, bottom = Spacing.md)
                .fillMaxWidth()
                .background(
                    colors.bgPage,
                    RoundedCornerShape(CornerRadius.lg),
                )
                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
        ) {
            DetailRow(
                label = stringResource(CoreRes.string.label_mobile),
                value = item.mobile ?: "-",
                valueStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
private fun BeneficiaryAvatar(
    letter: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(IconSize.large)
            .background(containerColor, RoundedCornerShape(CornerRadius.lg)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor,
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
            state = BeneficiariesUiState(
                fileNumber = 123804L,
                workshopId = "2361847",
                branchCode = "7",
                items = PreviewBeneficiaries,
            ),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun BeneficiariesScreenPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        BeneficiariesScreen(
            state = BeneficiariesUiState(
                fileNumber = 123804L,
                workshopId = "2361847",
                branchCode = "7",
                items = PreviewBeneficiaries,
            ),
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
