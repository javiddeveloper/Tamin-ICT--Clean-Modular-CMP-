package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeHeader
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeWorkshopSummaryCard
import com.tamin.taminhamrah.model.workshop.LegalRepresentativePR
import com.tamin.taminhamrah.ui.ActionMenuItem
import com.tamin.taminhamrah.ui.RecordActionMenu
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.LabeledBlock
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.ic_setting
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_edit
import taminx.core.core_ui.ic_trash
import taminx.core.core_ui.legal_representative_access_level_label
import taminx.core.core_ui.legal_representative_add_action
import taminx.core.core_ui.legal_representative_all_contracts_value
import taminx.core.core_ui.legal_representative_branch_code_label
import taminx.core.core_ui.legal_representative_cancel_action
import taminx.core.core_ui.legal_representative_collapse_action
import taminx.core.core_ui.legal_representative_delete_action
import taminx.core.core_ui.legal_representative_delete_confirm_action
import taminx.core.core_ui.legal_representative_delete_confirm_message
import taminx.core.core_ui.legal_representative_delete_confirm_title
import taminx.core.core_ui.legal_representative_edit_action
import taminx.core.core_ui.legal_representative_electronic_notification
import taminx.core.core_ui.legal_representative_empty_list_hint
import taminx.core.core_ui.legal_representative_empty_list_message
import taminx.core.core_ui.legal_representative_insured_registration
import taminx.core.core_ui.legal_representative_internet_list
import taminx.core.core_ui.legal_representative_list_section_label
import taminx.core.core_ui.legal_representative_more_details_action
import taminx.core.core_ui.legal_representative_operations_action
import taminx.core.core_ui.legal_representative_selected_contracts_label
import taminx.core.core_ui.legal_representative_start_date_label
import taminx.core.core_ui.legal_representative_workshop_code_label

private enum class LegalRepresentativeAction { Edit, Delete }

@Composable
fun LegalRepresentativeListScreen(
    workshopId: String,
    branchCode: String,
    workshopName: String,
    workshopSubtitle: String,
    ticket: String,
    onBackClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onEditClicked: (LegalRepresentativePR) -> Unit,
    viewModel: LegalRepresentativeListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ON_RESUME also fires on first entry, so this alone covers both the initial load and a
    // re-fetch whenever the user pops back onto this screen after adding, editing, or deleting
    // a representative on the screen above.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.sendIntent(LegalRepresentativeListIntent.Load(workshopId, branchCode, ticket))
    }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is LegalRepresentativeListEvent.NavigateToAdd -> onAddClicked()
            is LegalRepresentativeListEvent.NavigateToEdit -> onEditClicked(event.target)
        }
    }

    val taminColors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        LegalRepresentativeHeader(onBackClicked = onBackClicked) {
            LegalRepresentativeWorkshopSummaryCard(workshopName = workshopName, subtitle = workshopSubtitle)
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.lg)) {
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        Res.string.legal_representative_list_section_label,
                        uiState.representatives.size,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textPrimary,
                )
                AddRepresentativeChip(
                    text = stringResource(Res.string.legal_representative_add_action),
                    onClick = { viewModel.sendIntent(LegalRepresentativeListIntent.AddClicked) },
                )
            }
            Spacer(Modifier.height(Spacing.md))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(
                        color = taminColors.blueText,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    uiState.error != null -> ErrorStateView(
                        message = uiState.error,
                        onDismiss = {},
                        onRetry = {
                            viewModel.sendIntent(
                                LegalRepresentativeListIntent.Load(workshopId, branchCode, ticket)
                            )
                        },
                        modifier = Modifier.align(Alignment.Center),
                    )

                    uiState.representatives.isEmpty() -> LegalRepresentativeEmptyState(
                        modifier = Modifier.align(Alignment.TopCenter),
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        items(uiState.representatives, key = { it.stakeId }) { representative ->
                            LegalRepresentativeCard(
                                representative = representative,
                                isExpanded = uiState.expandedStakeId == representative.stakeId,
                                isMenuOpen = uiState.menuOpenStakeId == representative.stakeId,
                                onToggleExpand = {
                                    viewModel.sendIntent(LegalRepresentativeListIntent.ToggleExpand(representative.stakeId))
                                },
                                onToggleMenu = {
                                    viewModel.sendIntent(
                                        LegalRepresentativeListIntent.ToggleMenu(
                                            if (uiState.menuOpenStakeId == representative.stakeId) null else representative.stakeId
                                        )
                                    )
                                },
                                onDismissMenu = { viewModel.sendIntent(LegalRepresentativeListIntent.ToggleMenu(null)) },
                                onEdit = { viewModel.sendIntent(LegalRepresentativeListIntent.EditClicked(representative)) },
                                onDelete = { viewModel.sendIntent(LegalRepresentativeListIntent.RequestDelete(representative)) },
                            )
                        }
                    }
                }
            }
        }
    }

    val deleteTarget = uiState.deleteTarget
    if (deleteTarget != null) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.legal_representative_delete_confirm_title),
            description = stringResource(
                Res.string.legal_representative_delete_confirm_message,
                deleteTarget.fullName ?: deleteTarget.nationalId,
            ),
            icon = vectorResource(Res.drawable.ic_trash),
            iconTint = taminColors.dangerText,
            iconBackground = taminColors.dangerBorder,
            onDismissRequest = { viewModel.sendIntent(LegalRepresentativeListIntent.CancelDelete) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.legal_representative_delete_confirm_action),
                    onClick = { viewModel.sendIntent(LegalRepresentativeListIntent.ConfirmDelete) },
                    background = Brush.linearGradient(listOf(taminColors.dangerText, taminColors.dangerText)),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(Res.string.legal_representative_cancel_action),
                    onClick = { viewModel.sendIntent(LegalRepresentativeListIntent.CancelDelete) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        )
    }
}

