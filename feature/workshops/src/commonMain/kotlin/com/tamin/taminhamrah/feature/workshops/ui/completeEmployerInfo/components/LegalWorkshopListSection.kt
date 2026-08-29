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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.employerInfo.WorkshopItemPR
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.theme.CornerRadius
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
import taminx.core.core_ui.employer_info_workshop_code
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.no_items_found

private val ButtonGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF3B6FD4), Color(0xFF173D7E)),
)

@Composable
fun LegalWorkshopListSection(
    workshops: ImmutableList<WorkshopItemPR>,
    expandedWorkshopIds: ImmutableSet<String>,
    onToggleExpanded: (String) -> Unit,
    onSelectWorkshop: (WorkshopItemPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (workshops.isEmpty()) {
        EmptyStateMessage(
            icon = Icons.Outlined.Info,
            title = stringResource(Res.string.no_items_found),
            modifier = modifier.fillMaxWidth().padding(Spacing.xxl),
        )
        return
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        workshops.forEach { workshop ->
            val isExpanded = expandedWorkshopIds.contains(workshop.id)
            WorkshopCardItem(
                workshop = workshop,
                isExpanded = isExpanded,
                onToggleExpanded = { onToggleExpanded(workshop.id) },
                onSelectWorkshop = { onSelectWorkshop(workshop) },
            )
        }
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
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x0A0F172A),
                spotColor = Color(0x0A0F172A),
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
                    fontWeight = FontWeight.Bold,
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
            val badgeBorder = if (workshop.isLegal) Color(0xFFDCE7FB) else Color(0xFFF0DCA8)

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
                        fontWeight = FontWeight.Bold,
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
                            fontWeight = FontWeight.Bold,
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
                val branchText = if (workshop.bcode.isNotBlank()) {
                    "${workshop.branch} · ${workshop.bcode}"
                } else {
                    workshop.branch
                }
                Text(
                    text = branchText.ifBlank { "-" },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
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
                    isLtr = true,
                )
                DetailRow(
                    label = stringResource(Res.string.employer_info_email),
                    value = workshop.email.ifBlank { "-" },
                    isLtr = true,
                )
                DetailRow(
                    label = stringResource(Res.string.employer_info_mobile),
                    value = workshop.mobile.ifBlank { "-" },
                    isLtr = true,
                )
                DetailRow(
                    label = stringResource(Res.string.employer_info_address),
                    value = workshop.address.ifBlank { "-" },
                    isLtr = false,
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
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                ),
            )
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
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
                        ambientColor = Color(0x20173D7E),
                        spotColor = Color(0x20173D7E),
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(ButtonGradient)
                    .clickable(onClick = onSelectWorkshop),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.employer_info_btn_complete),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.5.sp,
                    ),
                )
                Spacer(modifier = Modifier.size(6.dp))
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    tint = Color.White,
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
                    .border(1.dp, Color(0xFFF0DCA8), RoundedCornerShape(13.dp))
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
                        color = Color(0xFF8A5C08),
                        lineHeight = 20.sp,
                        fontSize = 10.5.sp,
                    ),
                    textAlign = TextAlign.Justify,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isLtr: Boolean,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = colors.textMuted,
                fontSize = 10.5.sp,
            ),
        )
        if (isLtr) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 11.sp,
                    ),
                )
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 11.sp,
                ),
                textAlign = TextAlign.Left,
            )
        }
    }
}
