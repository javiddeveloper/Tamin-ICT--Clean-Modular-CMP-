package com.tamin.taminhamrah.mapper.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.home.HomeSectionPR
import com.tamin.taminhamrah.model.home.HomeServiceSection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * The menu rows that belong to [section], in the section's declared order.
 *
 * Order comes from [HomeServiceSection.members], not from the menu. A member the menu does not
 * carry is skipped; a `COMPLETELY_DISABLED` row is skipped (it has no message to explain itself
 * with). `TEMPORARY_DISABLED` / `DISABLED` rows are kept — the card renders them dimmed.
 */
fun List<MainServiceDN>.servicesOf(section: HomeServiceSection): List<MainServiceDN> {
    val byId = associateBy { it.id }
    return section.members.mapNotNull { flag -> byId[flag.id] }
        .filter { it.status != MenuServiceStatusDN.COMPLETELY_DISABLED }
}

/**
 * Every «دسترسی سریع» chip section, resolved against this menu. Sections that end up empty are
 * dropped so no chip leads to a blank grid.
 *
 * `@Composable` because the chip label is a string resource; the result is `remember`ed on the
 * resolved sections so the chip row is handed the same instance across recompositions.
 */
@Composable
fun List<MainServiceDN>.toQuickAccessSections(): ImmutableList<HomeSectionPR> {
    val resolved = HomeServiceSection.quickAccess().map { section ->
        HomeSectionPR(
            section = section,
            title = stringResource(section.titleRes),
            services = servicesOf(section).toImmutableList(),
        )
    }.filter { it.services.isNotEmpty() }
    return remember(resolved) { resolved.toImmutableList() }
}

/** The «خدمات ویژه» rows, resolved against this menu, in the section's declared order. */
fun List<MainServiceDN>.featuredServices(): ImmutableList<MainServiceDN> =
    servicesOf(HomeServiceSection.featured()).toImmutableList()
