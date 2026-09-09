package com.tamin.taminhamrah.feature.stories.data

import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.feature.stories.model.StoryChannel
import com.tamin.taminhamrah.feature.stories.model.StoryCta
import com.tamin.taminhamrah.feature.stories.model.StoryItem
import com.tamin.taminhamrah.feature.stories.model.StoryMedia
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryPalette
import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import taminx.feature.stories.generated.resources.Res
import taminx.feature.stories.generated.resources.ic_story_assistant
import taminx.feature.stories.generated.resources.ic_story_employer
import taminx.feature.stories.generated.resources.ic_story_insured
import taminx.feature.stories.generated.resources.ic_story_pensioner
import taminx.feature.stories.generated.resources.ic_story_public_relations

/** Sample media bundled with the module, so the image and video paths run without a network. */
private const val SAMPLE_IMAGE = "files/story_sample_image.jpg"
private const val SAMPLE_VIDEO = "files/story_sample_video.mp4"

/**
 * The bundled catalogue: five channels of three slides each, copy and palette taken from the
 * design reference.
 *
 * Stands in for a web service that does not exist yet. It is deliberately the only thing in this
 * feature that knows the stories are fabricated — [StoryCatalog] and everything above it treat it
 * as any other source that can be slow and can fail.
 *
 * [latencyMs] is what makes the rail's loading state reachable at all; without it the list would
 * be there before the first frame and the shimmer would never be seen. It is a property rather
 * than a constant so a test can take it to zero.
 */
