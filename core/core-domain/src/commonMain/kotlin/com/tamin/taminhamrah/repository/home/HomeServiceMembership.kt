package com.tamin.taminhamrah.repository.home

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * The [FeatureFlag] membership of each home-screen service section — [frequent]/[history]/[aid]/
 * [pensioner]/[employer] for the «دسترسی سریع» chips, [featured] for the «خدمات ویژه» cards.
 *
 * **Single source of truth.** core-ui's `HomeServiceSection` enum (which also carries each
 * section's title resource and placement) references these lists instead of declaring its own, and
 * [HomeContentPlaceholders] does the same for the offline-first cache — so the two can never drift
 * apart. Lives here, not in core-ui, purely because [HomeContentPlaceholders] (core-data's only
 * legal source for this) needs it and core-data must not depend on core-ui.
 *
 * **Declaration order is the display order** of the services inside each section — the design's
 * order, not [FeatureFlag] id order. A service may sit in more than one list (e.g. عناوین شغلی is
 * both [history] and [featured]).
 */
object HomeServiceMembership {
    /**
     * پرکاربرد — curated shortlist, shown first. Exempt from the quick-access tile cap so every
     * entry here is always shown.
     */
    val frequent: List<FeatureFlag> = listOf(
        FeatureFlag.CONTRACTS,            // امور قراردادها و پرداخت
        FeatureFlag.PRESCRIPTION,         // نسخه
        FeatureFlag.PAY_ROLL,             // فیش حقوقی
        FeatureFlag.COMBINED_RECORD,      // کلیه سوابق — replaces the retired سوابق تلفیقی shortcut, which lost its menu row when the three history rows were merged
        FeatureFlag.FREELANCE_INSURANCE,  // بیمه صاحبان حرف و مشاغل آزاد
        FeatureFlag.BANK_ACCOUNT_LIST,    // حساب بانکی
        FeatureFlag.VIEW_SHORT_TERM,      // درخواست‌های تعهدات کوتاه مدت
    )

    /** سابقه */
    val history: List<FeatureFlag> = listOf(
        FeatureFlag.COMBINED_RECORD,                       // کلیه سوابق — the one row left after «سوابق تلفیقی» and «سوابق و دستمزد» were merged into it
        FeatureFlag.SEND_INSURANCE_HISTORY_TO_INSTITUTION, // اعلام سابقه
        FeatureFlag.FRACTION_CONTRACT,                     // کسری از ماه
        FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY,        // اعتراض به سوابق ناموجود — not OBJECTION_INSURANCE_HISTORY ("اعتراض به سابقه کسری دارای کسری کارکرد یا اشکال")
        FeatureFlag.VIEW_TITLE_JOB,                        // عناوین شغلی
        FeatureFlag.LIST_OF_INSPECTIONS_PERFORMED,         // بازرسی‌ها
        FeatureFlag.INQUIRY_EDUCATION,                      // گواهی تحصیل
    )

    /** کمک‌هزینه */
    val aid: List<FeatureFlag> = listOf(
        FeatureFlag.WEDDING_PRESENT,              // هدیه ازدواج
        FeatureFlag.REQUEST_FOR_PREGNANCY_PAY,    // بارداری
        FeatureFlag.OROTEZ_PROTEZ,                // اورتز و پروتز
        FeatureFlag.REQUEST_PAYMENT_FOR_ILL_DAYS, // غرامت بیماری
        FeatureFlag.REQUEST_FUNERAL_GRANT,        // مراسم ترحیم
        FeatureFlag.OCCURRENCE,                   // اعلام حادثه
        FeatureFlag.OPTIONAL_INSURANCE,           // بیمه اختیاری
    )

    /** مستمری */
    val pensioner: List<FeatureFlag> = listOf(
        FeatureFlag.RETIREMENT_PENSION,
        FeatureFlag.PENSION_INQUIRY,
        FeatureFlag.PAY_ROLL,
        FeatureFlag.EDICT_PENSIONER,
        FeatureFlag.ISSUANCE_WAGE_CERTIFICATE,
        FeatureFlag.DEFERRED_INSTALLMENT_CERTIFICATE,
        FeatureFlag.GIRL_SURVIVOR,
        FeatureFlag.DISABILITY_PENSION,
    )

    /** کارفرما */
    val employer: List<FeatureFlag> = listOf(
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
    )

    /** خدمات ویژه — the three cards above the chip row. */
    val featured: List<FeatureFlag> = listOf(
        FeatureFlag.VIEW_TITLE_JOB,
        FeatureFlag.OCCURRENCE,
        FeatureFlag.REQUEST_FOR_PREGNANCY_PAY,
    )
}
