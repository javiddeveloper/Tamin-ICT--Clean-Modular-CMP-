package com.tamin.taminhamrah.data.repository.stories
import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.model.stories.StoryCtaDN
import com.tamin.taminhamrah.model.stories.StoryItemDN
import com.tamin.taminhamrah.model.stories.StoryMediaDN
/*
 * ─── Trying your own photos and clips ──────────────────────────────────────────────────────────
 *
 * 1. Drop the file into `feature/stories/src/commonMain/composeResources/files/`.
 *    Any name, any number of them. Nothing is generated from that folder — the path is resolved
 *    as a plain string — so no Gradle sync is needed, only a rebuild to repackage the assets.
 *
 * 2. Name it below and put it on whichever slide you want to look at:
 *
 *        media = StoryMediaDN.BundledImage("files/my_photo.jpg")
 *        media = StoryMediaDN.BundledVideo("files/my_clip.mp4")
 *
 *    A remote address works the same way, with `StoryMediaDN.Image(url)` / `Video(url)` — but see
 *    the warning about image URLs in `docs/vault/Stories.md`: the shared image loader sends the
 *    signed-in user's token to whatever host it fetches from. Video URLs do not go through it.
 *
 * 3. What to expect: a picture is cropped to fill (portrait 9:16 fits without losing anything) and
 *    stays up for 6.2 s; a clip is cropped the same way and its own length drives the progress bar.
 *    A path that names nothing shows «نمایش این محتوا ممکن نشد» and the story moves on rather than
 *    stalling — so a typo looks like that, not like a crash.
 * ───────────────────────────────────────────────────────────────────────────────────────────────
 */
/** The two clips shipped as samples. Replace the files, or add your own beside them. */
private const val SAMPLE_IMAGE = "files/story_sample_image.jpg"
private const val SAMPLE_VIDEO = "files/file_example.mp4"
/**
 * The bundled «تازه‌ها» catalogue: five channels of three slides each, copy taken from the design
 * reference.
 *
 * Stands in for a web service that does not exist yet, and is the **only** thing in the app that
 * knows these stories are fabricated. Everything above [StoryRepositoryImpl] treats them as any
 * other data that can be slow and can fail.
 *
 * When the endpoint arrives this file is deleted whole and `StoryRepositoryImpl` calls a remote
 * data source instead. Nothing else has to move.
 */
