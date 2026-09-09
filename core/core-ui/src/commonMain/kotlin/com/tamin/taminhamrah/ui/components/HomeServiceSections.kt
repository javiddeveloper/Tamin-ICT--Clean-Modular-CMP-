package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.home.HomeSectionPR
import com.tamin.taminhamrah.model.home.HomeServiceSection
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.home_quick_access_all
import taminx.core.core_ui.home_section_featured
import taminx.core.core_ui.hub_quick_access

private const val QUICK_ACCESS_COLUMNS = 4

/**
 * At most this many service tiles per chip; the rest live behind «همهٔ خدمات» (the خدمات bottom
 * tab). 7 tiles + the «همهٔ خدمات» tile fills two rows of [QUICK_ACCESS_COLUMNS].
 *
 * [HomeServiceSection.FREQUENT] («پرکاربرد») is exempt — it is already a curated shortlist, so all
 * of its members are shown as declared.
 */
private const val QUICK_ACCESS_MAX = 7
private const val FEATURED_MAX = 3

/**
 * «دسترسی سریع» — a header with the «همهٔ خدمات» link, a horizontally scrollable chip row over the
 * quick-access [sections], and a 4-column grid of the selected section's services followed by a
 * «همهٔ خدمات» action tile.
 *
 * [sections] is already resolved and stripped of empty sections. If [selectedSection] resolved
 * empty (so it is not in the row), the first section is shown instead.
 */
@Composable
fun HomeQuickAccessSection(
    sections: ImmutableList<HomeSectionPR>,
    selectedSection: HomeServiceSection,
    onSectionSelected: (HomeServiceSection) -> Unit,
    onServiceClick: (MainServiceDN) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (sections.isEmpty()) return
    val current = sections.firstOrNull { it.section == selectedSection } ?: sections.first()

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = stringResource(Res.string.hub_quick_access),
            trailing = stringResource(Res.string.home_quick_access_all),
            onTrailingClick = onSeeAll,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            sections.forEach { section ->
                SelectableChip(
                    text = section.title,
                    selected = section.section == current.section,
                    onClick = { onSectionSelected(section.section) },
                )
            }
        }

        // up to QUICK_ACCESS_MAX service tiles (پرکاربرد exempt) + a trailing "همهٔ خدمات" action
        // tile, laid out 4 per row
        val visibleServices = if (current.section == HomeServiceSection.FREQUENT) {
            current.services
        } else {
            current.services.take(QUICK_ACCESS_MAX)
        }
        val cells: List<MainServiceDN?> = buildList {
            addAll(visibleServices)
            add(null)
        }
        cells.chunked(QUICK_ACCESS_COLUMNS).forEach { rowCells ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                rowCells.forEach { cell ->
                    if (cell == null) {
                        HomeActionGridTile(
                            label = stringResource(Res.string.home_quick_access_all),
                            icon = Icons.Default.Apps,
                            onClick = onSeeAll,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        HomeServiceGridTile(
                            service = cell,
                            onClick = { onServiceClick(cell) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                repeat(QUICK_ACCESS_COLUMNS - rowCells.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * «خدمات ویژه» — up to three promoted services rendered as bordered cards (icon tile centered,
 * name beneath). Renders nothing when the menu carries none of them.
 */
@Composable
fun HomeFeaturedSection(
    services: ImmutableList<MainServiceDN>,
    onServiceClick: (MainServiceDN) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (services.isEmpty()) return
    val shown = services.take(FEATURED_MAX)

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(title = stringResource(Res.string.home_section_featured))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            shown.forEach { service ->
                FeaturedServiceCard(
                    service = service,
                    onClick = { onServiceClick(service) },
                    modifier = Modifier.weight(1f),
                )
            }
            repeat(FEATURED_MAX - shown.size) { Box(modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    trailing: String? = null,
    onTrailingClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (trailing != null && onTrailingClick != null) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTrailingClick,
                ),
            )
        }
    }
}

@Composable
private fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    CustomChip(
        text = text,
        containerColor = if (selected) colors.blueBg else colors.bgSurface,
        textColor = if (selected) colors.blueText else colors.textSecondary,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) colors.blueBorder else colors.border,
        ),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
    )
}

@Composable
private fun FeaturedServiceCard(
    service: MainServiceDN,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                RoundedCornerShape(CornerRadius.card),
            )
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(CornerRadius.card))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.lg, horizontal = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        ServiceIconTile(icon = serviceIconFor(service.icon), contentDescription = service.name)
        Text(
            text = service.name.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun previewService(name: String, icon: String, status: MenuServiceStatusDN = MenuServiceStatusDN.ACTIVE) =
    MainServiceDN(id = name.hashCode(), name = name, icon = icon, status = status)

private val previewSections = persistentListOf(
    HomeSectionPR(
        section = HomeServiceSection.HISTORY,
        title = "سابقه",
        services = persistentListOf(
            previewService("کلیه سوابق", "budget"),
            previewService("اعلام سابقه", "paper-plane"),
            previewService("کسری از ماه", "employer_info"),
            previewService("اعتراض سابقه", "protest", MenuServiceStatusDN.TEMPORARY_DISABLED),
            previewService("عناوین شغلی", "list"),
            previewService("بازرسی‌ها", "cctv"),
            previewService("گواهی تحصیل", "student_inquiry"),
        ),
    ),
    HomeSectionPR(HomeServiceSection.AID, "کمک‌هزینه", persistentListOf(previewService("هدیه ازدواج", "love"))),
    HomeSectionPR(HomeServiceSection.EMPLOYER, "کارفرما", persistentListOf(previewService("کارگاه‌ها", "workshop"))),
)

private val previewFeatured = persistentListOf(
    previewService("عناوین شغلی", "list"),
    previewService("اعلام حادثه", "update"),
    previewService("کمک‌هزینهٔ بارداری", "pregnancystp"),
)

@PreviewRtlTheme
@Composable
private fun HomeServiceSectionsPreview() {
    PreviewRtlThemeContent {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            HomeFeaturedSection(services = previewFeatured, onServiceClick = {})
            HomeQuickAccessSection(
                sections = previewSections,
                selectedSection = HomeServiceSection.HISTORY,
                onSectionSelected = {},
                onServiceClick = {},
                onSeeAll = {},
            )
        }
    }
}
