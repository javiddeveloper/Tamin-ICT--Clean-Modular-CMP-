package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.employerInfo.WorkshopItemPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.StaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_address
import taminx.core.core_ui.employer_info_branch
import taminx.core.core_ui.employer_info_btn_close
import taminx.core.core_ui.employer_info_btn_complete
import taminx.core.core_ui.employer_info_btn_details
import taminx.core.core_ui.employer_info_email
import taminx.core.core_ui.employer_info_legal_badge
import taminx.core.core_ui.employer_info_let_date
import taminx.core.core_ui.employer_info_mobile
import taminx.core.core_ui.employer_info_real_badge
import taminx.core.core_ui.employer_info_real_notice
import taminx.core.core_ui.employer_info_value_missing
import taminx.core.core_ui.employer_info_workshop_code
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.no_items_found

/**
 * Emits the workshop cards straight into the page's lazy list.
 *
 * A `LazyListScope` extension rather than a composable holding its own `Column`: an employer can
 * hold up to a hundred agreements, and a non-lazy column composes and keeps every one of them
 * alive whether it is on screen.
 */
fun LazyListScope.legalWorkshopListSection(
    workshops: ImmutableList<WorkshopItemPR>,
    expandedWorkshopIds: ImmutableSet<String>,
    entranceState: StaggeredEntranceState,
    onToggleExpanded: (String) -> Unit,
    onSelectWorkshop: (WorkshopItemPR) -> Unit,
) {
    if (workshops.isEmpty()) {
        item(key = "workshops-empty") {
            EmptyStateMessage(
                icon = Icons.Outlined.Info,
                title = stringResource(Res.string.no_items_found),
                modifier = Modifier.fillMaxWidth().padding(Spacing.xxl),
            )
        }
        return
    }

    itemsIndexed(workshops, key = { _, item -> item.id }) { index, workshop ->
        WorkshopCardItem(
            workshop = workshop,
            isExpanded = expandedWorkshopIds.contains(workshop.id),
            onToggleExpanded = { onToggleExpanded(workshop.id) },
            onSelectWorkshop = { onSelectWorkshop(workshop) },
            modifier = Modifier
                // The app's own list entrance: the value is read inside a graphicsLayer, so a
                // frame of it costs no recomposition.
                .staggeredItemEntrance(index = index, key = workshop.id, state = entranceState)
                .padding(horizontal = Spacing.lg, vertical = Spacing.xs),
        )
    }
}

@Composable
private fun WorkshopCardItem(
    workshop: WorkshopItemPR,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSelectWorkshop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = CardElevation,
                shape = RoundedCornerShape(18.dp),
                ambientColor = colors.shadowSubtle,
                spotColor = colors.shadowSubtle,
            )
            .clip(RoundedCornerShape(18.dp))
            .background(colors.bgSurface)
            .border(1.dp, colors.border, RoundedCornerShape(18.dp))
            .padding(13.dp),
    ) {
        // Title + Badge Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = workshop.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    lineHeight = 22.sp,
                ),
                modifier = Modifier.weight(1f).padding(end = Spacing.xs),
            )

            // Badge
            val badgeText = if (workshop.isLegal) {
                stringResource(Res.string.employer_info_legal_badge)
            } else {
                stringResource(Res.string.employer_info_real_badge)
            }
            val badgeFg = if (workshop.isLegal) colors.blueText else colors.orangeText
            val badgeBg = if (workshop.isLegal) colors.blueBg else colors.orangeBg
            val badgeBorder = if (workshop.isLegal) colors.blueBorder else colors.orangeBg

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(badgeBg)
                    .border(1.dp, badgeBorder, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = badgeFg,
                        fontSize = 9.5.sp,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(9.dp))

        // Code & Branch 2-column grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            // Workshop Code
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.bgPage)
                    .padding(horizontal = 9.dp, vertical = 7.dp),
            ) {
                Text(
                    text = stringResource(Res.string.employer_info_workshop_code),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = colors.textMuted,
                        fontSize = 9.5.sp,
                    ),
                )
                Spacer(modifier = Modifier.height(2.dp))
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = workshop.code,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                        ),
                    )
                }
            }

            // Branch
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.bgPage)
                    .padding(horizontal = 9.dp, vertical = 7.dp),
            ) {
                Text(
                    text = stringResource(Res.string.employer_info_branch),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = colors.textMuted,
                        fontSize = 9.5.sp,
                    ),
                )
                Spacer(modifier = Modifier.height(2.dp))
                val branchText = workshop.branchLabel
                Text(
                    text = branchText.ifBlank { stringResource(Res.string.employer_info_value_missing) },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Expandable Details
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp)
                    .border(
                        width = 1.dp,
                        color = colors.divider,
                        shape = RoundedCornerShape(0.dp),
                    )
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                DetailRow(
                    label = stringResource(Res.string.employer_info_let_date),
                    value = workshop.letDate.ifBlank { "-" },
                    numeric = true,
                )
                DetailRow(
                    label = stringResource(Res.string.employer_info_email),
                    value = workshop.email.ifBlank { "-" },
                    numeric = true,
                )
                DetailRow(
                    label = stringResource(Res.string.employer_info_mobile),
                    value = workshop.mobile.ifBlank { "-" },
                    numeric = true,
                )
                DetailRow(
                    label = stringResource(Res.string.employer_info_address),
                    value = workshop.address.ifBlank { "-" },
                    numeric = false,
                )
            }
        }

        Spacer(modifier = Modifier.height(9.dp))

        // Toggle Details Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(colors.bgPage)
                .clickable(onClick = onToggleExpanded),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (isExpanded) {
                    stringResource(Res.string.employer_info_btn_close)
                } else {
                    stringResource(Res.string.employer_info_btn_details)
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary,
                ),
            )
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier
                    .size(14.dp)
                    .rotate(if (isExpanded) 90f else -90f),
            )
        }

        // Action: Complete Info Button (for legal) OR Notice (for real)
        if (workshop.isLegal) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = colors.shadowPrimary,
                        spotColor = colors.shadowPrimary,
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.buttonGradient)
                    .clickable(onClick = onSelectWorkshop),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.employer_info_btn_complete),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onGradient,
                        fontSize = 12.5.sp,
                    ),
                )
                Spacer(modifier = Modifier.size(6.dp))
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    contentDescription = null,
                    tint = colors.onGradient,
                    modifier = Modifier.size(15.dp),
                )
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(colors.orangeBg)
                    .border(1.dp, colors.orangeBg, RoundedCornerShape(13.dp))
                    .padding(horizontal = 11.dp, vertical = 9.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.orangeText,
                    modifier = Modifier.size(15.dp).padding(top = 1.dp),
                )
                Text(
                    text = stringResource(Res.string.employer_info_real_notice),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.orangeText,
                        lineHeight = 20.sp,
                        fontSize = 10.5.sp,
                    ),
                    textAlign = TextAlign.Justify,
                )
            }
        }
    }
}

private val CardElevation = 6.dp