/** Dashed, bordered notice shown in place of the list when a workshop has no representatives yet. */
@Composable
private fun LegalRepresentativeEmptyState(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.xlg)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(taminColors.bgSurface)
            .dashedOutline(taminColors.border, CornerRadius.xlg, Thickness.border)
            .padding(vertical = Spacing.xl, horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = stringResource(Res.string.legal_representative_empty_list_message),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = taminColors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.legal_representative_empty_list_hint),
            style = MaterialTheme.typography.labelSmall,
            color = taminColors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LegalRepresentativeCard(
    representative: LegalRepresentativePR,
    isExpanded: Boolean,
    isMenuOpen: Boolean,
    onToggleExpand: () -> Unit,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val electronicNotificationLabel = stringResource(Res.string.legal_representative_electronic_notification)
    val internetListLabel = stringResource(Res.string.legal_representative_internet_list)
    val insuredRegistrationLabel = stringResource(Res.string.legal_representative_insured_registration)
    val accessLabel = listOfNotNull(
        electronicNotificationLabel.takeIf { representative.hasElectronicNotification },
        internetListLabel.takeIf { representative.hasInternetList },
        insuredRegistrationLabel.takeIf { representative.hasInsuredRegistration },
    ).joinToString(" · ")

    // Spacing around the collapsible details block is applied inside it (not via a blanket
    // `Arrangement.spacedBy` here), so the gap animates away together with the content instead of
    // vanishing in a single frame once AnimatedVisibility fully disposes it after collapsing —
    // see RecordCard.kt for the same convention.
    Column(
        modifier = Modifier.fillMaxWidth().taminSurface().padding(Spacing.lg),
    ) {
        Column {
            Text(
                text = representative.fullName ?: representative.nationalId,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
            )
            val mobile = representative.mobile
            if (!mobile.isNullOrBlank()) {
                NumericText(
                    text = mobile,
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted,
                )
            }
        }

        if (accessLabel.isNotEmpty()) {
            Spacer(Modifier.height(Spacing.md))
            LabeledBlock(
                label = stringResource(Res.string.legal_representative_access_level_label),
                value = accessLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(taminColors.bgPage)
                    .padding(Spacing.md),
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier.padding(top = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                DetailRow(
                    label = stringResource(Res.string.legal_representative_workshop_code_label),
                    value = representative.workshopId,
                )
                DetailRow(
                    label = stringResource(Res.string.legal_representative_branch_code_label),
                    value = representative.branchCode,
                )
                DetailRow(
                    label = stringResource(Res.string.legal_representative_start_date_label),
                    value = representative.startDateLabel,
                )
                if (representative.special) {
                    DetailRow(
                        label = stringResource(Res.string.legal_representative_selected_contracts_label),
                        value = stringResource(Res.string.legal_representative_all_contracts_value),
                        numeric = false,
                    )
                }
            }
        }
        Spacer(Modifier.height(Spacing.md))
        val chevronRotation by animateFloatAsState(
            targetValue = if (isExpanded) -90f else 90f,
            label = "legal-representative-chevron",
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminOutlinedButton(
                text = stringResource(
                    if (isExpanded) Res.string.legal_representative_collapse_action
                    else Res.string.legal_representative_more_details_action
                ),
                onClick = onToggleExpand,
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                iconModifier = Modifier.size(IconSize.small).graphicsLayer { rotationZ = chevronRotation },
                iconPosition = IconPosition.End,
                containerColor = taminColors.blueBg,
                borderColor = Color.Transparent,
                contentColor = taminColors.blueText,
                height = 44.dp,
                textStyle = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.weight(0.5f)) {
                TaminFilledButton(
                    text = stringResource(Res.string.legal_representative_operations_action),
                    onClick = onToggleMenu,
                    icon = vectorResource(Res.drawable.ic_setting),
                    iconPosition = IconPosition.End,
                    background = taminColors.iconGradientSuccess,
                    height = 44.dp,
                    textStyle = MaterialTheme.typography.titleSmall,
                )
                RecordActionMenu(
                    expanded = isMenuOpen,
                    items = persistentListOf(
                        ActionMenuItem(
                            value = LegalRepresentativeAction.Edit,
                            label = stringResource(Res.string.legal_representative_edit_action),
                            icon = Res.drawable.ic_tamin_edit,
                        ),
                        ActionMenuItem(
                            value = LegalRepresentativeAction.Delete,
                            label = stringResource(Res.string.legal_representative_delete_action),
                            icon = Res.drawable.ic_trash,
                            isDestructive = true,
                        ),
                    ),
                    onDismiss = onDismissMenu,
                    onSelect = {
                        onDismissMenu()
                        when (it) {
                            LegalRepresentativeAction.Edit -> onEdit()
                            LegalRepresentativeAction.Delete -> onDelete()
                        }
                    },
                )
            }
        }
    }
}


/** Compact pill button sized to its label, sitting beside the section title instead of a full-width CTA. */
@Composable
fun AddRepresentativeChip(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector = Icons.Filled.Add,
    iconPosition: IconPosition = IconPosition.Start,
) {
    val taminColors = LocalTaminColors.current
    val iconContent: @Composable () -> Unit = {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(IconSize.small),
        )
    }
    val label: @Composable () -> Unit = {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(taminColors.buttonGradient)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.page, vertical = Spacing.smPlus),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (iconPosition == IconPosition.Start) {
            iconContent()
            label()
        } else {
            label()
            iconContent()
        }
    }
}
