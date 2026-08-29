package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoScreenState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoTab
import com.tamin.taminhamrah.model.employerInfo.WorkshopItemPR
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_subtitle
import taminx.core.core_ui.employer_info_tab_legal
import taminx.core.core_ui.employer_info_tab_real
import taminx.core.core_ui.employer_info_title
import taminx.core.core_ui.employer_info_workshop_code
import taminx.core.core_ui.ic_branch
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun EmployerInfoHero(
    screen: CompleteEmployerInfoScreenState,
    tab: CompleteEmployerInfoTab,
    onSelectTab: (CompleteEmployerInfoTab) -> Unit,
    userFullName: String,
    userNationalCode: String,
    selectedWorkshop: WorkshopItemPR?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Hero background area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .background(colors.heroGradient)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(14.dp))
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Text(
                        text = stringResource(Res.string.employer_info_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(modifier = Modifier.size(36.dp))
                }

                if (screen == CompleteEmployerInfoScreenState.LIST) {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    // Centered Icon with subtle concentric circle rings
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Outer ring 1
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape),
                        )
                        // Outer ring 2
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape),
                        )
                        // Icon Box
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.18f))
                                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.ic_branch),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.employer_info_subtitle),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textHeaderSubtitle,
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(34.dp))
                } else if (screen == CompleteEmployerInfoScreenState.LEGAL_FORM && selectedWorkshop != null) {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    // Workshop info chip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                            .padding(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.ic_branch),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(19.dp),
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedWorkshop.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = stringResource(Res.string.employer_info_workshop_code),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.textHeaderSubtitle,
                                    ),
                                )
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Text(
                                        text = selectedWorkshop.code,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.txtNameProfile,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Overlapping User Card (only on List screen)
        if (screen == CompleteEmployerInfoScreenState.LIST) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .offset(y = (-28).dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(CornerRadius.lg),
                            ambientColor = colors.shadowSubtle,
                            spotColor = colors.shadowSubtle,
                        )
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(colors.bgSurface)
                        .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.lg))
                        .padding(horizontal = Spacing.sm, vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = userFullName.ifBlank { "-" },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.blueText,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "نام و نام خانوادگی",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.textMuted,
                            ),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(colors.divider),
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                text = userNationalCode.ifBlank { "-" },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.blueText,
                                ),
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "کد ملی",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.textMuted,
                            ),
                        )
                    }
                }
            }

            // Tab Selector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .offset(y = (-14).dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.bgSurface)
                        .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    // Legal Tab
                    val isLegal = tab == CompleteEmployerInfoTab.LEGAL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isLegal) Modifier.background(colors.buttonGradient)
                                else Modifier.background(Color.Transparent)
                            )
                            .clickable { onSelectTab(CompleteEmployerInfoTab.LEGAL) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.employer_info_tab_legal),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isLegal) Color.White else colors.textSecondary,
                                fontSize = 12.5.sp,
                            ),
                        )
                    }

                    // Real Tab
                    val isReal = tab == CompleteEmployerInfoTab.REAL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isReal) Modifier.background(colors.buttonGradient)
                                else Modifier.background(Color.Transparent)
                            )
                            .clickable { onSelectTab(CompleteEmployerInfoTab.REAL) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.employer_info_tab_real),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isReal) Color.White else colors.textSecondary,
                                fontSize = 12.5.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}