internal fun mockStoryChannels(): List<StoryChannelDN> = listOf(
    publicRelationsChannel(),
    insuredChannel(),
    assistantChannel(),
    pensionerChannel(),
    employerChannel(),
)
private fun publicRelationsChannel() = StoryChannelDN(
    key = "pr",
    name = "روابط عمومی سازمان",
    shortName = "روابط عمومی",
    time = "امروز · ۰۹:۴۰",
    items = listOf(
        StoryItemDN(
            id = "pr:0",
            title = "تأمین‌من به‌روز شد",
            body = "پرداخت حق بیمه، مشاهدهٔ سوابق و دریافت فیش، همه در یک صفحه جمع شده است.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("دیدن سوابق من", "tamin://feature/WAGE_AND_HISTORY"),
        ),
        // The slide the design renders as its reference screenshot, and the one carrying the
        // picture so that an image story is reachable without a service.
        StoryItemDN(
            id = "pr:1",
            title = "خدمات غیرحضوری",
            body = "بیشتر درخواست‌ها را از همین اپ ثبت کنید؛ مراجعه به شعبه فقط برای موارد ضروری لازم است.",
            media = StoryMediaDN.BundledImage(SAMPLE_IMAGE),
        ),
        StoryItemDN(
            id = "pr:2",
            title = "ارتباط با ما",
            body = "پاسخ‌گویی تلفنی و پیام‌رسان سازمان، همهٔ روزهای هفته در دسترس شماست.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("سایت رسمی سازمان", "https://tamin.ir"),
        ),
    ),
)
private fun insuredChannel() = StoryChannelDN(
    key = "ins",
    name = "بیمه‌شده‌ها",
    shortName = "بیمه‌شده‌ها",
    time = "امروز · ۰۹:۱۰",
    items = listOf(
        StoryItemDN(
            id = "ins:0",
            title = "سابقهٔ شما، یک‌جا",
            body = "همهٔ سال‌های بیمه‌پردازی، روزهای کارکرد و دستمزد هر ماه را در یک نمودار ببینید.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("کلیهٔ سوابق", "tamin://feature/COMBINED_RECORD"),
        ),
        StoryItemDN(
            id = "ins:1",
            title = "سابقهٔ جامانده را اعلام کنید",
            body = "اگر بازه‌ای از کارکرد شما ثبت نشده، درخواست بررسی را از اپ ثبت کنید.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("اعلام سابقه", "tamin://feature/OBJECTION_NON_EXISTENT_HISTORY"),
        ),
        StoryItemDN(
            id = "ins:2",
            title = "قرارداد و پرداخت حق بیمه",
            body = "قرارداد بیمهٔ اختیاری یا مشاغل آزاد را ببندید و اقلام ماهانه را همان‌جا پرداخت کنید.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("امور قراردادها", "tamin://feature/CONTRACTS"),
        ),
    ),
)
private fun assistantChannel() = StoryChannelDN(
    key = "ai",
    name = "هوش مصنوعی · یارا",
    shortName = "هوش مصنوعی",
    time = "امروز · ۰۸:۱۵",
    items = listOf(
        // The clip is on the opening slide of this channel so a video story is one tap from the
        // rail rather than buried behind two pictures.
        StoryItemDN(
            id = "ai:0",
            title = "یارا، دستیار هوشمند",
            body = "سؤال‌های بیمه‌ای خود را به زبان ساده بپرسید و پاسخ روشن بگیرید.",
            media = StoryMediaDN.BundledVideo(SAMPLE_VIDEO),
            cta = StoryCtaDN("شروع گفت‌وگو", "tamin://feature/AGENT"),
        ),
        StoryItemDN(
            id = "ai:1",
            title = "چقدر تا بازنشستگی مانده؟",
            body = "یارا سابقهٔ شما را می‌خواند و شرایط بازنشستگی را ساده توضیح می‌دهد.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("پرسیدن از یارا", "tamin://feature/AGENT"),
        ),
        StoryItemDN(
            id = "ai:2",
            title = "پاسخ همراه با ارجاع",
            body = "هر پاسخ به قانون و بخشنامهٔ مربوط ارجاع داده می‌شود تا خیال شما راحت باشد.",
            media = StoryMediaDN.None,
        ),
    ),
)
private fun pensionerChannel() = StoryChannelDN(
    key = "pen",
    name = "مستمری‌بگیران",
    shortName = "مستمری‌بگیران",
    time = "دیروز · ۱۹:۳۰",
    items = listOf(
        StoryItemDN(
            id = "pen:0",
            title = "فیش حقوقی هر ماه",
            body = "فیش هر ماه پس از واریز مستمری در اپ قابل مشاهده و دریافت است.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("مشاهدهٔ فیش حقوقی", "tamin://feature/PAY_ROLL"),
        ),
        StoryItemDN(
            id = "pen:1",
            title = "حکم مستمری",
            body = "آخرین حکم و احکام گذشتهٔ خود را ببینید و نسخهٔ آن را ذخیره کنید.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("حکم مستمری", "tamin://feature/EDICT_PENSIONER"),
        ),
        StoryItemDN(
            id = "pen:2",
            title = "گواهی بدون مراجعه",
            body = "گواهی حقوق و گواهی کسر اقساط را از همین اپ درخواست کنید.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("بدون اکشن", null),
        ),
    ),
)
private fun employerChannel() = StoryChannelDN(
    key = "emp",
    name = "کارفرمایان",
    shortName = "کارفرمایان",
    time = "دیروز · ۱۲:۰۵",
    items = listOf(
        StoryItemDN(
            id = "emp:0",
            title = "لیست و پرداخت",
            body = "لیست حق بیمه را ارسال و بدهی کارگاه را در همان صفحه پرداخت کنید.",
            media = StoryMediaDN.None,
            cta = StoryCtaDN("کارگاه‌های من", "tamin://feature/WORKSHOPS"),
        ),
        StoryItemDN(
            id = "emp:1",
            title = "نتیجهٔ بازرسی",
            body = "گزارش بازرسی کارگاه و مهلت اعتراض را از اپ پیگیری کنید.",
            media = StoryMediaDN.None,
        ),
        StoryItemDN(
            id = "emp:2",
            title = "نمایندهٔ الکترونیک",
            body = "برای هر کارگاه نمایندهٔ رسمی تعریف کنید تا خدمات را از طرف شما بگیرد.",
            media = StoryMediaDN.None,
        ),
    ),
)
