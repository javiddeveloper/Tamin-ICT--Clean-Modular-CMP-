package com.tamin.taminhamrah.model.home

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
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
        members = listOf(
            FeatureFlag.CONTRACTS,          // پرداخت  — "امور قراردادها و پرداخت" (35); TODO confirm
            FeatureFlag.PRESCRIPTION,       // نسخه
            FeatureFlag.PAY_ROLL,           // فیش حقوقی
            FeatureFlag.MERGE_HISTORY,      // سوابق  — TODO confirm: id 6 / 7 / 8
            FeatureFlag.FREELANCE_INSURANCE, // حق بیمه — no exact menu match; TODO confirm
            FeatureFlag.BANK_ACCOUNT_LIST,  // حساب بانکی
            FeatureFlag.VIEW_SHORT_TERM,    // درخواست‌ها — TODO confirm
        ),
    ),

    /** سابقه — matches the design's «سابقه» chip. */
    HISTORY(
        titleRes = Res.string.home_section_history,
        placement = SectionPlacement.QUICK_ACCESS,
        members = listOf(
            FeatureFlag.COMBINED_RECORD,                       // کلیه سوابق  — TODO confirm: id 6 / 7 / 8
            FeatureFlag.SEND_INSURANCE_HISTORY_TO_INSTITUTION, // اعلام سابقه
            FeatureFlag.FRACTION_CONTRACT,                     // کسری از ماه
            FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY,        // اعتراض سابقه — TODO confirm: id 10 vs 42
            FeatureFlag.VIEW_TITLE_JOB,                        // عناوین شغلی
            FeatureFlag.LIST_OF_INSPECTIONS_PERFORMED,         // بازرسی‌ها
            FeatureFlag.INQUIRY_EDUCATION,                     // گواهی تحصیل
        ),
    ),

    /** کمک‌هزینه — matches the design's «کمک‌هزینه» chip. */
    AID(
        titleRes = Res.string.home_section_aid,
        placement = SectionPlacement.QUICK_ACCESS,
        members = listOf(
            FeatureFlag.WEDDING_PRESENT,             // هدیه ازدواج
            FeatureFlag.REQUEST_FOR_PREGNANCY_PAY,   // بارداری
            FeatureFlag.OROTEZ_PROTEZ,               // اورتز و پروتز
            FeatureFlag.REQUEST_PAYMENT_FOR_ILL_DAYS, // غرامت بیماری
            FeatureFlag.REQUEST_FUNERAL_GRANT,       // مراسم ترحیم
            FeatureFlag.OCCURRENCE,                  // اعلام حادثه
            FeatureFlag.OPTIONAL_INSURANCE,          // بیمه اختیاری
        ),
    ),

    /** مستمری */
    PENSIONER(
        titleRes = Res.string.home_section_pensioner,
        placement = SectionPlacement.QUICK_ACCESS,
        members = listOf(
            FeatureFlag.RETIREMENT_PENSION,
            FeatureFlag.PENSION_INQUIRY,
            FeatureFlag.PAY_ROLL,
            FeatureFlag.EDICT_PENSIONER,
            FeatureFlag.ISSUANCE_WAGE_CERTIFICATE,
            FeatureFlag.DEFERRED_INSTALLMENT_CERTIFICATE,
            FeatureFlag.GIRL_SURVIVOR,
            FeatureFlag.DISABILITY_PENSION,
        ),
    ),

    /** کارفرما */
    EMPLOYER(
        titleRes = Res.string.home_section_employer,
        placement = SectionPlacement.QUICK_ACCESS,
        members = listOf(
            FeatureFlag.WORKSHOPS,
            FeatureFlag.CONTRACT_INFO,
            FeatureFlag.ASSIGNER_CONTRACT,
            FeatureFlag.COMPLETE_WORKSHOP_INFO,
            FeatureFlag.STACK_HOLDER_LIST,
            FeatureFlag.FOLLOW_PROTEST_STATUS,
            FeatureFlag.REGISTER_AGREEMENT,
            FeatureFlag.PERFORMED_INSPECTION,
            FeatureFlag.INSTALLMENT_DEBT,
            FeatureFlag.CONSTRUCTION_INSURANCE,
            FeatureFlag.OCCURRENCE,
            FeatureFlag.LAWS,
        ),
    ),

    /** خدمات ویژه — the three cards above the chip row. */
    FEATURED(
        titleRes = Res.string.home_section_featured,
        placement = SectionPlacement.FEATURED,
        members = listOf(
            FeatureFlag.VIEW_TITLE_JOB,
            FeatureFlag.OCCURRENCE,
            FeatureFlag.REQUEST_FOR_PREGNANCY_PAY,
        ),
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
