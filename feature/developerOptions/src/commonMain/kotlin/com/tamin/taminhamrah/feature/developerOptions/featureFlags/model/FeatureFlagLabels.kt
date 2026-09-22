package com.tamin.taminhamrah.feature.developerOptions.featureFlags.model

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * The Persian name each [FeatureFlag] is known by, so the "تست فیچر فلگ‌ها" screen can show a
 * developer both the enum constant (to match against code) and what the service is actually called
 * (to match against the design/QA report).
 *
 * Sourced from `menu.json`'s `name` field for that id, except where this codebase already documents
 * that id's *real* meaning as something else — `STACK_HOLDER_LIST` and `OCCURRENCE` both have a
 * `menu.json` name that reads as a generic placeholder rather than the actual feature (see
 * `docs/vault/Feature-Flags.md` §5a and `HomeServiceMembership`'s own `// اعلام حادثه` comment); those
 * two follow the documented meaning instead. The three ids `menu.json` carries no row for at all (45,
 * 102, 109) reuse the label of the sibling id `FeatureNavigation` already routes them together with.
 *
 * Not sourced from `composeResources/strings.xml`: this is the server's own copy for a fixed,
 * numbered id, not app-authored UI text — the same reasoning that already lets [FeatureFlag]'s own
 * `// نسخه`-style comments and `HomeServiceMembership` skip a string resource for the same names.
 */
fun FeatureFlag.persianLabel(): String = when (this) {
    FeatureFlag.IDENTITY_INFO -> "اطلاعات هویتی"
    FeatureFlag.ACTIVE_RELATION -> "ارتباط فعال"
    FeatureFlag.BANK_ACCOUNT_LIST -> "شماره حساب‌ها"
    FeatureFlag.EDIT_IMAGE -> "ویرایش تصویر"
    FeatureFlag.DEPENDENTS -> "افراد تبعی"
    FeatureFlag.MERGE_HISTORY -> "سوابق تلفیقی"
    FeatureFlag.WAGE_AND_HISTORY -> "سوابق و دستمزد"
    FeatureFlag.COMBINED_RECORD -> "مجموع سوابق"
    FeatureFlag.SEND_INSURANCE_HISTORY_TO_INSTITUTION -> "اعلام سابقه"
    FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY -> "اعتراض به سوابق ناموجود"
    FeatureFlag.VIEW_TITLE_JOB -> "عناوین شغلی"
    FeatureFlag.VIEW_SHORT_TERM -> "درخواست‌های تعهدات کوتاه مدت"
    FeatureFlag.WEDDING_PRESENT -> "هدیه ازدواج"
    FeatureFlag.OROTEZ_PROTEZ -> "کمک هزینه اورتز و پروتز"
    FeatureFlag.REQUEST_FOR_PREGNANCY_PAY -> "کمک هزینه ایام بارداری"
    FeatureFlag.REQUEST_PAYMENT_FOR_ILL_DAYS -> "غرامت دستمزد ایام بیماری"
    FeatureFlag.REQUEST_FUNERAL_GRANT -> "کمک هزینه مراسم ترحیم"
    FeatureFlag.LIST_OF_INSPECTIONS_PERFORMED -> "بازرسی‌ها"
    FeatureFlag.CALCULATE_MARRIAGE_ALLOWANCE -> "محاسبه هدیه ازدواج"
    FeatureFlag.CALCULATE_WAGE_ILL_DAYS -> "محاسبه غرامت ایام بیماری"
    FeatureFlag.CALCULATE_WAGE_PREGNANCY -> "محاسبه غرامت ایام بارداری"
    FeatureFlag.CALCULATE_WAGE_PENSION, FeatureFlag.CALCULATE_WAGE_PENSION_109 -> "نحوه محاسبه مبلغ مستمری"
    FeatureFlag.DESERVED_TREATMENT, FeatureFlag.DESERVED_TREATMENT_101 -> "استحقاق / وضعیت حمایت درمانی"
    FeatureFlag.PRESCRIPTION, FeatureFlag.PRESCRIPTION_102 -> "نسخ الکترونیک"
    FeatureFlag.FREELANCE_INSURANCE -> "بیمه صاحبان حرف و مشاغل آزاد"
    FeatureFlag.STUDENT_INSURANCE -> "بیمه دانشجویی"
    FeatureFlag.CONTRACTS -> "امور قراردادها و پرداخت"
    FeatureFlag.HOUSEWIFE_INSURANCE -> "بیمه زنان خانه‌دار"
    FeatureFlag.OPTIONAL_INSURANCE -> "بیمه اختیاری"
    FeatureFlag.INQUIRY_EDUCATION -> "استعلام گواهی اشتغال به تحصیل"
    FeatureFlag.FRACTION_CONTRACT -> "تکمیل سوابق کسری از ماه"
    FeatureFlag.REQUEST_PENSION_BY_SURVIVOR, FeatureFlag.REQUEST_PENSION_BY_SURVIVOR_112 -> "درخواست/برقراری مستمری بازماندگان"
    FeatureFlag.RETIREMENT_PENSION -> "مستمری بازنشستگی"
    FeatureFlag.OBJECTION_INSURANCE_HISTORY, FeatureFlag.OBJECTION_INSURANCE_HISTORY_45 -> "اعتراض به سابقه کسری دار"
    FeatureFlag.MY_ELECTRONIC_FILE -> "پرونده الکترونیک من"
    FeatureFlag.WORKERS_PAYMENT_INFO -> "پرداخت حق بیمه کارگران ساختمانی"
    FeatureFlag.PENSION_INQUIRY -> "استعلام وضعیت مستمری"
    FeatureFlag.PAY_ROLL -> "مشاهده فیش حقوقی"
    FeatureFlag.EDICT_PENSIONER -> "مشاهده حکم"
    FeatureFlag.ISSUANCE_WAGE_CERTIFICATE -> "صدور گواهی حقوق"
    FeatureFlag.DEFERRED_INSTALLMENT_CERTIFICATE -> "گواهی کسر اقساط معوق"
    FeatureFlag.GIRL_SURVIVOR -> "تعهدنامه فرزندان دختر"
    FeatureFlag.DISABILITY_PENSION -> "مستمری از کارافتادگی"
    FeatureFlag.WORKSHOPS -> "کارگاه‌ها"
    FeatureFlag.CONTRACT_INFO -> "اطلاعات پیمان"
    FeatureFlag.ASSIGNER_CONTRACT -> "واگذارندگان"
    FeatureFlag.COMPLETE_WORKSHOP_INFO -> "تکمیل اطلاعات کارفرمایی"
    FeatureFlag.STACK_HOLDER_LIST -> "معرفی نماینده اشخاص حقوقی"
    FeatureFlag.FOLLOW_PROTEST_STATUS -> "پیگیری وضعیت اعتراض"
    FeatureFlag.REGISTER_AGREEMENT -> "درخواست خدمات غیرحضوری"
    FeatureFlag.PERFORMED_INSPECTION -> "بازرسی انجام شده"
    FeatureFlag.INSTALLMENT_DEBT -> "مدیریت بدهی"
    FeatureFlag.CONSTRUCTION_INSURANCE -> "بیمه ساختمانی"
    FeatureFlag.OCCURRENCE -> "اعلام حادثه"
    FeatureFlag.LAWS -> "قوانین"
    FeatureFlag.AGENT -> "دستیار هوشمند (ایجنت)"
}
