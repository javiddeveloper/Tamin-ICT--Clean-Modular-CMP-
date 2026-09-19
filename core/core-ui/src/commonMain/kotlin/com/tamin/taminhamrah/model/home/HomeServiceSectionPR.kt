package com.tamin.taminhamrah.model.home

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.repository.home.HomeServiceMembership
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.home_section_aid
import taminx.core.core_ui.home_section_employer
import taminx.core.core_ui.home_section_featured
import taminx.core.core_ui.home_section_frequent
import taminx.core.core_ui.home_section_history
import taminx.core.core_ui.home_section_pensioner

/**
 * Where a [HomeServiceSection] renders on the home screen.
 *
 * - [QUICK_ACCESS] — a selectable chip in the «دسترسی سریع» block; the grid below the chip row shows
 *   the selected section's services.
 * - [FEATURED] — the «خدمات ویژه» cards shown above the chip row; not selectable, always visible.
 */
enum class SectionPlacement { QUICK_ACCESS, FEATURED }

/**
 * The home screen's service categories.
 *
 * **This enum owns only the grouping** — the section's own label, which services belong to it, the
 * order they appear in, and where the section renders. Everything shown on a service card (its
 * title, icon, status, disabled message, target) is read at render time from the menu row
 * ([MainServiceDN]) that [members] resolves to, so a rename in `mockMenuData`/`menu.json` flows
 * through without touching this file.
 *
 * **Declaration order is the display order** — of the sections themselves and of the services
 * inside each one. It is the design's order, not [FeatureFlag] id order.
 *
 * A service may sit in more than one section (e.g. عناوین شغلی is both [HISTORY] and [FEATURED]).
 * Nothing here decides whether a card is reachable — that stays the server's answer, read through
 * `FeatureManager` on tap.
 */
enum class HomeServiceSection(
    val titleRes: StringResource,
    val placement: SectionPlacement,
    val members: List<FeatureFlag>,
) {
    /**
     * پرکاربرد — curated shortlist, shown first. Matches the design's «پر کاربرد» chip; exempt from
     * the quick-access tile cap so every entry here is always shown.
     */
    FREQUENT(
        titleRes = Res.string.home_section_frequent,
        placement = SectionPlacement.QUICK_ACCESS,
        members = HomeServiceMembership.frequent,
    ),

    /** سابقه — matches the design's «سابقه» chip. */
    HISTORY(
        titleRes = Res.string.home_section_history,
        placement = SectionPlacement.QUICK_ACCESS,
        members = HomeServiceMembership.history,
    ),

    /** کمک‌هزینه — matches the design's «کمک‌هزینه» chip. */
    AID(
        titleRes = Res.string.home_section_aid,
        placement = SectionPlacement.QUICK_ACCESS,
        members = HomeServiceMembership.aid,
    ),

    /** مستمری */
    PENSIONER(
        titleRes = Res.string.home_section_pensioner,
        placement = SectionPlacement.QUICK_ACCESS,
        members = HomeServiceMembership.pensioner,
    ),

    /** کارفرما */
    EMPLOYER(
        titleRes = Res.string.home_section_employer,
        placement = SectionPlacement.QUICK_ACCESS,
        members = HomeServiceMembership.employer,
    ),

    /** خدمات ویژه — the three cards above the chip row. */
    FEATURED(
        titleRes = Res.string.home_section_featured,
        placement = SectionPlacement.FEATURED,
        members = HomeServiceMembership.featured,
    ),
    ;

    companion object {
        /** The selectable chips, in display order. */
        fun quickAccess(): List<HomeServiceSection> =
            entries.filter { it.placement == SectionPlacement.QUICK_ACCESS }

        /** The «خدمات ویژه» section. */
        fun featured(): HomeServiceSection = FEATURED
    }
}

/**
 * One rendered section: its resolved [title] and the menu rows that belong to it, already ordered
 * and filtered. Built by `List<MainServiceDN>.toQuickAccessSections()` / `.featuredServices()`.
 */
@Immutable
data class HomeSectionPR(
    val section: HomeServiceSection,
    val title: String,
    val services: ImmutableList<MainServiceDN>,
)
