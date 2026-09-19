package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.MenuServiceStatus

/**
 * The menu the app shows while [com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSourceImpl.getMainMenu]
 * serves local data instead of `menu_data_<version>.txt`.
 *
 * **`id` decides the order on screen, not the order of this list.** The menu is cached in Room
 * (`menu_items`, `id` as the primary key) and read back through `MenuDao.getMenuItems()`, which
 * orders by `sorting` — null for every row here — then by `id`. So each block below is written in
 * display order and its ids ascend with it; keep the two in step when adding or moving a row.
 *
 * Ids are banded by audience (`showRole`), and every id must have a matching
 * [com.tamin.taminhamrah.model.common.FeatureFlag] or the tap does nothing:
 *
 * | Band | Audience | `showRole` |
 * |---|---|---|
 * | 1–37 | insured | `1` |
 * | 101–110 | pensioners | `2` |
 * | 1001–1010 | employers | `3` |
 * | 2000 | AI assistant | all |
 *
 * A service both an insured person and a pensioner reach gets **two rows with two ids** when each
 * audience needs it in its own position (ids 11 and 104), and **one row with `showRole = [1, 2]`**
 * when one position serves both (id 28).
 */
val mockMenuData = listOf(
    // ─── Insured (showRole 1) ────────────────────────────────────────────────
    MainServiceDto(id = 1, name = "اعتراض به سوابق ناموجود", showRole = listOf(1), icon = "protest", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 2, name = "اعلام سابقه", showRole = listOf(1), icon = "paper-plane", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3, name = "کلیه سوابق", showRole = listOf(1), icon = "budget", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 4, name = "کمک هزینه اروتز پروتز", showRole = listOf(1), icon = "crutch", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 5, name = "هدیه ازدواج", showRole = listOf(1), icon = "love", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 6, name = "عناوین شغلی", showRole = listOf(1), icon = "list", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 7, name = "کمک هزینه مراسم ترحیم", showRole = listOf(1), icon = "death", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 8, name = "غرامت دستمزد ایام بیماری", showRole = listOf(1), icon = "medical", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 9, name = "کمک هزینه ایام بارداری", showRole = listOf(1), icon = "pregnancystp", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 10, name = "تکمیل سوابق کسری از ماه", showRole = listOf(1), icon = "employer_info", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 11, name = " نحوه محاسبه مبلغ مستمری بازنشستگی ", showRole = listOf(1), icon = "calc", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 12, name = "بازرسی‌ها", showRole = listOf(1), icon = "cctv", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 13, name = "استعلام گواهی اشتغال به تحصیل", showRole = listOf(1), icon = "student_inquiry", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 14, name = "انعقاد قرارداد بیمه اختیاری", showRole = listOf(1), icon = "optional-insurance", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 15, name = "امور قراردادها و پرداخت", showRole = listOf(1), icon = "contract_payment", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 16, name = "برقراری مستمری بازنشستگی", showRole = listOf(1), icon = "ticket", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 17, name = "مستمری از کارافتادگی", showRole = listOf(1), icon = "disability", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 18, name = "اعتراض به سابقه کسری دارای کسری کارکرد یا اشکال", showRole = listOf(1), icon = "objecting_history_bugs", status = MenuServiceStatus.ACTIVE, message = "این سرویس موقتاً در دسترس نیست"),
    MainServiceDto(id = 19, name = "اعلام حادثه", showRole = listOf(1), icon = "update", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 20, name = "پرداخت حق بیمه کارگران ساختمانی", showRole = listOf(1), icon = "worker-insurance-payment", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 21, name = "برقراری مستمری توسط بازماندگان", showRole = listOf(1), icon = "survivors", status = MenuServiceStatus.ACTIVE),
    // WEB_VIEW, not ACTIVE: this is the only row with no native screen, and the status is what makes
    // `url` open from the home screen too — the services tab has its own LAWS special case, the home
    // screen does not.
    MainServiceDto(id = 22, name = "سامانه قوانین و مقررات تامین اجتماعی", showRole = listOf(1), icon = "document", status = MenuServiceStatus.WEB_VIEW, url = "https://law.tamin.ir/"),
    MainServiceDto(id = 23, name = "درخواست‌های تعهدات کوتاه مدت", showRole = listOf(1), icon = "obligation", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 24, name = "محاسبه هدیه ازدواج", showRole = listOf(1), icon = "wedding-presents", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 25, name = "محاسبه غرامت ایام بیماری", showRole = listOf(1), icon = "medicine", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 26, name = "محاسبه غرامت ایام بارداری", showRole = listOf(1), icon = "scan", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 27, name = "وضعیت حمایت درمانی", showRole = listOf(1), icon = "first-aid-kit", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 28, name = "نسخ الکترونیک", showRole = listOf(1, 2), icon = "folder", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 29, name = "انعقاد قرارداد بیمه صاحبان حرف و مشاغل آزاد", showRole = listOf(1), icon = "agreement-freelance", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 30, name = "انعقاد قرارداد بیمه دانشجویی", showRole = listOf(1), icon = "student", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 31, name = "انعقاد قرارداد بیمه زنان خانه‌دار", showRole = listOf(1), icon = "woman_agreement-freelance", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 32, name = "پرونده الکترونیک من", showRole = listOf(1), icon = "student_inquiry", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 33, name = "اطلاعات هویتی", showRole = listOf(1), icon = "user", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 34, name = "ارتباط فعال", showRole = listOf(1), icon = "relation", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 35, name = "شماره حساب‌ها", showRole = listOf(1), icon = "credit-card", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 36, name = "ویرایش تصویر", showRole = listOf(1), icon = "camera", status = MenuServiceStatus.DISABLED),
    MainServiceDto(id = 37, name = "افراد تبعی", showRole = listOf(1), icon = "relationship", status = MenuServiceStatus.DISABLED),

    // ─── Pensioners (showRole 2) ─────────────────────────────────────────────
    MainServiceDto(id = 101, name = "مشاهده حکم", showRole = listOf(2), icon = "announcement", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 102, name = "مشاهده فیش حقوقی", showRole = listOf(2), icon = "ticket", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 103, name = "استعلام وضعیت مستمری", showRole = listOf(2), icon = "insurance", status = MenuServiceStatus.ACTIVE, message = "سرویس استعلام وضعیت مستمری در حال بروزرسانی است"),
    // The pensioners' own row for id 11's service, so each audience can place it where it belongs.
    MainServiceDto(id = 104, name = " نحوه محاسبه مبلغ مستمری بازنشستگی ", showRole = listOf(2), icon = "calc", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 105, name = "گواهی کسر اقساط معوق", showRole = listOf(2), icon = "document", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 106, name = "صدور گواهی حقوق", showRole = listOf(2), icon = "stamp", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 107, name = "مستمری از کارافتادگی", showRole = listOf(2), icon = "disability", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 108, name = "برقراری مستمری توسط بازماندگان", showRole = listOf(2), icon = "survivors", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 109, name = "تعهدنامه فرزندان دختر", showRole = listOf(2), icon = "agreement", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 110, name = "استحقاق درمان", showRole = listOf(2), icon = "first-aid-kit", status = MenuServiceStatus.DISABLED),

    // ─── Employers (showRole 3) ──────────────────────────────────────────────
    MainServiceDto(id = 1001, name = "تکمیل اطلاعات کارفرمایی", showRole = listOf(3), icon = "employer_info", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1002, name = "واگذارندگان", showRole = listOf(3), icon = "ic_assigner", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1003, name = "کارگاه‌ها", showRole = listOf(3), icon = "workshop", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1004, name = "پیگیری وضعیت اعتراض به بدهی", showRole = listOf(3), icon = "protest", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1005, name = "بازرسی های کارگاه", showRole = listOf(3), icon = "cctv", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1006, name = "درخواست استفاده از خدمات غیرحضوری", showRole = listOf(3), icon = "onlineServiceReq", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1007, name = "گواهی حق بیمه ساختمانی", showRole = listOf(3), icon = "workshop", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1008, name = "معرفی نماینده اشخاص حقوقی", showRole = listOf(3), icon = "relationship", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1009, name = "ردیف های پیمان", showRole = listOf(3), icon = "contract", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1010, name = "مدیریت بدهی", showRole = listOf(3), icon = "student_inquiry", status = MenuServiceStatus.DISABLED),

    // ─── All audiences ───────────────────────────────────────────────────────
    MainServiceDto(id = 2000, name = "دستیار هوشمند (آزمایشی)", showRole = listOf(1, 2, 3), icon = "bot", status = MenuServiceStatus.ACTIVE)
)
