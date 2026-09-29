package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.MenuServiceStatus

/**
 * The menu the app shows while [com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSourceImpl.getMainMenu]
 * serves local data instead of `menu_data_<version>.txt`.
 *
 * **`sorting` decides the order on screen, not `id` and not the order of this list** — `id` is the
 * legacy server's own id for the service (see [com.tamin.taminhamrah.model.common.FeatureFlag]) and
 * is not sequential within a band, so it can't be relied on for display order. The menu is cached in
 * Room (`menu_items`, `id` as the primary key) and read back through `MenuDao.getMenuItems()`, which
 * orders by `sorting` first, `id` only as a tie-break. Each row below carries the `sorting` value
 * that reproduces the design's intended order; this list's own top-to-bottom order follows the same
 * sequence purely for readability and isn't itself load-bearing.
 *
 * Ids are banded by audience (`showRole`), and every id must have a matching
 * [com.tamin.taminhamrah.model.common.FeatureFlag] or the tap does nothing:
 *
 * | Band | Audience | `showRole` |
 * |---|---|---|
 * | 1–47 | insured | `1` |
 * | 101–113 | pensioners | `2` |
 * | 1001–1012 | employers | `3` |
 * | 2000 | AI assistant | all |
 *
 * A service both an insured person and a pensioner reach gets **two rows with two ids** when each
 * audience needs it in its own position (ids 23 and 109), and **one row with `showRole = [1, 2]`**
 * when one position serves both (id 26).
 */