class MockStorySource(
    private val latencyMs: Long = 700L,
) : StorySource {

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun channels(): List<StoryChannel> {
        delay(latencyMs)
        // Resolved here rather than held as constants: the bundled files become addressable URIs
        // only at runtime, and only from a coroutine.
        val sampleImage = Res.getUri(SAMPLE_IMAGE)
        val sampleVideo = Res.getUri(SAMPLE_VIDEO)
        return listOf(
            publicRelations(sampleImage),
            insured(),
            assistant(sampleVideo),
            pensioners(),
            employers(),
        )
    }

    private fun publicRelations(imageUrl: String) = StoryChannel(
        key = "pr",
        name = "روابط عمومی سازمان",
        shortName = "روابط عمومی",
        time = "امروز · ۰۹:۴۰",
        palette = StoryPalette(
            ringStart = Color(0xFF7FB4FF),
            ringEnd = Color(0xFF1F4FA3),
            avatarStart = Color(0xFF3B6FE8),
            avatarEnd = Color(0xFF1FB6D8),
            backdropStart = Color(0xFF0B2450),
            backdropMid = Color(0xFF123B77),
            backdropMidStop = 0.55f,
            backdropEnd = Color(0xFF0E5E84),
            ctaTone = Color(0xFF123B77),
            iconTint = Color(0xFFEAF1FF),
            iconTone = Color(0xFF1F4FA3),
        ),
        icon = Res.drawable.ic_story_public_relations,
        items = persistentListOf(
            StoryItem(
                id = "pr:0",
                title = "تأمین‌من به‌روز شد",
                body = "پرداخت حق بیمه، مشاهدهٔ سوابق و دریافت فیش، همه در یک صفحه جمع شده است.",
                media = StoryMedia.None,
                cta = StoryCta("دیدن سوابق من", FeatureFlag.WAGE_AND_HISTORY),
                baseLikes = 312,
            ),
            // The slide the design renders as the reference screenshot, and the one carrying the
            // image path so that a picture story is reachable without a service.
            StoryItem(
                id = "pr:1",
                title = "خدمات غیرحضوری",
                body = "بیشتر درخواست‌ها را از همین اپ ثبت کنید؛ مراجعه به شعبه فقط برای موارد ضروری لازم است.",
                media = StoryMedia.Image(imageUrl),
                baseLikes = 243,
            ),
            StoryItem(
                id = "pr:2",
                title = "ارتباط با ما",
                body = "پاسخ‌گویی تلفنی و پیام‌رسان سازمان، همهٔ روزهای هفته در دسترس شماست.",
                media = StoryMedia.None,
                baseLikes = 98,
            ),
        ),
    )

    private fun insured() = StoryChannel(
        key = "ins",
        name = "بیمه‌شده‌ها",
        shortName = "بیمه‌شده‌ها",
        time = "امروز · ۰۹:۱۰",
        palette = StoryPalette(
            ringStart = Color(0xFF7BE3A8),
            ringEnd = Color(0xFF03794A),
            avatarStart = Color(0xFF0B8A57),
            avatarEnd = Color(0xFF3BC98D),
            backdropStart = Color(0xFF04321F),
            backdropMid = Color(0xFF0A6340),
            backdropMidStop = 0.55f,
            backdropEnd = Color(0xFF123B77),
            ctaTone = Color(0xFF0A6340),
            iconTint = Color(0xFFE6F7EE),
            iconTone = Color(0xFF03794A),
        ),
        icon = Res.drawable.ic_story_insured,
        items = persistentListOf(
            StoryItem(
                id = "ins:0",
                title = "سابقهٔ شما، یک‌جا",
                body = "همهٔ سال‌های بیمه‌پردازی، روزهای کارکرد و دستمزد هر ماه را در یک نمودار ببینید.",
                media = StoryMedia.None,
                cta = StoryCta("کلیهٔ سوابق", FeatureFlag.COMBINED_RECORD),
                baseLikes = 187,
            ),
            StoryItem(
                id = "ins:1",
                title = "سابقهٔ جامانده را اعلام کنید",
                body = "اگر بازه‌ای از کارکرد شما ثبت نشده، درخواست بررسی را از اپ ثبت کنید.",
                media = StoryMedia.None,
                cta = StoryCta("اعلام سابقه", FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY),
                baseLikes = 154,
            ),
            StoryItem(
                id = "ins:2",
                title = "قرارداد و پرداخت حق بیمه",
                body = "قرارداد بیمهٔ اختیاری یا مشاغل آزاد را ببندید و اقلام ماهانه را همان‌جا پرداخت کنید.",
                media = StoryMedia.None,
                cta = StoryCta("امور قراردادها", FeatureFlag.CONTRACTS),
                baseLikes = 121,
            ),
        ),
    )

    private fun assistant(videoUrl: String) = StoryChannel(
        key = "ai",
        name = "هوش مصنوعی · یارا",
        shortName = "هوش مصنوعی",
        time = "امروز · ۰۸:۱۵",
        palette = StoryPalette(
            ringStart = Color(0xFFB9A6FF),
            ringEnd = Color(0xFF7C5CFF),
            avatarStart = Color(0xFF7C5CFF),
            avatarEnd = Color(0xFF22B8D6),
            backdropStart = Color(0xFF241A5C),
            backdropMid = Color(0xFF3A2A8F),
            backdropMidStop = 0.52f,
            backdropEnd = Color(0xFF155E7C),
            ctaTone = Color(0xFF3A2A8F),
            iconTint = Color(0xFFF1ECFF),
            iconTone = Color(0xFF7C5CFF),
        ),
        icon = Res.drawable.ic_story_assistant,
        items = persistentListOf(
            // The clip lives on the opening slide of this channel so that a video story is one
            // tap from the rail rather than buried behind two images.
            StoryItem(
                id = "ai:0",
                title = "یارا، دستیار هوشمند",
                body = "سؤال‌های بیمه‌ای خود را به زبان ساده بپرسید و پاسخ روشن بگیرید.",
                media = StoryMedia.Video(videoUrl),
                cta = StoryCta("شروع گفت‌وگو", FeatureFlag.AGENT),
                baseLikes = 245,
            ),
            StoryItem(
                id = "ai:1",
                title = "چقدر تا بازنشستگی مانده؟",
                body = "یارا سابقهٔ شما را می‌خواند و شرایط بازنشستگی را ساده توضیح می‌دهد.",
                media = StoryMedia.None,
                cta = StoryCta("پرسیدن از یارا", FeatureFlag.AGENT),
                baseLikes = 176,
            ),
            StoryItem(
                id = "ai:2",
                title = "پاسخ همراه با ارجاع",
                body = "هر پاسخ به قانون و بخشنامهٔ مربوط ارجاع داده می‌شود تا خیال شما راحت باشد.",
                media = StoryMedia.None,
                baseLikes = 89,
            ),
        ),
    )

    private fun pensioners() = StoryChannel(
        key = "pen",
        name = "مستمری‌بگیران",
        shortName = "مستمری‌بگیران",
        time = "دیروز · ۱۹:۳۰",
        palette = StoryPalette(
            ringStart = Color(0xFF7FE7E0),
            ringEnd = Color(0xFF0E7C82),
            avatarStart = Color(0xFF0E7C82),
            avatarEnd = Color(0xFF5FD8D2),
            backdropStart = Color(0xFF06333A),
            backdropMid = Color(0xFF0E5F66),
            backdropMidStop = 0.55f,
            backdropEnd = Color(0xFF12405F),
            ctaTone = Color(0xFF0E5F66),
            iconTint = Color(0xFFE6F6F5),
            iconTone = Color(0xFF0E7C82),
        ),
        icon = Res.drawable.ic_story_pensioner,
        items = persistentListOf(
            StoryItem(
                id = "pen:0",
                title = "فیش حقوقی هر ماه",
                body = "فیش هر ماه پس از واریز مستمری در اپ قابل مشاهده و دریافت است.",
                media = StoryMedia.None,
                cta = StoryCta("مشاهدهٔ فیش حقوقی", FeatureFlag.PAY_ROLL),
                baseLikes = 204,
            ),
            StoryItem(
                id = "pen:1",
                title = "حکم مستمری",
                body = "آخرین حکم و احکام گذشتهٔ خود را ببینید و نسخهٔ آن را ذخیره کنید.",
                media = StoryMedia.None,
                cta = StoryCta("حکم مستمری", FeatureFlag.EDICT_PENSIONER),
                baseLikes = 141,
            ),
            StoryItem(
                id = "pen:2",
                title = "گواهی بدون مراجعه",
                body = "گواهی حقوق و گواهی کسر اقساط را از همین اپ درخواست کنید.",
                media = StoryMedia.None,
                baseLikes = 73,
            ),
        ),
    )

    private fun employers() = StoryChannel(
        key = "emp",
        name = "کارفرمایان",
        shortName = "کارفرمایان",
        time = "دیروز · ۱۲:۰۵",
        palette = StoryPalette(
            ringStart = Color(0xFFFFD48A),
            ringEnd = Color(0xFFC97E0A),
            avatarStart = Color(0xFFE7A33A),
            avatarEnd = Color(0xFFC97E0A),
            backdropStart = Color(0xFF4A2E06),
            backdropMid = Color(0xFF8A5A10),
            backdropMidStop = 0.52f,
            backdropEnd = Color(0xFF2F3E60),
            ctaTone = Color(0xFF8A5A10),
            iconTint = Color(0xFFFDF1DE),
            iconTone = Color(0xFFC97E0A),
        ),
        icon = Res.drawable.ic_story_employer,
        items = persistentListOf(
            StoryItem(
                id = "emp:0",
                title = "لیست و پرداخت",
                body = "لیست حق بیمه را ارسال و بدهی کارگاه را در همان صفحه پرداخت کنید.",
                media = StoryMedia.None,
                cta = StoryCta("کارگاه‌های من", FeatureFlag.WORKSHOPS),
                baseLikes = 132,
            ),
            StoryItem(
                id = "emp:1",
                title = "نتیجهٔ بازرسی",
                body = "گزارش بازرسی کارگاه و مهلت اعتراض را از اپ پیگیری کنید.",
                media = StoryMedia.None,
                baseLikes = 66,
            ),
            StoryItem(
                id = "emp:2",
                title = "نمایندهٔ الکترونیک",
                body = "برای هر کارگاه نمایندهٔ رسمی تعریف کنید تا خدمات را از طرف شما بگیرد.",
                media = StoryMedia.None,
                baseLikes = 51,
            ),
        ),
    )
}
