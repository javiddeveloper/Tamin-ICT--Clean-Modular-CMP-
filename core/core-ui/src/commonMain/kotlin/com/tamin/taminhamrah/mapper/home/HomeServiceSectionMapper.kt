package com.tamin.taminhamrah.mapper.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.home.HomeSectionPR
import com.tamin.taminhamrah.model.home.HomeServiceSection
import com.tamin.taminhamrah.model.home.QuickAccessDN
import com.tamin.taminhamrah.model.home.SpecialServiceDN
import com.tamin.taminhamrah.repository.home.HomeQuickAccessGroup
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

/** Reconstructs enough of [MainServiceDN] to render a cached quick-access/special-service row and
 *  to drive a tap: `handleServiceClick` only ever reads [MainServiceDN.id] off this object, the
 *  rest of the gating is re-resolved live via `FeatureManager` at tap time. */
private fun QuickAccessDN.toMainService(): MainServiceDN =
    MainServiceDN(id = flag.id, name = title, icon = iconUrl, status = status)

private fun SpecialServiceDN.toMainService(): MainServiceDN =
    MainServiceDN(id = flag.id, name = title, icon = iconUrl, status = status)

/**
 * The cached quick-access rows, grouped back into the same 5 chip sections
 * ([HomeServiceSection]'s `QUICK_ACCESS`-placed entries) the live menu path renders — matched to
 * [HomeQuickAccessGroup] by name, since core-domain (where the cache tags rows) can't reference
 * this enum. `COMPLETELY_DISABLED` rows and empty sections are dropped, mirroring
 * [toQuickAccessSections] exactly, just sourced from the offline cache instead of the live menu.
 */
@Composable
fun List<QuickAccessDN>.toHomeSections(): ImmutableList<HomeSectionPR> {
    val byGroup = groupBy { it.group }
    val resolved = HomeServiceSection.quickAccess().mapNotNull { section ->
        val group = HomeQuickAccessGroup.entries.find { it.name == section.name } ?: return@mapNotNull null
        val services = byGroup[group]
            ?.filter { it.status != MenuServiceStatusDN.COMPLETELY_DISABLED }
            ?.map { it.toMainService() }
            ?.toImmutableList()
            ?: return@mapNotNull null
        if (services.isEmpty()) null else HomeSectionPR(section = section, title = stringResource(section.titleRes), services = services)
    }
    return remember(resolved) { resolved.toImmutableList() }
}

/** The cached «خدمات ویژه» rows, resolved the same way [featuredServices] resolves the live menu. */
fun List<SpecialServiceDN>.toMainServices(): ImmutableList<MainServiceDN> =
    filter { it.status != MenuServiceStatusDN.COMPLETELY_DISABLED }
        .map { it.toMainService() }
        .toImmutableList()