val mockMenuData = listOf(
    // ─── Insured (showRole 1) ────────────────────────────────────────────────
    MainServiceDto(id = 10, sorting = 1, name = "اعتراض به سوابق ناموجود", showRole = listOf(1), icon = "protest", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 9, sorting = 2, name = "اعلام سابقه", showRole = listOf(1), icon = "paper-plane", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 8, sorting = 3, name = "کلیه سوابق", showRole = listOf(1), icon = "budget", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 15, sorting = 4, name = "کمک هزینه اروتز پروتز", showRole = listOf(1), icon = "crutch", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 14, sorting = 5, name = "هدیه ازدواج", showRole = listOf(1), icon = "love", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 11, sorting = 6, name = "عناوین شغلی", showRole = listOf(1), icon = "list", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 18, sorting = 7, name = "کمک هزینه مراسم ترحیم", showRole = listOf(1), icon = "death", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 17, sorting = 8, name = "غرامت دستمزد ایام بیماری", showRole = listOf(1), icon = "medical", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 16, sorting = 9, name = "کمک هزینه ایام بارداری", showRole = listOf(1), icon = "pregnancystp", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 39, sorting = 10, name = "تکمیل سوابق کسری از ماه", showRole = listOf(1), icon = "employer_info", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 23, sorting = 11, name = " نحوه محاسبه مبلغ مستمری بازنشستگی ", showRole = listOf(1), icon = "calc", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 19, sorting = 12, name = "بازرسی‌ها", showRole = listOf(1), icon = "cctv", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 38, sorting = 13, name = "استعلام گواهی اشتغال به تحصیل", showRole = listOf(1), icon = "student_inquiry", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 37, sorting = 14, name = "انعقاد قرارداد بیمه اختیاری", showRole = listOf(1), icon = "optional-insurance", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 35, sorting = 15, name = "امور قراردادها و پرداخت", showRole = listOf(1), icon = "contract_payment", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 41, sorting = 16, name = "برقراری مستمری بازنشستگی", showRole = listOf(1), icon = "ticket", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 44, sorting = 17, name = "مستمری از کارافتادگی", showRole = listOf(1), icon = "disability", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 42, sorting = 18, name = "اعتراض به سابقه کسری دارای کسری کارکرد یا اشکال", showRole = listOf(1), icon = "objecting_history_bugs", status = MenuServiceStatus.ACTIVE, message = "این سرویس موقتاً در دسترس نیست"),
    MainServiceDto(id = 1011, sorting = 19, name = "اعلام حادثه", showRole = listOf(1), icon = "update", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 47, sorting = 20, name = "پرداخت حق بیمه کارگران ساختمانی", showRole = listOf(1), icon = "worker-insurance-payment", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 40, sorting = 21, name = "برقراری مستمری توسط بازماندگان", showRole = listOf(1), icon = "survivors", status = MenuServiceStatus.ACTIVE),
    // WEB_VIEW, not ACTIVE: this is the only row with no native screen, and the status is what makes
    // `url` open from the home screen too — the services tab has its own LAWS special case, the home
    // screen does not.
    MainServiceDto(id = 1012, sorting = 22, name = "سامانه قوانین و مقررات تامین اجتماعی", showRole = listOf(1), icon = "document", status = MenuServiceStatus.WEB_VIEW, url = "https://law.tamin.ir/"),
    MainServiceDto(id = 13, sorting = 23, name = "درخواست‌های تعهدات کوتاه مدت", showRole = listOf(1), icon = "obligation", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 20, sorting = 24, name = "محاسبه هدیه ازدواج", showRole = listOf(1), icon = "wedding-presents", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 21, sorting = 25, name = "محاسبه غرامت ایام بیماری", showRole = listOf(1), icon = "medicine", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 22, sorting = 26, name = "محاسبه غرامت ایام بارداری", showRole = listOf(1), icon = "scan", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 25, sorting = 27, name = "وضعیت حمایت درمانی", showRole = listOf(1), icon = "first-aid-kit", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 26, sorting = 28, name = "نسخ الکترونیک", showRole = listOf(1, 2), icon = "folder", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 33, sorting = 29, name = "انعقاد قرارداد بیمه صاحبان حرف و مشاغل آزاد", showRole = listOf(1), icon = "agreement-freelance", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 34, sorting = 30, name = "انعقاد قرارداد بیمه دانشجویی", showRole = listOf(1), icon = "student", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 36, sorting = 31, name = "انعقاد قرارداد بیمه زنان خانه‌دار", showRole = listOf(1), icon = "woman_agreement-freelance", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 46, sorting = 32, name = "پرونده الکترونیک من", showRole = listOf(1), icon = "student_inquiry", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 1, sorting = 33, name = "اطلاعات هویتی", showRole = listOf(1), icon = "user", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 2, sorting = 34, name = "ارتباط فعال", showRole = listOf(1), icon = "relation", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 3, sorting = 35, name = "شماره حساب‌ها", showRole = listOf(1), icon = "credit-card", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 4, sorting = 36, name = "ویرایش تصویر", showRole = listOf(1), icon = "camera", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 5, sorting = 37, name = "افراد تبعی", showRole = listOf(1), icon = "relationship", status = MenuServiceStatus.DISABLED),

    // ─── Pensioners (showRole 2) ─────────────────────────────────────────────
    MainServiceDto(id = 106, sorting = 38, name = "مشاهده حکم", showRole = listOf(2), icon = "announcement", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 105, sorting = 39, name = "مشاهده فیش حقوقی", showRole = listOf(2), icon = "ticket", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 104, sorting = 40, name = "استعلام وضعیت مستمری", showRole = listOf(2), icon = "insurance", status = MenuServiceStatus.ACTIVE, message = "سرویس استعلام وضعیت مستمری در حال بروزرسانی است"),
    // The pensioners' own row for CALCULATE_WAGE_PENSION's service, so each audience can place it
    // where it belongs.
    MainServiceDto(id = 109, sorting = 41, name = " نحوه محاسبه مبلغ مستمری بازنشستگی ", showRole = listOf(2), icon = "calc", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 108, sorting = 42, name = "گواهی کسر اقساط معوق", showRole = listOf(2), icon = "document", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 107, sorting = 43, name = "صدور گواهی حقوق", showRole = listOf(2), icon = "stamp", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 113, sorting = 44, name = "مستمری از کارافتادگی", showRole = listOf(2), icon = "disability", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 112, sorting = 45, name = "برقراری مستمری توسط بازماندگان", showRole = listOf(2), icon = "survivors", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 110, sorting = 46, name = "تعهدنامه فرزندان دختر", showRole = listOf(2), icon = "agreement", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 101, sorting = 47, name = "استحقاق درمان", showRole = listOf(2), icon = "first-aid-kit", status = MenuServiceStatus.DISABLED),

    // ─── Employers (showRole 3) ──────────────────────────────────────────────
    MainServiceDto(id = 1004, sorting = 48, name = "تکمیل اطلاعات کارفرمایی", showRole = listOf(3), icon = "employer_info", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1003, sorting = 49, name = "واگذارندگان", showRole = listOf(3), icon = "ic_assigner", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1001, sorting = 50, name = "کارگاه‌ها", showRole = listOf(3), icon = "workshop", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1006, sorting = 51, name = "پیگیری وضعیت اعتراض به بدهی", showRole = listOf(3), icon = "protest", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1008, sorting = 52, name = "بازرسی های کارگاه", showRole = listOf(3), icon = "cctv", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1007, sorting = 53, name = "درخواست استفاده از خدمات غیرحضوری", showRole = listOf(3), icon = "onlineServiceReq", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1010, sorting = 54, name = "گواهی حق بیمه ساختمانی", showRole = listOf(3), icon = "workshop", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1005, sorting = 55, name = "معرفی نماینده اشخاص حقوقی", showRole = listOf(3), icon = "relationship", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1002, sorting = 56, name = "ردیف های پیمان", showRole = listOf(3), icon = "contract", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1009, sorting = 57, name = "مدیریت بدهی", showRole = listOf(3), icon = "student_inquiry", status = MenuServiceStatus.DISABLED),

    // ─── All audiences ───────────────────────────────────────────────────────
    MainServiceDto(id = 2000, sorting = 58, name = "دستیار هوشمند (آزمایشی)", showRole = listOf(1, 2, 3), icon = "bot", status = MenuServiceStatus.ACTIVE),

    // ─── Provisional — pending real registration on the server ──────────────
    // Mirrors FeatureFlag.kt's own "Provisional ids" block one for one. Remove a row here the
    // same day its FeatureFlag id is replaced with the server's real one.
    MainServiceDto(id = 3001, sorting = 59, name = "تغییر شماره موبایل", showRole = listOf(1, 2), icon = "mobile", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3002, sorting = 60, name = "صندوق شخصی", showRole = listOf(1, 2, 3), icon = "inbox", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3003, sorting = 61, name = "لیست درخواست‌ها", showRole = listOf(1, 2, 3), icon = "list", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3004, sorting = 62, name = "تازه‌ها و ذخیره رویدادها", showRole = listOf(1, 2, 3), icon = "calendar", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3005, sorting = 63, name = "پرونده سلامت من", showRole = listOf(1, 2), icon = "first-aid-kit", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3006, sorting = 64, name = "مراکز درمانی طرف قرارداد", showRole = listOf(1, 2), icon = "medical", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3007, sorting = 65, name = "هزینه‌های سال جاری", showRole = listOf(1, 2), icon = "budget", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3008, sorting = 66, name = "آخرین درخواست‌ها", showRole = listOf(1, 2, 3), icon = "list", status = MenuServiceStatus.ACTIVE),
)
