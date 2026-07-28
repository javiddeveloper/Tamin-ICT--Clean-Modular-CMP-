package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChartKind
import com.tamin.taminhamrah.feature.agent.service.base.ChartSeries
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.filterValue
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.model.agent.AgentActionKey

/**
 * Demo service that renders one bubble type per call, so every answer shape can be
 * exercised end to end without a backend.
 *
 * The fixture sends one entity per `variant`, which makes them arrive one after another
 * in the chat exactly like a real multi-step answer. Sample content is drawn from a
 * public news item about the organisation's finances purely so the numbers and imagery
 * look realistic.
 *
 * Delete this together with [AgentActionKey.SHOWCASE] once the real backend is wired.
 */
class ShowcaseAgentService : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.SHOWCASE)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val bubble = when (params.filterValue("variant")) {
            "rich_text" -> ChatBubbleContent.RichText(
                header = HEADLINE,
                body = LEAD,
                footnote = "منبع: دنیای اقتصاد — ۱۴۰۵/۰۴/۲۲"
            )

            "text" -> ChatBubbleContent.Text(
                "بر پایه این گزارش، مصارف ماهانه سازمان حدود ۲۱۰ همت است در حالی که " +
                    "وصول حق بیمه ماهانه کمتر از ۱۲۰ همت گزارش شده است."
            )

            "key_value" -> ChatBubbleContent.KeyValue(
                title = "ارقام کلیدی گزارش",
                items = listOf(
                    "کسری ماهانه" to "۹۰ همت",
                    "مصارف ماهانه" to "۲۱۰ همت",
                    "وصول حق بیمه ماهانه" to "کمتر از ۱۲۰ همت",
                    "بدهی دولت" to "۷۵۰ همت",
                    "بدهی کارفرمایان" to "۲۰۰ همت",
                    "بیمه‌شدگان تحت پوشش" to "۴۷ میلیون نفر",
                    "مستمری‌بگیران" to "۵.۲ میلیون نفر"
                ).toKeyValueRows()
            )

            "chart" -> ChatBubbleContent.Chart(
                title = "منابع و مصارف ماهانه (همت)",
                kind = ChartKind.BAR,
                labels = listOf("مصارف", "وصولی", "کسری"),
                series = listOf(ChartSeries(name = "ماهانه", values = listOf(210.0, 120.0, 90.0))),
                valueUnit = "همت"
            )

            "chart_line" -> ChatBubbleContent.Chart(
                title = "روند بدهی‌ها (همت)",
                kind = ChartKind.LINE,
                labels = listOf("کارفرمایان", "دولت"),
                series = listOf(ChartSeries(name = "بدهی", values = listOf(200.0, 750.0))),
                valueUnit = "همت"
            )

            "image" -> ChatBubbleContent.Image(
                source = ARTICLE_IMAGE,
                caption = "نشست خبری مدیرعامل سازمان تامین اجتماعی"
            )

            "video" -> ChatBubbleContent.Video(
                source = "https://www.tamin.ir/video/sample.mp4",
                thumbnailUrl = ARTICLE_IMAGE,
                durationMs = 96_000L,
                caption = "گزارش تصویری نشست خبری"
            )

            "voice" -> ChatBubbleContent.Voice(
                source = "https://www.tamin.ir/audio/sample.m4a",
                durationMs = 18_000L,
                amplitudes = SAMPLE_WAVEFORM,
                caption = "خلاصه صوتی گزارش"
            )

            "deep_link" -> ChatBubbleContent.DeepLink(
                title = "مشاهده سوابق و دستمزد",
                destination = "workshops"
            )

            "web_link" -> ChatBubbleContent.WebLink(
                title = "متن کامل گزارش در دنیای اقتصاد",
                url = ARTICLE_URL
            )

            "processing" -> ChatBubbleContent.ProcessingSteps(
                steps = listOf("بررسی درخواست", "دریافت آمار", "آماده‌سازی پاسخ"),
                currentActiveIndex = 2,
                isCompleted = true
            )

            "error" -> ChatBubbleContent.ServiceError(
                message = "دریافت آمار لحظه‌ای ممکن نشد.",
                canRetryPrompt = true,
                actionKey = AgentActionKey.SHOWCASE
            )

            "suggestions" -> ChatBubbleContent.SuggestedPrompts(
                prompts = listOf(
                    "بدهی دولت به تامین اجتماعی چقدر است؟",
                    "چند نفر مستمری‌بگیر هستند؟",
                    "شرایط بیمه بیکاری چیست؟"
                )
            )

            else -> ChatBubbleContent.Text(params.message ?: "نمونه‌ای برای نمایش انتخاب نشد.")
        }

        return AgentServiceResult.Success(listOf(bubble))
    }

    private companion object {
        const val HEADLINE = "کسری ۹۰ همتی تامین اجتماعی"
        const val LEAD =
            "مدیرعامل سازمان تامین اجتماعی از ناترازی ۹۰ همتی منابع و بدهی ۷۵۰ همتی دولت " +
                "به این سازمان خبر داد و به شمار ۲۹۰ هزار نفری متقاضیان بیمه بیکاری اشاره کرد."
        const val ARTICLE_URL =
            "https://donya-e-eqtesad.com/بخش-بازار-پول-ارز-116/4281737"
        const val ARTICLE_IMAGE =
            "https://cdn.donya-e-eqtesad.com/thumbnail/lxq0x0mNjWDN/" +
                "QHn8O9nsSzT8qCU7RegsN6Pbb5v74eEtbKeSOh05RaYNq9kWHVLNyUt7TZyzEhnm/" +
                "0d50adf9ZjoxMzU1NDQ5MC5qcGd8ZnVpOjE2MjY0NTIxfGw6ZmF8djoxfHdpOjU2Nw+copy.jpg"

        /** A hand-shaped waveform so the voice bubble looks like real speech. */
        val SAMPLE_WAVEFORM = listOf(
            2000, 6500, 12000, 18000, 9000, 4200, 15000, 22000, 17000, 8000,
            3000, 11000, 19000, 26000, 21000, 12000, 5000, 9500, 16000, 7000
        )
    }
}
